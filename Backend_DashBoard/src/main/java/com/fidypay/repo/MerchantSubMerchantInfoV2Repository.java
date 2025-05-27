package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.MerchantSubMerchantInfoV2;

public interface MerchantSubMerchantInfoV2Repository extends JpaRepository<MerchantSubMerchantInfoV2, Long> {

	@Query(value = "SELECT * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where  msmi.IS_DELETED !='Y' AND  msmi.MERCHANT_ID=?1 ORDER BY msmi.SUB_MERCHANT_INFO_ID DESC", nativeQuery = true)
	Page<MerchantSubMerchantInfoV2> findByMerchantIds(Long merchantId, Pageable pageable);

	@Query(value = "SELECT * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where  msmi.IS_DELETED !='Y'  ORDER BY msmi.SUB_MERCHANT_INFO_ID DESC", nativeQuery = true)
	Page<MerchantSubMerchantInfoV2> findAllWithPage(Pageable pageable);

	@Query(value = "SELECT * FROM MERCHANT_SUB_MERCHANT_INFO_V2 where MERCHANT_ID=?1", nativeQuery = true)
	List<MerchantSubMerchantInfoV2> findVpaListByMerchantId(Long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_SUB_MERCHANT_INFO_V2 where SUB_MERCHANT_ADDITIONAL_INFO=?1 and BANK_ID=?2", nativeQuery = true)
	MerchantSubMerchantInfoV2 findBySubmerchantVpa(String submerchantVpa, String bankId);

	@Query(value = "SELECT * FROM MERCHANT_SUB_MERCHANT_INFO_V2 where MERCHANT_ID=?1", nativeQuery = true)
	MerchantSubMerchantInfoV2 findByMerchantId(Long mMerchantId);

	@Query(value = "SELECT msmi.SUB_MERCHANT_BANK_DETAILS,msmi.SUB_MERCHANT_KEY FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where msmi.SUB_MERCHANT_ADDITIONAL_INFO=?1", nativeQuery = true)
	List<?> findByBankDetailByVPA(String submerchantVpa);

	@Query(value = "SELECT * FROM MERCHANT_SUB_MERCHANT_INFO_V2 where MERCHANT_ID=?1 and SUB_MERCHANT_INFO_ID=?2", nativeQuery = true)
	MerchantSubMerchantInfoV2 findByMerchantIdsAnsSubMerchantInfoId(long merchantId, long subMerchantInfoId);

	@Query(value = "Select * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where msmi.SUB_MERCHANT_MOBILE_NUMBER =?1", nativeQuery = true)
	MerchantSubMerchantInfoV2 findByMobileNumber(String mobileNumber);

	MerchantSubMerchantInfoV2 findBySubMerchantInfoIdV2(long subMerchantInfoIdV2);

	boolean existsBySubMerchantInfoIdV2(long getSubmerchantInfoId);

	@Query(value = "Select * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where msmi.MERCHANT_ID=?1 and msmi.SUB_MERCHANT_NAME =?2", nativeQuery = true)
	List<MerchantSubMerchantInfoV2> findByMerchantName(long merchantId, String merchantName);

	@Query(value = "Select * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where msmi.MERCHANT_ID=?1 and msmi.SUB_MERCHANT_ADDITIONAL_INFO =?2", nativeQuery = true)
	List<MerchantSubMerchantInfoV2> findByVpa(long merchantId, String vpa);

	@Query(value = "Select * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where msmi.MERCHANT_ID=?1 and msmi.SUB_MERCHANT_BUSSINESS_NAME =?2", nativeQuery = true)
	List<MerchantSubMerchantInfoV2> findBySubMerchantBussinessName(long merchantId, String subMerchantBussinessName);

	@Query(value = "Select * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where msmi.MERCHANT_ID=?1 and msmi.SUB_MERCHANT_MOBILE_NUMBER =?2", nativeQuery = true)
	List<MerchantSubMerchantInfoV2> findByMobile(long merchantId, String mobile);

	@Query(value = "SELECT count(*) FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where  msmi.MERCHANT_ID=?1", nativeQuery = true)
	int findByMerchantID(long merchantId);

	@Query(value = "Select * FROM MERCHANT_SUB_MERCHANT_INFO_V2 msmi where msmi.SUB_MERCHANT_MOBILE_NUMBER =?1 LIMIT 1", nativeQuery = true)
	List<MerchantSubMerchantInfoV2> findByMobile(String mobile);

	@Query(value = "SELECT * FROM MERCHANT_SUB_MERCHANT_INFO_V2 where SUB_MERCHANT_ADDITIONAL_INFO=?1", nativeQuery = true)
	MerchantSubMerchantInfoV2 findBySubmerchantVpa(String submerchantVpa);
}
