package com.fidypay.wallet;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.entity.CoreTransactions;
import com.fidypay.entity.MerchantWalletTransactions;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class WalletServiceHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceHelper.class);
	
	@Autowired
	private MerchantsRepository merchantRepository;

	@Autowired
	private MerchantWalletTransactionsRepository walletTransactionRepository;
	
	@Transactional
	public void processSingleTransaction(WalletRequest request)
			throws ParseException, IOException, InterruptedException {

		LOGGER.info("--------------Start processSingleTransaction----------------");

		LOGGER.info("Merchant Id : " + request.getMerchantId());
		Merchants merchant = merchantRepository.findByIdForUpdate(request.getMerchantId())
				.orElseThrow(() -> new RuntimeException("Merchant not found"));

		double previousBalance = merchant.getMerchantFloatAmount();
		double newBalance;

		if ("Debit".equalsIgnoreCase(request.getTransactionType())) {
			if (previousBalance < request.getAmount()) {
				throw new RuntimeException("Insufficient balance");
			}
			newBalance = previousBalance - request.getAmount();
		} else if ("Credit".equalsIgnoreCase(request.getTransactionType())) {
			newBalance = previousBalance + request.getAmount();
		} else {
			throw new RuntimeException("Invalid transaction type");
		}

		MerchantWalletTransactions transaction = new MerchantWalletTransactions();
		transaction.setMerchants(merchant);
		transaction.setCoreTransactions(new CoreTransactions(request.getCoreTransactionId()));
		transaction.setIsReverted(request.getTransactionType().charAt(0));
		transaction.setMercWalletPreviousBalance(previousBalance);
		transaction.setWalletTrxnType(request.getTransactionType());
		transaction.setMercWalletNewBalance(newBalance);
		transaction.setDescription(request.getDescription());
		transaction.setWalletTxnRefNo(request.getWalletTxnRefNo());
		transaction.setWalletTxnDate(Timestamp.valueOf(DateAndTime.getCurrentTimeInIST()));
		transaction.setServiceName(request.getServiceName());
		transaction.setTrxnRefId(request.getTransactionRefId());
		transaction.setMerchantTrxnRefId(request.getMerchantTransactionRefId());

		walletTransactionRepository.save(transaction);

		merchant.setMerchantFloatAmount(newBalance);
		merchantRepository.save(merchant);

		LOGGER.info("--------------End processSingleTransaction----------------");
	}
	
}
