package it.percassi.batch;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClientException;

import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.service.NewRelicServiceRequest;
import it.percassi.batch.nrelic.service.NewRelicServiceResponse;
import it.percassi.batch.nrelic.service.NrMetricService;


public class NrJobReader implements ItemReader<NewRelicResponse> {

	@Autowired
	@Qualifier("nrMetricService")
	public NrMetricService service;
	//
	// @Value("${nr.fe.id}")
	// private String feId;
	//
	// @Value("${nr.be.id}")
	// private String beId;

	private String metricName;
	private String metricValue;
	private String serverId;

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(String metricValue) {
		this.metricValue = metricValue;
	}

	private boolean  isCallWsFinished = false;
	NewRelicResponse nrResponse = null;

	private static final Logger LOG = LoggerFactory.getLogger(NrJobReader.class);

	public NrJobReader() {

	}



	public NrJobReader(String metricName, String metricValue, String serverId) {
		this.metricName=metricName;
		this.metricValue=metricValue;
		this.serverId=serverId;
	}

	@Override
	public  NewRelicResponse read() throws UnexpectedInputException, ParseException, NonTransientResourceException,
			RestClientException, IOException {
		if (!isCallWsFinished) {

			final LocalDateTime now = LocalDateTime.now();
			final LocalDateTime fromDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
			final LocalDateTime toDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59);

			final NewRelicServiceRequest serviceRequest = new NewRelicServiceRequest(fromDate, toDate, metricName, 0,
					true, metricValue, Integer.parseInt(serverId));
			final NewRelicServiceResponse serviceResponse = service.getNrMetric(serviceRequest);
			if (serviceResponse != null) {
				LOG.info("NR response:{} ", serviceResponse.getNewRelicResponse().toString());
				nrResponse = serviceResponse.getNewRelicResponse();
				isCallWsFinished = true;
				return nrResponse;
			}
	
		}
		return null;
	}

}
