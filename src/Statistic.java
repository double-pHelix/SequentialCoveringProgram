

public class Statistic {

	private String coverage;
	private String accuracy;
	
	public Statistic(String coverage, String accuracy) {
		super();
		this.coverage = coverage;
		this.accuracy = accuracy;
	}
	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	
	
}
