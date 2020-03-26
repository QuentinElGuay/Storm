package urlVisits;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

public class PageVisitCountBolt extends BaseRichBolt {
	private HashMap<String, Integer> pageVisitCounts;
	OutputCollector outputCollector;

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		pageVisitCounts = new HashMap<String, Integer>();	
		this.outputCollector = collector;
	}
	
	@Override
	public void execute(Tuple input) {
		String url = input.getStringByField("url");
		pageVisitCounts.putIfAbsent(url, 0);
		pageVisitCounts.put(url, pageVisitCounts.get(url) + 1);
		
		System.out.printf("Received %d visit(s) for page %s.\n", pageVisitCounts.get(url), url);
		
		outputCollector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}
}
