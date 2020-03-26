package urlVisits;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class VisitCountBolt extends BaseRichBolt {
	private Integer totalVisitCount;
	OutputCollector outputCollector;
	
	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		this.outputCollector = collector;
		totalVisitCount = 0;	
	}

	@Override
	public void execute(Tuple input) {
		Integer userId = input.getIntegerByField("userId");
		String url = input.getStringByField("url");

		totalVisitCount++;
		System.out.printf("Received %d visit(s).\n", totalVisitCount);
		
		outputCollector.emit(input, new Values(url, userId));
		outputCollector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// Transmits the tuple to the following bolts
		declarer.declare(new Fields("url", "userId"));
	}

}
