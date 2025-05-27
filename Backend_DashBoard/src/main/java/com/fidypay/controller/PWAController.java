
package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.MerchantType;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantTypeRepository;
import com.fidypay.request.MerchantSubMerchantRequest;
import com.fidypay.request.TransactionHistoryRequest;
import com.fidypay.service.MerchantSubMerchantInfoService;
import com.fidypay.service.MerchantsService;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PWAController {

	@Autowired
	private MerchantsService merchantsService;


	@Autowired
	private MerchantTypeRepository merchantTypeRepository;

	@Autowired
	private MerchantSubMerchantInfoService merchantSubMerchantInfoService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;
	
	@ApiOperation(value = "Login With OTP")
	@PostMapping(value = "/loginWithOTP")
	public Map<String, Object> merchantLoginWithOTP(@RequestParam("mobileNo") String mobileNo) throws Exception {
		return merchantsService.merchantLoginWithOTP(mobileNo);
	}

	@ApiOperation(value = "checkMobileNo")
	@PostMapping("/checkMobileNo")
	public Map<String, Object> checkMobileNo(@RequestParam("mobileNo") String mobileNo) throws Exception {
		return merchantsService.checkMobileNo(mobileNo);
	}

	@ApiOperation(value = "checkEmailId")
	@PostMapping("/checkEmailId")
	public Map<String, Object> checkEmailId(@RequestParam("emailid") String emailid) throws Exception {
		return merchantsService.checkEmailId(emailid);
	}

	@ApiOperation(value = "checkPanNumber")
	@PostMapping("/checkPanNumber/{panNo}")
	public String checkPanNumber(@PathVariable("panNo") String panNo) throws Exception {
		return merchantsService.checkPanNumber(panNo);
	}

	@ApiOperation(value = "Merchant SubMerchant Register")
	@PostMapping("/V2/merchantRegister")
	public Map<String, Object> merchantSubMerchantRegister(@RequestHeader(value = "MId") String MID,
			@Valid @RequestBody MerchantSubMerchantRequest merchantSubMerchantRequest) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
	
		try {
			Long merchantTypeId = Long.parseLong(Encryption.decString(MID));
			MerchantType merchantType = merchantTypeRepository.findById(merchantTypeId).get();
			if (merchantType != null) {
				String partnerKey = "bHhNTlRaNj";
				map = merchantsService.merchantSubMerchantRegister(merchantSubMerchantRequest, merchantType,
						partnerKey);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;

	}

	@ApiOperation(value = "Get MCC Details List")
	@GetMapping(value = "/getMCCDetailsList")
	public Map<String, Object> getMCCDetailsList() {
		return merchantSubMerchantInfoService.getMccDetailsList();
	}

	@ApiOperation(value = "Get Bussiness Type List")
	@GetMapping(value = "/getBussinessTypeList")
	public Map<String, Object> getBussinessTypeList() {
		return merchantSubMerchantInfoService.getBussinessTypeList();
	}

	@ApiOperation(value = "transactionHistory")
	@PostMapping("/transactionHistory")
	public Map<String, Object> transactionHistory(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody TransactionHistoryRequest transactionHistoryRequest) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

		} else {
			  MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				if(merchantInfo!=null) { 
                  String cId=String.valueOf(merchantInfo.getMerchantId()); 
				return merchantsService.transactionHistory(transactionHistoryRequest, cId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		}
		return map;

	}

}
