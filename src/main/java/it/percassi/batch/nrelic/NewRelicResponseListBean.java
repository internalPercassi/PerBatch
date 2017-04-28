package it.percassi.batch.nrelic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.percassi.batch.nrelic.model.NewRelicResponse;

public class NewRelicResponseListBean {

	List<NewRelicResponse> newRelicResponselistBean = new ArrayList<>();
	

	public List<NewRelicResponse> getNewRelicResponselistBean() {
		return newRelicResponselistBean;
	}

	public void setNewRelicResponselistBean(List<NewRelicResponse> newRelicResponselistBean) {
		this.newRelicResponselistBean = newRelicResponselistBean;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
