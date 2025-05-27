package com.fidypay.repo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PayinTransactionalDetail;

@Repository
public interface PayinTransactionDetailRepository extends JpaRepository<PayinTransactionalDetail, Long> {

	boolean existsByMerchantTransactionRefId(String merchantTransactionRefId);

	boolean existsByPayeeVpa(String payeeVpa);

	PayinTransactionalDetail findByMerchantTransactionRefId(String merchantTransactionRefId);

	Optional<PayinTransactionalDetail> findBymerchantTransactionRefId(String merchantTransactionRefId);

	@Transactional
	@Modifying
	@Query(value = "update PAYIN_TRANSACTION_DETAIL ptd set ptd.TRANSACTION_STATUS=?1 where ptd.REQUEST_ID=?2", nativeQuery = true)
	int updateTransactionStatus(String transactionStatus, long coreTrxnId);

//	@Query(value = "SELECT ptd.REQUEST_ID,ptd.TRANSACTION_AMOUNT,ptd.PAYEE_VPA,ptd.MERCHANT_BUSSINESS_NAME,ptd.MERCHANT_ID,ptd.SUBMERCHANT_BUSSINESS_NAME,ptd.IS_SETTELED FROM PAYIN_TRANSACTION_DETAIL ptd where ptd.IS_SETTELED='0' and (ptd.TRANSACTION_STATUS='S' OR ptd.TRANSACTION_STATUS='SUCCESS')  and ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) GROUP BY  ptd.REQUEST_ID,ptd.TRANSACTION_AMOUNT,ptd.PAYEE_VPA,ptd.MERCHANT_BUSSINESS_NAME,ptd.MERCHANT_ID,ptd.SUBMERCHANT_BUSSINESS_NAME,ptd.IS_SETTELED ORDER BY ptd.PAYEE_VPA", nativeQuery = true)
//	List<?> findAllMerchantSettlementTransactions(String startDate, String endDate);

	@Query(value = "update PAYIN_TRANSACTION_DETAIL ptd set ptd.IS_SETTELED ='1' where and ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) and ptd.PAYEE_VPA=?3", nativeQuery = true)
	PayinTransactionalDetail updateStatusByDateAndVPA(Timestamp sDate, String endDate, String settlementVpa);

	@Query(value = "SELECT COUNT(ptd.REQUEST_ID),SUM(ptd.TRANSACTION_AMOUNT), ptd.MERCHANT_ID,ptd.PAYEE_VPA,ptd.MERCHANT_BUSSINESS_NAME,ptd.SUBMERCHANT_BUSSINESS_NAME FROM PAYIN_TRANSACTION_DETAIL ptd where ptd.TRANSACTION_STATUS='S' and ptd.IS_RECONCILE='0' And ptd.IS_SETTELED='0' And ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) GROUP BY ptd.MERCHANT_ID,ptd.PAYEE_VPA,ptd.MERCHANT_BUSSINESS_NAME,ptd.SUBMERCHANT_BUSSINESS_NAME;", nativeQuery = true)
	List<?> findAllMerchantSettlementTransactions(String startDate, String endDate);

	@Query(value = "SELECT COUNT(ptd.REQUEST_ID),SUM(ptd.TRANSACTION_AMOUNT), ptd.MERCHANT_ID,ptd.PAYEE_VPA,ptd.MERCHANT_BUSSINESS_NAME,ptd.SUBMERCHANT_BUSSINESS_NAME FROM PAYIN_TRANSACTION_DETAIL ptd where ptd.TRANSACTION_STATUS='S' and ptd.IS_RECONCILE='0' And ptd.IS_SETTELED='0' And ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) AND ptd.MERCHANT_ID=?3 GROUP BY ptd.MERCHANT_ID,ptd.PAYEE_VPA,ptd.MERCHANT_BUSSINESS_NAME,ptd.SUBMERCHANT_BUSSINESS_NAME;", nativeQuery = true)
	List<?> findAllMerchantSettlementTransactionsWithMerchantId(String startDate, String endDate, Long merchantId);

	@Query(value = "SELECT count(ptd.REQUEST_ID),sum(ptd.TRANSACTION_AMOUNT) FROM PAYIN_TRANSACTION_DETAIL ptd \n"
			+ "where ptd.TRANSACTION_STATUS='S' and ptd.IS_RECONCILE='0' And ptd.IS_SETTELED='0' And ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) \n"
			+ "And  NOT EXISTS (Select mt.TRANSACTION_ID from  MERCHANT_TRANSACTIONS mt where ptd.REQUEST_ID=mt.TRANSACTION_ID)", nativeQuery = true)
	List<?> findAllMerchantSettlementTransactions2(String startDate, String endDate);

