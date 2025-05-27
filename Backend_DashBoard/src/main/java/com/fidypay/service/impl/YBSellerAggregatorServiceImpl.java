package com.fidypay.service.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.service.YBSellerAggregatorService;

@Service
public class YBSellerAggregatorServiceImpl implements YBSellerAggregatorService {

	@Autowired
	private MerchantSubMerchantInfoV2Repository merchantSubMerchantInfoV2Repository;

	@Override
	public MerchantSubMerchantInfoV2 save(long merchantId, String submerchantAction, String vpa, String bankDetailsJson,
			String sellerIdentifier, String subMerchantResponse, String subMerchantRequest, String partnerKey,
			String qrString, String status, String subMerchantType, Timestamp trxnDate, String bussinessName,
			String userRequest, String subMerchantName, String mobileNumber, String emailId, String pan, String mcc,
			String gst, String bankId) {

		MerchantSubMerchantInfoV2 merchantSubMerchantInfo = new MerchantSubMerchantInfoV2();
		merchantSubMerchantInfo.setIsDeleted('N');
		merchantSubMerchantInfo.setMerchantId(merchantId);
		merchantSubMerchantInfo.setSubMerchantAction(submerchantAction);
		merchantSubMerchantInfo.setSubMerchantAdditionalInfo(vpa);
		merchantSubMerchantInfo.setSubMerchantBankDetails(bankDetailsJson);
		merchantSubMerchantInfo.setSubMerchantId(sellerIdentifier);
		merchantSubMerchantInfo.setSubMerchantInfo(subMerchantResponse);
		merchantSubMerchantInfo.setSubMerchantRegisterInfo(subMerchantRequest);
		merchantSubMerchantInfo.setSubMerchantKey(partnerKey);
		merchantSubMerchantInfo.setSubMerchantQRString(qrString);
		merchantSubMerchantInfo.setSubMerchantStatus(status);
		merchantSubMerchantInfo.setSubMerchantType(subMerchantType);
		merchantSubMerchantInfo.setSubMerchantDate(trxnDate);
		merchantSubMerchantInfo.setSubMerchantUserRequest(userRequest);
		merchantSubMerchantInfo.setSubMerchantBussinessName(bussinessName);
		merchantSubMerchantInfo.setSubMerchantName(subMerchantName);
		merchantSubMerchantInfo.setSubMerchantMobileNumber(mobileNumber);
		merchantSubMerchantInfo.setSubMerchantEmailId(emailId);
		merchantSubMerchantInfo.setSubMerchantPan(pan);
		merchantSubMerchantInfo.setSubMerchantMCC(mcc);
		merchantSubMerchantInfo.setSubMerchantEditAction("NA");
		merchantSubMerchantInfo.setSubMerchanModifiedtDate(trxnDate);
		merchantSubMerchantInfo.setSubMerchantGst(gst);
		merchantSubMerchantInfo.setBankId(bankId);

		return merchantSubMerchantInfoV2Repository.save(merchantSubMerchantInfo);
	}

}
