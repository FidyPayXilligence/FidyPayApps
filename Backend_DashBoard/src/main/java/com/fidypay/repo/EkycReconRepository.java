package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.EKYCReconciliation;

public interface EkycReconRepository extends JpaRepository<EKYCReconciliation, Long> {

	@Query(value = "SELECT * FROM EKYC_RECONCILIATION e" + "	where e.MERCHANT_ID = ?1 and"
			+ "	((e.FROM_DATE>= ?2) " + "	and (e.TO_DATE<=?3))", nativeQuery = true)
	List<EKYCReconciliation> findAllReconTransactionsByDates(Long merchantId, String fromDate, String toDate);

	@Query(value = "SELECT * FROM EKYC_RECONCILIATION er where er.FROM_DATE>=?1 and er.TO_DATE<=?2 and er.MERCHANT_ID=?3", nativeQuery = true)
	List<EKYCReconciliation> findByFromDateAndToDateAndMerchantId(String fromDate, String toDate, long merchantId);

	@Query(value = "SELECT * FROM EKYC_RECONCILIATION er where er.FROM_DATE>=?1 and er.TO_DATE<=?2 and er.MERCHANT_ID=?3", nativeQuery = true)
	Page<EKYCReconciliation> findByFromDateAndToDateAndMerchantIdL(String fromDate, String toDate, long merchantId,
			org.springframework.data.domain.Pageable paging);
}
