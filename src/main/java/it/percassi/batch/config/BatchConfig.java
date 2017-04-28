package it.percassi.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import it.percassi.batch.NrJobListener;
import it.percassi.batch.NrJobReader;
import it.percassi.batch.NrJobWriter;
import it.percassi.batch.NrResponseProcessor;
import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.utils.PerPortalConstants;

@EnableScheduling
@EnableBatchProcessing
@Configuration
@Import(AppConfig.class)
@PropertySource("classpath:batch.properties")
public class BatchConfig {

	private static final Logger LOG = LoggerFactory.getLogger(BatchConfig.class);

	@Autowired
	private SimpleJobLauncher jobLauncher;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	
	 @Value("${nr.fe.id}")
	 private String feId;
	
	 @Value("${nr.be.id}")
	 private String beId;
	 	
 

	//@Scheduled(cron="${cron.job.expression}")
	@Scheduled(fixedRate = 1200000)
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
	public ResourcelessTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}
	
//	@Bean
//	public TaskExecutor taskExecutor(){
//		ThreadPoolTaskExecutor taskExecutor= new ThreadPoolTaskExecutor();
//		taskExecutor.setMaxPoolSize(10);
//		taskExecutor.setCorePoolSize(5);
//		taskExecutor.afterPropertiesSet();
//		return taskExecutor;
//	}
	@Bean
	public MapJobRepositoryFactoryBean mapJobRepositoryFactory(ResourcelessTransactionManager txManager)
			throws Exception {

		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(txManager);

		factory.afterPropertiesSet();

		return factory;
	}

	@Bean
	public JobRepository jobRepository(MapJobRepositoryFactoryBean factory) throws Exception {
		return factory.getObject();
	}

	@Bean
	public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}
	@Bean
	public Job callNrJob() {
		return jobBuilderFactory.get("callNrJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobListener())
				.start(step1())
				.next(step2())
				.next(step3())
				.next(step4())
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("callNrServiceStep")
				.<NewRelicResponse, String>chunk(1)
				.reader(reader1())
				.processor(processor())
				.writer(writer())
				.build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("callNrServiceStep")
				.<NewRelicResponse, String>chunk(1)
				.reader(reader2())
				.processor(processor())
				.writer(writer())
				.build();
	}	@Bean
	public Step step3() {
		return stepBuilderFactory.get("callNrServiceStep")
				.<NewRelicResponse, String>chunk(1)
				.reader(reader3())
				.processor(processor())
				.writer(writer())
				.build();
	}	@Bean
	public Step step4() {
		return stepBuilderFactory.get("callNrServiceStep")
				.<NewRelicResponse, String>chunk(1)
				.reader(reader4())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
	@Bean
	@StepScope
	public NrJobReader reader1() {
		 return new NrJobReader(PerPortalConstants.NR_METRICS[0],PerPortalConstants.NR_VALUES[0],beId);
	}
	@Bean
	@StepScope
	public NrJobReader reader2() {
		 return new NrJobReader(PerPortalConstants.NR_METRICS[0],PerPortalConstants.NR_VALUES[1],beId);
	}
	@Bean
	@StepScope
	public NrJobReader reader3() {
		 return new NrJobReader(PerPortalConstants.NR_METRICS[1],PerPortalConstants.NR_VALUES[0],beId);
	}
	@Bean
	@StepScope
	public NrJobReader reader4() {
		 return new NrJobReader(PerPortalConstants.NR_METRICS[1],PerPortalConstants.NR_VALUES[1],beId);
	}
	@Bean	
	public NrResponseProcessor processor() {
		return new NrResponseProcessor();
	}

	@Bean
	public NrJobWriter writer() {
		return new NrJobWriter();
	}

	@Bean
	public JobExecutionListener jobListener() {
		return new NrJobListener();
	}
	


}
