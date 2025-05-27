package com.fidypay.service;



import java.sql.Timestamp;

import com.fidypay.entity.MerchantSubMerchantInfoV2;

public interface YBSellerAggregatorService {

	MerchantSubMerchantInfoV2 save(long merchantId, String submerchantAction, String vpa, String bankDetailsJson,
			String sellerIdentifier, String subMerchantResponse, String subMerchantRequest, String partnerKey,
			String qrString, String status, String subMerchantType,Timestamp trxnDate,String bussinessName,String userRequest
			,String subMerchantName,String mobileNumber,String emailId,String pan,String mcc,String gst,String bankId);
}
