package it.percassi.batch.nrelic.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import it.percassi.utils.CustomeLocalDateTimeDeserializer;

public class Timeslices {

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomeLocalDateTimeDeserializer.class)
	private LocalDateTime from;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomeLocalDateTimeDeserializer.class)
	private LocalDateTime to;

	private Values values;

	public LocalDateTime getFrom() {
		return from;
	}

	public void setFrom(LocalDateTime from) {
		this.from = from;
	}

	public LocalDateTime getTo() {
		return to;
	}

	public void setTo(LocalDateTime to) {
		this.to = to;
	}

	public Values getValues() {
		return values;
	}

	public void setValues(Values values) {
		this.values = values;
	}

}
