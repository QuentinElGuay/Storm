package urlVisits;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class App 
{
  public static void main( String[] args ) throws Exception
  {      
      //Interact with the cluster...
  	TopologyBuilder builder = new TopologyBuilder();
	builder.setSpout("page-visits", new PageVisitSpout());
	builder.setBolt("visit-counts", new VisitCountBolt())
		.shuffleGrouping("page-visits");
	builder.setBolt("page-counts", new PageVisitCountBolt(), 2)
		.fieldsGrouping("page-visits", new Fields("url"));
	builder.setBolt("user-counts", new UserVisitCountBolt(), 2)
		.fieldsGrouping("page-visits", new Fields("userId"));
	  StormTopology topology = builder.createTopology();
	//          
	  Config config = new Config();
	  config.setMessageTimeoutSecs(2);
	  String topologyName = "urlVisits";
	  
	  if(args.length > 0 && args[0].equals("remote"))
	  {
		  System.out.println("Cluster running in remote mode.");
		  StormSubmitter.submitTopology(topologyName, config, topology);
	  }
	  else
	  {
		  System.out.println("Cluster running in local mode.");
		  LocalCluster cluster = new LocalCluster();
		  cluster.submitTopology(topologyName, config, topology);
	  }
  }
}
