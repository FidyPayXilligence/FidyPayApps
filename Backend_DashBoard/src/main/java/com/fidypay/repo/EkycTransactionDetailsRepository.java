package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycTransactionDetails;

@Repository
public interface EkycTransactionDetailsRepository extends JpaRepository<EkycTransactionDetails, Long> {

	boolean existsByMerchantTransactionRefId(String merchantTransactionRefId);

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3))", nativeQuery = true)
	Page<EkycTransactionDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate,
			Pageable paging);

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4", nativeQuery = true)
	Page<EkycTransactionDetails> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate,
			Long merchantServiceId, Pageable paging);

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4", nativeQuery = true)
	Page<EkycTransactionDetails> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate,
			Long status, Pageable paging);

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 and etd.MERCHANT_SERVICE_ID=?5", nativeQuery = true)
	Page<EkycTransactionDetails> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate,
			String endDate, Long status, Long merchantServiceId, Pageable paging);

	// Without Page

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) order by etd.CREATION_DATE desc", nativeQuery = true)
	List<EkycTransactionDetails> findByStartDateAndEndDateWithoutPage(Long merchantId, String startDate,
			String endDate);

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4 order by etd.CREATION_DATE desc", nativeQuery = true)
	List<EkycTransactionDetails> findByStartDateAndEndDateANDServiceWithoutPage(Long merchantId, String startDate,
			String endDate, Long merchantServiceId);

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 order by etd.CREATION_DATE desc", nativeQuery = true)
	List<EkycTransactionDetails> findByStartDateAndEndDateANDStatusWithoutPage(Long merchantId, String startDate,
			String endDate, Long status);

	@Query(value = "SELECT * FROM EKYC_TRANSACTION_DETAILS etd WHERE etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 and etd.MERCHANT_SERVICE_ID=?5 order by etd.CREATION_DATE desc", nativeQuery = true)
	List<EkycTransactionDetails> findByStartDateAndEndDateANDStatusANDServiceWithoutPage(Long merchantId,
			String startDate, String endDate, Long status, Long merchantServiceId);

	@Query(value = "SELECT COUNT(etd.REQUEST_ID) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Integer findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	@Query(value = "SELECT SUM(etd.TRANSACTION_AMOUNT) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((btd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Double findByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	// service wise
	
	@Query(value = "Select etd.EKYC_SERVICENAME,COUNT(etd.EKYC_TRANSACTION_ID) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) GROUP BY etd.EKYC_SERVICENAME ORDER BY etd.EKYC_SERVICENAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantId(long merchantId, String from, String to);

	@Query(value = "Select etd.STATUS,COUNT(etd.EKYC_TRANSACTION_ID) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.EKYC_SERVICENAME=?4 GROUP BY etd.STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceName(long merchantId, String from, String to, String serviceName);

	@Query(value = "Select etd.EKYC_SERVICENAME,COUNT(etd.EKYC_TRANSACTION_ID) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4 GROUP BY etd.EKYC_SERVICENAME ORDER BY etd.EKYC_SERVICENAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMserviceId(long merchantId, String from, String to, long merchantServiceId);

	@Query(value = "Select etd.EKYC_SERVICENAME,COUNT(etd.EKYC_TRANSACTION_ID) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.TRANSACTION_STATUS_ID=?4 GROUP BY etd.EKYC_SERVICENAME ORDER BY etd.EKYC_SERVICENAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndStatusId(long merchantId, String from, String to, long statusId);

	@Query(value = "Select etd.STATUS,COUNT(etd.EKYC_TRANSACTION_ID) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.EKYC_SERVICENAME=?4 and etd.TRANSACTION_STATUS_ID=?5 GROUP BY etd.STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndStatusId(long merchantId, String from, String to,
			String serviceName, long statusId);

	@Query(value = "Select etd.EKYC_SERVICENAME,COUNT(etd.EKYC_TRANSACTION_ID) from EKYC_TRANSACTION_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.CREATION_DATE>=?2) and (etd.CREATION_DATE<=?3)) and etd.MERCHANT_SERVICE_ID=?4 and etd.TRANSACTION_STATUS_ID=?5 GROUP BY etd.EKYC_SERVICENAME ORDER BY etd.EKYC_SERVICENAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMServiceIdAndStatusId(long merchantId, String from, String to,
			long merchantServiceId, long statusId);

}
