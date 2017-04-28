package it.percassi.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class NrJobListener implements JobExecutionListener {

	private static final Logger LOG = LoggerFactory.getLogger(NrJobListener.class);

	@Override
	public void afterJob(JobExecution jobExecution) {

		LOG.info("Job {} terminated with status {} ,started at {} stopped at {}", jobExecution.getId(),
				jobExecution.getExitStatus(), jobExecution.getStartTime(), jobExecution.getEndTime());
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		LOG.info("Starting Job {} at {}", jobExecution.getId(), jobExecution.getStartTime());

	}

}
