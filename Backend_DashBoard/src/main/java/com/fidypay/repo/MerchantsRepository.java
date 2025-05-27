package com.fidypay.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.Merchants;

@Repository
public interface MerchantsRepository extends JpaRepository<Merchants, Long> {

	@Query(value = "Select M.MERCHANT_EMAIL from MERCHANTS M where M.MERCHANT_ID=?1", nativeQuery = true)
	String getMerchantEmail(long merchantId);

	@Query(value = "Select m.MERCHANT_ALTERNATE_EMAIL from MERCHANTS m where m.MERCHANT_ID=?1", nativeQuery = true)
	String getJsonByMerchantId(long merchantId);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_ID=?1", nativeQuery = true)
	List<Merchants> findListByMerchantId(long merchantId);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_EMAIL=?1", nativeQuery = true)
	Merchants findByEmail(String email);

	@Query(value = "Select m.MERCHANT_FIRSTNAME,m.MERCHANT_LASTNAME,m.MERCHANT_ADDRESS1,m.MERCHANT_CITY,m.MERCHANT_STATE,m.MERCHANT_ZIPCODE,m.MERCHANT_BUSINESS_NAME,m.MERCHANT_EMAIL,m.MERCHANT_PHONE from MERCHANTS m where m.MERCHANT_ID=?1", nativeQuery = true)
	List<?> findMerchantDetails(long merchantId);

	@Query(value = "Select m.MERCHANT_PASSWORD from MERCHANTS m where m.MERCHANT_ID=?1", nativeQuery = true)
	String getPassword(long merchantId);

	@Query(value = "Update MERCHANTS m Set m.MERCHANT_PASSWORD=?1 where m.MERCHANT_ID=?2", nativeQuery = true)
	void updatePassword(String password, long merchantId);

	@Query(value = "Select M.MERCHANT_PHONE,M.MERCHANT_PASSWORD,M.MERCHANT_EMAIL,M.MERCHANT_ID,M.MERCHANT_BUSINESS_NAME,M.MERCHANT_TYPE_ID,M.MERCHANT_FIRSTNAME,M.SECOND_SEC_ANSWER,M.MERCHANT_CITY from MERCHANTS M where M.MERCHANT_EMAIL=?1", nativeQuery = true)
	List<?> merchantLoginDashbaord(String merchantEmail);

	@Query(value = "Select m.MERCHANT_FLOAT_AMOUNT from MERCHANTS m where m.MERCHANT_ID=?1", nativeQuery = true)
	double findMerchantWallet(long merchantId);

	@Query(value = "Select * from MERCHANTS m where m.MERCHANT_EMAIL=?1 AND m.MERCHANT_PHONE=?2", nativeQuery = true)
	List<?> findByEmailAndMobileNo(String email, String mobileNo);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_ID=?1", nativeQuery = true)
	Merchants findMerchant(long merchantId);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_PHONE=?1", nativeQuery = true)
	Merchants findByMobileNo(String mobileNo);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_PASSWORD=?1 AND M.MERCHANT_EMAIL=?2", nativeQuery = true)
	Merchants findByPasswordAndEmail(String password, String email);

	@Query(value = "Select M.MERCHANT_EMAIL from MERCHANTS M where M.MERCHANT_ID=?1", nativeQuery = true)
	String findMerchantEmail(long merchantId);

	boolean existsByMerchantEmailAndIsMerchantActive(String encString, char c);

	boolean existsByMerchantPhoneAndIsMerchantActive(String encString, char c);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_EMAIL=?1 AND M.MERCHANT_PHONE=?2", nativeQuery = true)
	Optional<Merchants> findByEmailAndPhone(String email, String mobile);

	boolean existsByMerchantPhone(String encString);

	boolean existsByMerchantEmail(String encString);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_EMAIL=?1 AND M.MERCHANT_PHONE=?2", nativeQuery = true)
	Merchants findByMerchantEmailAndPhone(String email, String mobile);

	@Query(value = "Select m.MERCHANT_BUSINESS_NAME from MERCHANTS m where m.MERCHANT_ID=?1", nativeQuery = true)
	String findByIdMerchantBussinessName(long merchantId);

	@Query(value = "Select m.VERTICAL_REGIONS from MERCHANTS m where m.MERCHANT_ID=?1", nativeQuery = true)
	String findByMerchantId(long merchantId);

}
