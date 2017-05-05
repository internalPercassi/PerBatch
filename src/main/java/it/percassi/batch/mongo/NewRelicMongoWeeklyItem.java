package it.percassi.batch.mongo;

public class NewRelicMongoWeeklyItem extends NewRelicMongoDailyItem{
	
	private static final long serialVersionUID = -6405382136041512967L;
	
	private int weekNumber;

	public int getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}
	

}
