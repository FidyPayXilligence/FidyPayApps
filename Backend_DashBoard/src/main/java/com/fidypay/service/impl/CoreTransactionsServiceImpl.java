package com.fidypay.service.impl;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.repo.CoreTransactionsRepository;
import com.fidypay.repo.PayinTransactionDetailRepository;
import com.fidypay.service.CoreTransactionsService;

@Service
public class CoreTransactionsServiceImpl implements CoreTransactionsService {
	
	@Autowired
	private PayinTransactionDetailRepository payinTransactionDetailRepository;

	@Override
	public List<?> getAllYearWiseData(Long merchantId, String year) {
		List<?> allYearLst = null;
		try {

			allYearLst = payinTransactionDetailRepository.getAllYearWiseData(merchantId, year);
			Iterator<?> it = allYearLst.iterator();
			while (it.hasNext()) {
				double amount = 0;
				Object[] tuple = (Object[]) it.next();

				String month = String.valueOf(tuple[0]);

				List<?> t = payinTransactionDetailRepository.getAllYearWiseData2(merchantId, year, month);
				Iterator<?> ite = t.iterator();
				while (ite.hasNext()) {
					Object[] tuple2 = (Object[]) ite.next();
					//amount = amount + Encryption.decFloat(((Double) tuple2[1]).doubleValue());
					amount = amount + ((Double) tuple2[1]).doubleValue();
				}
				tuple[1] = amount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allYearLst;

	}

	@Override
	public List<?> getWeekDataByYearAndMonth(Long merchantId, String year, String month) {
		List<?> monthLst = null;

		try {

			monthLst = payinTransactionDetailRepository.getWeekDataByYearAndMonth(merchantId, year, month);

			Iterator<?> it = monthLst.iterator();
			while (it.hasNext()) {
				double amount = 0;
				Object[] tuple = (Object[]) it.next();

				String week = String.valueOf(tuple[0]);

				List<?> t = payinTransactionDetailRepository.getWeekDataByYearAndMonth2(merchantId, year, month, week);

				Iterator<?> ite = t.iterator();
				while (ite.hasNext()) {
					Object[] tuple2 = (Object[]) ite.next();
				//	amount = amount + Encryption.decFloat(((double) tuple2[1]));
					amount = amount + ((double) tuple2[1]);
				}
				tuple[1] = Double.valueOf(amount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return monthLst;
	}

	@Override
	public List<?> getDayDataByYearAndMonth(Long merchantId, String year, String month) {
		List<?> dayLst = null;
		try {

			dayLst = payinTransactionDetailRepository.getDayDataByYearAndMonth(merchantId, year, month);

			Iterator<?> it = dayLst.iterator();
			while (it.hasNext()) {
				double amount = 0;
				Object[] tuple = (Object[]) it.next();
				String day = String.valueOf(tuple[0]);

				List<?> t = payinTransactionDetailRepository.getDayDataByYearAndMonth2(merchantId, year, month, day);

				Iterator<?> ite = t.iterator();
				while (ite.hasNext()) {
					Object[] tuple2 = (Object[]) ite.next();
					amount = amount + (double) tuple2[1];
				}
				tuple[1] = Double.valueOf(amount);
			}
			return dayLst;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayLst;
	}

	@Override
	public List<?> showTransactionsByPiChart(Long merchantId, String year, String month) {
		List<?> monthLst = null;

		try {

			monthLst = payinTransactionDetailRepository.getPiChartWeekDataByYearAndMonth(merchantId, year, month);

			Iterator<?> it = monthLst.iterator();
			while (it.hasNext()) {
				double amount = 0;
				String serviceName = "";
				BigInteger transactions;
				Object[] tuple = (Object[]) it.next();

				// String month=String.valueOf(tuple[0]);

				amount = Encryption.decFloat(((double) tuple[1]));
				serviceName = String.valueOf(tuple[2]);
				serviceName = Encryption.decString(serviceName);
				transactions = (BigInteger) tuple[3];

				tuple[1] = Double.valueOf(amount);
				tuple[2] = String.valueOf(serviceName);
				tuple[3] = transactions;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return monthLst;
	}

	@Override
	public List<?> showTransactionAmountByPiChart(Long merchantId, String year, String month) {
		List<?> monthLst = null;

		try {

			monthLst = payinTransactionDetailRepository.getTrxnAmountPiChartWeekDataByYearAndMonth(merchantId, year, month);

			Iterator<?> it = monthLst.iterator();
			while (it.hasNext()) {
				double amount = 0;
				String serviceName = "";
				BigInteger transactions;
				Object[] tuple = (Object[]) it.next();

				// amount=Encryption.decFloat(((double) tuple[0]));

				serviceName = String.valueOf(tuple[1]);
				serviceName = Encryption.decString(serviceName);
				transactions = (BigInteger) tuple[2];
				List<Double> amountList = payinTransactionDetailRepository.getTrxnAmountPiChartWeekDataByYearAndMonth2(
						merchantId, year, month, serviceName);

				for (Double d : amountList) {
					//amount = amount + Encryption.decFloat(d);
					amount = amount + d;

				}
				tuple[0] = Double.valueOf(amount);
				tuple[1] = String.valueOf(serviceName);
				tuple[2] = transactions;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return monthLst;
	}

}
