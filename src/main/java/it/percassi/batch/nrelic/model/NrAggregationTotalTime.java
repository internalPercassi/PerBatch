package it.percassi.batch.nrelic.model;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NrAggregationTotalTime {

	private LocalDateTime fromDate;
	private LocalDateTime toDate;
	private float webExternalAverageTime;
	private int webExternalCallCount;
	private int httpDispatcherCallCount;
	
	private float totalTime;

	public LocalDateTime getFrom() {
		return fromDate;
	}

	public void setFrom(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public float getWebExternalAverageTime() {
		return webExternalAverageTime;
	}

	public void setWebExternalAverageTime(float webExternalAverageTime) {
		this.webExternalAverageTime = webExternalAverageTime;
	}

	public int getWebExternalCallCount() {
		return webExternalCallCount;
	}

	public void setWebExternalCallCount(int webExternalCallCount) {
		this.webExternalCallCount = webExternalCallCount;
	}

	public int getHttpDispatcherCallCount() {
		return httpDispatcherCallCount;
	}

	public void setHttpDispatcherCallCount(int httpDispatcherCallCount) {
		this.httpDispatcherCallCount = httpDispatcherCallCount;
	}
	public float getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(float totalTime) {
		this.totalTime = totalTime;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}




}
