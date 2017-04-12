package it.percassi.batch.nrelic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Sigolotto
 *
 */
public class Values {

	@JsonProperty("call_count")
	private int callCount;

	@JsonProperty("average_response_time")
	private float averageResponseTime;

	public int getCallCount() {
		return callCount;
	}

	public void setCallCount(int callCount) {
		this.callCount = callCount;
	}

	public float getAverageResponseTime() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(float averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}


}
