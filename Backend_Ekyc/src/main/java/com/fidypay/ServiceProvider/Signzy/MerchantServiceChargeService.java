package com.fidypay.ServiceProvider.Signzy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.MerchantServiceCharges;
import com.fidypay.entity.MerchantServiceCommission;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantServiceChargesRepository;
import com.fidypay.repo.MerchantServiceCommissionRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.ServiceInfoRepository;

@Service
public class MerchantServiceChargeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantServiceChargeService.class);

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargesRepository merchantServiceChargesRepository;
	
	@Autowired
	private MerchantServiceCommissionRepository merchantServiceCommissionRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	public double getMerchantServiceCharges(Long merchantId, String category, double amount) {
		double charges = 0.0;
		try {
			Long merchantServiceId = null;

			String serviceName = Encryption.encString(category);
			LOGGER.info("Service " + serviceName);

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(serviceName);
			Long serviceId = serviceInfo.getServiceId();

			LOGGER.info("serviceId: " + serviceId);

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			merchantServiceId = merchantsService.getMerchantServiceId();

			LOGGER.info("merchantServiceId: " + merchantServiceId);

			List<MerchantServiceCharges> list = merchantServiceChargesRepository
					.findByMerchantServiceId(merchantServiceId);

			for (MerchantServiceCharges merchantServiceCharges : list) {

				String type = Encryption.decString(merchantServiceCharges.getMerchantServiceChargeType());

				String start = String.valueOf(merchantServiceCharges.getMerchantServiceChargeStart());
				String end = String.valueOf(merchantServiceCharges.getMerchantServiceChargeEnd());

				double tStart = Double.valueOf(start);
				double tEnd = Double.valueOf(end);

				double rate = merchantServiceCharges.getMerchantServiceChargeRate();

				LOGGER.info("tStart: " + tStart);

				LOGGER.info("tEnd: " + tEnd);

				LOGGER.info("rate: " + rate);

				switch (type) {

				case "Percentage":

					if ((amount >= tStart || amount <= tEnd)) {

						double fpchaerge = amount * rate / 100;

						double tax = fpchaerge * 18 / 100;

						double cAmount = fpchaerge + tax;

						charges = cAmount;
					}
					else {
						charges=0.0;	
					}

					break;

				case "Flat":

					if ((amount >= tStart || amount <= tEnd)) {
						double fpchaerge = 1 * rate;
						double tax = fpchaerge * 18 / 100;
						double cAmount = fpchaerge + tax;

						charges = cAmount;
					}
					else {
						charges=0.0;	
					}
					break;
				}
			}

		} catch (Exception e) {
			charges = 0d;
		}
		return charges;
	}

	
	public double getMerchantServiceChargesV2(Long merchantServiceId, double amount) {
		double charges = 0.0;
		try {

			List<MerchantServiceCharges> list = merchantServiceChargesRepository
					.findByMerchantServiceId(merchantServiceId);
			
			 if (list == null || list.isEmpty()) {
		            throw new RuntimeException("No records found for merchantServiceId: " + merchantServiceId);
		        }

			for (MerchantServiceCharges merchantServiceCharges : list) {

				String type = Encryption.decString(merchantServiceCharges.getMerchantServiceChargeType());

				String start = String.valueOf(merchantServiceCharges.getMerchantServiceChargeStart());
				String end = String.valueOf(merchantServiceCharges.getMerchantServiceChargeEnd());

				double tStart = Double.parseDouble(start);
				double tEnd = Double.parseDouble(end);

				double rate = merchantServiceCharges.getMerchantServiceChargeRate();

				amount=rate;
				
				LOGGER.info("tStart: {}" , tStart);

				LOGGER.info("tEnd: {}" , tEnd);

				LOGGER.info("rate: {}" , rate);

				switch (type) {

				case "Percentage":

					if (amount >= tStart && amount <= tEnd) {

			        	double fpchaerge = amount * rate / 100;

						double tax = fpchaerge * 18 / 100;

						double cAmount = fpchaerge + tax;

						charges = cAmount;
						LOGGER.info("Case1 charges: " + charges);
						return charges;
					}
					else {
						charges=amount;	
						LOGGER.info("Case2 charges: " + charges);
					}

					break;

				case "Flat":

					if (amount >= tStart && amount <= tEnd) {
						double fpchaerge = 1 * rate;
						double tax = fpchaerge * 18 / 100;
						double cAmount = fpchaerge + tax;

						charges = cAmount;
						LOGGER.info("Case1 Flat: " + charges);
						return charges;
					}
					else {
						charges=amount;	
						LOGGER.info("Case2 Flat: " + charges);
					}
					break;
				}
			}

		} catch (Exception e) {
			charges = amount;
		}
		return charges;
	}
	
	
	public double getMerchantServiceCommissionV2(Long merchantServiceId, double amount) {
		double charges = 0.0;
		try {

			List<MerchantServiceCommission> list = merchantServiceCommissionRepository
					.findByMerchantServiceIdForCommission(merchantServiceId);

			for (MerchantServiceCommission merchantServiceCommission : list) {

				String type = Encryption.decString(merchantServiceCommission.getMerchantServiceCommissionType());

				String start = String.valueOf(merchantServiceCommission.getMerchantServiceCommissionStart());
				String end = String.valueOf(merchantServiceCommission.getMerchantServiceCommissionEnd());

				double tStart = Double.valueOf(start);
				double tEnd = Double.valueOf(end);

				double rate = merchantServiceCommission.getMerchantServiceCommissionRate();

				LOGGER.info("tStart: " + tStart);

				LOGGER.info("tEnd: " + tEnd);

				LOGGER.info("rate: " + rate);

				switch (type) {

				case "Percentage":

					if (amount >= tStart && amount <= tEnd) {

						double fpchaerge = amount * rate / 100;

						double tax = fpchaerge * 18 / 100;

						double cAmount = fpchaerge + tax;

						charges = cAmount;
					}
					else {
						charges=0.0;	
					}

					break;

				case "Flat":

					if (amount >= tStart && amount <= tEnd) {
						double fpchaerge = 1 * rate;
						double tax = fpchaerge * 18 / 100;
						double cAmount = fpchaerge + tax;

						charges = cAmount;
					}
					else {
						charges=0.0;	
					}
					break;
				}
			}

		} catch (Exception e) {
			charges = 0d;
		}
		return charges;
	}
	
	public static void main(String args[]) {

		double res = new MerchantServiceChargeService().getMerchantServiceCharges(1L, "MOBILE POSTPAID", 2);

		System.out.println("res: " + res);

	}

}
