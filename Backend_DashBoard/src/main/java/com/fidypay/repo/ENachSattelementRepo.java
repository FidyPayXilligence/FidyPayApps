package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.ENachMerchantSettelment;

@Repository
public interface ENachSattelementRepo extends JpaRepository<ENachMerchantSettelment, Long> {

	@Query(value = "SELECT SUM (er.COLLECTION_AMOUNT), er.MERCHANT_ID FROM ENACH_RECONCILIATION er "
			+ "WHERE er.FROM_DATE>=?1 and er.TO_DATE<=?2 and er.MERCHANT_ID=?3 GROUP BY MERCHANT_ID", nativeQuery = true)
	List<?> findAllMerchantSettlementTransactions(String startDate, String endDate, Long merchantId);

	@Query(value = "SELECT * FROM ENACH_MERCHANT_SETTLEMENT er where er.FROM_DATE>=?1 and er.TO_DATE<=?2 and er.MERCHANT_ID=?3", nativeQuery = true)
	List<ENachMerchantSettelment> findByFromDateAndToDateAndMerchantId(String fromDate, String toDate, long merchantId);

	@Query(value = "SELECT * FROM ENACH_MERCHANT_SETTLEMENT emr WHERE (emr.FROM_DATE>=?1 and emr.TO_DATE<=?2) And emr.MERCHANT_ID=?3", nativeQuery = true)
	Page<ENachMerchantSettelment> findBypageSizeANDFromDateAndToDate(Pageable pageble, String fromDate, String toDate,long merchantId);
}
