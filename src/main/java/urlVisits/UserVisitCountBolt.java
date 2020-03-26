package urlVisits;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

public class UserVisitCountBolt extends BaseRichBolt {
	private HashMap<Integer, Integer> userVisitCounts;
	OutputCollector outputCollector;
	
	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		userVisitCounts = new HashMap<Integer, Integer>();
		this.outputCollector = collector;
	}

	@Override
	public void execute(Tuple input) {
		Integer userId = input.getIntegerByField("userId");
		userVisitCounts.putIfAbsent(userId, 0);
		userVisitCounts.put(userId, userVisitCounts.get(userId) + 1);
		
		System.out.printf("Received %d visit(s) from user %s.\n", userVisitCounts.get(userId), userId);
		
		outputCollector.ack(input);
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {}

}
