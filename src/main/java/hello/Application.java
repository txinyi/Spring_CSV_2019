package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder; 
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; 

@SpringBootApplication 
@ComponentScan ("hello")  
@EntityScan("hello")
@EnableJpaRepositories("hello")
public class Application extends SpringBootServletInitializer { 
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
