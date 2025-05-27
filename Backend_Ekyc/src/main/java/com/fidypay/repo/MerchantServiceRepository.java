package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.MerchantService;

public interface MerchantServiceRepository extends JpaRepository<MerchantService, Long> {
	
	
	@Query(value = "SELECT * from MERCHANT_SERVICE ms  ORDER BY ms.MERCHANT_SERVICE_ID DESC", nativeQuery = true)
	List<MerchantService> findAllMerchantService(Pageable pageable);
	
	@Query(value = "SELECT count(*) from MERCHANT_SERVICE", nativeQuery = true)
	int totalMerchantService();
	
	
	@Query(value = "SELECT * from MERCHANT_SERVICE ms  Where ms.MERCHANT_ID=?1 AND ms.SERVICE_ID=?2 AND ms.SERVICE_PROVIDER_ID=?3", nativeQuery = true)
	MerchantService findByMerchantIdAndServiceIdAndServiceProviderId(long merchantId, long serviceId,
			long serviceProviderId);

	@Query(value = "SELECT * from MERCHANT_SERVICE ms  Where ms.MERCHANT_ID=?1", nativeQuery = true)
	List<MerchantService> findAllMerchantServiceByMerchantId(Long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_SERVICE ms WHERE ms.MERCHANT_ID=?1 and ms.SERVICE_ID=?2", nativeQuery = true)
	MerchantService findByMerchantIdAndServiceId(Long merchantId, Long serviceId);
	
	
	boolean existsByServiceIdAndMerchantIdAndIsMerchantServiceActive(Long serviceId, long merchantId,char isMerchantServiceActive);
}
