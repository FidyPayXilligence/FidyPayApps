package com.fidypay.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantUserPermission;

@Repository
public interface MerchantUserPermissionRepository extends JpaRepository<MerchantUserPermission, Long> {
	boolean existsByMerchantUserId(long merchantUserId);

	MerchantUserPermission findByMerchantUserId(long merchantUserId);
	
	
	@Query(value = "SELECT * FROM MERCHANT_USER_PERMISSION mup where mup.MERCHANT_ID=?1", nativeQuery = true)
	Page<MerchantUserPermission> findByMerchantIdAndPaging(long merchantId, Pageable paging);
}
