package it.percassi.batch.config;

import java.time.DayOfWeek;
import java.time.LocalDate;

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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import it.percassi.batch.NrJobListener;
import it.percassi.batch.mongo.NewRelicMongoDailyItem;
import it.percassi.batch.mongo.NewRelicMongoMonthlyItem;
import it.percassi.batch.mongo.NewRelicMongoWeeklyItem;
import it.percassi.batch.nrelic.model.NewRelicResponse;
import it.percassi.batch.processors.NrDailyProcessor;
import it.percassi.batch.processors.NrMonthProcessor;
import it.percassi.batch.processors.NrWeeklyProcessor;
import it.percassi.batch.reader.NrJobReader;
import it.percassi.batch.writers.NrJobDailyWriter;
import it.percassi.batch.writers.NrJobMonthlyWriter;
import it.percassi.batch.writers.NrJobWeeklyWriter;
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


	@Scheduled(cron="${cron.job.expression}")
	public void launchJob() {

		LocalDate now = LocalDate.now();
		int dayOfTheMonth = now.getDayOfMonth();
		DayOfWeek firstDayOfTheWeek = now.getDayOfWeek();
		JobParameters param = new JobParametersBuilder()
				.addString("DailyJobID", String.valueOf(System.currentTimeMillis())).toJobParameters();

		LOG.info("Calling job with parameters {} ", param);

		try {
			

				jobLauncher.run(callNrDailyJob(), param);
				
				if(firstDayOfTheWeek.getValue() == 1){
					param = new JobParametersBuilder()
							.addString("DailyJobID", String.valueOf(System.currentTimeMillis()))
							.toJobParameters();
					jobLauncher.run(callNrWeekly(), param);
				}


				if (dayOfTheMonth == 1) {

					param = new JobParametersBuilder()
							.addString("MonthlyJobID", String.valueOf(System.currentTimeMillis()))
							.toJobParameters();
					jobLauncher.run(callNrMonthly(), param);
				}
				
				

		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOG.error("Job Exeception: {}", e);
		}

	}

	@Bean
	public ResourcelessTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}

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
	public Job callNrDailyJob() {
		return jobBuilderFactory.get("dailyJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobListener())
				.start(dailyCall1())
				.next(dailyCall2())
				.next(dailyCall3())
				.next(dailyCall4())
				.build();
	}

	@Bean
	public Job callNrMonthly() {

		return jobBuilderFactory.get("monthlyJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobListener())
				.start(monthlyCall1())
				.next(monthlyCall2())
				.next(monthlyCall3())
				.next(monthlyCall4())
				.build();
	}
	@Bean
	public Job callNrWeekly(){
		return jobBuilderFactory.get("weeklyJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobListener())
				.start(weeklyCall1())
				.next(weeklyCall2())
				.next(weeklyCall3())
				.next(weeklyCall4())
				.build();
	}
	@Bean
	public Step dailyCall1() {
		return stepBuilderFactory.get("dailyCall1")
				.<NewRelicResponse, NewRelicMongoDailyItem>chunk(1)
				.reader(dailyReader1())
				.processor(dailyProcessor())
				.writer(dailyWriter())
				.build();
	}

	@Bean
	public Step dailyCall2() {
		return stepBuilderFactory.get("dailyCall2")
				.<NewRelicResponse, NewRelicMongoDailyItem>chunk(1)
				.reader(dailyReader2())
				.processor(dailyProcessor())
				.writer(dailyWriter())
				.build();
	}

	@Bean
	public Step dailyCall3() {
		return stepBuilderFactory.get("dailyCall3")
				.<NewRelicResponse, NewRelicMongoDailyItem>chunk(1)
				.reader(dailyReader3())
				.processor(dailyProcessor())
				.writer(dailyWriter())
				.build();
	}

	@Bean
	public Step dailyCall4() {
		return stepBuilderFactory.get("dailyCall4")
				.<NewRelicResponse, NewRelicMongoDailyItem>chunk(1)
				.reader(dailyReader4())
				.processor(dailyProcessor())
				.writer(dailyWriter())
				.build();
	}
	@Bean
	public Step weeklyCall1(){
		return stepBuilderFactory.get("weeklyCall1")
				.<NewRelicResponse, NewRelicMongoWeeklyItem>chunk(1)
				.reader(weeklyReader1())
				.processor(weekyProcessor())
				.writer(weeklyWriter())
				.build();
	}

	@Bean
	public Step weeklyCall2() {
		return stepBuilderFactory.get("weeklyCall2")
				.<NewRelicResponse, NewRelicMongoWeeklyItem>chunk(1)
				.reader(weeklyReader2())
				.processor(weekyProcessor())
				.writer(weeklyWriter())
				.build();
	}
	@Bean
	public Step weeklyCall3() {
		return stepBuilderFactory.get("weeklyCall3")
				.<NewRelicResponse, NewRelicMongoWeeklyItem>chunk(1)
				.reader(weeklyReader3())
				.processor(weekyProcessor())
				.writer(weeklyWriter())
				.build();
	}
	@Bean
	public Step weeklyCall4() {
		return stepBuilderFactory.get("weeklyCall4")
				.<NewRelicResponse, NewRelicMongoWeeklyItem>chunk(1)
				.reader(weeklyReader4())
				.processor(weekyProcessor())
				.writer(weeklyWriter())
				.build();
	}
		
	@Bean
	public Step monthlyCall1() {
		return stepBuilderFactory.get("monthlyCall1")
				.<NewRelicResponse, NewRelicMongoMonthlyItem>chunk(1)
				.reader(monthlyReader1())
				.processor(monthlyProcessor())
				.writer(monthlyWriter())
				.build();
	}

	@Bean
	public Step monthlyCall2() {
		return stepBuilderFactory.get("monthlyCall2")
				.<NewRelicResponse, NewRelicMongoMonthlyItem>chunk(1)
				.reader(monthlyReader2())
				.processor(monthlyProcessor())
				.writer(monthlyWriter())
				.build();
	}

	@Bean
	public Step monthlyCall3() {
		return stepBuilderFactory.get("monthlyCall3")
				.<NewRelicResponse, NewRelicMongoMonthlyItem>chunk(1)
				.reader(monthlyReader3())
				.processor(monthlyProcessor())
				.writer(monthlyWriter())
				.build();
	}

	@Bean
	public Step monthlyCall4() {
		return stepBuilderFactory.get("monthlyCall4")
				.<NewRelicResponse, NewRelicMongoMonthlyItem>chunk(1)
				.reader(monthlyReader4())
				.processor(monthlyProcessor())
				.writer(monthlyWriter())
				.build();
	}

	@Bean
	@StepScope
	public NrJobReader dailyReader1() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[0], PerPortalConstants.NR_VALUES[0], beId, false,false);
	}

	@Bean
	@StepScope
	public NrJobReader dailyReader2() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[0], PerPortalConstants.NR_VALUES[1], beId, false,false);
	}

	@Bean
	@StepScope
	public NrJobReader dailyReader3() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[1], PerPortalConstants.NR_VALUES[0], beId, false,false);
	}

	@Bean
	@StepScope
	public NrJobReader dailyReader4() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[1], PerPortalConstants.NR_VALUES[1], beId,false,false);
	}
	
	
	@Bean
	@StepScope
	public NrJobReader weeklyReader1() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[0], PerPortalConstants.NR_VALUES[0], beId,true,false);
	}

	@Bean
	@StepScope
	public NrJobReader weeklyReader2() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[0], PerPortalConstants.NR_VALUES[1], beId, true,false);
	}

	@Bean
	@StepScope
	public NrJobReader weeklyReader3() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[1], PerPortalConstants.NR_VALUES[0], beId,true,false);
	}

	@Bean
	@StepScope
	public NrJobReader weeklyReader4() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[1], PerPortalConstants.NR_VALUES[1], beId,true,false);
	}
	
	

	@Bean
	@StepScope
	public NrJobReader monthlyReader1() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[0], PerPortalConstants.NR_VALUES[0], beId, false,true);
	}

	@Bean
	@StepScope
	public NrJobReader monthlyReader2() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[0], PerPortalConstants.NR_VALUES[1], beId, false,true);
	}

	@Bean
	@StepScope
	public NrJobReader monthlyReader3() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[1], PerPortalConstants.NR_VALUES[0], beId,false,true);
	}

	@Bean
	@StepScope
	public NrJobReader monthlyReader4() {
		return new NrJobReader(PerPortalConstants.NR_METRICS[1], PerPortalConstants.NR_VALUES[1], beId,false,true);
	}

	@Bean
	public NrDailyProcessor dailyProcessor() {
		return new NrDailyProcessor();
	}
	@Bean
	public NrWeeklyProcessor weekyProcessor(){
		return new NrWeeklyProcessor();
	}

	@Bean
	public NrMonthProcessor monthlyProcessor() {
		return new NrMonthProcessor();
	}

	@Bean
	public NrJobDailyWriter dailyWriter() {
		return new NrJobDailyWriter();
	}

	@Bean NrJobWeeklyWriter weeklyWriter(){
		return new NrJobWeeklyWriter();
	}
	@Bean
	public NrJobMonthlyWriter monthlyWriter() {
		return new NrJobMonthlyWriter();
	}

	@Bean
	public JobExecutionListener jobListener() {
		return new NrJobListener();
	}

}
