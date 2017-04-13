package it.percassi.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.percassi.batch.nrelic.NewRelicResponseListBean;
import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.model.Values;
import it.percassi.utils.PerPortalConstants;

public class NrResponseProcessor implements ItemProcessor<NewRelicResponseListBean, String> {

//	private static final Logger LOG = LoggerFactory.getLogger(NrJobReader.class);

	@Override
	public String process(NewRelicResponseListBean itemsToConvert) throws Exception {
		
		final StringBuilder stringBuilder = new StringBuilder();
		final ObjectMapper objectMapper = new ObjectMapper();
		List<NewRelicResponse> items =null;
		LocalDate day = null;
		
		if (itemsToConvert != null && itemsToConvert.getNewRelicResponselistBean().size() > 0) {
			items = itemsToConvert.getNewRelicResponselistBean();
			LocalDateTime localDate = items.get(0).getMetricData().getTo();
			day = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
		}

		for (NewRelicResponse item : items) {

			Values values = item.getMetricData().getMetrics().get(0).getTimeslices().get(0).getValues();

			NewRelicMongoItem newRelicMongoItem = new NewRelicMongoItem();

			boolean isAverageTimeNull = (values.getAverageResponseTime() == 0);
			float summarizeValue = (!isAverageTimeNull) ? values.getAverageResponseTime() : values.getCallCount();
			String valueName = (!isAverageTimeNull) ? PerPortalConstants.NEW_RELIC_AVG_RESP_TIME_VALUE
					: PerPortalConstants.NEW_RELIC_CALL_COUNT_VALUE;

			newRelicMongoItem.setDay(day);
			newRelicMongoItem.setMetricName(item.getMetricData().getMetrics().get(0).getName());
			newRelicMongoItem.setValueName(valueName);
			newRelicMongoItem.setValue(summarizeValue);

			// nrItemsToSave.add(newRelicMongoItem);
			stringBuilder.append(objectMapper.writeValueAsString(newRelicMongoItem));

		}
		return stringBuilder.toString();

	}

}
