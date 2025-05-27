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

	@Query(value = "SELECT * FROM MERCHANT_SERVICE ms Inner Join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID Inner Join SERVICE_CATEGORY sc ON si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID where ms.MERCHANT_ID=?1 and sc.SERVICE_CATEGORY_NAME='gBjo9hRVm3+Ek8tvHVXER4NDalVIIuLZLxzjFFhPQuY='", nativeQuery = true)
	List<MerchantService> findServicesByMerchantId(long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_SERVICE ms Inner Join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID Inner Join SERVICE_CATEGORY sc ON si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID where ms.MERCHANT_ID=?1 and sc.SERVICE_CATEGORY_NAME='QCXXR3dtDbgCEZpu6oiGlw=='", nativeQuery = true)
	List<MerchantService> findPayoutServicesByMerchantId(long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_SERVICE ms Inner Join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID Inner Join SERVICE_CATEGORY sc ON si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID where ms.MERCHANT_ID=?1 and sc.SERVICE_CATEGORY_NAME='Zg8QL/aIB2kMCZCqWJM7TA=='", nativeQuery = true)
	List<MerchantService> findPayinServicesByMerchantId(long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_SERVICE ms Inner Join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID Inner Join SERVICE_CATEGORY sc ON si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID where ms.MERCHANT_ID=?1 and sc.SERVICE_CATEGORY_NAME='1/jjuHS2SKD6jySjfr0fjA=='", nativeQuery = true)
	List<MerchantService> findEKycServicesByMerchantId(long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_SERVICE ms Inner Join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID Inner Join SERVICE_CATEGORY sc ON si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID where ms.MERCHANT_ID=?1 and sc.SERVICE_CATEGORY_NAME='oQTHSEbT1ZU9+3q4DsJniQ=='", nativeQuery = true)
	List<MerchantService> findPGServicesByMerchantId(long merchantId);
	
	@Query(value = "SELECT * FROM MERCHANT_SERVICE ms Inner Join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID Inner Join SERVICE_CATEGORY sc ON si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID where ms.MERCHANT_ID=?1 and sc.SERVICE_CATEGORY_NAME='swOQA3j+ax44opJF5xHHjA=='", nativeQuery = true)
	List<MerchantService> findENachServicesByMerchantId(long merchantId);

	
	@Query(value = "SELECT * from MERCHANT_SERVICE ms  Where ms.SERVICE_ID=?1", nativeQuery = true)
	List<MerchantService> findByServiceId(long serviceId);

	@Query(value = "SELECT Count(*) from MERCHANT_SERVICE ms  Where ms.SERVICE_ID=?1", nativeQuery = true)
	int totalMerchantsByServiceId(long serviceId);

	boolean existsByServiceIdAndMerchantIdAndIsMerchantServiceActive(Long serviceId, long merchantId,
			char isMerchantServiceActive);
}
