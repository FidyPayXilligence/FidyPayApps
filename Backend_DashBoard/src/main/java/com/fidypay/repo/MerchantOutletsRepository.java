package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantOutlets;

@Repository
public interface MerchantOutletsRepository extends JpaRepository<MerchantOutlets, Long> {

	
	@Query(value = "SELECT MAX(OUTLET_CODE) FROM MERCHANT_OUTLETS", nativeQuery = true)
	List<String> findByMaxOutletCode();

}
