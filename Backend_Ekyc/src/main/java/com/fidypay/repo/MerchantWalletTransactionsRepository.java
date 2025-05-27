package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantWalletTransactions;

@Repository
public interface MerchantWalletTransactionsRepository extends JpaRepository<MerchantWalletTransactions, Long> {

}
