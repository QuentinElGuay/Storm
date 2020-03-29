package velibMonitoring.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StationParsingBolt extends BaseRichBolt {
	
	private OutputCollector outputcollector;

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		outputcollector = collector;
	}

	@Override
	public void execute(Tuple input) {
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject json = (JSONObject)jsonParser.parse(input.getStringByField("value"));
			String city = (String)json.get("contract_name");
			Long station_id = (Long)json.get("number");
			Long available_stands = (Long)json.get("available_bike_stands");
			
			this.outputcollector.emit(input, new Values(city, station_id, available_stands));
			this.outputcollector.ack(input);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("city", "station_id", "available_stands"));
	}

}
