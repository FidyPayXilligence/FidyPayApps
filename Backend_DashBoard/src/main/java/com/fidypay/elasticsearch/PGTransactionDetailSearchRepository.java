//package com.fidypay.elasticsearch;
//
//import com.fidypay.entity.PGTransactionDetail;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import java.util.List;
//
//public interface PGTransactionDetailSearchRepository extends ElasticsearchRepository<PGTransactionDetail, Long> {
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}]}}")
//    Page<PGTransactionDetail> findByStartDateAndEndDate(long merchantId, String startDate, String endDate, Pageable paging);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}, \"filter\":[{\"term\": {\"MERCHANT_SERVICE_ID\": \"?3\"}}]}}")
//    Page<PGTransactionDetail> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate, Long merchantServiceId, Pageable paging);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}], \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\"}}]}}")
//    Page<PGTransactionDetail> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate, Long status, Pageable paging);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}}]}}")
//    Page<PGTransactionDetail> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate, String endDate, Long status, Long merchantServiceId, Pageable paging);
//    //=============================================================================================================================================================================
//    @Query("{ \"bool\": {\"must\": [ { \"term\": {\"MERCHANT_ID\": { \"value\": \"?0\" }  }}, { \"range\": {\"TRANSACTION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ] }}")
//    List<PGTransactionDetail> findByStartDateAndEndDate(Long merchantId, String startDate, String endDate);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"MERCHANT_SERVICE_ID\": \"?3\" }}]}}")
//    List<PGTransactionDetail> findByStartDateAndEndDateANDService(Long merchantId, String startDate, String endDate, Long merchantServiceId);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}}],  \"filter\":[{\"term\":{\"TRANSACTION_STATUS_ID\": \"?3\" }}]}}")
//    List<PGTransactionDetail> findByStartDateAndEndDateANDStatus(Long merchantId, String startDate, String endDate, Long statusId);
//
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"MERCHANT_ID\": \"?0\"}}, {\"range\": {\"TRANSACTION_DATE\": {\"gte\": \"?1\",\"lte\": \"?2\"}}},{\"term\": {\"TRANSACTION_STATUS_ID\": \"?3\"}}, {\"term\": {\"MERCHANT_SERVICE_ID\": \"?4\"}} ]}}")
//    List<PGTransactionDetail> findByStartDateAndEndDateANDStatusANDService(Long merchantId, String startDate, String endDate, Long statusId, Long merchantServiceId);
//    //=============================================================================================================================================================================
//    @Query("{\"bool\": { \"filter\": [ {\"term\": { \"MERCHANT_ID\": \"?0\" }},{\"range\": { \"TRANSACTION_DATE\": { \"gte\": \"?1\", \"lte\": \"?2\" } }  } ,{ \"term\": { \"TRANSACTION_STATUS_ID\": \"1\" } }] } }")
//    List<PGTransactionDetail> findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);
//}
