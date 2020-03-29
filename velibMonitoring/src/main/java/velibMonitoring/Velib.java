package velibMonitoring;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;

import velibMonitoring.bolts.CityStatsBolt;
import velibMonitoring.bolts.SaveResultsBolt;
import velibMonitoring.bolts.StationParsingBolt;

public class Velib 
{
	public static void main( String[] args ) throws Exception
	  {
		// Create topology Builder
		final TopologyBuilder tp = new TopologyBuilder();
		
		// Add a spout reading the Kafka topic "velib-stations" and part of group "city-stats" on localhost:9092
		tp.setSpout("stations_spout", new KafkaSpout<String, String>(
				KafkaSpoutConfig.builder("127.0.0.1:" + "9092", "velib-stations")
	            .setProp(ConsumerConfig.GROUP_ID_CONFIG, "city-stats")
	            .build()), 1);
		
		// Add a bolt to extract data from Kafka message (JSON) and emit by city
		tp.setBolt("station_parsing_bolt", new StationParsingBolt()).shuffleGrouping("stations_spout");
		
		// Add a bolt to compute the average number of empty bike stands by city
		tp.setBolt("city_stats_bolt", new CityStatsBolt().withTumblingWindow(BaseWindowedBolt.Duration.of(1000*60*5)), 2)
		.fieldsGrouping("station_parsing_bolt", new Fields("city"));
		
		// Add a bolt to write in CSV files
		tp.setBolt("save_results_bolt", new SaveResultsBolt(), 2).fieldsGrouping("city_stats_bolt", new Fields("city"));
		
		// Create topology Builder
    	StormTopology topology = tp.createTopology();

    	// Configuration 
    	Config config = new Config();
    	config.setMessageTimeoutSecs(60*30);
    	String topologyName = "velos";
    	
    	// Handle remote/local cluster
    	if(args.length > 0 && args[0].equals("remote")) {
    		StormSubmitter.submitTopology(topologyName, config, topology);
    	}
    	else {
    		LocalCluster cluster = new LocalCluster();
        	cluster.submitTopology(topologyName, config, topology);
    	}
	  }
}
