package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycTransactionDetails;

@Repository
public interface EkycTransactionDetailsRepository extends JpaRepository<EkycTransactionDetails, Long> {

	boolean existsByMerchantTransactionRefId(String merchantTransactionRefId);

//	EkycTransactionDetails save(long ekycRequestId, long merchantId, long merchantServiceId, String statusSuccess,
//			String statusSuccess2, double commission, double charges, String serviceName, Timestamp trxnDate,
//			String merchantTrxnRefId, String trxnRefId, String merchantTrxnRefId2, long l, long serviceProviderId,
//			char c);

	@Query(value = "select count(etd.MERCHANT_ID) from EKYC_TRANSACTION_DETAILS etd where etd.CREATION_DATE between :fromDate AND :toDate", nativeQuery = true)
	long getTotalRecord(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

	@Query(value = "select * from EKYC_TRANSACTION_DETAILS etd where etd.CREATION_DATE between :fromDate AND :toDate order by etd.CREATION_DATE", nativeQuery = true)
	List<EkycTransactionDetails> findByDateRangeSortById(@Param("fromDate") String fromDate,
			@Param("toDate") String toDate, Pageable pageable);

	@Query(value = "select * from EKYC_TRANSACTION_DETAILS etd where etd.CREATION_DATE between :fromDate AND :toDate order by etd.CREATION_DATE", nativeQuery = true)
	List<EkycTransactionDetails> findByDateBetweenPKR(@Param("fromDate") String fromDate,
			@Param("toDate") String toDate);

	// This commented query is used to fetch the volume of the transaction of
	// perticular provider like: Signzy/Decentro/Karza
	@Query(value = "SELECT DISTINCT etd.EKYC_SERVICENAME FROM EKYC_TRANSACTION_DETAILS etd WHERE (etd.CREATION_DATE>=?1 and etd.CREATION_DATE<=?2) and etd.SERVICE_PROVIDER_ID=?3", nativeQuery = true)
	List<String> findServiceNameList(String fromDate, String toDate, Long serviceProviderId);

	@Query(value = "SELECT " + "  (SELECT MAX(etd.CREATION_DATE) " + "   FROM EKYC_TRANSACTION_DETAILS etd "
			+ "   WHERE etd.CREATION_DATE >= ?1 " + "     AND etd.CREATION_DATE <= ?2 "
			+ "     AND etd.SERVICE_PROVIDER_ID = ?3 " + "     AND etd.EKYC_SERVICENAME = ?4) AS LATEST_CREATION_DATE, "
			+ "  COUNT(etd.EKYC_TRANSACTION_ID) AS TOTAL_COUNT " + "FROM EKYC_TRANSACTION_DETAILS etd "
			+ "WHERE etd.CREATION_DATE >= ?1 " + "  AND etd.CREATION_DATE <= ?2 "
			+ "  AND etd.SERVICE_PROVIDER_ID = ?3 " + "  AND etd.EKYC_SERVICENAME = ?4", nativeQuery = true)
	List<Object[]> findCountOfServiceName(String fromDate, String toDate, Long serviceProviderId, String serviceName);

}
