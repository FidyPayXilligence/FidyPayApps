package com.fidypay.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantUser;

@Repository
public interface MerchantUserRepository extends JpaRepository<MerchantUser, Long> {

	boolean existsByMerchantUserMobileNo(String mobileNo);

	boolean existsByMerchantUserEmail(String email);

	@Query(value = "SELECT * FROM MERCHANT_USER mu  where mu.MERCHANT_ID=?1", nativeQuery = true)
	List<MerchantUser> findByMerchantId(long merchantId);

	@Query(value = "SELECT * FROM MERCHANT_USER mu  where mu.MERCHANT_ID=?1 and mu.MERCHANT_USER_TYPE=?2", nativeQuery = true)
	Page<MerchantUser> findByMerchantIdAndPaging(long merchantId, String merchantUserType, Pageable paging);

	MerchantUser findByMerchantUserIdAndMerchantId(long merchantUserId, long merchantId);

	MerchantUser findByMerchantUserId(long merchantUserId);

	Optional<MerchantUser> findByMerchantUserMobileNo(String merchantUserMobileNo);

	Optional<MerchantUser> findByMerchantUserEmail(String encString);

	@Query(value = "SELECT * FROM MERCHANT_USER mu  where mu.MERCHANT_USER_EMAIL=?1", nativeQuery = true)
	MerchantUser findByMerchantUserEmailForPassword(String encString);

	MerchantUser findByMerchantUserMobileNoAndMerchantIdAndMerchantUserType(String merchantUserMobileNo,
			long merchantId, String merchantUserType);

	@Query(value = "SELECT * FROM MERCHANT_USER mu  where mu.MERCHANT_ID=?1", nativeQuery = true)
	MerchantUser findByMerchantIdForForgetPassword(long merchantId);

	boolean existsByMerchantUserId(long merchantUserId);

	boolean existsByMerchantUserEmailAndMerchantId(String email, long merchantId);

	boolean existsByMerchantUserMobileNoAndMerchantId(String mobileNo, long merchantId);

	boolean existsByMerchantUserMobileNoAndMerchantUserEmail(String mobile, String email);

	MerchantUser findByMerchantUserIdAndMerchantIdAndMerchantUserType(long merchantUserId, long merchantId,
			String merchantUserType);

	boolean existsByIsActiveAndMerchantUserId(char ch, long merchantUserId);

	boolean existsByMerchantUserIdAndIsActive(Long merchantUserId, char isActive);

	@Query(value = "SELECT Count(*) FROM MERCHANT_USER mu  where mu.MERCHANT_ID=?1", nativeQuery = true)
	int totalCountByMerchantId(long merchantId);

	boolean existsByMerchantUserIdAndIsActiveAndMerchantId(long merchantUserId, char isActive, long merchantId);

	Optional<MerchantUser> findByMerchantIdAndMerchantUserMobileNoAndIsActive(long merchantId, String mobile, char isActive);

	Optional<MerchantUser> findByMerchantUserKey(String encString);

}
