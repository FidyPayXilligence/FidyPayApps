package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.entity.MerchantService;
import com.fidypay.entity.MerchantServiceCharges;
import com.fidypay.entity.MerchantServiceCommission;
import com.fidypay.entity.PartnerServiceCharges;
import com.fidypay.entity.PartnerServiceCommission;
import com.fidypay.repo.MerchantServiceChargesRepository;
import com.fidypay.repo.MerchantServiceCommissionRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.PartnerServiceChargesRepository;
import com.fidypay.repo.PartnerServiceCommissionRepository;
import com.fidypay.service.MerchantsServiceService;
import com.fidypay.utils.ex.DateUtil;

@Service
public class MerchantsServiceServiceImpl implements MerchantsServiceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantsServiceImpl.class);

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private PartnerServiceChargesRepository partnerServiceChargesRepository;

	@Autowired
	private PartnerServiceCommissionRepository partnerServiceCommissionRepository;

	@Autowired
	private MerchantServiceChargesRepository merchantServiceChargesRepository;

	@Autowired
	private MerchantServiceCommissionRepository merchantServiceCommissionRepository;

	@Override
	public MerchantService assignMerchantService(long partnerServiceId, double amc, double otc, long merchantId,
			long serrviceProviderId, long serviceId, double subscriptionAmount, String subscriptionCycle,
			String serviceType) throws ParseException {
		Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());

		MerchantService merchantServicefind = merchantServiceRepository
				.findByMerchantIdAndServiceIdAndServiceProviderId(merchantId, serviceId, serrviceProviderId);

		MerchantService merchantService = new MerchantService();
		if (merchantServicefind == null) {
			merchantService.setAmc(amc);
			merchantService.setIsMerchantServiceActive('Y');
			merchantService.setMerchantId(merchantId);
			merchantService.setMerchantServiceCreationDate(trxnDate);
			merchantService.setOtc(otc);
			merchantService.setRemark("Service Assign");
			merchantService.setSerrviceProviderId(serrviceProviderId);
			merchantService.setServiceId(serviceId);
			merchantService.setSubscriptionAmount(subscriptionAmount);
			merchantService.setSubscriptionCycle(subscriptionCycle);
			merchantService.setServiceType(serviceType);
			merchantService = merchantServiceRepository.save(merchantService);
			long merchantServiceId = merchantService.getMerchantServiceId();

			if (serviceType == "Charge" || serviceType.equalsIgnoreCase("Charge")) {

				List<PartnerServiceCharges> list = partnerServiceChargesRepository
						.findByPartnerServiceId(partnerServiceId);

				if (list != null) {
					
					for(PartnerServiceCharges partnerServiceCharges: list) {
					
					MerchantServiceCharges merchantServiceCharges = new MerchantServiceCharges();

					merchantServiceCharges.setMerchantServiceId(merchantServiceId);
					merchantServiceCharges
							.setMerchantServiceChargeStart(partnerServiceCharges.getPartnerServiceChargeStart());
					merchantServiceCharges
							.setMerchantServiceChargeEnd(partnerServiceCharges.getPartnerServiceChargeEnd());
					merchantServiceCharges.setMerchantServiceChargeRate(
							Double.valueOf(partnerServiceCharges.getPartnerServiceChargeRate()));
					merchantServiceCharges.setMerchantServiceChargeDate(trxnDate);
					merchantServiceCharges
							.setMerchantServiceChargeType(partnerServiceCharges.getPartnerServiceChargeType());
					merchantServiceCharges.setIsMerchnatServiceChargeActive('Y');
					merchantServiceCharges = merchantServiceChargesRepository.save(merchantServiceCharges);
					}
				}

			} else {

				List<PartnerServiceCommission> list = partnerServiceCommissionRepository
						.findByPartnerServiceId(partnerServiceId);

				if (list != null) {
					
				for(PartnerServiceCommission partnerServiceCommission:list)	{
					MerchantServiceCommission commission = new MerchantServiceCommission();
					commission.setIsMerchnatServiceCommisionActive('Y');
					commission.setMerchantServiceId(merchantServiceId);
					commission
							.setMerchantServiceCommissionEnd(partnerServiceCommission.getPartnerServiceCommissionEnd());
					commission.setMerchantServiceCommissionRate(
							partnerServiceCommission.getPartnerServiceCommissionRate());
					commission.setMerchantServiceCommissionStart(
							partnerServiceCommission.getPartnerServiceCommissionStart());
					commission.setMerchantServiceCommissionDate(trxnDate);
					commission.setMerchantServiceCommissionType(
							partnerServiceCommission.getPartnerServiceCommissionType());
					commission = merchantServiceCommissionRepository.save(commission);
				}
				}

			}

		}
		LOGGER.info("Partner Service Assign to Merchant");
		return merchantService;
	}

}
