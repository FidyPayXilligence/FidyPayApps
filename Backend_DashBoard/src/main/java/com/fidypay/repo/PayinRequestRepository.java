package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PayinRequest;
@Repository
public interface PayinRequestRepository extends JpaRepository<PayinRequest, Long> {

	boolean existsByMerchantTransactionRefId(String merchantTransactionRefId);

	
	@Query(value = "Select pr.BANK_REQUEST,pr.USER_REQUEST from PAYIN_REQUEST pr where pr.MERCHANT_TRANSACTION_REF_ID=?1", nativeQuery = true)
	List<?> findByTrxnId(String trxnId);
	
	
	boolean existsByBankRequest(String bankRequest);




	

	
}
