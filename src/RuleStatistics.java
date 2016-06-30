

import java.util.ArrayList;
import java.util.List;

public class RuleStatistics {
	private List<Statistic> statistics;
	private String sampleSize;
	
	public RuleStatistics() {
		super();
		this.statistics = new ArrayList<Statistic>();
	}
	
	public RuleStatistics(List<Statistic> statistics) {
		super();
		this.statistics = statistics;
	}

	public List<Statistic> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<Statistic> statistics) {
		this.statistics = statistics;
	}
	public void appendStatistic(Statistic stat){
		statistics.add(stat);
	}
	public String getSampleSize() {
		return sampleSize;
	}
	public void setSampleSize(String sampleSize) {
		this.sampleSize = sampleSize;
	}
	
}
