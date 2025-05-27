//package com.fidypay.elasticsearch;
//
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import com.fidypay.entity.BBPSTrxnDetails;
//
//public interface BBPSTrxnDetailsSearchRepository extends ElasticsearchRepository<BBPSTrxnDetails, Long>{
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}]}}")
//	Page<BBPSTrxnDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate, Pageable paging);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//	Page<BBPSTrxnDetails> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate, Long serviceId, Pageable paging);
//	
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//	Page<BBPSTrxnDetails> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate, Long statusId, Pageable paging);
//	
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//	Page<BBPSTrxnDetails> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate, String endDate, Long statusId, Long serviceId, Pageable paging);
//
//	@Query("{\"bool\": { \"filter\": [ {\"term\": { \"MERCHANT_ID\": \"?0\" }},{\"range\": { \"TRANSACTION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ,{ \"term\": { \"TRANSACTION_STATUS_ID\": \"1\" } }] } }")
//	List<BBPSTrxnDetails> findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String date1, String date2);
//
//	@Query("{ \"bool\": {\"must\": [ { \"term\": {\"MERCHANT_ID\": { \"value\": \"?0\" }  }}, { \"range\": {\"TRANSACTION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ] }}")
//	List<BBPSTrxnDetails> findByStartDateAndEndDateWithoutPage(Long merchantId, String startDate, String endDate);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//	List<BBPSTrxnDetails> findByStartDateAndEndDateANDServiceWithoutPage(Long merchantId, String startDate, String endDate, Long serviceId);
//	
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//	List<BBPSTrxnDetails> findByStartDateAndEndDateANDStatusWithoutPage(Long merchantId, String startDate, String endDate, Long status);
//	
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//	List<BBPSTrxnDetails> findByStartDateAndEndDateANDStatusANDServiceWithoutPage(Long merchantId, String startDate, String endDate, Long status, Long serviceId);
//}
