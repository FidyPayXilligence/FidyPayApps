package com.fidypay.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.dto.SubMerchantDTO;
import com.fidypay.dto.SubMerchantDTOCoop;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.SubMerchantListRequest;
import com.fidypay.request.SubMerchantOnboardingRequest;
import com.fidypay.request.SubmerchantDocsRequest;
import com.fidypay.request.SubmerchatRequest;
import com.fidypay.response.SubMerchantDeatilsResponse;
import com.fidypay.response.SubMerchantDetailsResponse;
import com.fidypay.service.MerchantSubMerchantInfoService;
import com.fidypay.service.RegionDetailService;
import com.fidypay.service.SubMerchantService;
import com.fidypay.service.SubMerchantTempService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.ExcelExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/subMerchantInfo")
public class SubMerchantInfoController {

	@Autowired
	private SubMerchantTempService subMerchantTempService;

	@Autowired
	private MerchantSubMerchantInfoService merchantSubMerchantInfoService;

	@Autowired
	private SubMerchantService subMerchantService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(SubMerchantInfoController.class);

	@Autowired
	private RegionDetailService regionDetailService;

	@ApiOperation(value = "get Cities List By State Name")
	@GetMapping(value = "/getCitiesListByStateName")
	public Map<String, Object> getCitiesListByStateCode(@RequestParam("stateName") String stateName) {
		return regionDetailService.getCitiesByStateCode(stateName);
	}

	@ApiOperation(value = "get Cities List By State Code")
	@GetMapping(value = "/getCitiesListByStateCode")
	public Map<String, Object> getCitiesListByStateCodes(@RequestParam("stateCode") String stateCode) {
		return regionDetailService.getCitiesByStateCode(stateCode);
	}

	@ApiOperation(value = "Create Sub Merchant")
	@PostMapping("/createSubMerchant")
	public Map<String, Object> createSubMerchant(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody SubMerchantDTO subMerchantDTO)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				LOGGER.info("clientId : " + clientId);
				Long merchantId = merchantInfo.getMerchantId();

				String partnerKey = merchantInfo.getPartnerKeyUpi();

				LOGGER.info(" partnerKey :" + partnerKey);
				LOGGER.info("subMerchantDTO : " + subMerchantDTO.toString() + " merchantId : " + merchantId);

				return merchantSubMerchantInfoService.subMerchantRequest(subMerchantDTO, merchantId, partnerKey);

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

	@ApiOperation(value = "Create Sub Merchant For Cooprative Bank")
	@PostMapping("/createSubMerchantCoop")
	public Map<String, Object> createSubMerchantCOOP(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody SubMerchantDTOCoop subMerchantDTOCoop)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				LOGGER.info("clientId : " + clientId);
				Long merchantId = merchantInfo.getMerchantId();
				String partnerKey = merchantInfo.getPartnerKeyUpi();

				LOGGER.info("subMerchantDTO : " + subMerchantDTOCoop.toString() + " merchantId : " + merchantId);

				return merchantSubMerchantInfoService.subMerchantRequestCOOP(subMerchantDTOCoop, merchantId,
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

	@ApiOperation(value = "subMerchantList")
	@PostMapping(value = "/subMerchantList")
	public Map<String, Object> subMerchantList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody SubmerchatRequest submerchatRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return merchantSubMerchantInfoService.subMerchantList(merchantInfo.getMerchantId(), submerchatRequest);

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

	@ApiOperation(value = "subMerchantAllList")
	@PostMapping(value = "/subMerchantAllList")
	public Map<String, Object> subMerchantAllList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody SubmerchatRequest submerchatRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return merchantSubMerchantInfoService.subMerchantAllList(submerchatRequest);

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
	@PostMapping(value = "/getMCCDetailsList")
	public Map<String, Object> getMCCDetailsList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return merchantSubMerchantInfoService.getMccDetailsList();

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

	@ApiOperation(value = "subMerchantDeatisl")
	@PostMapping(value = "/subMerchantDetails/{subMerchantInfoId}")
	public Map<String, Object> subMerchantDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable long subMerchantInfoId) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return merchantSubMerchantInfoService.subMerchantDetails(merchantInfo.getMerchantId(),
						subMerchantInfoId);

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

	@ApiOperation(value = "Find Sub Merchant Data By Mobile Number")
	@PostMapping("/findByMobileNumber/{mobileNumber}")
	public Map<String, Object> findByMobileNumber(@PathVariable String mobileNumber) {

		return merchantSubMerchantInfoService.findByMobileNumber(mobileNumber);
	}

	@ApiOperation(value = "Update Sub Merchant Data By Submerchant Info Id")
	@PostMapping("/updateBySubMerchantId/{submerchantInfoId}")
	public Map<String, Object> updateBySubMerchantId(@Valid @RequestBody SubmerchantDocsRequest submerchantDocsRequest,
			@PathVariable String submerchantInfoId) {

		return merchantSubMerchantInfoService.updateBySubMerchantId(submerchantDocsRequest, submerchantInfoId);
	}

