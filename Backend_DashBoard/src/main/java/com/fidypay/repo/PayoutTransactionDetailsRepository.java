package com.fidypay.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PayoutTransactionDetails;

@Repository
public interface PayoutTransactionDetailsRepository extends JpaRepository<PayoutTransactionDetails, Long> {

	boolean existsByMerchantTransactionRefId(String merchantTransactionRefId);

	Optional<com.fidypay.entity.PayoutTransactionDetails> findByMerchantTransactionRefId(
			String merchantTransactionRefId);

	@Query(value = "select * from PAYOUT_TRANSACTION_DETAILS ptd where ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) and ptd.SERVICE_NAME=?3 and ptd.TRANSACTION_STATUS=?4", nativeQuery = true)
	List<PayoutTransactionDetails> findByTransactionDate(String from, String to, String serviceName, String status);

	@Transactional
	@Modifying
	@Query(value = "update PAYOUT_TRANSACTION_DETAILS ptd set ptd.TRANSACTION_STATUS = ?1,ptd.UTR=?2 where ptd.PAYOUT_TRANSACTION_ID = ?3", nativeQuery = true)
	int updateTransactionStatus(String transactionStatus, String utr, long coreTrxnId);

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3))", nativeQuery = true)
	Page<PayoutTransactionDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate,
			Pageable paging);

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4", nativeQuery = true)
	Page<PayoutTransactionDetails> findByStartDateAndEndDateANDService(long merchantId, String startDate,
			String endDate, Long serviceId, Pageable paging);

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4", nativeQuery = true)
	Page<PayoutTransactionDetails> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate,
			Long statusId, Pageable paging);

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.MERCHANT_SERVICE_ID=?5", nativeQuery = true)
	Page<PayoutTransactionDetails> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate,
			String endDate, Long statusId, Long serviceId, Pageable paging);

	@Query(value = "Select SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Double findByMerchantIdAndStartAndEndDate(long merchantId, String string, String string2);

	@Query(value = "Select SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS='Success'", nativeQuery = true)
	Double findTotalAmountByMerchantIdAndStartAndEndDate(long merchantId, String string, String string2);

	// Without Pagination

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) order by ptd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PayoutTransactionDetails> findByStartDateAndEndDateWithoutPage(Long merchantId, String startDate,
			String endDate);

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4 order by ptd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PayoutTransactionDetails> findByStartDateAndEndDateANDServiceWithoutPage(Long merchantId, String startDate,
			String endDate, Long serviceId);

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 order by ptd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PayoutTransactionDetails> findByStartDateAndEndDateANDStatusWithoutPage(Long merchantId, String startDate,
			String endDate, Long status);

	@Query(value = "Select * from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.MERTCHANT_SERVICE_ID=?5 order by ptd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PayoutTransactionDetails> findByStartDateAndEndDateANDStatusANDServiceWithoutPage(Long merchantId,
			String startDate, String endDate, Long status, Long serviceId);

	@Query(value = "Select COUNT(ptd.TRXN_REF_ID) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Integer findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String string, String string2);

	// service wise

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYOUT_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantId(long merchantId, String from, String to);

	@Query(value = "Select ptd.TRANSACTION_STATUS,COUNT(ptd.PAYOUT_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.SERVICE_NAME=?4 GROUP BY ptd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceName(long merchantId, String from, String to, String serviceName);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYOUT_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdANdMServiceId(long merchantId, String from, String to, long merchantServiceId);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYOUT_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdANdStatusId(long merchantId, String from, String to, long statusId);

	@Query(value = "Select ptd.TRANSACTION_STATUS,COUNT(ptd.PAYOUT_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.SERVICE_NAME=?4 and ptd.TRANSACTION_STATUS_ID=?5 GROUP BY ptd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndStatusId(long merchantId, String from, String to,
			String serviceName, long statusId);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYOUT_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYOUT_TRANSACTION_DETAILS ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4 and ptd.TRANSACTION_STATUS_ID=?5 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdANdMServiceIdStatusId(long merchantId, String from, String to,
			long merchantServiceId, long statusId);

}
