package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantOutletTills;

@Repository
public interface MerchantOutletTillsRepository extends JpaRepository<MerchantOutletTills, Long> {

	
	
}
