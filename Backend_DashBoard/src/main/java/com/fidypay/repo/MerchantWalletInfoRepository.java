package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantWalletInfo;
import java.util.*;

@Repository
public interface MerchantWalletInfoRepository extends JpaRepository<MerchantWalletInfo, Long> {

	@Query(value = "SELECT * FROM MERCHANT_WALLET_INFO WHERE MERCH_WALLET_ACCOUNT_NO=?1", nativeQuery = true)
	MerchantWalletInfo merchantInfo(String walletAcNumber);

	@Query(value = "SELECT * FROM MERCHANT_WALLET_INFO WHERE MERCHANT_ID=?1 and MERCH_WALLET_PIN='RATN0000100'", nativeQuery = true)
	List<MerchantWalletInfo> findByMerchantId(long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_WALLET_INFO WHERE MERCHANT_ID=?1", nativeQuery = true)
	MerchantWalletInfo findByMerchantIdd(long merchantId);
}
