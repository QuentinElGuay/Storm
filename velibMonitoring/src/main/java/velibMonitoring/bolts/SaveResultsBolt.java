package velibMonitoring.bolts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

public class SaveResultsBolt extends BaseRichBolt {
	OutputCollector outputCollector;

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		this.outputCollector = collector;
	}

	@Override
	public void execute(Tuple input) {
		try {
			this.process(input);
			this.outputCollector.ack(input);
		} catch (Exception e) {
			e.printStackTrace();
			this.outputCollector.fail(input);
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	public void process(Tuple input) throws IOException {
		// Read from incoming tuple
		String city = input.getStringByField("city");
		String date = input.getStringByField("date");
		Double availableBikeStands = input.getDoubleByField("available_bike_stands");

		// Append values to a file by city
		String filePath = String.format("/tmp/%s.csv", city);
		File csvFile = new File(filePath);
		if (!csvFile.exists()) {
			FileWriter writer = new FileWriter(filePath);
			writer.write("date;available bike stands\n");
			writer.close();
		}
		FileWriter writer = new FileWriter(filePath, true);
		System.out.printf("====== SaveResultsBolt: %s %s - %f available stands\n", date, city, availableBikeStands);
		writer.write(String.format("%s;%.2f\n", date, availableBikeStands));
		writer.close();
	}

}
