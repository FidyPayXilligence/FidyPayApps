package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.MerchantServiceCharges;
import com.fidypay.entity.MerchantServiceCommission;

public interface MerchantServiceCommissionRepository extends JpaRepository<MerchantServiceCommission, Long> {

	@Query(value = "SELECT * from MERCHANT_SERVICE_COMMISSION msc  ORDER BY msc.MERCHANT_SERVICE_COMMISSION_ID DESC", nativeQuery = true)
	List<MerchantServiceCommission> findAllMerchantServiceCommision(Pageable pageable);

	@Query(value = "SELECT  count(*) FROM MERCHANT_SERVICE_COMMISSION", nativeQuery = true)
	int totalMerchantServiceCommision();

	@Query(value = "SELECT * from MERCHANT_SERVICE_COMMISSION msc where msc.MERCHANT_SERVICE_ID=?1  ORDER BY msc.MERCHANT_SERVICE_COMMISSION_ID DESC", nativeQuery = true)
	MerchantServiceCommission findByMerchantServiceId(Long merchantServiceId);

	@Query(value = "SELECT * from MERCHANT_SERVICE_COMMISSION msc where msc.MERCHANT_SERVICE_ID=?1  ORDER BY msc.MERCHANT_SERVICE_ID DESC", nativeQuery = true)
	List<MerchantServiceCommission> findAllByMerchantServiceId(Long merchantServiceId);

	@Query(value = "SELECT * from MERCHANT_SERVICE_COMMISSION msc where msc.MERCHANT_SERVICE_ID=?1  And msc.MERCHANT_SERVICE_COMMISSION_START=?2", nativeQuery = true)
	MerchantServiceCommission existByMerchantServiceIdAndMerchantServiceCommissionStart(Long merchantServiceId,
			Long merchantServiceCommissionStart);

	@Query(value = "SELECT * from MERCHANT_SERVICE_COMMISSION msc where msc.MERCHANT_SERVICE_ID=?1  And msc.MERCHANT_SERVICE_COMMISSION_END=?2", nativeQuery = true)
	MerchantServiceCommission existByMerchantServiceIdAndMerchantServiceCommissionEnd(Long merchantServiceId,
			Long merchantServiceCommissionEnd);

	@Query(value = "SELECT * from MERCHANT_SERVICE_COMMISSION msc where msc.MERCHANT_SERVICE_ID=?1  And msc.MERCHANT_SERVICE_COMMISSION_START=?2 And  msc.MERCHANT_SERVICE_COMMISSION_END=?3", nativeQuery = true)
	MerchantServiceCommission existByMerchantServiceIdAndMerchantServiceCommissionStartAndMerchantServiceCommissionEnd(
			Long merchantServiceId, Long merchantServiceCommissionStart, Long merchantServiceCommissionEnd);

	@Query(value = "SELECT * from MERCHANT_SERVICE_COMMISSION msc where msc.MERCHANT_SERVICE_ID=?1 ORDER BY msc.MERCHANT_SERVICE_COMMISSION_ID DESC", nativeQuery = true)
	List<MerchantServiceCommission> findByMerchantServiceIdForCommission(Long merchantServiceId);

}
