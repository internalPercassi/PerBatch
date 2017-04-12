package it.percassi.batch.nrelic.service;


import it.percassi.batch.nrelic.model.NewRelicResponse;

public class NewRelicServiceResponse extends BaseResponse {

	private NewRelicResponse newRelicResponse;

	public NewRelicResponse getNewRelicResponse() {

		return newRelicResponse;
	}

	public NewRelicServiceResponse(NewRelicResponse newRelicResponse) {

		this.newRelicResponse = newRelicResponse;
	}

}