	@Query(value = "SELECT count(ptd.REQUEST_ID),sum(ptd.TRANSACTION_AMOUNT) FROM PAYIN_TRANSACTION_DETAIL ptd \n"
			+ "where ptd.TRANSACTION_STATUS='S' and ptd.IS_RECONCILE='0' And ptd.IS_SETTELED='0' And ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) and ptd.MERCHANT_ID \n"
			+ "And  NOT EXISTS (Select mt.TRANSACTION_ID from  MERCHANT_TRANSACTIONS mt where ptd.REQUEST_ID=mt.TRANSACTION_ID)", nativeQuery = true)
	List<?> findAllMerchantSettlementTransactions2WithMerchantId(String startDate, String endDate, Long merchantId);

	@Transactional
	@Modifying
	@Query(value = "update PAYIN_TRANSACTION_DETAIL ptd set ptd.IS_RECONCILE='1' where ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) and ptd.PAYEE_VPA=?3", nativeQuery = true)
	void updateStatusByDateAndVPA(Timestamp sDate, Timestamp endDate, String merchantVpa);

	@Transactional
	@Modifying
	@Query(value = "update PAYIN_TRANSACTION_DETAIL ptd set ptd.IS_SETTELED='1' where ((ptd.TRANSACTION_DATE>=?1) and (ptd.TRANSACTION_DATE<=?2)) and ptd.PAYEE_VPA=?3", nativeQuery = true)
	void updateSettlementStatusByDateAndVPA(Timestamp sDate, Timestamp endDate, String merchantVpa);

	@Query(value = "Select ptd.IS_SETTELED from PAYIN_TRANSACTION_DETAIL ptd  where ptd.REQUEST_ID=?1", nativeQuery = true)
	char findByRequestId(Long trxnId);

	@Query(value = "Select ptd.TRANSACTION_AMOUNT,ptd.REQUEST_ID,ptd.TRANSACTION_DATE,ptd.PAYER_VPA,ptd.IS_SETTELED,ptd.UTR,ptd.MERCHANT_TRANSACTION_REF_ID,ptd.SUBMERCHANT_BUSSINESS_NAME from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3))", nativeQuery = true)
	List<?> findByTransactionDate(long merchantId, String startDate, String endDate);

