package com.fidypay.repo;

import com.fidypay.entity.WealthTrxnDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FdReportRepository extends JpaRepository<WealthTrxnDetails, Long> {

	@Query(value = "Select * from WEALTH_TRXN_DETAILS wtd where ((wtd.DATE>=?1) and (wtd.DATE<=?2))", nativeQuery = true)
	Page<WealthTrxnDetails> findWealthTxnDetailsFromDateToDate(String fromDate, String toDate, Pageable pageable);

	@Query(value = "Select * from WEALTH_TRXN_DETAILS etd where etd.MERCHANT_ID=?1 and ((etd.DATE>=?2) and (etd.DATE<=?3)) order by etd.DATE desc", nativeQuery = true)
	List<WealthTrxnDetails> findByStartDateAndEndDate(Long merchantId, String startDate, String endDate);

	@Query(value = "Select * from WEALTH_TRXN_DETAILS etd where etd.MERCHANT_TRXN_REF_ID=?1", nativeQuery = true)
	WealthTrxnDetails findByTrxnId(String merchantTrxnRefId);

	@Query(value = "Select * from WEALTH_TRXN_DETAILS", nativeQuery = true)
	Page<WealthTrxnDetails> findAllWithPage(Pageable pageable);
}
