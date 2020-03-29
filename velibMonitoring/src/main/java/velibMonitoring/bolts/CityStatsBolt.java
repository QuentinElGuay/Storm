package velibMonitoring.bolts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

public class CityStatsBolt extends BaseWindowedBolt {
	private OutputCollector outputCollector;
	
	
	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		this.outputCollector = collector;
	}	
	
	@Override
	public void execute(TupleWindow inputWindow) {
		HashMap<String, HashMap<Long, ArrayList<Long>>> stationAvailableStands = new HashMap<String, HashMap<Long, ArrayList<Long>>>();
		
		// Read tuples from the inputWindow
		Long tupleCount = 0L;
		for(Tuple input : inputWindow.get())
		{
			String city = input.getStringByField("city");
			Long stationId = input.getLongByField("station_id");
			Long availableBikeStands = input.getLongByField("available_stands");
			
			stationAvailableStands.putIfAbsent(city, new HashMap<Long, ArrayList<Long>>());
			stationAvailableStands.get(city).putIfAbsent(stationId, new ArrayList<Long>());
			stationAvailableStands.get(city).get(stationId).add(availableBikeStands);
			
			outputCollector.ack(input);
			tupleCount += 1;
		}
		
		System.out.printf("========== CityStatsBolt received %d tuples", tupleCount);
		
		// Emit the average number of empty bike stand in the city for the time window.
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		for(Entry<String, HashMap<Long, ArrayList<Long>>> cityStats : stationAvailableStands.entrySet())
		{
			String city = cityStats.getKey();
			Double totalAvailableStands = 0.;
			
			for(Entry<Long, ArrayList<Long>> station : cityStats.getValue().entrySet())
			{
				Double totalStationStands = 0.;
				for(Long availableBikeStands : station.getValue())
				{
					totalStationStands += availableBikeStands;
				}
				
				Double averageAvailableStands = 0.;
				if(!station.getValue().isEmpty())
				{
					averageAvailableStands = totalStationStands / station.getValue().size();
				}
				totalAvailableStands += averageAvailableStands;
			}
			this.outputCollector.emit(new Values(city, now, totalAvailableStands));
		}
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("city", "date", "available_bike_stands"));
	}

}
