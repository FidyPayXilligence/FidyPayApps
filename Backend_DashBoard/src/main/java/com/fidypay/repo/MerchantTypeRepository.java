package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantType;

@Repository
public interface MerchantTypeRepository extends JpaRepository<MerchantType, Long> {
	
	@Query(value = "SELECT * FROM MERCHANT_TYPE mt WHERE mt.MERCHANT_TYPE_ID=?1", nativeQuery = true)
	MerchantType findByMerchantTypeId(String merchantTypeId);

}
