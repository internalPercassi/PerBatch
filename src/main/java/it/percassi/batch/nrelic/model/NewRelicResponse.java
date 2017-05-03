package it.percassi.batch.nrelic.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewRelicResponse {

	@JsonProperty("metric_data")
	private MetricsData metricData;

	public MetricsData getMetricData() {
		return metricData;
	}

	public void setMetricData(MetricsData metricData) {
		this.metricData = metricData;
	}
	
	@Override
	public String toString() {
		
		return ReflectionToStringBuilder.toString(this,ToStringStyle.JSON_STYLE);
	}


}
