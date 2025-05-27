package com.fidypay.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;

@Configuration
public class HttpClientConfig {

    @Value("${fidypay.http.client.connectTimeout:60}")
    private long connectTimeout;

    @Value("${fidypay.http.client.readTimeout:60}")
    private long readTimeout;

    @Value("${fidypay.http.client.writeTimeout:60}")
    private long writeTimeout;


    @Bean
    OkHttpClient createHttpClient() {
        return new OkHttpClient().newBuilder().connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS).writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .build();
    }

}