	@ApiOperation(value = "SubMerchant List By Filter")
	@PostMapping("/subMerchantListByFilter")
	public Map<String, Object> subMerchantListByFilter(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody SubMerchantListRequest subMerchantList) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return merchantSubMerchantInfoService.subMerchantListByFilter(merchantInfo.getMerchantId(),
						subMerchantList);

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

	@ApiOperation(value = "SubMerchant Check Limit")
	@PostMapping("/checkLimit/{vpa}")
	public Map<String, Object> checkLimit(@PathVariable(value = "vpa") String vpa)
			throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException,
			InvalidKeySpecException, Exception {
		return merchantSubMerchantInfoService.checkLimit(vpa);

	}

	@ApiOperation(value = "Count Of Sub-Merchant")
	@PostMapping("/countSubMerchant")
	public Map<String, Object> countOfOnboardMerchantByMerchant(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) {
		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return merchantSubMerchantInfoService.countOfMerchant(merchantInfo.getMerchantId());

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

	// for Help & Support
	@ApiOperation(value = "Find By Mobile")
	@PostMapping("/findByMobile/{mobile}")
	public Map<String, Object> findByMobile(@PathVariable(value = "mobile") String mobile) {
		return merchantSubMerchantInfoService.findByMobile(mobile);

	}

	@ApiOperation(value = "Get Sub Merchant Details")
	@PostMapping("/getSubMerchantDetails")
	public void getSubMerchantDetails(@RequestParam("merchantId") long merchantId, HttpServletResponse response)
			throws Exception {

		List<SubMerchantDetailsResponse> activityList = subMerchantService.getSubMerchantDetails(merchantId);

		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());

		ExcelExporter excelExporter = new ExcelExporter();
		ByteArrayInputStream byteArrayInputStream = excelExporter.exportSubMerchantDetails(activityList);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition",
				"attachment; filename=Sub Merchant Details" + currentDateTime + ".xlsx");
		IOUtils.copy(byteArrayInputStream, response.getOutputStream());

	}

	@GetMapping(value = "/subMerchantListExcel")
	public void subMerchantListExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, HttpServletResponse response) {
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				List<SubMerchantDeatilsResponse> activityList = subMerchantService
						.getSubMerchantDetailExcel(merchantInfo.getMerchantId());

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.subMerchantDetailsExcel(activityList);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment; filename=Sub Merchant Details" + currentDateTime + ".xlsx");
				IOUtils.copy(byteArrayInputStream, response.getOutputStream());
			}
		} catch (Exception e) {

		}
	}

	@PostMapping(value = "/subMerchantListExcelName")
	public void subMerchantListExcelName(HttpServletResponse response) throws IOException {
		List<SubMerchantDeatilsResponse> activityList = subMerchantService.getSubMerchantDetail();

		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());

		ExcelExporter excelExporter = new ExcelExporter();
		ByteArrayInputStream byteArrayInputStream = excelExporter.subMerchantDetailsExcelWithName(activityList);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition",
				"attachment; filename=Sub Merchant Details" + currentDateTime + ".xlsx");
		IOUtils.copy(byteArrayInputStream, response.getOutputStream());

	}

	@ApiOperation(value = "Get Sub Merchant Details Sheet")
	@GetMapping("/getSubMerchantDetailsSheet")
	public void getSubMerchantDetailsSheet(@RequestParam("merchantId") long merchantId, HttpServletResponse response)
			throws Exception {

		List<SubMerchantDetailsResponse> activityList = subMerchantService.getSubMerchantDetails(merchantId);

		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());

		ExcelExporter excelExporter = new ExcelExporter();
		ByteArrayInputStream byteArrayInputStream = excelExporter.exportSubMerchantDetailsSheet(activityList);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition",
				"attachment; filename=Sub Merchant Details" + currentDateTime + ".xlsx");
		IOUtils.copy(byteArrayInputStream, response.getOutputStream());

	}

	@ApiOperation(value = "Create Merchant By Mobile Number")
	@GetMapping("/cbank/sub-merchant/{subMerchantInfoId}")
	public Map<String, Object> createMerchantByMobile(
			@PathVariable(value = "subMerchantInfoId") String subMerchantInfoId) throws Exception {
		return subMerchantService.createSubMerchantById(subMerchantInfoId);
	}

	// Create Sub Merchant Using API for our merchants

	@ApiOperation(value = "Create Sub Merchant airtel")
	@PostMapping("/create-vpa")
	public Map<String, Object> createVPA(@RequestHeader("Authorization") String Authorization,
			@RequestHeader("Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody SubMerchantOnboardingRequest subMerchantRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));
			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())) {
				String merchantBusinessName = Encryption.decString(merchantInfo.getMerchantBusinessName());
				String logo = merchantInfo.getImageUrl();
				return subMerchantTempService.createVPA(Long.parseLong(Encryption.decString(clientId)),
						subMerchantRequest, merchantBusinessName, merchantInfo.getBankIdUpi(), logo);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

}
