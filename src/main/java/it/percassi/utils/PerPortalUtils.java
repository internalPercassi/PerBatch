package it.percassi.utils;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.ObjectError;
import org.springframework.web.util.UriComponentsBuilder;

public class PerPortalUtils {

	

	private static final String SLASH = "/";

	@Value("${nr.api.key}")
	private String apiKey;

	/**
	 * Simple method to generate an error message given a List of
	 * {@link ObjectError}
	 *
	 * @param errors A list of {@link ObjectError}
	 * @return An error string
	 */
	public static String generateErrorMessage(final List<ObjectError> errors) {

		final StringBuilder errorMessage = new StringBuilder();

		for (ObjectError error : errors) {

			errorMessage.append(error.getDefaultMessage());
		}
		return errorMessage.toString();

	}

	/***
	 *
	 * @param urlToCall
	 * @param fromDate
	 *            The starting date as {@link LocalDateTime}
	 * @param toDate
	 *            The ending date as {@link LocalDateTime}
	 * @param uriParams
	 *            the uri params in this form as {@link MultiValueMap}
	 * @param isSummarize
	 *            True if the result must be a summarize of the data
	 * @param period
	 *            The sampling time (in seconds)
	 * @return The uri {@link URI}
	 */
	public static URI generateUriToCall(String urlToCall, LocalDateTime fromDate, LocalDateTime toDate,
			MultiValueMap<String, String> uriParams, int period,boolean isSummarize) {

		final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(urlToCall).queryParams(uriParams)
				.queryParam("from", fromDate).queryParam("to", toDate);

		if (period != 0) {
			builder.queryParam("period", period);
		}
		if(isSummarize){
		builder.queryParam("summarize", isSummarize);
		}
		final URI uri = builder.buildAndExpand(uriParams).toUri();
		return uri;
	}

	public static HttpEntity<String> builHttpEntityNewRelicApi(String apiKey) {
		final HttpEntity<String> entity = new HttpEntity<String>("parameters", createNewRelicHttpHeaders(apiKey));
		return entity;
	}

	public static String createNewRelicUrl(String url, int machineId, String newRelicEndUrl) {
		final StringBuilder urlToCall = new StringBuilder();
		urlToCall.append(url).append(SLASH).append(machineId).append(newRelicEndUrl);
		return urlToCall.toString();
	}

	private static HttpHeaders createNewRelicHttpHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
		headers.set(PerPortalConstants.NEW_RELIC_API_KEY_HEADER, apiKey);
		return headers;
	}
}
