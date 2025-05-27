package com.fidypay.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantWalletTransactions;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.request.PassbookRequest;
import com.fidypay.response.PassbookPayload;
import com.fidypay.service.PassbookService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class PassbookServiceImpl implements PassbookService {

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Override
	public Map<String, Object> passBookTransactionList(PassbookRequest passbookRequest, long merchantId)
			throws Exception, ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Pageable paging = PageRequest.of(passbookRequest.getPageNo(), passbookRequest.getPageSize(),
					Sort.by("MERCHANT_WALLET_TRXN_ID").descending());

			String startDate = passbookRequest.getStartDate();
			String endDate = passbookRequest.getEndDate();
			startDate = startDate + " 00.00.00.0";
			endDate = endDate + " 23.59.59.9";

			AtomicInteger atomicInteger = new AtomicInteger(1);
			List<PassbookPayload> activityList = new ArrayList<PassbookPayload>();
			List<MerchantWalletTransactions> list = new ArrayList<MerchantWalletTransactions>();
			Page<MerchantWalletTransactions> listTransaction = merchantWalletTransactionsRepository
					.findByMerchantIdAndDate(merchantId, startDate, endDate, paging);

			list = listTransaction.getContent();
			if (list.size() != 0) {
				list.forEach(objects -> {

					PassbookPayload payLoad = new PassbookPayload();
					String walletType = objects.getWalletTrxnType();
					double walletNewBalance = objects.getMercWalletNewBalance();
					double walletPreviousBalance = objects.getMercWalletPreviousBalance();
					String merchantTrxnRefId = objects.getMerchantTrxnRefId();
					String serviceName = objects.getServiceName();
					String walletTrxnRefId = objects.getWalletTxnRefNo();

					if (walletType != null && walletType.equals("Debit")) {

						double a = Encryption.decFloat(walletNewBalance);
						double b = Encryption.decFloat(walletPreviousBalance);
						double c = b - a;

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						amount1.setMinimumIntegerDigits(1);
						String s = amount1.format(c);
						payLoad.setDebitAmount(s);
						payLoad.setCreditAmount("-");

					}

					if (walletType != null && walletType.equals("Credit")) {

						double a = Encryption.decFloat(walletNewBalance);
						double b = Encryption.decFloat(walletPreviousBalance);
						double c = a - b;

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						amount1.setMinimumIntegerDigits(1);
						String s = amount1.format(c);
						payLoad.setCreditAmount(s);
						payLoad.setDebitAmount("-");

					}

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					amount1.setMinimumIntegerDigits(1);
					String mAmount = amount1.format(walletNewBalance);
					String date = "NA";
					try {
						date = DateAndTime.dateFormatReports(objects.getWalletTxnDate().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					payLoad.setsNo(atomicInteger.getAndIncrement());
					payLoad.setTransactionDate(date);
					payLoad.setTrxnRefId(objects.getTrxnRefId());
					payLoad.setMerchantTrxnRefId(merchantTrxnRefId);
					payLoad.setAmount(mAmount);
					payLoad.setServiceName(serviceName);
					payLoad.setWalletTrxnRefId(walletTrxnRefId);
					activityList.add(payLoad);

				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Passbook Transaction List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("callBackList", activityList);
				map.put("currentPage", listTransaction.getNumber());
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Transaction not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public List<PassbookPayload> passBookTransactionListExcel(long merchantId, String startDate, String endDate) {
		List<PassbookPayload> activityList = new ArrayList<PassbookPayload>();
		try {
			startDate = startDate + " 00.00.00.0";
			endDate = endDate + " 23.59.59.9";

			List<MerchantWalletTransactions> list = merchantWalletTransactionsRepository
					.findByMerchantIdAndDate(merchantId, startDate, endDate);

			AtomicInteger atomicInteger = new AtomicInteger(1);

			if (list.size() != 0) {
				list.forEach(objects -> {

					PassbookPayload payLoad = new PassbookPayload();

					String walletType = objects.getWalletTrxnType();
					double walletNewBalance = objects.getMercWalletNewBalance();
					double walletPreviousBalance = objects.getMercWalletPreviousBalance();
					String merchantTrxnRefId = objects.getMerchantTrxnRefId();
					String serviceName = objects.getServiceName();
					String walletTrxnRefId = objects.getWalletTxnRefNo();
					
					if (walletType != null && walletType.equals("Debit")) {

						double a = Encryption.decFloat(walletNewBalance);
						double b = Encryption.decFloat(walletPreviousBalance);
						double c = b - a;

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						amount1.setMinimumIntegerDigits(1);
						String s = amount1.format(c);
						payLoad.setDebitAmount(s);
						payLoad.setCreditAmount("-");

					}

					if (walletType != null && walletType.equals("Credit")) {

						double a = Encryption.decFloat(walletNewBalance);
						double b = Encryption.decFloat(walletPreviousBalance);
						double c = a - b;

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						amount1.setMinimumIntegerDigits(1);
						String s = amount1.format(c);
						payLoad.setCreditAmount(s);
						payLoad.setDebitAmount("-");

					}

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					amount1.setMinimumIntegerDigits(1);
					String mAmount = amount1.format(walletNewBalance);

					String date = "NA";
					try {
						date = DateAndTime.dateFormatReports(objects.getWalletTxnDate().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					payLoad.setsNo(atomicInteger.getAndIncrement());
					payLoad.setTransactionDate(date);
					payLoad.setTrxnRefId(objects.getTrxnRefId());
					payLoad.setMerchantTrxnRefId(merchantTrxnRefId);
					payLoad.setAmount(mAmount);
					payLoad.setServiceName(serviceName);
                    payLoad.setWalletTrxnRefId(walletTrxnRefId);
					
					activityList.add(payLoad);

				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return activityList;
	}

}
