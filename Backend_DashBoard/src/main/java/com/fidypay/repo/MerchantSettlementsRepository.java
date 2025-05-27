package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantSettlements;

@Repository
public interface MerchantSettlementsRepository extends JpaRepository<MerchantSettlements, Long> {

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	List<MerchantSettlements> findByMerchantIdAndSettlementDate(Long merchantId, String startDate, String endDate);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	List<MerchantSettlements> findByMerchantIdAndSettlementDate(Long merchantId, String startDate, String endDate,
			Pageable paging);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	Page<MerchantSettlements> findByMerchantIdAndSettlementDateWithPage(Long merchantId, String startDate,
			String endDate, Pageable paging);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_VPA=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3))", nativeQuery = true)
	Page<MerchantSettlements> findByVPAAndSettlementDate(String vpa, String startDate, String endDate, Pageable paging);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_VPA=?1", nativeQuery = true)
	Page<MerchantSettlements> findByVPA(String vpa, Pageable paging);

	// 13decBharti

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) AND ms.SETTLEMENT_STATUS=?4", nativeQuery = true)
	Page<MerchantSettlements> findByMerchantIdAndSettlementDateAndStausWithPage(Long merchantId, String startDate,
			String endDate, String merchantStatus, Pageable paging);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) AND ms.MERCHANT_VPA=?4", nativeQuery = true)
	Page<MerchantSettlements> findByMerchantIdAndSettlementDateAndVpaWithPage(Long merchantId, String startDate,
			String endDate, String merchantVpa, Pageable paging);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) AND ms.SETTLEMENT_STATUS=?4 ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	List<MerchantSettlements> findByMerchantIdAndSettlementDateAndStatus(Long merchantId, String startDate,
			String endDate, String merchantStatus);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) AND  ms.MERCHANT_VPA=?4 ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	List<MerchantSettlements> findByMerchantIdAndSettlementDateAndVpa(Long merchantId, String startDate, String endDate,
			String merchantVpa);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3))  ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	List<MerchantSettlements> findByMerchantIdAndSettlementSDate(Long merchantId, String startDate, String endDate);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) AND  ms.MERCHANT_VPA=?4 AND  ms.SETTLEMENT_STATUS=?5  ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	Page<MerchantSettlements> findByMerchantIdAndSettlementDateAndVpaAndStatusWithPage(Long merchantId,
			String startDate, String endDate, String merchantVpa, String merchantStatus, Pageable paging);

	@Query(value = "SELECT * FROM MERCHANT_SETTLEMENTS ms WHERE ms.MERCHANT_ID=?1 AND ((ms.SETTLEMENT_DATE>=?2) and (ms.SETTLEMENT_DATE<=?3)) AND  ms.MERCHANT_VPA=?4 AND  ms.SETTLEMENT_STATUS=?5  ORDER BY ms.SETTLEMENT_DATE DESC", nativeQuery = true)
	List<MerchantSettlements> findByMerchantIdAndSettlementDateAndVpaAndStatus(Long merchantId, String startDate,
			String endDate, String merchantVpa, String merchantStatus);
}
