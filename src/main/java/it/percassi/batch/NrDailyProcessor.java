package it.percassi.batch;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import it.percassi.batch.nrelic.NewRelicMongoDailyItem;
import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.model.Values;
import it.percassi.utils.PerPortalConstants;

public class NrDailyProcessor implements ItemProcessor<NewRelicResponse, NewRelicMongoDailyItem> {

	private static final Logger LOG = LoggerFactory.getLogger(NrDailyProcessor.class);

	@Override
	public NewRelicMongoDailyItem process(NewRelicResponse item) throws Exception {

		LocalDateTime fromDate = item.getMetricData().getFrom();
		ZonedDateTime zdt = fromDate.atZone(ZoneId.systemDefault());
		Values values = item.getMetricData().getMetrics().get(0).getTimeslices().get(0).getValues();
		Date day = Date.from(zdt.toInstant());

		final NewRelicMongoDailyItem newRelicMongoItem = new NewRelicMongoDailyItem();

		boolean isAverageTimeNull = (values.getAverageResponseTime() == 0);
		float summarizeValue = (!isAverageTimeNull) ? values.getAverageResponseTime() : values.getCallCount();
		String valueName = (!isAverageTimeNull) ? PerPortalConstants.NEW_RELIC_AVG_RESP_TIME_VALUE
				: PerPortalConstants.NEW_RELIC_CALL_COUNT_VALUE;

		newRelicMongoItem.setDay(day);
		newRelicMongoItem.setMetricName(item.getMetricData().getMetrics().get(0).getName());
		newRelicMongoItem.setValueName(valueName);
		newRelicMongoItem.setValue(summarizeValue);

		LOG.info("Object processed: {}", newRelicMongoItem);
		return newRelicMongoItem;

	}

}
