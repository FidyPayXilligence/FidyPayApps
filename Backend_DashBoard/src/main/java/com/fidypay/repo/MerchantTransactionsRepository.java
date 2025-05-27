	package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantServices;
import com.fidypay.entity.MerchantTransactions;

@Repository
public interface MerchantTransactionsRepository extends JpaRepository<MerchantTransactions, Long> {

	@Query(value = "UPDATE MERCHANT_TRANSACTIONS SET mt.MERCHANT_OUTLET_TILL_ID =null WHERE mt.MERCHANT_TRXN_ID =?1 AND mt.TRANSACTION_ID=?2", nativeQuery = true)
	void updateMerOutTillId(long merchantTrxnId, long transactionId,String country);
	
	
	@Query(value = "select mt.MERCHANT_ID,mot.MERCHANT_OUTLET_ID from MERCHANT_TILLS mt inner join MERCHANT_OUTLET_TILLS mot on mt.MERCHANT_TILL_ID=mot.MERCHANT_TILL_ID where mt.TILL_CODE==?1", nativeQuery = true)
	MerchantServices findServiceCommision(String country, String tillCode, long serviceId);

	@Query(value = "select slab.FLAT_COMM from SERVICE_FLAT_COMM slab where slab.SERVICE_ID=?1 and slab.MIN_RANGE <=?2 and slab.MAX_RANGE >=amount=?2", nativeQuery = true)
	double fatchServiceSlab(String country, long serviceId, double amount);
	
	@Query(value = "update MERCHANT_TRANSACTIONS mt set mt.MERCHANT_COMISSION=?1  where mt.TRANSACTION_ID=?2", nativeQuery = true)
	void updateMerchantCommision(String country, long coreTxnId, double commission);


	@Query(value = "select * FROM MERCHANT_TRANSACTIONS mt where mt.TRANSACTION_ID=?1", nativeQuery = true)
	MerchantTransactions findByTrnxId(long transactionId);

	@Query(value = "select * FROM MERCHANT_TRANSACTIONS mt where mt.MERCHANT_ID=?1", nativeQuery = true)
	MerchantTransactions findByMerchantId(long merchantId);
}
