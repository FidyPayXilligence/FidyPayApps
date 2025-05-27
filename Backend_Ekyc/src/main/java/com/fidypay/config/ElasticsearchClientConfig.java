//package com.fidypay.config;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//import com.fidypay.converters.StringToLongConverter;
//import com.fidypay.utils.constants.ResponseMessage;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.fidypay.elasticsearch")
//@ComponentScan(basePackages = { "com.fidypay" })
//public class ElasticsearchClientConfig extends AbstractElasticsearchConfiguration {
//	
//	@Value("${elasticsearch.host}")
//	private String elasticsearchHost;
//
//	@Value("${elasticsearch.port}")
//	private int elasticsearchPort;
//	
//	@Override
//	@Bean
//	public RestHighLevelClient elasticsearchClient() {
//
//		final ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(ResponseMessage.IP)
//				.build();
//
//		return RestClients.create(clientConfiguration).rest();
//
//	}
//	
//	@Bean(destroyMethod = "close")
//	public RestHighLevelClient elasticsearchClient1() {
//		RestClientBuilder builder = RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort));
//		RestHighLevelClient client = new RestHighLevelClient(builder);
//		return client;
//	}
//
//	@Bean
//	@Primary
//	public ElasticsearchRestTemplate elasticsearchRestTemplate() {
//		return new ElasticsearchRestTemplate(elasticsearchClient());
//	}
//	
//	@Bean
//    @Override
//    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
//        List<Converter<?, ?>> converters = new ArrayList<>();
//        converters.add(new DateToTimestampConverter());
//		converters.add(new LongToTimestampConverter());
//		converters.add(new StringToLongConverter());
//        return new ElasticsearchCustomConversions(converters);
//    }	
//}
