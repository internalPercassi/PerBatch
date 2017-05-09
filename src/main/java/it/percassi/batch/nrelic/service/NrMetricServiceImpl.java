package it.percassi.batch.nrelic.service;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.utils.PerPortalConstants;
import it.percassi.utils.PerPortalUtils;

@Service
@Qualifier("nrMetricService")
public class NrMetricServiceImpl implements NrMetricService {

	@Value("${nr.url}")
	private String nrUrl;

	@Value("${nr.end.url}")
	private String endUrl;

	@Value("${nr.api.key}")
	private String apiKey;

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	
	private final static Logger LOG = LoggerFactory.getLogger(NrMetricServiceImpl.class);

	@Override
	public NewRelicServiceResponse getNrMetric(final NewRelicServiceRequest request)
			throws RestClientException, IOException {
		LOG.info("Incoming request: {} ", request.toString());
		final MultiValueMap<String, String> uriParams = new LinkedMultiValueMap<String, String>();
		final ObjectMapper objectMapper = new ObjectMapper();
		NewRelicServiceResponse response = null;

		uriParams.put(PerPortalConstants.NEW_RELIC_NAMES, Arrays.asList(request.getMetricName()));
		uriParams.put(PerPortalConstants.NEW_RELIC_VALUES, Arrays.asList(request.getValueParameter()));

		final String newRelicUrl = PerPortalUtils.createNewRelicUrl(nrUrl, request.getMachineId(), endUrl);
		final HttpEntity<String> newRelicEntity = PerPortalUtils.builHttpEntityNewRelicApi(apiKey);
		final URI uri = PerPortalUtils.generateUriToCall(newRelicUrl, request.getFromDate(), request.getToDate(),
				uriParams, request.getSamplePeriod(),request.isSummarize());

		LOG.info("URI generated : {} ", ReflectionToStringBuilder.toString(uri, ToStringStyle.SHORT_PREFIX_STYLE));

		ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, newRelicEntity, String.class);

		if (Objects.isNull(res)) {
			LOG.warn("Response received is null,called this uri {}",ReflectionToStringBuilder.toString(uri, ToStringStyle.MULTI_LINE_STYLE));
			response = new NewRelicServiceResponse(new NewRelicResponse());
			response.setMessage(PerPortalConstants.API_CALL_EMPTY_RESPONSE);
			response.setStatusCode(PerPortalConstants.API_CALL_KO);
			return response;
		}
		NewRelicResponse nrObj = objectMapper.readValue(res.getBody(), NewRelicResponse.class);
		response = new NewRelicServiceResponse(nrObj);
		response.setStatusCode(res.getStatusCodeValue());
		response.setMessage(PerPortalConstants.API_CALL_OK);
		LOG.info("Response received: {} ",ReflectionToStringBuilder.toString(response, ToStringStyle.MULTI_LINE_STYLE));
		return response;

	}

}
