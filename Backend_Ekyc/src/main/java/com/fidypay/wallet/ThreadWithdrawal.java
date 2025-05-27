//package com.fidypay.wallet;
//
//import java.text.ParseException;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//import com.fidypay.repo.MerchantWalletTransactionsRepository;
//import com.fidypay.repo.MerchantsRepository;
//
//public class ThreadWithdrawal extends Thread implements Runnable {
//
//	Bank object;
//	double amount;
//	double walletBalance;
//	long merchantId;
//	MerchantsRepository merchantsRepository;
//	MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;
//	long transactionId;
//	String merchantTrxnRefId;
//	String serviceName;
//	String trxnRefId;
//	String walletTxnRefNo;
//	String description;
//	private static final Lock lock = new ReentrantLock();
//
//	public ThreadWithdrawal(Bank ob, String merchantTrxnRefId, double money, double balance, long merchantIdd,
//			MerchantsRepository merchantsRepository2,
//			MerchantWalletTransactionsRepository merchantWalletTransactionsRepository2, long transactionIdd,
//			String serviceName2, String trxnRefId2, String walletTxnRefNo2,String description2) {
//		this.object = ob;
//		this.merchantTrxnRefId = merchantTrxnRefId;
//		this.amount = money;
//		this.walletBalance = balance;
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
//		synchronized (ThreadWithdrawal.class) {
//			lock.lock();
//			try {
//                try {
//                    object.withdrawn(merchantTrxnRefId, amount, walletBalance, merchantId, merchantsRepository,
//                            merchantWalletTransactionsRepository, transactionId, serviceName, trxnRefId, walletTxnRefNo, description);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            } finally {
//				lock.unlock();
//			}
//		}
//	}
//}
