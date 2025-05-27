package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.Validate;
import com.fidypay.request.ValidateOtp;
import com.fidypay.service.AadhaarService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.EkycCommonLogicConfig;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/aadhar")
public class AadharController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AadharController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private EkycRequestRepository ekycRequestRepository;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private AadhaarService aadhaarService;

	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;

	@ApiOperation(value = "Verify Aadhaar Signzy API(Aadhar Verify)")
	@PostMapping("/verifyAadhaar/{uId}")
	public Map<String, Object> verifyAadhaar(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String uId) {
		Map<String, Object> map = new HashMap<>();

		try {
			// String serviceName = "Aadhar Verify";
			String serviceName = "Aadhaar Verify";

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {} ", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> merchantsOpt = merchantsRepository.findById(merchantInfo.getMerchantId());
				LOGGER.info("merchantsOpt :{}", merchantsOpt.isPresent());
				if (merchantsOpt.isPresent()) {
					Merchants merchants = merchantsOpt.get();
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						return map;

					}
					// map = aadhaarService.saveDataForVerify(uId, merchantInfo.getMerchantId(),
					// merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
					// merchants.getMerchantEmail());

					map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							ResponseMessage.SERVICE_CLOSED);
				}

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

	@ApiOperation(value = "Basic Verify Aadhaar Signzy API(Aadhar Basic Verify)")
	@PostMapping("/basicVerifyAadhaar/{uId}")
	public Map<String, Object> basicVerifyAadhaar(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String uId) {
		Map<String, Object> map = new HashMap<>();

		try {
			// String serviceName = "Aadhar Basic Verify";
			String serviceName = "Aadhaar Basic Verify";

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {} ", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> merchantsOpt = merchantsRepository.findById(merchantInfo.getMerchantId());
				LOGGER.info("merchantsOpt :{}", merchantsOpt.isPresent());
				if (merchantsOpt.isPresent()) {
					Merchants merchants = merchantsOpt.get();

					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;
					}

					// return aadhaarService.saveDataForBasicVerify(uId,
					// merchantInfo.getMerchantId(),
					// merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
					// merchants.getMerchantEmail());

					map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							ResponseMessage.SERVICE_CLOSED);
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@ApiOperation(value = "Create URL Signzy API(Get URL For EAdhar)")
	@PostMapping("/createUrl")
	public Map<String, Object> createURl(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("url") String url) {
		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {} ", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {} ", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();
					String serviceName = "Get URL For EAdhar";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}
					// return aadhaarService.saveDataForGetURL(merchantInfo.getMerchantId(),
					// merchants.getMerchantFloatAmount(), url, merchants.getMerchantBusinessName(),
					// merchants.getMerchantEmail());

					map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							ResponseMessage.SERVICE_CLOSED);
				}

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

	@ApiOperation(value = "EAadhar Signzy API(EAdhar)")
	@PostMapping("/eAadhar/{requestId}")
	public Map<String, Object> eAadhar(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String requestId) throws Exception {

		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {} ", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {
				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				LOGGER.info("findById: {}", findById.isPresent());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();
					String serviceName = "EAdhar";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}

					// return aadhaarService.eAdhar(requestId, merchantInfo.getMerchantId(),
					// merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
					// merchants.getMerchantEmail());

					map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							ResponseMessage.SERVICE_CLOSED);
				}

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

	@ApiOperation(value = "Get Details(Get Details EAdhar)")
	@PostMapping("/getDetails/{requestId}")
	public Map<String, Object> getDetails(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String requestId) throws Exception {
		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {} ", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {

					Merchants merchants = findById.get();
					String serviceName = "Get Details EAdhar";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}

					// return aadhaarService.getDetailsEAdhar(requestId,
					// merchantInfo.getMerchantId(),
					// merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
					// merchants.getMerchantEmail());

					map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							ResponseMessage.SERVICE_CLOSED);
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	// @PostMapping("/createUrlNew/{requestId}")
	// public String createURlNew(@RequestHeader("Authorization") String
	// Authorization,
	// @RequestHeader(value = "Client-Id") String clientId,
	// @RequestHeader("Client-Secret")
	// String clientSecret,
	// @Valid @PathVariable String requestId) throws Exception {
	// String response = null;
	// JSONObject jsonObject = new JSONObject();
	//
	// try {
	// String password = AuthenticationVerify.authenticationPassword(Authorization);
	//
	// LOGGER.info(", password:" + password + ", clientSecret: " + clientSecret);
	// Merchants merchants =
	// merchantsRepository.findByPasswordAndEmail(Encryption.encString(password),
	// clientSecret);
	// if (merchants.getIsMerchantActive() == '1'
	// && password.equals(Encryption.decString(merchants.getMerchantPassword()))
	// && clientSecret.equals(merchants.getMerchantEmail())
	// && Long.parseLong(Encryption.decString(clientId)) ==
	// merchants.getMerchantId()) {
	//
	// response = aadhaarServiceImpl.createUrlForDigilockerNew(requestId);
	// LOGGER.info(", response :" + response);
	//
	// } else {
	// jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
	// jsonObject.put(ResponseMessage.DESCRIPTION,
	// ResponseMessage.UNAUTHORISED_DESCRIPTION);
	// jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
	// response = jsonObject.toString();
	//
	// }
	// } catch (Exception e) {
	// jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
	// jsonObject.put(ResponseMessage.DESCRIPTION,
	// ResponseMessage.UNAUTHORISED_DESCRIPTION);
	// jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
	// response = jsonObject.toString();
	// }
	//
	// return response;
	// }

	@ApiOperation(value = "Generate Otp API(Validate Aadhaar)")
	@PostMapping("/generateOtp/{aadhaarNumber}")
	public Map<String, Object> generateOtp(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String aadhaarNumber) {

		Map<String, Object> map = new HashMap<>();

		try {
			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info("clientSecret: {}" + clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info("merchantInfoId: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {
				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();
					String serviceName = "Validate Aadhaar";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						LOGGER.info(" serviceName: " + serviceName);
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}
					return aadhaarService.saveDataForGenerateOtp(aadhaarNumber, merchantInfo.getMerchantId(),
							merchants.getMerchantFloatAmount(), serviceName, merchants.getMerchantBusinessName(),
							merchants.getMerchantEmail());
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			}

		} catch (Exception ex) {
			LOGGER.error("Exception : {}", ex.getLocalizedMessage(), ex);
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;

	}

	@ApiOperation(value = "Validate Otp API(Validate Aadhaar)")
	@PostMapping("/validateOtp")
	public Map<String, Object> validateOtp(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody ValidateOtp validateotp) {

		LOGGER.info("ValidateOtp Request: {}", validateotp);
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info("clientSecret: {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info("merchantInfoId: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());

				if (findById.isPresent()) {
					Merchants merchants = findById.get();
					String serviceName = "Validate Aadhaar";

					if (validateotp.getInitiation_transaction_id().trim().isEmpty()
							|| validateotp.getInitiation_transaction_id() == null) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put(ResponseMessage.DESCRIPTION, "merchantTxnRefId should not be null.");
						return map;

					}

					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}

					if (!ekycRequestRepository.existsByUserRequest(validateotp.getInitiation_transaction_id())) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.TRANSACTION_ID_NOT_VALID);
						return map;

					}

					return aadhaarService.saveDataForAadharValidate(validateotp, merchantInfo.getMerchantId(),
							merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
							merchants.getMerchantEmail());
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			}

		} catch (Exception ex) {
			LOGGER.error("Exception in validateOtp: {}", ex.getLocalizedMessage(), ex);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@ApiOperation(value = "Validate KYC API(Single KYC API)")
	@PostMapping("/validateKYC")
	public Map<String, Object> validateKYC(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody Validate validate) {
		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();
					String serviceName = "Single KYC API";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						return map;

					}
					return aadhaarService.saveDataForValidate(validate, merchantInfo.getMerchantId(),
							merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
							merchants.getMerchantEmail());
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	// ELK
	// @PostMapping("/save-fromDate-toDate-in-elasticsearch")
	// public ResponseEntity<Iterable<EkycTransactionDetails>> saveInElasticsearch(
	// @RequestBody ElasticMigrationRequest elasticMigrationRequest) {
	// Iterable<EkycTransactionDetails> ekycResult =
	// aadhaarService.migrateDataByDateRange(elasticMigrationRequest);
	// return ResponseEntity.ok().body(ekycResult);
	// }
	//
	// @PostMapping("/search-all-ekyc-txn-details-from-elastic")
	// public ResponseEntity<Iterable<EkycTransactionDetails>>
	// findAllEkycTxnDetailsFromElastic() {
	// Iterable<EkycTransactionDetails> ekycResult =
	// aadhaarService.findAllEkycTxnFromElasticsearch();
	// return ResponseEntity.ok().body(ekycResult);
	// }
	//
	// @GetMapping("/_index/ekyc-txn-details")
	// public ResponseEntity<Void> indexEkycTransactionDetails(@RequestParam String
	// fromDate,
	// @RequestParam String toDate) {
	// LOGGER.info("REST request to do elastic index on EkycTransactionDetails");
	// long resultCount = ekycTransactionDetailsRepository.getTotalRecord(fromDate,
	// toDate);
	// System.out.println("RECORDS COUNT " + resultCount);
	// int pageSize = fidypayProperties.getConfigs().getIndexPageSize();
	// int lastPageNumber = (int) (Math.ceil(resultCount / pageSize));
	// for (int i = 0; i <= lastPageNumber; i++) {
	// aadhaarService.doIndex(i, pageSize, fromDate, toDate);
	// }
	// System.out.println("EkycTransactionDetails Re-index completed
	// successfully.");
	// return ResponseEntity.ok().headers(HeaderUtils.createAlert("Elastic index is
	// completed
	// ", "")).build();
	// }
}
