package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.TransactionStatus;

@Repository
public interface TransactionStatusRepository extends JpaRepository<TransactionStatus, Long> {
	
}
