package it.percassi.batch.nrelic;

public class NewRelicMongoMonthlyItem extends NewRelicMongoDailyItem {

	private static final long serialVersionUID = -2653101666082005739L;

	private int yearMonth;

	public int getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(int yearMonth) {
		this.yearMonth = yearMonth;
	}

	

	


}
