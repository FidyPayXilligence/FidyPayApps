//package com.fidypay.elasticsearch;
//
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//import com.fidypay.entity.ENachTransactionDetails;
//
//@Repository
//public interface ENachTransactionDetailsSearchRepository
//		extends ElasticsearchRepository<ENachTransactionDetails, Long> {
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}]}}")
//	Page<ENachTransactionDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate,
//			Pageable paging);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//	Page<ENachTransactionDetails> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate,
//			Long merchantServiceId, Pageable paging);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//	Page<ENachTransactionDetails> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate,
//			Long status, Pageable paging);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//	Page<ENachTransactionDetails> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate,
//			String endDate, Long status, Long merchantServiceId, Pageable paging);
//
//	@Query("{\"bool\": { \"filter\": [ {\"term\": { \"MERCHANT_ID\": \"?0\" }},{\"range\": { \"TRANSACTION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ,{ \"term\": { \"TRANSACTION_STATUS_ID\": \"1\" } }] } }")
//	List<ENachTransactionDetails> findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate,
//			String endDate);
//
//	@Query("{ \"bool\": {\"must\": [ { \"term\": {\"MERCHANT_ID\": { \"value\": \"?0\" }  }}, { \"range\": {\"TRANSACTION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ] }}")
//	List<ENachTransactionDetails> findByStartDateAndEndDate(Long merchantId, String startDate, String endDate);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//	List<ENachTransactionDetails> findByStartDateAndEndDateANDService(Long merchantId, String startDate, String endDate,
//			Long merchantServiceId);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//	List<ENachTransactionDetails> findByStartDateAndEndDateANDStatus(Long merchantId, String startDate, String endDate,
//			Long statusId);
//
//	@Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//	List<ENachTransactionDetails> findByStartDateAndEndDateANDStatusANDService(Long merchantId, String startDate,
//			String endDate, Long statusId, Long merchantServiceId);
//}
