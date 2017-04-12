package it.percassi.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.batch.item.ItemProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.model.Values;
import it.percassi.utils.PerPortalConstants;

public class NrResponseProcessor implements ItemProcessor<List<NewRelicResponse>, String> {


	@Override
	public String process(List<NewRelicResponse> itemsToConvert) throws Exception {
		
		final List<NewRelicMongoItem> nrItemsToSave = new ArrayList<>();
		final ObjectMapper obejctMapper = new ObjectMapper();
		LocalDate day = null;
		
		if(!itemsToConvert.isEmpty()){
			LocalDateTime localDate = itemsToConvert.get(0).getMetricData().getTo();
			day = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
		}
		
		for (NewRelicResponse item : itemsToConvert) {

			Values values = item.getMetricData().getMetrics().get(0).getTimeslices().get(0).getValues();
			System.out.println("Metrics: "+ReflectionToStringBuilder.toString(item.getMetricData().getMetrics().get(0), 
					ToStringStyle.MULTI_LINE_STYLE					
					));
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
		
		String objectToSave = obejctMapper.writeValueAsString(nrItemsToSave);
		return objectToSave;
	}

	

}
