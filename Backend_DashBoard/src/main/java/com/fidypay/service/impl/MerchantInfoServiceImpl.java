package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.ServiceProvider.AWS.AmazonClient;
import com.fidypay.dto.MerchantInfoResponse;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.MerchantInfoRequest;
import com.fidypay.request.MerchantInfoUpdateRequest;
import com.fidypay.service.MerchantInfoService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class MerchantInfoServiceImpl implements MerchantInfoService {

	private static final Logger log = LoggerFactory.getLogger(MerchantInfoServiceImpl.class);

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private MerchantsRepository merchantsrepository;

	@Autowired
	private AmazonClient amazonClient;

	@Override
	public Map<String, Object> saveMerchantInfo(MerchantInfoRequest merchantinforequest) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();

		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		MerchantInfo info = new MerchantInfo();

		log.info("Inside saveMerchantInfo.");

		if (merchantinforepository.existsByMerchantId(merchantinforequest.getMerchantId())) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "merchantId already exists.");

			return map;
		}

		info.setMerchantId(merchantinforequest.getMerchantId());
		info.setCreationDate(trxnDate);
		info.setMerchantBusinessName(Encryption.encString(merchantinforequest.getMerchantBussinessName()));
		info.setPgRedirectUrl(merchantinforequest.getPgRedirectUrl());
		info.setPgCallbackUrl(merchantinforequest.getPgCallbackUrl());
		info.setImageUrl(merchantinforequest.getImageUrl());
		info.seteNachRedirectUrl(merchantinforequest.geteNachRedirectUrl());
		info.seteNachCallbackUrl(merchantinforequest.geteNachCallbackUrl());
		info.setUpiCallbackUrl(merchantinforequest.getUpiCallbackUrl());
		info.setPayoutCallbackUrl(merchantinforequest.getPayoutCallbackUrl());
		info.seteCollectValidateUrl(merchantinforequest.geteCollectValidateUrl());
		info.seteCollectNotifyUrl(merchantinforequest.geteCollectNotifyUrl());
		info.setBbpsCallbackUrl(merchantinforequest.getBbpsCallbackUrl());
		info.setClientId(Encryption.encString(merchantinforequest.getClientId()));
		info.setClientSecret(Encryption.encString(merchantinforequest.getClientSecret()));
		info.setUsername(Encryption.encString(merchantinforequest.getUsername()));
		info.setPassword(Encryption.encString(merchantinforequest.getPassword()));
		info.seteCollectCorpId(merchantinforequest.geteCollectCorpId());
		info.setBankIdUpi(merchantinforequest.getBankIdUpi());
		info.setBankIdJson(merchantinforequest.getBankIdJson());
		info.setPartnerKeyUpi(merchantinforequest.getPartnerKeyUpi());
		info.setIsMerchantActive('1');
		info.setQrImage("NA");
		info.setBulkPayoutCallBackURL(merchantinforequest.getBulkPayoutCallBackURL());
		merchantinforepository.save(info);

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
		return map;
	}

	@Override
	public Map<String, Object> findByMerchantId(long merchantId) {

		Map<String, Object> map = new HashMap<String, Object>();

		log.info("Inside getMerchantinfoByMerchantId.");

		MerchantInfo info = merchantinforepository.findByMerchantId(merchantId);

		if (info != null) {

			MerchantInfoResponse response = new MerchantInfoResponse();
			String logo = info.getImageUrl();
			if (!logo.equalsIgnoreCase("NA")) {
				logo = ResponseMessage.LOGO_URL + logo;
			}
			String qrImage = info.getQrImage();
			if (!qrImage.equalsIgnoreCase("NA")) {
				qrImage = ResponseMessage.LOGO_URL + qrImage;
			}

			response.setMerchantInfoId(info.getMerchantInfoId());
			response.setMerchantId(info.getMerchantId());
			response.setCreationDate(info.getCreationDate().toString());
			response.setMerchantBusinessName(Encryption.decString(info.getMerchantBusinessName()));
			response.setPgRedirectUrl(info.getPgRedirectUrl());
			response.setPgCallbackUrl(info.getPgCallbackUrl());
			response.setImageUrl(logo);
			response.setQrImage(qrImage);
			response.seteNachRedirectUrl(info.geteNachRedirectUrl());
			response.seteNachCallbackUrl(info.geteNachCallbackUrl());
			response.setUpiCallbackUrl(info.getUpiCallbackUrl());
			response.setPayoutCallbackUrl(info.getPayoutCallbackUrl());
			response.seteCollectValidateUrl(info.geteCollectValidateUrl());
			response.seteCollectNotifyUrl(info.geteCollectNotifyUrl());
			response.setBbpsCallbackUrl(info.getBbpsCallbackUrl());
			response.setClientId(Encryption.decString(info.getClientId()));
			response.setClientSecret(Encryption.decString(info.getClientSecret()));
			response.setUsername(Encryption.decString(info.getUsername()));
			response.setPassword(Encryption.decString(info.getPassword()));
			response.seteCollectCorpId(info.geteCollectCorpId());
			response.setBankIdUpi(info.getBankIdUpi());
			response.setBankIdJson(info.getBankIdJson());
			response.setPartnerKeyUpi(info.getPartnerKeyUpi());
			response.setIsMerchantActive(info.getIsMerchantActive());
			response.setBulkPayoutCallBackURL(info.getBulkPayoutCallBackURL());
			response.setDebitPresentationCallbackUrl(info.getDebitPresentationCallbackUrl());

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put("data", response);

			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		return map;
	}

	@Override
	public Map<String, Object> addByMerchantId(long merchantId) throws ParseException {

		log.info("Inside transferByMerchantId.");

		Map<String, Object> map = new HashMap<String, Object>();

		Merchants merchants = merchantsrepository.findById(merchantId).get();

		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		if (merchants == null) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

			return map;
		}

		if (merchantinforepository.existsByMerchantId(merchantId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_ALREADY_REGISTERED);
			return map;

		}

		MerchantInfo info = new MerchantInfo();
		String email = merchants.getMerchantEmail();
		String merchantId1 = String.valueOf(merchants.getMerchantId());
		String firstName = merchants.getMerchantFirstname();
		String password = merchants.getMerchantPassword();
		String merchantNationality = merchants.getMerchantNationality();
		Character isMerchantActive = merchants.getIsMerchantActive();

		if (merchantNationality == null) {
			merchantNationality = "NA";
		}

		info.setMerchantId(merchants.getMerchantId());
		info.setCreationDate(trxnDate);
		info.setMerchantBusinessName("NA");
		info.setPgRedirectUrl("NA");
		info.setPgCallbackUrl("NA");
		info.setImageUrl("NA");
		info.seteNachRedirectUrl("NA");
		info.seteNachCallbackUrl("NA");
		info.setUpiCallbackUrl("NA");
		info.setPayoutCallbackUrl("NA");
		info.seteCollectValidateUrl("NA");
		info.seteCollectNotifyUrl("NA");
		info.setBbpsCallbackUrl("NA");
		info.setClientId(Encryption.encString(Encryption.encString(merchantId1)));
		info.setClientSecret(Encryption.encString(email));
		info.setUsername(firstName);
		info.setPassword(password);
		info.seteCollectCorpId("NA");
		info.setBankIdUpi("NA");
		info.setBulkPayoutCallBackURL("NA");
		info.setBankIdJson("NA");
		info.setPartnerKeyUpi(merchantNationality);
		info.setIsMerchantActive(isMerchantActive);
		info.setQrImage("NA");
		merchantinforepository.save(info);

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);

		return map;
	}

	@Override
	public Map<String, Object> updateMerchantInfoByMerchantId(long merchantId,
			MerchantInfoUpdateRequest merchantinforequest) {

		Map<String, Object> map = new HashMap<String, Object>();

		log.info("Inside updateMerchantInfoByMerchantId.");

		MerchantInfo info = merchantinforepository.findByMerchantId(merchantId);
		if (info != null) {
			// pg

			if (merchantinforequest.getPgRedirectUrl() != null && !merchantinforequest.getPgRedirectUrl().equals("")) {
				log.info("Inside getPgRedirectUrl.");
				info.setPgRedirectUrl(merchantinforequest.getPgRedirectUrl());
			}

			if (merchantinforequest.getPgCallbackUrl() != null && !merchantinforequest.getPgCallbackUrl().equals("")) {
				log.info("Inside getPgCallbackUrl.");
				info.setPgCallbackUrl(merchantinforequest.getPgCallbackUrl());
			}

			// ENACH

			if (merchantinforequest.geteNachRedirectUrl() != null
					&& !merchantinforequest.geteNachRedirectUrl().equals("")) {
				log.info("Inside geteNachRedirectUrl.");
				info.seteNachRedirectUrl(merchantinforequest.geteNachRedirectUrl());
			}

			if (merchantinforequest.geteNachCallbackUrl() != null
					&& !merchantinforequest.geteNachCallbackUrl().equals("")) {
				log.info("Inside geteNachCallbackUrl.");
				info.seteNachCallbackUrl(merchantinforequest.geteNachCallbackUrl());
			}
			if (merchantinforequest.getDebitPresentationCallbackUrl() != null
					&& !merchantinforequest.getDebitPresentationCallbackUrl().equals("")) {
				log.info("Inside geteNachCallbackUrl.");
				info.setDebitPresentationCallbackUrl(merchantinforequest.getDebitPresentationCallbackUrl());
			}
//payout
			if (merchantinforequest.getPayoutCallbackUrl() != null
					&& !merchantinforequest.getPayoutCallbackUrl().equals("")) {
				log.info("Inside getPayoutCallbackUrl.");
				info.setPayoutCallbackUrl(merchantinforequest.getPayoutCallbackUrl());
			}

			if (merchantinforequest.geteCollectValidateUrl() != null
					&& !merchantinforequest.geteCollectValidateUrl().equals("")) {
				log.info("Inside geteCollectValidateUrl.");
				info.seteCollectValidateUrl(merchantinforequest.geteCollectValidateUrl());
			}

			if (merchantinforequest.geteCollectNotifyUrl() != null
					&& !merchantinforequest.geteCollectNotifyUrl().equals("")) {
				log.info("Inside geteCollectNotifyUrl.");
				info.seteCollectNotifyUrl(merchantinforequest.geteCollectNotifyUrl());
			}
			if (merchantinforequest.geteCollectCorpId() != null
					&& !merchantinforequest.geteCollectCorpId().equals("")) {
				log.info("Inside geteCollectCorpId.");
				info.seteCollectCorpId(merchantinforequest.geteCollectCorpId());
			}
			if (merchantinforequest.getBulkPayoutCallBackURL() != null
					&& !merchantinforequest.getBulkPayoutCallBackURL().equals("")) {
				log.info("Inside getBulkPayoutCallBackURL.");
				info.setBulkPayoutCallBackURL(merchantinforequest.getBulkPayoutCallBackURL());
			}
//bbps
			if (merchantinforequest.getBbpsCallbackUrl() != null
					&& !merchantinforequest.getBbpsCallbackUrl().equals("")) {
				log.info("Inside getBbpsCallbackUrl.");
				info.setBbpsCallbackUrl(merchantinforequest.getBbpsCallbackUrl());
			}

//payin
			if (merchantinforequest.getUpiCallbackUrl() != null
					&& !merchantinforequest.getUpiCallbackUrl().equals("")) {
				log.info("Inside getUpiCallbackUrl.");
				info.setUpiCallbackUrl(merchantinforequest.getUpiCallbackUrl());
			}
			if (merchantinforequest.getBankIdUpi() != null && !merchantinforequest.getBankIdUpi().equals("")) {
				log.info("Inside getBankIdUpi.");
				info.setBankIdUpi(merchantinforequest.getBankIdUpi());
			}

			if (merchantinforequest.getBankIdJson() != null && !merchantinforequest.getBankIdJson().equals("")) {
				log.info("Inside getBankIdJson.");
				info.setBankIdJson(merchantinforequest.getBankIdJson());
			}

			if (merchantinforequest.getPartnerKeyUpi() != null && !merchantinforequest.getPartnerKeyUpi().equals("")) {
				log.info("Inside getPartnerKeyUpi.");
				info.setPartnerKeyUpi(merchantinforequest.getPartnerKeyUpi());
			}

			merchantinforepository.save(info);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);

			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		return map;
	}

	@Override
	public Map<String, Object> logoUpload(long merchantId, MultipartFile file, String imageName) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (!imageName.equals("Logo") && !imageName.equals("QR Image")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please Pass Logo or QR Image on imageName parameter");
			return map;
		}

		if (file.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please select image");
			return map;
		}

		MerchantInfo info = merchantinforepository.findByMerchantId(merchantId);
		String key = "NA";

		if (info != null) {

			switch (imageName) {
			case "Logo":

				if (info.getImageUrl().equalsIgnoreCase("NA")) {
					key = amazonClient.uploadFile(file);
					info.setImageUrl(key);
				}

				if (!info.getImageUrl().equalsIgnoreCase("NA")) {
					String imageKey = info.getImageUrl();
					amazonClient.deleteFileFromS3Bucket(imageKey);
					key = amazonClient.uploadFile(file);
					info.setImageUrl(key);
				}
				merchantinforepository.save(info);
				break;

			case "QR Image":

				if (info.getQrImage().equalsIgnoreCase("NA")) {
					key = amazonClient.uploadFile(file);
					info.setQrImage(key);
				}

				if (!info.getQrImage().equalsIgnoreCase("NA")) {
					String qrKey = info.getQrImage();
					amazonClient.deleteFileFromS3Bucket(qrKey);
					key = amazonClient.uploadFile(file);
					info.setQrImage(key);
				}

				merchantinforepository.save(info);
				break;

			default:
				break;
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
			map.put("url", ResponseMessage.LOGO_URL + key);
			return map;

		}
		return map;
	}

	@Override
	public Map<String, Object> removeImage(long merchantId, String imageName) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (!imageName.equals("Logo") && !imageName.equals("QR Image")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please Pass Logo or QR Image on imageName parameter");
			return map;
		}

		MerchantInfo info = merchantinforepository.findByMerchantId(merchantId);
		if (info != null) {

			switch (imageName) {
			case "Logo":

				String imageKey = info.getImageUrl();
				amazonClient.deleteFileFromS3Bucket(imageKey);
				info.setImageUrl("NA");
				merchantinforepository.save(info);
				break;

			case "QR Image":

				String qrKey = info.getQrImage();
				amazonClient.deleteFileFromS3Bucket(qrKey);
				info.setQrImage("NA");
				merchantinforepository.save(info);
				break;

			default:
				break;
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Remove Successfully");

			return map;

		}
		return map;
	}

	@Override
	public Map<String, Object> findUPIBankId(long merchantId) {

		Map<String, Object> map = new HashMap<String, Object>();

		log.info("Inside getMerchantinfoByMerchantId.");

		String upiBankId = merchantinforepository.findBankIdUPI(merchantId);

		if (upiBankId.equalsIgnoreCase("NA")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			return map;
		}

		if (upiBankId != null) {
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put("upiBankId", upiBankId);

			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> uploadBanner(long merchantId, @NotNull MultipartFile file) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (file.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please select image");
			return map;
		}

		MerchantInfo info = merchantinforepository.findByMerchantId(merchantId);
		String key = "NA";

		if (info.getImageUrl().equalsIgnoreCase("NA")) {
			key = amazonClient.uploadFile(file);
			info.setOtherInfo1(key);
		}

		if (!info.getImageUrl().equalsIgnoreCase("NA")) {
			String imageKey = info.getOtherInfo1();
			amazonClient.deleteFileFromS3Bucket(imageKey);
			key = amazonClient.uploadFile(file);
			info.setOtherInfo1(key);
		}
		merchantinforepository.save(info);

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
		map.put("key", key);
		return map;

	}

}
