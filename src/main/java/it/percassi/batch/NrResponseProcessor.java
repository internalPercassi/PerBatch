package it.percassi.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.batch.item.ItemProcessor;

import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.model.Values;
import it.percassi.utils.PerPortalConstants;

public class NrResponseProcessor implements ItemProcessor<List<NewRelicResponse>, List<NewRelicMongoItem>> {

//    private static final Logger log = LoggerFactory.getLogger(NrResponseProcessor.class);

	@Override
	public List<NewRelicMongoItem> process(List<NewRelicResponse> itemsToConvert) throws Exception {
		
		final List<NewRelicMongoItem> nrItemsToSave = Collections.emptyList();		
		LocalDate day = null;
		
		if(!itemsToConvert.isEmpty()){
			LocalDateTime localDate = itemsToConvert.get(0).getMetricData().getTo();
			day = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
		}
		
		for (NewRelicResponse item : itemsToConvert) {

			Values values = item.getMetricData().getMetrics().get(0).getTimeslices().get(0).getValues();
			NewRelicMongoItem  newRelicMongoItem = new NewRelicMongoItem();
			
			boolean isAverageTimeNull = Objects.isNull(values.getAverageResponseTime());
			float summarizeValue = (!isAverageTimeNull) ? values.getAverageResponseTime() : values.getCallCount();
			String valueName = (!isAverageTimeNull) ? PerPortalConstants.NEW_RELIC_AVG_RESP_TIME_VALUE : PerPortalConstants.NEW_RELIC_CALL_COUNT_VALUE;
			
			newRelicMongoItem.setDay(day);
			newRelicMongoItem.setMetricName(item.getMetricData().getMetrics().get(0).getName());
			newRelicMongoItem.setValueName(valueName);
			newRelicMongoItem.setValue(summarizeValue);
			
			nrItemsToSave.add(newRelicMongoItem);
		}
		
				
		return nrItemsToSave;
	}

	

}
