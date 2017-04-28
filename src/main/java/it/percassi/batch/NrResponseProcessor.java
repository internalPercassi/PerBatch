package it.percassi.batch;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.nrelic.model.Values;
import it.percassi.utils.PerPortalConstants;

public class NrResponseProcessor implements ItemProcessor<NewRelicResponse, String> {

	private static final Logger LOG = LoggerFactory.getLogger(NrJobReader.class);

	@Override
	public String process(NewRelicResponse item) throws Exception {
		
		final StringBuilder stringBuilder = new StringBuilder();
		final ObjectMapper objectMapper = new ObjectMapper();
		LocalDate day = null;
		
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

			stringBuilder.append(objectMapper.writeValueAsString(newRelicMongoItem));
			LOG.info("newRelicMongoItem: "+newRelicMongoItem);

//		}
		return stringBuilder.toString();

	}

}
