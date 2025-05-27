//package com.fidypay.elasticsearch;
//
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import com.fidypay.entity.PayoutTransactionDetails;
//
//public interface PayoutTransactionDetailsSearchRepository extends ElasticsearchRepository<PayoutTransactionDetails, Long>{
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}]}}")
//    Page<PayoutTransactionDetails> findByStartDateAndEndDate(Long merchantId, String startDate, String endDate, Pageable paging);
//        
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//    Page<PayoutTransactionDetails> findByStartDateAndEndDateANDService(Long merchantId, String startDate, String endDate, Long serviceId, Pageable paging);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//    Page<PayoutTransactionDetails> findByStartDateAndEndDateANDStatus(Long merchantId, String startDate, String endDate, Long status, Pageable paging);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//    Page<PayoutTransactionDetails> findByStartDateAndEndDateANDStatusANDService(Long merchantId, String startDate, String endDate, Long status, Long serviceId, Pageable paging);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}]}}")
//    List<PayoutTransactionDetails> findByStartDateAndEndDateWithoutPage(Long merchantId, String startDate, String endDate);
// 	
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//    List<PayoutTransactionDetails> findByStartDateAndEndDateANDServiceWithoutPage(Long merchantId, String startDate, String endDate, Long serviceId);
// 	
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//    List<PayoutTransactionDetails> findByStartDateAndEndDateANDStatusWithoutPage(Long merchantId, String startDate, String endDate, Long status);
// 	
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//    List<PayoutTransactionDetails> findByStartDateAndEndDateANDStatusANDServiceWithoutPage(Long merchantId, String startDate, String endDate, Long status, Long serviceId);
//    
//    @Query("{\"bool\": { \"filter\": [ {\"term\": { \"MERCHANT_ID\": \"?0\" }},{\"range\": { \"TRANSACTION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ,{ \"term\": { \"TRANSACTION_STATUS_ID\": \"1\" } }] } }")
//	List<PayoutTransactionDetails> findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String string, String string2);
//}
