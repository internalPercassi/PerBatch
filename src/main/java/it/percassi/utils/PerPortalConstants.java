package it.percassi.utils;

public class PerPortalConstants {

	public static final String NEW_RELIC_API_KEY_HEADER = "X-Api-Key";
	public static final String NEW_RELIC_NAMES = "names";
	public static final String NEW_RELIC_VALUES = "values";
	public static final String NEW_RELIC_CALL_COUNT_VALUE = "call_count";
	public static final String NEW_RELIC_AVG_RESP_TIME_VALUE = "average_response_time";
	public static final String MIME_TYPE_TEXT_CSV = "text/csv";
	public static final String MIME_TYPE_EXCEL_CSV = "application/vnd.ms-excel";
	public static final int SAMPLE_NR_PERIOD = 7200;
	public static final String API_CALL_OK = "API call successfully";
	public static final String API_CALL_EMPTY_RESPONSE = "Empty response";
	public static final int API_CALL_KO = -1;
	public static final String NEW_RELIC_COLLECTION_DAILY = "newRelicDaily";
	public static final String NEW_RELIC_COLLECTION_MONTHLY = "newRelicMonthly";
	public static final String NEW_RELIC_COLLECTION_WEEKLY = "newRelicWeekly";



	public static final String[] NR_METRICS = { "HttpDispatcher", "EndUser", };
	public static final String[] NR_VALUES = { "call_count", "average_response_time" };
	

}
