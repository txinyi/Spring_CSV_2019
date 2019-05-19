package csv;
  
import org.springframework.batch.core.launch.JobLauncher; 
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean; 
import org.springframework.batch.support.transaction.ResourcelessTransactionManager; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;  

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;  

import org.springframework.web.servlet.ViewResolver; 
import org.springframework.web.servlet.config.annotation.EnableWebMvc; 
import org.springframework.web.servlet.view.InternalResourceViewResolver; 


@EnableWebMvc
@Configuration 
@EnableBatchProcessing
public class ApplicationConfiguration { // extends WebMvcConfigurerAdapter
 
	@Bean
    public ResourcelessTransactionManager batchTransactionManager(){
        ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();
        return transactionManager;
    }

    @Bean
    protected JobRepository jobRepository(ResourcelessTransactionManager batchTransactionManager) throws Exception{
        MapJobRepositoryFactoryBean jobRepository = new MapJobRepositoryFactoryBean();
        jobRepository.setTransactionManager(batchTransactionManager);
        return (JobRepository)jobRepository.getObject();
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }
    
    
    @Bean
    public ViewResolver jspViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/pages/"); 	///WEB-INF/classes/templates/
        bean.setSuffix(".jsp");
        return bean;
    } 
}