//package com.fidypay.wallet;
//
//import java.sql.Timestamp;
//import java.text.ParseException;
//
//import org.springframework.stereotype.Service;
//
//import com.fidypay.entity.CoreTransactions;
//import com.fidypay.entity.MerchantWalletTransactions;
//import com.fidypay.entity.Merchants;
//import com.fidypay.repo.MerchantWalletTransactionsRepository;
//import com.fidypay.repo.MerchantsRepository;
//import com.fidypay.utils.ex.DateAndTime;
//
//@Service
//public class Bank {
//
//	
//	static synchronized String withdrawn(String merchantTrxnRefId, double withdrawal, double total, long merchantId,
//			MerchantsRepository merchantsRepository,
//			MerchantWalletTransactionsRepository merchantWalletTransactionsRepository, Long transactionId,
//			String serviceName, String trxnRefId, String walletTxnRefNo,String description) throws ParseException {
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		Merchants merchants = merchantsRepository.findById(merchantId).get();
//		double merchantWallet = merchants.getMerchantFloatAmount();
//		if (merchantWallet >= withdrawal) {
//
//			System.out.println(merchantTrxnRefId + " withdrawn " + withdrawal);
//			double merchantWalletNew = merchantWallet - withdrawal;
//			System.out.println("Balance after withdrawal: " + merchantWalletNew);
//
//			System.out.println("Merchant Id: " + merchantId);
//
//			merchants.setMerchantFloatAmount(merchantWalletNew);
//			merchants = merchantsRepository.save(merchants);
//
//			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
//
//			MerchantWalletTransactions merchantWalletTransactions = new MerchantWalletTransactions();
//			merchantWalletTransactions.setCoreTransactions(new CoreTransactions(transactionId));
//			merchantWalletTransactions.setMerchants(new Merchants(merchantId));
//			merchantWalletTransactions.setDescription(description);
//			merchantWalletTransactions.setMercWalletNewBalance(merchantWalletNew);
//			merchantWalletTransactions.setMercWalletPreviousBalance(merchantWallet);
//			merchantWalletTransactions.setMerchantTrxnRefId(merchantTrxnRefId);
//			merchantWalletTransactions.setWalletTxnDate(trxnDate);
//			merchantWalletTransactions.setIsReverted('D');
//			merchantWalletTransactions.setWalletTrxnType("Debit");
//			merchantWalletTransactions.setServiceName(serviceName);
//			merchantWalletTransactions.setWalletTxnRefNo(walletTxnRefNo);
//			merchantWalletTransactions.setTrxnRefId(trxnRefId);
//
//			merchantWalletTransactionsRepository.save(merchantWalletTransactions);
//
//		
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return "withdrawn";
//		}
//
//		
//		else {
//			System.out.println(merchantTrxnRefId + " you can not withdraw " + withdrawal);
//			System.out.println("your balance is: " + total);
//
//			
//			try {
//
//				
//				Thread.sleep(100);
//			}
//
//			catch (InterruptedException e) {
//
//			
//				e.printStackTrace();
//			}
//			return "Not withdrawn";
//		}
//	}
//
//	
//	static synchronized void deposit(String merchantTrxnRefId, double deposit, long merchantId,
//			MerchantsRepository merchantsRepository,
//			MerchantWalletTransactionsRepository merchantWalletTransactionsRepository, long transactionId,
//			String serviceName, String trxnRefId, String walletTxnRefNo, String description) throws ParseException {
//
//		try {
//			Thread.sleep(50);
//		}
//
//		
//		catch (InterruptedException e) {
//
//			e.printStackTrace();
//		}
//		Merchants merchants = merchantsRepository.findById(merchantId).get();
//		double merchantWallet = merchants.getMerchantFloatAmount();
//
//		System.out.println(merchantTrxnRefId + " deposited " + deposit);
//
//		double merchantWalletNew = merchantWallet + deposit;
//		System.out.println("Balance after deposit: " + merchantWalletNew);
//
//		merchants.setMerchantFloatAmount(merchantWalletNew);
//		merchants = merchantsRepository.save(merchants);
//
//		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
//
//		MerchantWalletTransactions merchantWalletTransactions = new MerchantWalletTransactions();
//		merchantWalletTransactions.setCoreTransactions(new CoreTransactions(transactionId));
//		merchantWalletTransactions.setMerchants(new Merchants(merchantId));
//		merchantWalletTransactions.setDescription(description);
//		merchantWalletTransactions.setMercWalletNewBalance(merchantWalletNew);
//		merchantWalletTransactions.setMercWalletPreviousBalance(merchantWallet);
//		merchantWalletTransactions.setMerchantTrxnRefId(merchantTrxnRefId);
//		merchantWalletTransactions.setWalletTxnDate(trxnDate);
//		merchantWalletTransactions.setIsReverted('C');
//		merchantWalletTransactions.setWalletTrxnType("Credit");
//		merchantWalletTransactions.setServiceName(serviceName);
//		merchantWalletTransactions.setWalletTxnRefNo(walletTxnRefNo);
//		merchantWalletTransactions.setTrxnRefId(trxnRefId);
//
//		merchantWalletTransactionsRepository.save(merchantWalletTransactions);
//
//		try {
//
//			Thread.sleep(50);
//		}
//
//		catch (InterruptedException e) {
//
//			e.printStackTrace();
//		}
//	}
//}
