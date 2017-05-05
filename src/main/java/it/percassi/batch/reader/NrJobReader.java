package it.percassi.batch.reader;

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

	private String metricName;
	private String metricValue;
	private String serverId;
	private boolean isMonthlyCall;
	private boolean isWeeklyCall;


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
	
	public boolean isMonthlyCall() {
		return isMonthlyCall;
	}
	public boolean isWeeklyCall() {
		return isWeeklyCall;
	}

	private boolean  isCallWsFinished = false;
	NewRelicResponse nrResponse = null;

	private static final Logger LOG = LoggerFactory.getLogger(NrJobReader.class);

	public NrJobReader() {

	}



	public NrJobReader(String metricName, String metricValue, String serverId,boolean isWeeklyCall,boolean isMonthlyCall) {
		this.metricName=metricName;
		this.metricValue=metricValue;
		this.serverId=serverId;
		this.isWeeklyCall=isWeeklyCall;
		this.isMonthlyCall=isMonthlyCall;
		
	}

	@Override
	public  NewRelicResponse read() throws UnexpectedInputException, ParseException, NonTransientResourceException,
			RestClientException, IOException {
		if (!isCallWsFinished) {

			LocalDateTime fromDate=LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();
			LocalDateTime toDate=LocalDateTime.now().toLocalDate().atStartOfDay().minusSeconds(1);
			
			if(isWeeklyCall){
				
				fromDate=LocalDateTime.now().minusWeeks(1).toLocalDate().atStartOfDay();
				toDate=LocalDateTime.now().toLocalDate().atStartOfDay().minusSeconds(1);
			}
			
			if(isMonthlyCall){
				fromDate=LocalDateTime.now().minusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
				toDate=LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay().minusSeconds(1);				
			}
		
			final NewRelicServiceRequest serviceRequest = new NewRelicServiceRequest(fromDate, toDate, metricName, 0,
					true, metricValue, Integer.valueOf(serverId));
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
