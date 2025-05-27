package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.AddressRequest;
import com.fidypay.request.ElectricityBillRequest;
import com.fidypay.request.MobileNumberAuthenticationRequest;
import com.fidypay.request.NameRequest;
import com.fidypay.service.ESignService;
import com.fidypay.service.ElectricityBillAuthenticationService;
import com.fidypay.service.FaceMatchingService;
import com.fidypay.service.GSTService;
import com.fidypay.service.MobileNumberAuthenticationService;
import com.fidypay.service.NameAndAddressSimilarityService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/v2")
public class KarzaAPIsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(KarzaAPIsController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private ElectricityBillAuthenticationService electricityBillAuthenticationService;

	@Autowired
	private MobileNumberAuthenticationService mobileNumberAuthenticationService;

	@Autowired
	private NameAndAddressSimilarityService nameAndAddressSimilarityService;

	@Autowired
	private FaceMatchingService faceMatchingService;

	@Autowired
	private ESignService eSignService;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private GSTService gSTService;

	@ApiOperation(value = "Electricity Bill Authentication")
	@PostMapping("/electricityBillAuthentication")
	public Map<String, Object> electricityBillAuthentication(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody ElectricityBillRequest electricityBillRequest,
			@RequestHeader("Authorization") String Authorization) {

		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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

					map = electricityBillAuthenticationService.electricityBillAuthentication(electricityBillRequest,
							merchants.getMerchantId(), merchants.getMerchantFloatAmount(),
							merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
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

	@ApiOperation(value = "Electricity Service Providers List")
	@GetMapping("/electricityServiceProvidersList")
	public Map<String, Object> electricityServiceProvidersList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestHeader("Authorization") String Authorization) {

		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				map = electricityBillAuthenticationService.electricityServiceProvidersList();

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

	@ApiOperation(value = "Mobile Number Authentication")
	@PostMapping("/mobileNumberAuthentication")
	public Map<String, Object> mobileNumberAuthentication(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody MobileNumberAuthenticationRequest mobileNumberAuthenticationRequest,
			@RequestHeader("Authorization") String Authorization) {

		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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

					map = mobileNumberAuthenticationService.mobileNumberAuthentication(
							mobileNumberAuthenticationRequest.getMobileNo(), merchants.getMerchantId(),
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
	

	@ApiOperation(value = "Name Similarity")
	@PostMapping("/nameSimilarity")
	public Map<String, Object> nameSimilarity(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @Valid @RequestBody NameRequest nameRequest,
			@RequestHeader("Authorization") String Authorization) {

		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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
					map = nameAndAddressSimilarityService.nameSimilarity(nameRequest, merchants.getMerchantId(),
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

	@ApiOperation(value = "Address Similarity")
	@PostMapping("/addressSimilarity")
	public Map<String, Object> addressSimilarity(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @Valid @RequestBody AddressRequest addressRequest,
			@RequestHeader("Authorization") String Authorization) {

		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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

					map = nameAndAddressSimilarityService.addressSimilarity(addressRequest, merchants.getMerchantId(),
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

//	@ApiOperation(value = "Driving License Karza API")
//	@PostMapping("/drivingLicense")
//	public String drivingLicense(@RequestHeader(value = "Client-Id") String clientId,
//			@RequestHeader("Client-Secret") String clientSecret,
//			@Valid @RequestBody DrivingLicenceKarzaRequest drivingLicenceKarzaRequest ,
//			@RequestHeader("Authorization") String Authorization) throws Exception {
//		String response = null;
//		JSONObject jsonObject = new JSONObject();
//
//		try {
//			
//			String password = AuthenticationVerify.authenticationPassword(Authorization);
//			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
//			LOGGER.info(" clientSecret " + clientSecret);
//
//			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
//					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
//					Encryption.encString(password));
//
//			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());
//
//			if (merchantInfo.getIsMerchantActive() == '1'
//					&& Encryption.encString(password).equals(merchantInfo.getPassword())
//					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
//					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {
//
//				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
//
//				response = nameAndAddressSimilarityService.drivingLicence(drivingLicenceKarzaRequest,merchants.getMerchantId(),merchants.getMerchantFloatAmount());
//				
//			} else {
//				jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
//				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
//				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
//				response = jsonObject.toString();
//
//			}
//
//		} catch (Exception e) {
//			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
//			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
//			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
//			response = jsonObject.toString();
//		}
//
//		return response;
//	}

	@ApiOperation(value = "Face Matching")
	@PostMapping("/faceMatcher")
	public Map<String, Object> faceMatching(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestPart(value = "file1") @NotNull MultipartFile file1,
			@RequestPart(value = "file2") @NotNull MultipartFile file2) {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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
					return faceMatchingService.faceMatcher(file1, file2, merchants.getMerchantId(),
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

	@ApiOperation(value = "EPF UAN Verification")
	@PostMapping("/epfUANVerification")
	public Map<String, Object> epfUANVerification(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @Valid @RequestParam String uanNumber,
			@RequestHeader("Authorization") String Authorization) {

		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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

					map = electricityBillAuthenticationService.epfUANValidation(uanNumber, merchants.getMerchantId(),
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

	@PostMapping("/getAuthenticateForESign")
	public Map<String, Object> getAuthenticateForESign(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestPart(value = "file") @NotNull MultipartFile file) {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

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

					return eSignService.getAuthenticateForESign(name, email, file, merchants.getMerchantId(),
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

	@PostMapping("/getESignDocument")
	public Map<String, Object> getEsignDocument(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("documentId") String documentId) {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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

					return eSignService.getEsignDocument(documentId, merchants.getMerchantId(),
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

	@PostMapping("/deleteESignDocument")
	public Map<String, Object> deleteESignDocument(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("documentId") String documentId) {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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

					return eSignService.deleteEsignDocument(documentId, merchants.getMerchantId(),
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

	@PostMapping("/GSTSearchBasisPAN")
	public Map<String, Object> GSTSearchBasisPAN(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam String panNo) {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

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

					return gSTService.GSTSearchBasisPAN(panNo, merchants.getMerchantId(),
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
}
