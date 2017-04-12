package it.percassi.batch.nrelic.service;

import java.io.IOException;

import org.springframework.web.client.RestClientException;

public interface NrMetricService {

	public NewRelicServiceResponse getNrMetric(NewRelicServiceRequest request)
			throws RestClientException, IOException;


}
