package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.BBPSMerchantSettlement;

@Repository
public interface BBPSMerchantSettlementRepository extends JpaRepository<BBPSMerchantSettlement, Long> {

}
