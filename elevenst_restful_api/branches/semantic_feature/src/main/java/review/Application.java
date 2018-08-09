package review;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;


@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	private static final Logger LOG = Logger.getLogger(Application.class);
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    

    public static void main(String[] args) {
    	try {
    		SpringApplication.run(Application.class, args);
    	} catch(Exception e) {
    		LOG.error(e);
    	}
    }

}
