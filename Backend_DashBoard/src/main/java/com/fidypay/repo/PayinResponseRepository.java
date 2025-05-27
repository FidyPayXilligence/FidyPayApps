package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PayinResponse;
@Repository
public interface PayinResponseRepository extends JpaRepository<PayinResponse, Long> {
	
	List<PayinResponse> findByMerchantTransactionRefId(String trxn_id);


}
