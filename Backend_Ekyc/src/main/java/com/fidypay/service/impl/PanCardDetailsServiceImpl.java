package com.fidypay.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.request.DrivingLicenceKarzaRequest;
import com.fidypay.request.FetchPassportRequest;
import com.fidypay.request.NSDLRequest;
import com.fidypay.request.VoterIdRequestV2;
import com.fidypay.service.BankAccountVerificationService;
import com.fidypay.service.DrivingLicenceService;
import com.fidypay.service.GSTService;
import com.fidypay.service.NameAndAddressSimilarityService;
import com.fidypay.service.PanCardDetailsService;
import com.fidypay.service.PanService;
import com.fidypay.service.PassportService;
import com.fidypay.service.VoterIdService;
import com.fidypay.utils.constants.ResponseMessage;

@Service
public class PanCardDetailsServiceImpl implements PanCardDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanCardDetailsServiceImpl.class);

	@Autowired
	private PanService panService;

	@Autowired
	private VoterIdService voterIdService;

	@Autowired
	private PassportService passportService;

	@Autowired
	private DrivingLicenceService drivingLicenceService;

	@Autowired
	private NameAndAddressSimilarityService nameAndAddressSimilarityService;

	@Autowired
	private GSTService gSTService;

	@Autowired
	private BankAccountVerificationService bankAccountVerificationService;

	@Override
	public Map<String, Object> fetchPanDetails(String panNumber, long merchantId, Double merchantFloatAmount,
			String businessName, String email) {

		LOGGER.info("Inside fetchPanDetails");

		Map<String, Object> map = new HashMap<>();

		String providerName = ResponseMessage.DECENTRO;

		LOGGER.info("providerName {}", providerName);

		switch (providerName) {

		case ResponseMessage.SIGNZY:
			LOGGER.info("Signzy {}");
			map = panService.saveDataForFetchPan(panNumber, merchantId, merchantFloatAmount, businessName, email);

			break;

		case ResponseMessage.KARZA:
			LOGGER.info("Karza {}");
			map = panService.panProfileDetails(panNumber, merchantId, merchantFloatAmount, businessName, email);

			break;

		case ResponseMessage.DECENTRO:
			LOGGER.info("Decentro {}");
			map = panService.validatePan(panNumber, merchantId, merchantFloatAmount, businessName, email);

			break;

		default:
			break;
		}
		return map;
	}

	@Override
	public Map<String, Object> fetchVoterDetails(VoterIdRequestV2 voterIdRequestV2, long merchantId,
			Double merchantFloatAmount, String password, String businessName, String email) {

		LOGGER.info("Inside fetchVoterDetails");

		Map<String, Object> map = new HashMap<>();

		String providerName = ResponseMessage.DECENTRO;

		LOGGER.info("providerName {}", providerName);

		switch (providerName) {
		case ResponseMessage.SIGNZY:

			break;

		case ResponseMessage.KARZA:

			map = voterIdService.saveDataForVoterV2(voterIdRequestV2, merchantId, merchantFloatAmount, password,
					businessName, email);

			break;

		case ResponseMessage.DECENTRO:

			map = voterIdService.saveDataForValidateVoterId(voterIdRequestV2, merchantId, merchantFloatAmount, password,
					businessName, email);

			break;

		default:
			break;
		}
		return map;
	}

	@Override
	public Map<String, Object> fetchPassportDetails(FetchPassportRequest fetchPassportRequest, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {

		LOGGER.info("Inside fetchPassportDetails");

		Map<String, Object> map = new HashMap<>();

		String providerName = "Signzy";

		LOGGER.info("providerName {}", providerName);

		switch (providerName) {

		case "Signzy":

			map = passportService.saveDataForPassportFetch(fetchPassportRequest, merchantId, merchantFloatAmount,
					businessName, email);

			break;

		case "Karza Tech":

			break;

		case "Decentro":

			break;

		default:
			break;
		}
		return map;
	}

	@Override
	public Map<String, Object> fetchDrivingLicenseDetails(DrivingLicenceKarzaRequest drivingLicenceKarzaRequest,
			long merchantId, Double merchantFloatAmount, String businessName, String email) {

		LOGGER.info("Inside fetchDrivingLicenseDetails");

		Map<String, Object> map = new HashMap<>();

		String providerName = ResponseMessage.DECENTRO;

		LOGGER.info("providerName {}", providerName);

		switch (providerName) {

		case ResponseMessage.KARZA:

			map = nameAndAddressSimilarityService.drivingLicence(drivingLicenceKarzaRequest, merchantId,
					merchantFloatAmount, businessName, email);

			break;

		case ResponseMessage.SIGNZY:

			break;

		case ResponseMessage.DECENTRO:
			map = drivingLicenceService.saveDataForDrivingLicence(drivingLicenceKarzaRequest, merchantId,
					merchantFloatAmount, businessName, email);
			break;

		default:
			break;
		}
		return map;
	}

	@Override
	public Map<String, Object> fetchGstinDetails(String gSTIN, long merchantId, Double merchantFloatAmount,
			String businessName, String email, String providerName) {

		LOGGER.info("Inside fetchGstinDetails");

		Map<String, Object> map = new HashMap<>();

		//providerName = ResponseMessage.DECENTRO;

		LOGGER.info("providerName {}", providerName);

		switch (providerName) {

			case ResponseMessage.KARZA:
				map = gSTService.saveDataForGSTINSearchKarza(gSTIN, merchantId, merchantFloatAmount, businessName, email);
				break;
			case ResponseMessage.SIGNZY:
				map = gSTService.saveDataForGSTINSearch(gSTIN, merchantId, merchantFloatAmount, businessName, email);
				break;
			case ResponseMessage.DECENTRO:
				map = gSTService.saveDataForValidateGSTINSearch(gSTIN, merchantId, merchantFloatAmount, businessName,
						email);
				break;
			case ResponseMessage.BEFISC:
				map = gSTService.saveDataForGSTINSearchKarza(gSTIN, merchantId, merchantFloatAmount, businessName, email);
				break;
	
			default:
				break;
		}
		return map;
	}

	@Override
	public Map<String, Object> bankAccountVerificationPennyDrop(NSDLRequest nsdlRequest, long merchantId,
			Double merchantFloatAmount, String bussinessName, String email) {

		LOGGER.info("Inside bankAccountVerificationPennyDrop");

		Map<String, Object> map = new HashMap<>();

//		String providerName = ResponseMessage.SIGNZY;
		String providerName = "NSDL";

		LOGGER.info("providerName {}", providerName);

		switch (providerName) {

		case ResponseMessage.SIGNZY:

			map = bankAccountVerificationService.bankAccountVerificationPennyDrop(nsdlRequest, merchantId,
					merchantFloatAmount, bussinessName, email);

			break;

		case "NSDL":

			map = bankAccountVerificationService.accountVerificationPennyDrop(nsdlRequest, merchantId,
					merchantFloatAmount, bussinessName, email);

			break;

		default:
			break;
		}

		return map;
	}
}
