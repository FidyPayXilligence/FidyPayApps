package com.fidypay.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties("spring.datasource")
public class ProfilingConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfilingConfiguration.class);
	
	@Value("${spring.datasource.url}")
    private String databaseUrl;
	
    @Profile("dev")
    @Bean
    public String devDatabaseConnection() {
    	LOGGER.info("DEVELOPMENT PROFILE IS ACTIVE =>: {}", databaseUrl);
    	return "DEVELOPMENT ENVIRONMENT";
	}
    
    @Profile("uat")
	@Bean
	public String testDatabaseConnection() {
    	LOGGER.info("UAT PROFILE IS ACTIVE =>: {}", databaseUrl);
		return "UAT ENVIRONMENT";
	}
    
    @Profile("prod")
	@Bean
	public String prodDatabaseConnection() {
    	LOGGER.info("PROD PROFILE IS ACTIVE =>: {} ", databaseUrl);
		return "PRODUCTION ENVIRONMENT";
	} 
}
