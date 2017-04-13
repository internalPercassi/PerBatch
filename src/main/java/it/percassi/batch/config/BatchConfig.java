package it.percassi.batch.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import it.percassi.batch.NrJobListener;
import it.percassi.batch.NrJobReader;
import it.percassi.batch.NrJobWriter;
import it.percassi.batch.NrResponseProcessor;
import it.percassi.batch.nrelic.NewRelicResponseListBean;
import it.percassi.batch.nrelic.service.NrMetricService;

@EnableScheduling
@Configuration
@Import({ AppConfig.class, BatchScheduler.class })
@PropertySource("classpath:batch.properties")
public class BatchConfig implements SchedulingConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(BatchConfig.class);

	@Autowired
	private SimpleJobLauncher jobLauncher;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private NrJobReader reader;
	
	@Autowired
	private NrJobWriter writer;


	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	@Bean
	public Executor taskExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}

	// @Scheduled(cron="${cron.job.expression}")
	@Scheduled(fixedRate = 120000)
	public void launchJob() {

		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();

		LOG.info("Calling job with parameters {} ", param);

		try {
			jobLauncher.run(callNrJob(), param);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOG.error("Job Exeception: {}", e);
		}

	}

	@Bean
	public Job callNrJob() {
		return jobBuilderFactory.get("callNrJob").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(callNrServiceStep()).end().build();
	}

	@Bean
	public Step callNrServiceStep() {
		return stepBuilderFactory.get("callNrServiceStep")
				.<NewRelicResponseListBean, String>chunk(1)
				.reader(reader)
				.processor(processor())
				.writer(writer)
				.build();
	}

//	@Bean
//	public ItemReader<NewRelicResponseListBean> reader(NrMetricService nrMetricService, String feId, String beId) {
//		return new NrJobReader(nrMetricService, feId, beId);
//	}

	@Bean
	public NrResponseProcessor processor() {
		return new NrResponseProcessor();
	}

//	@Bean
//	public ItemWriter<String> writer() {
//		return new NrJobWriter();
//	}

	@Bean
	public JobExecutionListener listener() {
		return new NrJobListener();
	}

}
