//package com.fidypay.converters;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.sql.Timestamp;
//
///**
// * @author prave
// * @Date 07-09-2023
// */
//@Component
//public class ElasticsearchConverter {
//
//    private final RestHighLevelClient client;
//
//    public ElasticsearchConverter() {
//        client = new RestHighLevelClient(RestClient.builder(
//                new HttpHost("65.1.40.107", 9200, "http")));
//    }
//
//    public Timestamp convertStringToTimestamp(String index, String field, String stringValue) throws IOException {
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(QueryBuilders.matchPhraseQuery(field, stringValue));
//
//        SearchRequest searchRequest = new SearchRequest(index);
//        searchRequest.source(sourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        return null;
//    }
//}
