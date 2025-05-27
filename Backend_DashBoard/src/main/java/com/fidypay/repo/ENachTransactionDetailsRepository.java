package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.ENachTransactionDetails;

@Repository
public interface ENachTransactionDetailsRepository extends JpaRepository<ENachTransactionDetails, Long> {

	@Query(value = "Select COUNT(etd.TRXN_REF_ID) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Integer findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	@Query(value = "Select SUM(etd.TRANSACTION_AMOUNT) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Double findByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3))", nativeQuery = true)
	Page<ENachTransactionDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate,
			Pageable paging);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4", nativeQuery = true)
	Page<ENachTransactionDetails> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate,
			Long merchantServiceId, Pageable paging);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4", nativeQuery = true)
	Page<ENachTransactionDetails> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate,
			Long status, Pageable paging);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 and etd.MERCHANT_SERVICE_ID=?5", nativeQuery = true)
	Page<ENachTransactionDetails> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate,
			String endDate, Long status, Long merchantServiceId, Pageable paging);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) order by etd.TRANSACTION_DATE desc", nativeQuery = true)
	List<ENachTransactionDetails> findByStartDateAndEndDate(Long merchantId, String startDate, String endDate);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4 order by etd.TRANSACTION_DATE desc", nativeQuery = true)
	List<ENachTransactionDetails> findByStartDateAndEndDateANDService(Long merchantId, String startDate, String endDate,
			Long merchantServiceId);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 order by etd.TRANSACTION_DATE desc", nativeQuery = true)
	List<ENachTransactionDetails> findByStartDateAndEndDateANDStatus(Long merchantId, String startDate, String endDate,
			Long statusId);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 and etd.MERCHANT_SERVICE_ID=?5 order by etd.TRANSACTION_DATE desc", nativeQuery = true)
	List<ENachTransactionDetails> findByStartDateAndEndDateANDStatusANDService(Long merchantId, String startDate,
			String endDate, Long statusId, Long merchantServiceId);

	// service wise

	@Query(value = "Select etd.SERVICE_NAME,COUNT(etd.ENACH_TRANSACTION_ID),SUM(etd.TRANSACTION_AMOUNT) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) GROUP BY etd.SERVICE_NAME ORDER BY etd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantId(long merchantId, String from, String to);

	@Query(value = "Select etd.TRANSACTION_STATUS,COUNT(etd.ENACH_TRANSACTION_ID),SUM(etd.TRANSACTION_AMOUNT) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.SERVICE_NAME=?4 GROUP BY etd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceName(long merchantId, String from, String to, String serviceName);

	@Query(value = "Select etd.SERVICE_NAME,COUNT(etd.ENACH_TRANSACTION_ID),SUM(etd.TRANSACTION_AMOUNT) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4 GROUP BY etd.SERVICE_NAME ORDER BY etd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdANdMServiceId(long merchantId, String startDate, String endDate,
			long merchantServiceId);

	@Query(value = "Select etd.SERVICE_NAME,COUNT(etd.ENACH_TRANSACTION_ID),SUM(etd.TRANSACTION_AMOUNT) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 GROUP BY etd.SERVICE_NAME ORDER BY etd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdANdStatusId(long merchantId, String startDate, String endDate, long statusId);

	@Query(value = "Select etd.TRANSACTION_STATUS,COUNT(etd.ENACH_TRANSACTION_ID),SUM(etd.TRANSACTION_AMOUNT) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.SERVICE_NAME=?4 and etd.TRANSACTION_STATUS_ID=?5 GROUP BY etd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndStatusId(long merchantId, String startDate, String endDate,
			String serviceName, long statusId);

	@Query(value = "Select etd.SERVICE_NAME,COUNT(etd.ENACH_TRANSACTION_ID),SUM(etd.TRANSACTION_AMOUNT) from ENACH_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.TRANSACTION_DATE>=?2) and (etd.TRANSACTION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4 and etd.TRANSACTION_STATUS_ID=?5 GROUP BY etd.SERVICE_NAME ORDER BY etd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdANdMServiceIdStatusId(long merchantId, String startDate, String endDate,
			long merchantServiceId, long statusId);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.ENACH_TRANSACTION_ID=?1", nativeQuery = true)
	ENachTransactionDetails findByENachTransactionId(long eNachTransactionId);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.MANDATE_ID=?1 AND etd.SERVICE_NAME IN ('Mandate Registrations ESign', 'Mandate Registrations') AND etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Page<ENachTransactionDetails> findByMandateId(String mandateId, Pageable paging);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.CUSTOMER_ID=?1 AND etd.SERVICE_NAME IN ('Mandate Registrations ESign', 'Mandate Registrations') AND etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Page<ENachTransactionDetails> findByMobileNumber(String mobileNumber, Pageable paging);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.SERVICE_PROVIDER_UTILITY_CODE=?1 AND etd.SERVICE_NAME IN ('Mandate Registrations ESign', 'Mandate Registrations') AND etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Page<ENachTransactionDetails> findByUtilityCode(String utilityCode, Pageable paging);

	@Query(value = "Select * from ENACH_TRANSACTION_DETAILS etd where etd.CUSTOMER_BANK_ACCOUNT_NO=?1 AND etd.SERVICE_NAME IN ('Mandate Registrations ESign', 'Mandate Registrations') AND etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Page<ENachTransactionDetails> findByCustomerAccountNumber(String customerAccountNumber, Pageable paging);

}
