package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantInfo;

@Repository
public interface MerchantInfoRepository extends JpaRepository<MerchantInfo, Long> {

	boolean existsByMerchantId(long merchantId);

	MerchantInfo findByMerchantId(long merchantId);

	@Query(value = "SELECT BANK_ID_UPI FROM  MERCHANT_INFO WHERE MERCHANT_ID=?1", nativeQuery = true)
	String findBankIdUPI(long merchantId);

	boolean existsByClientIdAndClientSecret(String encString, String encString2);

	@Query(value = "SELECT * FROM  MERCHANT_INFO WHERE CLIENT_ID=?1 and CLIENT_SECRET=?2 and IS_MERCHANT_ACTIVE=?3", nativeQuery = true)
	MerchantInfo findByClientIdAndClientSecretAndIsMerchantActive(String clientId, String clientSecret,
			char isMerchantActive);

	@Query(value = "SELECT IMAGE_URL FROM  MERCHANT_INFO WHERE MERCHANT_ID=?1", nativeQuery = true)
	String findImageUrl(long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_INFO mi WHERE mi.CLIENT_ID=?1 and mi.CLIENT_SECRET=?2 and mi.USERNAME=?3 and mi.PASSWORD=?4", nativeQuery = true)
	MerchantInfo findByClientIdAndClientSecretAndUserNameAndPassword(String clientId, String clientSecret,
			String userName, String passowrd);
}
