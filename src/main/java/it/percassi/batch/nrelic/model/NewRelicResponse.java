package it.percassi.batch.nrelic.model;

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


}
