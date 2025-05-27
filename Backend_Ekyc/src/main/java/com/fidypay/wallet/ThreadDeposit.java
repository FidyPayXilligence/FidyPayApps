//package com.fidypay.wallet;
//
//import java.text.ParseException;
//
//import com.fidypay.repo.MerchantWalletTransactionsRepository;
//import com.fidypay.repo.MerchantsRepository;
//
//public class ThreadDeposit extends Thread {
//
//	Bank object;
//
//	double amount;
//	long merchantId;
//	MerchantsRepository merchantsRepository;
//	MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;
//	long transactionId;
//	String merchantTrxnRefId;
//	String serviceName;
//	String trxnRefId;
//	String walletTxnRefNo;
//	String description;
//
//	public ThreadDeposit(Bank ob, String merchantTrxnRefId2, double money, long merchantIdd,
//			MerchantsRepository merchantsRepository2,
//			MerchantWalletTransactionsRepository merchantWalletTransactionsRepository2, long transactionIdd,
//			String serviceName2, String trxnRefId2, String walletTxnRefNo2, String description2) {
//		this.object = ob;
//		this.merchantTrxnRefId = merchantTrxnRefId2;
//		this.amount = money;
//		this.merchantId = merchantIdd;
//		this.merchantsRepository = merchantsRepository2;
//		this.merchantWalletTransactionsRepository = merchantWalletTransactionsRepository2;
//		this.transactionId = transactionIdd;
//		this.serviceName = serviceName2;
//		this.trxnRefId = trxnRefId2;
//		this.walletTxnRefNo = walletTxnRefNo2;
//		this.description = description2;
//	}
//
//	public void run() {
//		try {
//			object.deposit(merchantTrxnRefId, amount, merchantId, merchantsRepository,
//					merchantWalletTransactionsRepository, transactionId, serviceName, trxnRefId, walletTxnRefNo,
//					description);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	}
//}
