package it.percassi.batch;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.service.NewRelicServiceRequest;
import it.percassi.batch.nrelic.service.NewRelicServiceResponse;
import it.percassi.batch.nrelic.service.NrMetricService;
import it.percassi.utils.PerPortalConstants;

@Component
public class NrJobReader implements ItemReader<List<NewRelicResponse>> {

	@Autowired
	@Qualifier("nrMetricService")
	private NrMetricService nrMetricService;
	@Value("${nr.be.id}")
	private String beId;
	@Value("${nr.fe.id}")
	private String feId;
	
	
	@Override
	public List<NewRelicResponse> read()
			throws UnexpectedInputException, ParseException, NonTransientResourceException, RestClientException, IOException {
		
		
		final List<NewRelicResponse> nrResponses = Collections.emptyList();
		final LocalDateTime now = LocalDateTime.now();
		final LocalDateTime fromDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),0,0);
		final LocalDateTime toDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),23,59);

		//TODO Call nr for all metrics needed
		final NewRelicServiceRequest serviceRequest = new NewRelicServiceRequest(fromDate, toDate, "HttpDispatcher", 0,
				true, PerPortalConstants.NEW_RELIC_CALL_COUNT_VALUE, Integer.valueOf(this.feId));
		final NewRelicServiceResponse serviceResponse = this.nrMetricService.getNrMetric(serviceRequest);
		if (serviceResponse != null) {			
			nrResponses.add(serviceResponse.getNewRelicResponse());
		}
		return nrResponses;
	}

}
