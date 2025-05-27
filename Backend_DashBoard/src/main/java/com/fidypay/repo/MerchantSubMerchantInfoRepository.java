package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.MerchantSubMerchantInfo;

public interface MerchantSubMerchantInfoRepository extends JpaRepository<MerchantSubMerchantInfo, Long> {

	@Query(value ="Select msmi.SUB_MERCHANT_ID,msmi.SUB_MERCHANT_REGISTER_INFO from MERCHANT_SUB_MERCHANT_INFO msmi where msmi.SUB_MERCHANT_ADDITIONAL_INFO=?1", nativeQuery = true)
	List<?> getSubMerchantIdByVPA(String subMerchantVPA);
	
	@Query(value ="update MerchantSubMerchantInfo set isDeleted=Y where subMerchantId=?1",nativeQuery = true)
	void updateSubMerchantForDelete(String subMerchantId);
	
	@Query(value ="SELECT * FROM MERCHANT_SUB_MERCHANT_INFO msmi where  msmi.IS_DELETED !='Y' AND  msmi.MERCHANT_ID=?1 ORDER BY msmi.SUB_MERCHANT_INFO_ID DESC", nativeQuery = true)
	List<MerchantSubMerchantInfo> findByMerchantIds(Long merchantId,Pageable pageable);

	@Query(value ="SELECT count(*) FROM MERCHANT_SUB_MERCHANT_INFO msmi where  msmi.IS_DELETED !='Y' AND msmi.MERCHANT_ID=?1", nativeQuery = true)
	int countByMerchantIds(Long merchantId);

	@Query(value ="SELECT * FROM MERCHANT_SUB_MERCHANT_INFO where MERCHANT_ID=?1", nativeQuery = true)
	List<MerchantSubMerchantInfo> findVpaListByMerchantId(Long merchantId);

	@Query(value ="SELECT * FROM MERCHANT_SUB_MERCHANT_INFO where SUB_MERCHANT_ADDITIONAL_INFO=?1", nativeQuery = true)
	MerchantSubMerchantInfo findBySubmerchantVpa(String submerchantVpa);

	@Query(value ="SELECT * FROM MERCHANT_SUB_MERCHANT_INFO where MERCHANT_ID=?1", nativeQuery = true)
	MerchantSubMerchantInfo findByMerchantId(Long mMerchantId);

	
}
