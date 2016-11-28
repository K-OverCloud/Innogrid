package com.inno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

@SpringBootApplication
@EnableScheduling
public class VerifOverCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerifOverCloudApplication.class, args);
	}

	/*	 	
	 @Bean
    public ScheduledExecutorFactoryBean scheduledExecutorService() {
        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(5);
        return bean;
    }

	@Bean
	public com.inno.util.SpringApplicationContext springApplicationContext(){
		return new com.inno.util.SpringApplicationContext();
	}
	*/
}
