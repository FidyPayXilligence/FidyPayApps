package com.fidypay.repo;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.Merchants;

@Repository
public interface MerchantsRepository extends JpaRepository<Merchants, Long> {

	@Query(value = "SELECT M.MERCHANT_FLOAT_AMOUNT FROM MERCHANTS M WHERE M.MERCHANT_ID=?1", nativeQuery = true)
	double findMerchantWalletByMerchantId(long merId);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_PASSWORD=?1 AND M.MERCHANT_EMAIL=?2", nativeQuery = true)
	Merchants findByPasswordAndEmail(String password, String email);

	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_ID=?1", nativeQuery = true)
	Merchants findByMerchantId(long merchantIdInLong);

	@Query(value = "Select m.MERCHANT_ALTERNATE_EMAIL from MERCHANTS m where m.MERCHANT_ID=?1", nativeQuery = true)
	String getJsonByMerchantId(long merchantId);
	
	@Query(value = "Select * from MERCHANTS M where M.MERCHANT_PASSWORD=?1 AND M.MERCHANT_EMAIL=?2 AND M.MERCHANT_ID=?3 AND M.MERCHANT_FIRSTNAME=?4", nativeQuery = true)
	Merchants findByPasswordAndEmailAndMerchantId(String password, String email, long merchantId, String firstName);
	
	Optional<Merchants> findByMerchantPhone(String phone);

	Optional<Merchants> findByMerchantEmail(String encString);

	Optional<Merchants> findByMerchantPhoneAndMerchantEmail(String encString,String phone);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Merchants m WHERE m.merchantId = :merchantId")
	Optional<Merchants> findByIdForUpdate(@Param("merchantId") Long merchantId);
	
	
}
