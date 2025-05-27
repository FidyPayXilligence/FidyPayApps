package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.BBPSMerchantReconciliation;

@Repository
public interface BBPSMerchantReconciliationRepository extends JpaRepository<BBPSMerchantReconciliation, Long> {

	@Query(value = "SELECT * FROM  BBPS_MERCHANT_RECONCILIATION bmr WHERE (bmr.FROM_DATE>=?1 and bmr.TO_DATE<=?2) and bmr.IS_VERIFIED='0'", nativeQuery = true)
	List<BBPSMerchantReconciliation> findAllSettlementData(String from, String to);

	@Query(value = "SELECT * FROM  BBPS_MERCHANT_RECONCILIATION bmr WHERE (bmr.FROM_DATE>=?1 and bmr.TO_DATE<=?2) And bmr.MERCHANT_ID=?3", nativeQuery = true)
	Page<BBPSMerchantReconciliation> findByFromDateAndTODate(String fromDate, String toDate, Long merchantId,
			Pageable paging);

	@Query(value = "SELECT * FROM  BBPS_MERCHANT_RECONCILIATION bmr WHERE (bmr.FROM_DATE>=?1 and bmr.TO_DATE<=?2) and bmr.MERCHANT_SERVICE_ID=?3 And bmr.MERCHANT_ID=?4", nativeQuery = true)
	Page<BBPSMerchantReconciliation> findByFromDateAndTODateAndMerchantServiceId(String fromDate, String toDate,
			Long merchantServiceId, Long merchantId, Pageable paging);

	@Query(value = "SELECT Count(*) FROM  BBPS_MERCHANT_RECONCILIATION bmr WHERE (bmr.FROM_DATE>=?1 and bmr.TO_DATE<=?2) And bmr.MERCHANT_ID=?3", nativeQuery = true)
	int findTotalRecordsByFromDateAndTODate(String fromDate, String toDate, long merchantId);

	@Query(value = "SELECT Count(*) FROM  BBPS_MERCHANT_RECONCILIATION bmr WHERE (bmr.FROM_DATE>=?1 and bmr.TO_DATE<=?2) and bmr.MERCHANT_SERVICE_ID=?3 And bmr.MERCHANT_ID=?4", nativeQuery = true)
	int findTotalRercordsByFromDateAndTODateAndMerchantServiceId(String fromDate, String toDate, Long merchantServiceId,
			long merchantId);

	@Query(value = "SELECT * FROM  BBPS_MERCHANT_RECONCILIATION bmr WHERE (bmr.FROM_DATE>=?1 and bmr.TO_DATE<=?2) And bmr.MERCHANT_ID=?3", nativeQuery = true)
	List<BBPSMerchantReconciliation> findByFromDateAndTODateWithoutPage(String fromDate, String toDate,
			Long merchantId);

	@Query(value = "SELECT * FROM  BBPS_MERCHANT_RECONCILIATION bmr WHERE (bmr.FROM_DATE>=?1 and bmr.TO_DATE<=?2) and bmr.MERCHANT_SERVICE_ID=?3 And bmr.MERCHANT_ID=?4", nativeQuery = true)
	List<BBPSMerchantReconciliation> findByFromDateAndTODateAndMerchantServiceIdWithoutPage(String fromDate,
			String toDate, Long merchantServiceId, Long merchantId);

}
