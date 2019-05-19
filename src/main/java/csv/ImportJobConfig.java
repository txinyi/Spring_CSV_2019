package csv;
 
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step; 
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor; 
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode; 
import org.springframework.core.io.FileSystemResource; 

import csv.domain.User;
import csv.processor.UserItemProcessor;
 

@Configuration
public class ImportJobConfig  {  
	private Logger Log = LoggerFactory.getLogger(ImportJobConfig.class);
	@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;
    
    public String getFilePath;
	 
    @Bean
    @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FlatFileItemReader<User> csvReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
    	getFilePath = pathToFile; 
    	
    	if (getFilePath == null) {
    		//read default 
    		getFilePath = "src/main/resources/empty.csv";
    	}
    	
    	Log.info("ppath: " + getFilePath + ".........................\n");
        FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
        reader.setResource(new FileSystemResource(getFilePath)); 
        reader.setLineMapper(new DefaultLineMapper<User>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
            	setNames(new String[] { "name", "salary" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                setTargetType(User.class);
            }});
        }});
        return reader;
    }

    @Bean
	ItemProcessor<User, User> csvProcessor() {
		return new UserItemProcessor();
	}

    //Insert csv data into the database
  	@Bean
  	public JdbcBatchItemWriter<User> csvWriter() {
  		 JdbcBatchItemWriter<User> csvWriter = new JdbcBatchItemWriter<User>();
  		 csvWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
  		 csvWriter.setSql("INSERT INTO user (name, salary) VALUES (:name, :salary)");
  		 csvWriter.setDataSource(dataSource);
  	        return csvWriter;
  	}  
   
  	// begin job info   
 	@Bean
 	public Step csvFileToDatabaseStep() { 
 		return stepBuilderFactory.get("csvFileToDatabaseStep")
 				.<User, User>chunk(1)
 				.reader(csvReader(getFilePath))
 				.processor(csvProcessor())
 				.writer(csvWriter())
 				.build();
 	} 
 	
 	@Bean
 	Job csvFileToDatabaseJob(JobCompletionNotificationListener listener) {
 		return jobBuilderFactory.get("csvFileToDatabaseJob")
 				.incrementer(new RunIdIncrementer())
 				.listener((JobExecutionListener) listener)
 				.flow(csvFileToDatabaseStep())
 				.end()
 				.build();
 	}
 	// end job info

}