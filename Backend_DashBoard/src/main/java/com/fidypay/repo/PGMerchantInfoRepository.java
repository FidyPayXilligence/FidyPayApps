package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PGMerchantInfo;

@Repository
public interface PGMerchantInfoRepository extends JpaRepository<PGMerchantInfo, Long> {

	boolean existsByApiMerchantIdAndIsActiveAndMerchantId(String apiMerchantId, char isActive,long merchantId);

	@Query(value = "Select * from PG_MERCHANT_INFO pmi where pmi.MERCHANT_ID=?1 and pmi.IS_ACTIVE='1'", nativeQuery = true)
	List<PGMerchantInfo> findByMerchantId(long merchantId);

	@Query(value = "Select * from PG_MERCHANT_INFO pmi where pmi.PG_MERCHANT_KEY=?1", nativeQuery = true)
	PGMerchantInfo findByPgMerchantKey(String pgMerchantKey);

}
