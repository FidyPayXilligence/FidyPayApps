package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.ServiceProvider.Karza.KarzaService;
import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.ServiceProvider.Signzy.FaceMatcherAndImageLivenessService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.EkycWorkflowServiceRequest;
import com.fidypay.request.ImageURLs;
import com.fidypay.request.WorkflowKYCRequest;
import com.fidypay.service.EkycWorkflowServiceSer;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/workflowUserService")
public class EkycWorkflowUserServiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EkycWorkflowUserServiceController.class);

	@Autowired
	private EkycWorkflowServiceSer ekycWorkflowServiceSer;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private KarzaService karzaService;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private FaceMatcherAndImageLivenessService facematcherandimagelivenessservice;

	@PostMapping("/findByEkycUserId")
	public Map<String, Object> findByEkycUserId(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("ekycUserId") String ekycUserId) {

		Map<String, Object> response = new HashMap<String, Object>();

		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			
			if (merchantInfo != null) {

				long merchantId = merchantInfo.getMerchantId();

				LOGGER.info("merchantId:- " + merchantId);
				return ekycWorkflowServiceSer.findByEkycUserId(Long.valueOf(ekycUserId), merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				response.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		}
		catch (NumberFormatException ex) {
			ex.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		} 
		
		catch (Exception e) {
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			response.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return response;
	}

	@PostMapping("/isVerified")
	public Map<String, Object> isVerified(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("ekycWorkflowServiceId") long ekycWorkflowServiceId,
			@RequestParam("isVerified") String isVerified,@RequestParam("rejectReason") String rejectReason) {

		Map<String, Object> response = new HashMap<String, Object>();

		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- " + merchantId);

				return ekycWorkflowServiceSer.updateById(ekycWorkflowServiceId, merchantId, isVerified,rejectReason);

			} else {
                response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				response.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			response.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return response;
	}

	@GetMapping("/getDocumentData")
	public Map<String, Object> getDocumentData(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("ekycWorkflowServiceId") String ekycWorkflowServiceId) {

		Map<String, Object> response = new HashMap<String, Object>();

		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- " + merchantId);

				return ekycWorkflowServiceSer.getDocumentData(Long.valueOf(ekycWorkflowServiceId), merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				response.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		}catch (NumberFormatException ex) {
			ex.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		} catch (Exception e) {
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			response.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return response;
	}

	@PostMapping("/workFlow/kyc")
	public Map<String, Object> workFlowKyc(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestBody WorkflowKYCRequest workflowKYCRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return karzaService.saveDataForOCRKycAWSForWorkFlow(workflowKYCRequest, merchants.getMerchantId(),
						merchants.getMerchantFloatAmount());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;

	}

	@PostMapping("/documentUpload")
	public Map<String, Object> documentUpload(@RequestParam("workflowToken") String workflowToken,
			@RequestParam("userWorkflowToken") String userWorkflowToken, @RequestParam("fileName") String fileName,
			@RequestParam("optional") String optional, @RequestParam("optional2") String optional2,
			@RequestPart(value = "file", required = false) MultipartFile file,
			@RequestPart(value = "file2", required = false) MultipartFile file2) {
		return ekycWorkflowServiceSer.documentUpload(workflowToken, userWorkflowToken, fileName, file, optional,
				optional2, file2);

	}

	@PostMapping("/gstSearch/{GSTIN}")
	public Map<String, Object> GSTINSearch(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable String GSTIN) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {


				return ekycWorkflowServiceSer.GSTINSearch(GSTIN);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;

	}

	@ApiOperation(value = "Image Liveness")
	@PostMapping("/imageLiveness")
	public Map<String, Object> imageLiveness(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody ImageURLs imageURLs) {

		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);

			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				String serviceName = "Image Liveness";
				if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)), serviceName)) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
					return map;

				}
				return facematcherandimagelivenessservice.saveDataForImagePassiveLiveness(imageURLs.getImageUrl1(),
						merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Face Match")
	@PostMapping("/faceMatch")
	public Map<String, Object> faceMatch(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody ImageURLs imageURLs) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return facematcherandimagelivenessservice.saveDataForFaceMatcher(imageURLs.getImageUrl1(),
						imageURLs.getImageUrl2(), merchants.getMerchantId(), merchants.getMerchantFloatAmount());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;

	}
	
	
	
	
	@ApiOperation(value = "Save Ekyc Workflow User Service")
	@PostMapping("/saveEkycWorkflowUserService")
	public Map<String, Object> saveEkycWorkflowUserService(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody EkycWorkflowServiceRequest ekycWorkflowServiceRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return ekycWorkflowServiceSer.saveEkycWorkflowUserService(ekycWorkflowServiceRequest, merchants.getMerchantId());

				
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}
}
