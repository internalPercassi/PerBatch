package it.percassi.batch.nrelic.model;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Metrics {

	private List<Timeslices> timeslices;
	private String name;

	public List<Timeslices> getTimeslices() {
		return timeslices;
	}

	public void setTimeslices(List<Timeslices> timeslices) {
		this.timeslices = timeslices;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
