package com.fidypay.repo;

import com.fidypay.entity.MerchantUserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author prave
 * @Date 09-10-2023
 */
@Repository
public interface MerchantUserActivityRepository extends JpaRepository<MerchantUserActivity, Long> {
	
	@Query(value = "SELECT * FROM MERCHANT_USER_ACTIVITY mua WHERE mua.MERCHANT_ID=?1 and ((mua.DATE>=?2) and (mua.DATE<=?3)) and mua.TYPE='MERCHANT'", nativeQuery = true)
	Page<MerchantUserActivity> findActivityByMerchantIdAndDateRange(long merchantId, String fromDate, String toDate,
			Pageable pageable);

	@Query(value = "SELECT * FROM MERCHANT_USER_ACTIVITY mua WHERE mua.MERCHANT_USER_ID=?1 and ((mua.DATE>=?2) and (mua.DATE<=?3)) and mua.TYPE='MERCHANT'", nativeQuery = true)
	Page<MerchantUserActivity> findActivityByUserIdAndDateRange(long merchantUserId, String fromDate, String toDate,
			Pageable pageable);
	
}