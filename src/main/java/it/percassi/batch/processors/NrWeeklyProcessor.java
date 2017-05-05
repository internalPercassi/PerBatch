package it.percassi.batch.processors;

import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import it.percassi.batch.mongo.NewRelicMongoWeeklyItem;
import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.model.Values;
import it.percassi.utils.PerPortalConstants;

public class NrWeeklyProcessor implements ItemProcessor<NewRelicResponse, NewRelicMongoWeeklyItem>{
	
	private static final Logger LOG = LoggerFactory.getLogger(NrWeeklyProcessor.class);


	@Override
	public NewRelicMongoWeeklyItem process(NewRelicResponse item) throws Exception {
		
		Values values = item.getMetricData().getMetrics().get(0).getTimeslices().get(0).getValues();
		final NewRelicMongoWeeklyItem newRelicMongoWeeklyItem = new NewRelicMongoWeeklyItem();
		LocalDateTime fromDate = item.getMetricData().getFrom();
		

		TemporalField weekField = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 
		int weekNumber = fromDate.toLocalDate().get(weekField);

		
		boolean isAverageTimeNull = (values.getAverageResponseTime() == 0);
		float summarizeValue = (!isAverageTimeNull) ? values.getAverageResponseTime() : values.getCallCount();
		String valueName = (!isAverageTimeNull) ? PerPortalConstants.NEW_RELIC_AVG_RESP_TIME_VALUE
				: PerPortalConstants.NEW_RELIC_CALL_COUNT_VALUE;

		newRelicMongoWeeklyItem.setDay(null);
		newRelicMongoWeeklyItem.setMetricName(item.getMetricData().getMetrics().get(0).getName());
		newRelicMongoWeeklyItem.setValueName(valueName);
		newRelicMongoWeeklyItem.setValue(summarizeValue);
		newRelicMongoWeeklyItem.setWeekNumber(weekNumber);
		LOG.info("item processed: {}",newRelicMongoWeeklyItem.toString());
		return newRelicMongoWeeklyItem;
	}

}
