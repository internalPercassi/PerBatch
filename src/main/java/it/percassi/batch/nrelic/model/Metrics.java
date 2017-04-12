package it.percassi.batch.nrelic.model;

import java.util.List;

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

}
