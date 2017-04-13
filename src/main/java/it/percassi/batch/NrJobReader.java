package it.percassi.batch;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import it.percassi.batch.nrelic.NewRelicResponseListBean;
import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.service.NewRelicServiceRequest;
import it.percassi.batch.nrelic.service.NewRelicServiceResponse;
import it.percassi.batch.nrelic.service.NrMetricService;
import it.percassi.utils.PerPortalConstants;

@Component
public class NrJobReader implements ItemReader<NewRelicResponseListBean> {

	private static final Logger LOG = LoggerFactory.getLogger(NrJobReader.class);

//	private final NrMetricService service;
//	private final String beId;
//	private final String feId;
//
//	public NrJobReader(NrMetricService service, String beId, String feId) {
//
//		super();
//		this.service = service;
//		this.beId = beId;
//		this.feId = feId;
//
//	}
	
	@Autowired
	@Qualifier("nrMetricService")
	private NrMetricService service;

	@Value("${nr.be.id}")
	private String beId;

	@Value("${nr.fe.id}")
	private String feId;


	@Override
	public NewRelicResponseListBean read() throws UnexpectedInputException, ParseException,
			NonTransientResourceException, RestClientException, IOException {

		final NewRelicResponseListBean nrResponseBean = new NewRelicResponseListBean();
		final List<NewRelicResponse> nrResponses = new ArrayList<>();
		final LocalDateTime now = LocalDateTime.now();
		final LocalDateTime fromDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
		final LocalDateTime toDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59);

		for (String metric : PerPortalConstants.NR_METRICS) {

			for (String value : PerPortalConstants.NR_VALUES) {

				final NewRelicServiceRequest serviceRequest = new NewRelicServiceRequest(fromDate, toDate, metric, 0,
						true, value, Integer.valueOf(this.feId));
				final NewRelicServiceResponse serviceResponse = this.service.getNrMetric(serviceRequest);
				LOG.info("NR response:{} ", serviceResponse.getNewRelicResponse());
				if (serviceResponse != null && serviceResponse.getNewRelicResponse() != null) {

					nrResponses.add(serviceResponse.getNewRelicResponse());
				}
			}
		}
		nrResponseBean.setNewRelicResponselistBean(nrResponses);
		return nrResponseBean;
	}
}
