package it.percassi.batch;

import java.io.Serializable;
import java.time.LocalDate;

public class NewRelicMongoItem implements Serializable{

	private static final long serialVersionUID = -4820815317741060556L;
	
	private String metricName;
	private LocalDate day;
	private float value;
	private String valueName;

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

}
