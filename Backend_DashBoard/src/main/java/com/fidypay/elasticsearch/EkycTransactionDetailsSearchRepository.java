//package com.fidypay.elasticsearch;
//
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import com.fidypay.entity.EkycTransactionDetails;
//
//public interface EkycTransactionDetailsSearchRepository extends ElasticsearchRepository<EkycTransactionDetails, Long>{
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"CREATION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}]}}")
//	Page<EkycTransactionDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate, Pageable paging);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"CREATION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//	Page<EkycTransactionDetails> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate, Long merchantServiceId, Pageable paging);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"CREATION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//	Page<EkycTransactionDetails> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate, Long status, Pageable paging);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"CREATION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//	Page<EkycTransactionDetails> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate, String endDate, Long status, Long merchantServiceId, Pageable paging);
//
//	@Query("{\"bool\": { \"filter\": [ {\"term\": { \"MERCHANT_ID\": \"?0\" }},{\"range\": { \"CREATION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ,{ \"term\": { \"TRANSACTION_STATUS_ID\": \"1\" } }] } }")
//	List<EkycTransactionDetails> findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);
//
//	@Query("{ \"bool\": {\"must\": [ { \"term\": {\"MERCHANT_ID\": { \"value\": \"?0\" }  }}, { \"range\": {\"CREATION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ] }}")
//	List<EkycTransactionDetails> findByStartDateAndEndDateWithoutPage(Long merchantId, String startDate, String endDate);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"CREATION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//	List<EkycTransactionDetails> findByStartDateAndEndDateANDServiceWithoutPage(Long merchantId, String startDate, String endDate, Long merchantServiceId);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"CREATION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//	List<EkycTransactionDetails> findByStartDateAndEndDateANDStatusWithoutPage(Long merchantId, String startDate, String endDate, Long status);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"CREATION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//	List<EkycTransactionDetails> findByStartDateAndEndDateANDStatusANDServiceWithoutPage(Long merchantId, String startDate, String endDate, Long status, Long merchantServiceId);
//}
