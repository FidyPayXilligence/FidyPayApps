package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.ENachReconciliation;

@Repository
public interface ENachReconciliationRepository extends JpaRepository<ENachReconciliation, Long> {

	@Query(value = "SELECT * FROM ENACH_RECONCILIATION er where er.FROM_DATE>=?1 and er.TO_DATE<=?2 and er.MERCHANT_ID=?3", nativeQuery = true)
	List<ENachReconciliation> findByFromDateAndToDateAndMerchantId(String fromDate, String toDate, long merchantId);

	@Query(value = "SELECT * FROM ENACH_RECONCILIATION emr WHERE (emr.FROM_DATE>=?1 and emr.TO_DATE<=?2) And emr.MERCHANT_ID=?3", nativeQuery = true)
	Page<ENachReconciliation> findBypageSizeANDFromDateAndToDate(Pageable pageble, String fromDate, String toDate,
			long merchantId);
}
