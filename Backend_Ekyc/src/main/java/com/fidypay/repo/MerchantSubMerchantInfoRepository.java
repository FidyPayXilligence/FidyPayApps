package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.MerchantSubMerchantInfo;

public interface MerchantSubMerchantInfoRepository extends JpaRepository<MerchantSubMerchantInfo, Long> {

	@Query(value ="Select msmi.SUB_MERCHANT_ID,msmi.SUB_MERCHANT_REGISTER_INFO from MERCHANT_SUB_MERCHANT_INFO msmi where msmi.SUB_MERCHANT_ADDITIONAL_INFO=?1", nativeQuery = true)
	List<?> getSubMerchantIdByVPA(String subMerchantVPA);
	
	
	@Query(value = "Select * from MERCHANT_SUB_MERCHANT_INFO msmi", nativeQuery = true)
	List<MerchantSubMerchantInfo> findSUB_MERCHANT_REGISTER_INFO();
}
