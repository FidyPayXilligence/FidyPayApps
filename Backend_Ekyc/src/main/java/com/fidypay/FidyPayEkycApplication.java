package com.fidypay;

import java.text.ParseException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fidypay.config.ApplicationProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@ComponentScan(basePackages = "com.fidypay")
@EnableElasticsearchRepositories(basePackages = "com.fidypay.elasticsearch")
@Configuration
@EnableJpaRepositories(basePackages = "com.fidypay.repo")
@EnableConfigurationProperties({ApplicationProperties.class})
public class FidyPayEkycApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws ParseException, Exception {
		SpringApplication.run(FidyPayEkycApplication.class, args);

	}

}
