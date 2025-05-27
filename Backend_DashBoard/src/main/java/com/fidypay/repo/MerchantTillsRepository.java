package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantTills;

@Repository
public interface MerchantTillsRepository extends JpaRepository<MerchantTills, Long> {

	@Query(value = "SELECT sp.service_Provider_Id FROM SERVICE_PROVIDERS sp WHERE sp.sp_Name=?1", nativeQuery = true)
	long getServiceProviderId(String spName);

	@Query(value = "SELECT mt.TILL_FLOAT_AMOUNT,mt.MERCHANT_TILL_ID,mt.MERCHANT_ID FROM MERCHANT_TILLS mt WHERE mt.MERCHANT_TILL_ID=?1", nativeQuery = true)
	List<?> findByMERCHANTTILLID(long merchantillId);

	@Query(value = "SELECT MAX(TILL_CODE) FROM MERCHANT_TILLS", nativeQuery = true)
	List<String> findMaxTillCode();
}