	@Query(value = "Select ptd.TRANSACTION_AMOUNT,ptd.REQUEST_ID,ptd.TRANSACTION_DATE,ptd.PAYER_VPA,ptd.IS_SETTELED,ptd.UTR,ptd.MERCHANT_TRANSACTION_REF_ID,ptd.SUBMERCHANT_BUSSINESS_NAME from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ptd.PAYEE_VPA=?2 and ((ptd.TRANSACTION_DATE>=?3) and (ptd.TRANSACTION_DATE<=?4))", nativeQuery = true)
	List<?> findByTransactionDateANDVpa(long merchantId, String vpa, String startDate, String endDate);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3))", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDate(Long merchantId, String startDate, String endDate,
			Pageable paging);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDateANDService(Long merchantId, String startDate,
			String endDate, Long serviceId, Pageable paging);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDateANDStatus(Long merchantId, String startDate, String endDate,
			Long status, Pageable paging);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.MERCHANT_SERVICE_ID=?5", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDateANDStatusANDService(Long merchantId, String startDate,
			String endDate, Long status, Long serviceId, Pageable paging);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.PAYEE_VPA=?4", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDateAndVpa(Long merchantId, String startDate, String endDate,
			String vpa, Pageable paging);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and  ptd.MERCHANT_SERVICE_ID=?4 and ptd.PAYEE_VPA=?5", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDateANDServiceAndVpa(Long merchantId, String startDate,
			String endDate, Long serviceId, String vpa, Pageable paging);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.PAYEE_VPA=?5", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDateANDStatusANDVPA(Long merchantId, String startDate,
			String endDate, Long status, String vpa, Pageable paging);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.MERCHANT_SERVICE_ID=?5 and ptd.PAYEE_VPA=?6", nativeQuery = true)
	Page<PayinTransactionalDetail> findByStartDateAndEndDateANDStatusANDServiceANDVPA(Long merchantId, String startDate,
			String endDate, Long status, Long serviceId, String vpa, Pageable paging);

	@Query(value = "SELECT SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Double findByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	@Query(value = "SELECT COUNT(ptd.TRXN_REF_ID) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Integer findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	// Without Page

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateWithoutPage(Long merchantId, String startDate,
			String endDate);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateANDServiceWithoutPage(Long merchantId, String startDate,
			String endDate, Long serviceId);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateANDStatusWithoutPage(Long merchantId, String startDate,
			String endDate, Long status);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.MERCHANT_SERVICE_ID=?5 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateANDStatusANDServiceWithoutPage(Long merchantId,
			String startDate, String endDate, Long status, Long serviceId);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.PAYEE_VPA=?4 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateWithoutPageAndVpa(Long merchantId, String startDate,
			String endDate, String vpa);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4 and ptd.PAYEE_VPA=?5 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateANDServiceWithoutPageAndVpa(Long merchantId,
			String startDate, String endDate, Long serviceId, String vpa);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.PAYEE_VPA=?5 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateANDStatusWithoutPageANDVPA(Long merchantId,
			String startDate, String endDate, Long status, String vpa);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 and ptd.MERCHANT_SERVICE_ID=?5 and ptd.PAYEE_VPA=?6 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateANDStatusANDServiceWithoutPageANDVPA(Long merchantId,
			String startDate, String endDate, Long status, Long serviceId, String vpa);

	// Toal Transaction Report

	@Query(value = "SELECT SUM(TRANSACTION_AMOUNT) as TRANSACTION_AMOUNT, SERVICE_NAME, TRANSACTION_STATUS FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) group by SERVICE_NAME, TRANSACTION_STATUS", nativeQuery = true)
	List findServiceTotalTransactionByStartDateAndEndDate(Long merchantId, String startDate, String endDate);

	// Service wise

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantId(long merchantId, String from, String to);

	@Query(value = "Select ptd.TRANSACTION_STATUS,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.SERVICE_NAME=?4 GROUP BY ptd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceName(long merchantId, String from, String to, String serviceName);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMServiceId(long merchantId, String from, String to, long merchantServiceId);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.TRANSACTION_STATUS_ID=?4 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndStatusId(long merchantId, String from, String to, long statusId);

	@Query(value = "Select ptd.TRANSACTION_STATUS,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.SERVICE_NAME=?4 and ptd.TRANSACTION_STATUS_ID=?5 GROUP BY ptd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndStatusId(long merchantId, String from, String to,
			String serviceName, long statusId);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.MERCHANT_SERVICE_ID=?4 and ptd.TRANSACTION_STATUS_ID=?5 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMServiceIdAndStatusId(long merchantId, String from, String to,
			long merchantServiceId, long statusId);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.PAYEE_VPA=?4 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndVpa(long merchantId, String from, String to, String vpa);

	@Query(value = "Select ptd.TRANSACTION_STATUS,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.SERVICE_NAME=?4 and ptd.PAYEE_VPA=?5 GROUP BY ptd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndVpa(long merchantId, String from, String to, String serviceName,
			String vpa);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.PAYEE_VPA=?4 and ptd.MERCHANT_SERVICE_ID=?5 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndVpaAndMServiceId(long merchantId, String from, String to, String vpa,
			long merchantServiceId);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.PAYEE_VPA=?4 and ptd.TRANSACTION_STATUS_ID=?5 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndVpaAndStatusId(long merchantId, String from, String to, String vpa,
			long statusId);

	@Query(value = "Select ptd.TRANSACTION_STATUS,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.SERVICE_NAME=?4 and ptd.PAYEE_VPA=?5 and ptd.TRANSACTION_STATUS_ID=?6 GROUP BY ptd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndVpaAndStatusId(long merchantId, String from, String to,
			String serviceName, String vpa, long statusId);

	@Query(value = "Select ptd.SERVICE_NAME,COUNT(ptd.PAYIN_TRANSACTION_ID),SUM(ptd.TRANSACTION_AMOUNT) from PAYIN_TRANSACTION_DETAIL ptd where ptd.MERCHANT_ID=?1 and ((ptd.TRANSACTION_DATE>=?2) and (ptd.TRANSACTION_DATE<=?3)) and ptd.PAYEE_VPA=?4 and ptd.TRANSACTION_STATUS_ID=?5 and ptd.MERCHANT_SERVICE_ID=?6 GROUP BY ptd.SERVICE_NAME ORDER BY ptd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndVpaAndStatusIdAndMServiceId(long merchantId, String from, String to, String vpa,
			long statusId, long merchantServiceId);

	// New Dashboard
	@Query(value = "SELECT EXTRACT(month FROM ptd.TRANSACTION_DATE) AS month,SUM(ptd.TRANSACTION_AMOUNT) AS amount FROM PAYIN_TRANSACTION_DETAIL ptd  WHERE ptd.TRANSACTION_STATUS_ID =1 AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 GROUP BY EXTRACT(month FROM ptd.TRANSACTION_DATE)", nativeQuery = true)
	List<?> getAllYearWiseData(Long merchantId, String year);

	@Query(value = "SELECT ptd.PAYIN_TRANSACTION_ID,ptd.TRANSACTION_AMOUNT FROM PAYIN_TRANSACTION_DETAIL ptd  WHERE ptd.TRANSACTION_STATUS_ID =1  AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3", nativeQuery = true)
	List<?> getAllYearWiseData2(Long merchantId, String year, String month);

	@Query(value = "SELECT EXTRACT(week FROM ptd.TRANSACTION_DATE) AS week,SUM(ptd.TRANSACTION_AMOUNT) AS amount FROM PAYIN_TRANSACTION_DETAIL ptd  WHERE ptd.TRANSACTION_STATUS_ID =1 AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3 GROUP BY EXTRACT(week FROM ptd.TRANSACTION_DATE)", nativeQuery = true)
	List<?> getWeekDataByYearAndMonth(Long merchantId, String year, String month);

	@Query(value = "SELECT ptd.PAYIN_TRANSACTION_ID,ptd.TRANSACTION_AMOUNT FROM PAYIN_TRANSACTION_DETAIL ptd  WHERE ptd.TRANSACTION_STATUS_ID =1  AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3 AND week(ptd.TRANSACTION_DATE)=?4", nativeQuery = true)
	List<?> getWeekDataByYearAndMonth2(Long merchantId, String year, String month, String week);

	@Query(value = "SELECT EXTRACT(day FROM ptd.TRANSACTION_DATE) AS day,SUM(ptd.TRANSACTION_AMOUNT) AS amount FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.TRANSACTION_STATUS_ID =1 AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3 GROUP BY EXTRACT(day FROM ptd.TRANSACTION_DATE)", nativeQuery = true)
	List<?> getDayDataByYearAndMonth(Long merchantId, String year, String month);

	@Query(value = "SELECT ptd.PAYIN_TRANSACTION_ID,ptd.TRANSACTION_AMOUNT FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.TRANSACTION_STATUS_ID =1  AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3 AND day(ptd.TRANSACTION_DATE)=?4", nativeQuery = true)
	List<?> getDayDataByYearAndMonth2(Long merchantId, String year, String month, String day);

	@Query(value = "SELECT EXTRACT(month FROM ptd.TRANSACTION_DATE) AS month,SUM(ptd.TRANSACTION_AMOUNT) AS amount,ptd.SERVICE_NAME AS servicename,count(ptd.PAYIN_TRANSACTION_ID) AS transaction  FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.TRANSACTION_STATUS_ID =1 AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3 GROUP BY EXTRACT(month FROM ptd.TRANSACTION_DATE),servicename", nativeQuery = true)
	List<?> getPiChartWeekDataByYearAndMonth(Long merchantId, String year, String month);

	@Query(value = "SELECT SUM(ptd.TRANSACTION_AMOUNT) AS amount,ptd.SERVICE_NAME AS servicename,count(ptd.PAYIN_TRANSACTION_ID) AS transaction FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.TRANSACTION_STATUS_ID =1 AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3 GROUP BY servicename", nativeQuery = true)
	List<?> getTrxnAmountPiChartWeekDataByYearAndMonth(Long merchantId, String year, String month);

	@Query(value = "SELECT ptd.TRANSACTION_AMOUNT FROM PAYIN_TRANSACTION_DETAIL ptd  WHERE ptd.TRANSACTION_STATUS_ID =1 AND ptd.MERCHANT_ID=?1 AND year(ptd.TRANSACTION_DATE)=?2 AND month(ptd.TRANSACTION_DATE)=?3 AND ptd.SERVICE_NAME=?4", nativeQuery = true)
	List<Double> getTrxnAmountPiChartWeekDataByYearAndMonth2(Long merchantId, String year, String month,
			String encString);

	@Query(value = "SELECT * FROM PAYIN_TRANSACTION_DETAIL ptd WHERE ptd.MERCHANT_ID=?1 ORDER BY ptd.TRANSACTION_DATE DESC", nativeQuery = true)
	List<PayinTransactionalDetail> findByStartDateAndEndDateWithPage(long merchantId, Pageable pageable);

}
