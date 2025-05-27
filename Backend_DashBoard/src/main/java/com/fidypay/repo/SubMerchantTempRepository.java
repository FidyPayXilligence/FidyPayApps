package com.fidypay.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.fidypay.entity.SubMerchantTemp;

@Repository
public interface SubMerchantTempRepository extends JpaRepository<SubMerchantTemp, Long> {

	@Query(value = "SELECT * FROM SUB_MERCHANT_TEMP smt where smt.EMAIL=?1 And smt.MOBILE=?2 And smt.IS_ONBOARDING ='1' And smt.IS_SUBMERCHANT='1'", nativeQuery = true)
	List<SubMerchantTemp> findByEmailAndMobile(String encString, String encString2);

	SubMerchantTemp findBySubMerchantKeyAndMerchantIdAndIsMerchant(String encString, long merchantId, char isMerchant);

	@Query(value = "SELECT * From SUB_MERCHANT_TEMP smt where ((smt.DATE>=?1 ) and( DATE<=?2)) and smt.MERCHANT_ID=?3 and smt.IS_MERCHANT='0'", nativeQuery = true)
	Page<SubMerchantTemp> findByStartDateAndEndDateAndMerchantId(String startDate, String endDate, long merchantId,
			Pageable paging);

	boolean existsByMobile(String encString);

	boolean existsByEmail(String encString);

	@Query(value = "SELECT * From SUB_MERCHANT_TEMP smt where ((smt.DATE>=?1 ) and ( DATE<=?2)) and smt.IS_ONBOARDING='1' and smt.IS_ACTIVE='1' and smt.IS_SUBMERCHANT='1' and smt.IS_MERCHANT='0'  and smt.JSON_RESPONSE!='NA'", nativeQuery = true)
	List<SubMerchantTemp> findByIsActiveAndIsSubMerchantAndIsOnboardingAndDate(String from, String to);

	@Transactional
	@Modifying
	@Query(value = "update SUB_MERCHANT_TEMP smt set smt.IS_MERCHANT =?1 where smt.SUB_MERCHANT_TEMP_ID =?2", nativeQuery = true)
	int updateOnboardingIsMerchant(char isMerchant, long subMerchantTempId);

	SubMerchantTemp findByMobileAndMerchantIdAndIsMerchant(String mobile, long merchantId, char isMerchant);

	SubMerchantTemp findBySubMerchantTempIdAndMerchantIdAndIsMerchant(long subMerchantTempId, long merchantId,
			char isMerchant);

	SubMerchantTemp findBySubMerchantKey(String subMerchantKey);

	Optional<SubMerchantTemp> findByMobile(String mobile);

	@Query(value = "SELECT * From SUB_MERCHANT_TEMP smt where smt.MERCHANT_ID=?1 and smt.IS_MERCHANT='0'", nativeQuery = true)
	Page<SubMerchantTemp> findByMerchantId(long merchantId, Pageable paging);

	SubMerchantTemp findBySubMerchantTempIdAndMerchantId(long parseLong, long merchantId);

	@Query(value = "SELECT * From SUB_MERCHANT_TEMP smt where smt.MERCHANT_ID=?1 and smt.IS_ONBOARDING='1' and smt.IS_ACTIVE='1' and smt.IS_SUBMERCHANT='1' and smt.IS_MERCHANT='0'  and smt.JSON_RESPONSE!='NA'", nativeQuery = true)
	SubMerchantTemp findBySubMerchantTempId(long subMerchantTempId);

	List<SubMerchantTemp> findByMerchantIdAndMerchantUserId(long merchantId, long parseLong);

	@Query(value = "SELECT * From SUB_MERCHANT_TEMP smt where ((smt.DATE>=?1 ) and( DATE<=?2)) and smt.MERCHANT_ID=?3 and smt.MERCHANT_USER_ID=?4", nativeQuery = true)
	Page<SubMerchantTemp> findByStartDateAndEndDateAndMerchantIdAndMerchantUserId(String startDate, String endDate,
			long merchantId, long merchantUserId, Pageable paging);

	@Query(value = "SELECT * From SUB_MERCHANT_TEMP smt where smt.MERCHANT_ID=?1 and smt.MERCHANT_USER_ID=?2", nativeQuery = true)
	Page<SubMerchantTemp> findByMerchantIdAndMerchantUserId(long merchantId, long merchantUserId, Pageable paging);

	SubMerchantTemp findBySubMerchantKeyAndMerchantIdAndMerchantUserId(String encString, long merchantId,
			long merchantUserId);

	SubMerchantTemp findByMobileAndMerchantIdAndMerchantUserId(String encString, long merchantId, long merchantUserId);

}
