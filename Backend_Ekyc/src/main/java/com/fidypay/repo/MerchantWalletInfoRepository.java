package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantWalletInfo;

@Repository
public interface MerchantWalletInfoRepository extends JpaRepository<MerchantWalletInfo, Long> {

}
