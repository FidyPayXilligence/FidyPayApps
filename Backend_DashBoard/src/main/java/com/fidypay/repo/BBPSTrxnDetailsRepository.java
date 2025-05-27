package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.BBPSTrxnDetails;

@Repository
public interface BBPSTrxnDetailsRepository extends JpaRepository<BBPSTrxnDetails, Long> {

	@Query(value = "SELECT btd.MERCHANT_TRANSACTION_REF_ID,btd.TRXN_ID,btd.SERVICE_NAME FROM BBPS_TRANSACTION_DETAILS btd  WHERE btd.TRXN_ID=?1 ", nativeQuery = true)
	List<?> findByTrxnId(String trxn_id);

	@Query(value = "SELECT btd.MERCHANT_TRANSACTION_REF_ID,btd.TRXN_ID,btd.SERVICE_NAME FROM BBPS_TRANSACTION_DETAILS btd  WHERE btd.MERCHANT_TRANSACTION_REF_ID=?1 ", nativeQuery = true)
	List<?> findByMerchantTrxnRefId(String trxn_id);

	@Query(value = "SELECT btd.MERCHANT_TRANSACTION_REF_ID,btd.TRXN_ID FROM BBPS_TRANSACTION_DETAILS btd  WHERE (btd.TRANSACTION_DATE>=?1 and btd.TRANSACTION_DATE>=?2)", nativeQuery = true)
	List<?> findByTrxnDate(String from, String to);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3))", nativeQuery = true)
	Page<BBPSTrxnDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate, Pageable paging);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) AND btd.MERCHANT_SERVICE_ID=?4", nativeQuery = true)
	Page<BBPSTrxnDetails> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate,
			Long serviceId, Pageable paging);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.TRANSACTION_STATUS_ID=?4", nativeQuery = true)
	Page<BBPSTrxnDetails> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate,
			Long statusId, Pageable paging);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.TRANSACTION_STATUS_ID=?4 and btd.MERCHANT_SERVICE_ID=?5", nativeQuery = true)
	Page<BBPSTrxnDetails> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate,
			String endDate, Long statusId, Long serviceId, Pageable paging);

	@Query(value = "SELECT SUM(btd.TRANSACTION_AMOUNT) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Double findByMerchantIdAndStartAndEndDate(long merchantId, String date1, String date2);

	// Without Page

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) order by btd.TRANSACTION_DATE desc", nativeQuery = true)
	List<BBPSTrxnDetails> findByStartDateAndEndDateWithoutPage(Long merchantId, String startDate, String endDate);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) AND btd.MERCHANT_SERVICE_ID=?4 order by btd.TRANSACTION_DATE desc", nativeQuery = true)
	List<BBPSTrxnDetails> findByStartDateAndEndDateANDServiceWithoutPage(Long merchantId, String startDate,
			String endDate, Long serviceId);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.TRANSACTION_STATUS_ID=?4 order by btd.TRANSACTION_DATE desc", nativeQuery = true)
	List<BBPSTrxnDetails> findByStartDateAndEndDateANDStatusWithoutPage(Long merchantId, String startDate,
			String endDate, Long status);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.TRANSACTION_STATUS_ID=?4 and btd.MERCHANT_SERVICE_ID=?5 order by btd.TRANSACTION_DATE desc", nativeQuery = true)
	List<BBPSTrxnDetails> findByStartDateAndEndDateANDStatusANDServiceWithoutPage(Long merchantId, String startDate,
			String endDate, Long status, Long serviceId);

	@Query(value = "SELECT COUNT(btd.TRXN_ID) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Integer findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	// service wise

	@Query(value = "Select btd.SERVICE_NAME,COUNT(btd.BBPS_TRANSACTION_ID),SUM(btd.TRANSACTION_AMOUNT) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) GROUP BY btd.SERVICE_NAME ORDER BY btd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantId(long merchantId, String from, String to);

	@Query(value = "Select btd.TRANSACTION_STATUS,COUNT(btd.BBPS_TRANSACTION_ID),SUM(btd.TRANSACTION_AMOUNT) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.SERVICE_NAME=?4 GROUP BY btd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceName(long merchantId, String from, String to, String serviceName);

	@Query(value = "Select btd.SERVICE_NAME,COUNT(btd.BBPS_TRANSACTION_ID),SUM(btd.TRANSACTION_AMOUNT) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.MERCHANT_SERVICE_ID=?4 GROUP BY btd.SERVICE_NAME ORDER BY btd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMServiceId(long merchantId, String from, String to, long merchantServiceId);

	@Query(value = "Select btd.SERVICE_NAME,COUNT(btd.BBPS_TRANSACTION_ID),SUM(btd.TRANSACTION_AMOUNT) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.TRANSACTION_STATUS_ID=?4 GROUP BY btd.SERVICE_NAME ORDER BY btd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndStatusId(long merchantId, String from, String to, long statusId);

	@Query(value = "Select btd.TRANSACTION_STATUS,COUNT(btd.BBPS_TRANSACTION_ID),SUM(btd.TRANSACTION_AMOUNT) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.SERVICE_NAME=?4 and btd.TRANSACTION_STATUS_ID=?5 GROUP BY btd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndStatusId(long merchantId, String from, String to,
			String serviceName, long statusId);

	@Query(value = "Select btd.SERVICE_NAME,COUNT(btd.BBPS_TRANSACTION_ID),SUM(btd.TRANSACTION_AMOUNT) from BBPS_TRANSACTION_DETAILS btd where btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.MERCHANT_SERVICE_ID=?4 and btd.TRANSACTION_STATUS_ID=?5 GROUP BY btd.SERVICE_NAME ORDER BY btd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMServiceIdAndStatusId(long merchantId, String from, String to,
			long merchantServiceId, long statusId);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS  btd WHERE btd.MERCHANT_ID=?1 and ((btd.TRANSACTION_DATE>=?2) and (btd.TRANSACTION_DATE<=?3)) and btd.MERCHANT_USER_ID=?4 and btd.SERVICE_NAME!='QHTf03mi2fuv7fDuWD6pmA==' ORDER BY btd.TRANSACTION_DATE desc", nativeQuery = true)
	Page<BBPSTrxnDetails> findByStartDateAndEndDateForBBPSTransactionHistory(long merchantId, String startDate,
			String endDate, long merchantUserId, Pageable paging);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd  WHERE btd.MERCHANT_TRANSACTION_REF_ID=?1 ORDER BY btd.TRANSACTION_DATE desc", nativeQuery = true)
	List<BBPSTrxnDetails> findBBPSDetailsByMerchantTrxnRefId(String merchantTrxnRefId);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd  WHERE btd.TRXN_IDENTIFIER=?1 ORDER BY btd.TRANSACTION_DATE desc LIMIT 15", nativeQuery = true)
	List<BBPSTrxnDetails> findBBPSDetailsByTrxnIdentifier(String mobile);

	@Query(value = "SELECT * FROM BBPS_TRANSACTION_DETAILS btd  WHERE btd.MERCHANT_TRANSACTION_REF_ID=?1 ", nativeQuery = true)
	BBPSTrxnDetails findByMerchantTrxnRefIdV2(String trxn_id);

	BBPSTrxnDetails findByPaymentId(String decString);

	boolean existsByPaymentId(String merchantTrxnRefId);

}
