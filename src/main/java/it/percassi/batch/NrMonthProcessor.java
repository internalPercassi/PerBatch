package it.percassi.batch;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import it.percassi.batch.nrelic.NewRelicMongoMonthlyItem;
import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.model.Values;
import it.percassi.utils.PerPortalConstants;

public class NrMonthProcessor implements ItemProcessor<NewRelicResponse, NewRelicMongoMonthlyItem> {
	
	private static final Logger LOG = LoggerFactory.getLogger(NrMonthProcessor.class);

	@Override
	public NewRelicMongoMonthlyItem process(NewRelicResponse item) throws Exception {
		
		Values values = item.getMetricData().getMetrics().get(0).getTimeslices().get(0).getValues();
		NewRelicMongoMonthlyItem newRelicMongoMonthlyItem = new NewRelicMongoMonthlyItem();
		
		LocalDateTime fromDate = item.getMetricData().getFrom();
		
		String year= Integer.toString(fromDate.getYear());
		String month = null;
		
		if(fromDate.getMonthValue()<10){
			
			month="0"+Integer.toString(fromDate.getMonthValue());
		}
		
		else{
			
			month=Integer.toString(fromDate.getMonthValue());
		}
	
		
		boolean isAverageTimeNull = (values.getAverageResponseTime() == 0);
		float summarizeValue = (!isAverageTimeNull) ? values.getAverageResponseTime() : values.getCallCount();
		String valueName = (!isAverageTimeNull) ? PerPortalConstants.NEW_RELIC_AVG_RESP_TIME_VALUE
				: PerPortalConstants.NEW_RELIC_CALL_COUNT_VALUE;

		newRelicMongoMonthlyItem.setDay(null);
		newRelicMongoMonthlyItem.setMetricName(item.getMetricData().getMetrics().get(0).getName());
		newRelicMongoMonthlyItem.setValueName(valueName);
		newRelicMongoMonthlyItem.setValue(summarizeValue);
		newRelicMongoMonthlyItem.setYearMonth(Integer.valueOf(year+month));
		LOG.info("item processed: {}",newRelicMongoMonthlyItem.toString());
		return newRelicMongoMonthlyItem;
	}

}
