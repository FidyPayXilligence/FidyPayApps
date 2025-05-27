package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantWalletTransactions;

@Repository
public interface MerchantWalletTransactionsRepository extends JpaRepository<MerchantWalletTransactions, Long> {

	@Query(value = "SELECT mwt.WALLET_TXN_DATE,mwt.WALLET_TXN_REF_NO,mwt.WALLET_TRXN_TYPE,mwt.MERC_WALLET_NEW_BALANCE,mwt.MERC_WALLET_PREVIOUS_BALANCE,mt.MERCHANT_TRXN_REF_ID,si.SERVICE_NAME FROM CORE_TRANSACTIONS ct INNER JOIN MERCHANT_TRANSACTIONS mt ON mt.TRANSACTION_ID = ct.TRANSACTION_ID INNER JOIN MERCHANT_WALLET_TRANSACTIONS mwt ON ct.TRANSACTION_ID=mwt.TRANSACTION_ID INNER JOIN SERVICE_INFO si ON si.SERVICE_ID=ct.SERVICE_ID WHERE  mwt.MERCHANT_ID=?1  AND ((mwt.WALLET_TXN_DATE>=?2) and (mwt.WALLET_TXN_DATE<=?3)) ORDER BY mwt.WALLET_TXN_DATE DESC", nativeQuery = true)
	List<?> findByMerchantIdAndStartDateToEndDate(Long merchantId, String startDate,
			String endDate);

	@Query(value = "SELECT mwt.WALLET_TXN_DATE,mwt.WALLET_TXN_REF_NO,mwt.WALLET_TRXN_TYPE,mwt.MERC_WALLET_NEW_BALANCE,mwt.MERC_WALLET_PREVIOUS_BALANCE,mt.MERCHANT_TRXN_REF_ID,si.SERVICE_NAME FROM CORE_TRANSACTIONS ct INNER JOIN MERCHANT_TRANSACTIONS mt ON mt.TRANSACTION_ID = ct.TRANSACTION_ID INNER JOIN MERCHANT_WALLET_TRANSACTIONS mwt ON ct.TRANSACTION_ID=mwt.TRANSACTION_ID  INNER JOIN SERVICE_INFO si ON si.SERVICE_ID=ct.SERVICE_ID  WHERE mwt.MERCHANT_ID=?1 AND ((mwt.WALLET_TXN_DATE>=?2) and (mwt.WALLET_TXN_DATE<=?3))", nativeQuery = true)
	List<?> findByMerchantIdAndStartDateToEndDate(Long merchantId, String startDate,
			String endDate, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM CORE_TRANSACTIONS ct INNER JOIN MERCHANT_TRANSACTIONS mt ON mt.TRANSACTION_ID = ct.TRANSACTION_ID INNER JOIN MERCHANT_WALLET_TRANSACTIONS mwt ON ct.TRANSACTION_ID=mwt.TRANSACTION_ID  INNER JOIN SERVICE_INFO si ON si.SERVICE_ID=ct.SERVICE_ID  WHERE  mwt.MERCHANT_ID=?1  AND ((mwt.WALLET_TXN_DATE>=?2) and (mwt.WALLET_TXN_DATE<=?3))", nativeQuery = true)
	int countByMerchantIdAndStartDateToEndDate(Long merchantId, String startDate, String endDate);

	
	@Query(value = "SELECT * FROM MERCHANT_WALLET_TRANSACTIONS mwt WHERE mwt.MERCHANT_ID=?1 AND ((mwt.WALLET_TXN_DATE>=?2) and (mwt.WALLET_TXN_DATE<=?3)) ORDER BY mwt.MERCHANT_WALLET_TRXN_ID DESC", nativeQuery = true)
	Page<MerchantWalletTransactions> findByMerchantIdAndDate(long merchantId, String startDate, String endDate,
	Pageable paging);

	@Query(value = "SELECT * FROM MERCHANT_WALLET_TRANSACTIONS mwt WHERE mwt.MERCHANT_ID=?1 AND ((mwt.WALLET_TXN_DATE>=?2) and (mwt.WALLET_TXN_DATE<=?3)) ORDER BY mwt.MERCHANT_WALLET_TRXN_ID DESC", nativeQuery = true)
	List<MerchantWalletTransactions> findByMerchantIdAndDate(long merchantId, String startDate, String endDate);
}
