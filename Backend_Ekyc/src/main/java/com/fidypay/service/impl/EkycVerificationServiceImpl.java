package com.fidypay.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycVerification;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.Partners;
import com.fidypay.repo.EkycVerificationRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.PartnersRepository;
import com.fidypay.request.EkycMerchantRequest;
import com.fidypay.service.EkycVerificationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.EmailAPIImpl;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.SMSAPIImpl;

@Service
public class EkycVerificationServiceImpl implements EkycVerificationService {

	@Autowired
	private EkycVerificationRepository ekycVerificationRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private PartnersRepository partnersRepository;

	@Autowired
	private EmailAPIImpl emailAPIImpl;

	@Override
	public boolean existsByMobile(String mobile) {
		return ekycVerificationRepository.existsByMobile(mobile);
	}

	@Override
	public boolean existsByEmail(String email) {
		return ekycVerificationRepository.existsByEmail(email);
	}

	@Override
	public Map<String, Object> saveEkycVerification(String phone, String email) {

		Map<String, Object> map = new HashedMap<String, Object>();
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		Optional<Merchants> optional = merchantsRepository
				.findByMerchantPhoneAndMerchantEmail(Encryption.encString(phone), Encryption.encString(email));
		Optional<Partners> partner = partnersRepository.findByPartnerMobileAndPartnerEmail(Encryption.encString(phone),
				Encryption.encString(email));

		if (optional.isPresent() && partner.isPresent()) {

			EkycVerification ekycVerification = new EkycVerification();
			ekycVerification.setMobile(phone);
			ekycVerification.setEmail(email);
			ekycVerification.setType("Partner");
			ekycVerification.setIsVerifidePanNO('0');
			ekycVerification.setIsVerifideAccount('0');
			ekycVerification.setIsGstVerifide('0');
			ekycVerification.setIsSignatoryVerifide('0');
			ekycVerification.setIsAadharVerifide('0');
			ekycVerification.setAadharJsonResponse("NA");
			ekycVerification.setAadharNo("NA");
			ekycVerification.setAadharRefId("NA");
			ekycVerification.setAccountJsonResponse("NA");
			ekycVerification.setAccountNo("NA");
			ekycVerification.setBankName("NA");
			ekycVerification.setBranchName("NA");
			ekycVerification.setSignatoryPanJsonResponse("NA");
			ekycVerification.setCinNo("NA");
			ekycVerification.setConsituteOfName("NA");
			ekycVerification.setCreationDate(timestamp);
			ekycVerification.setGstJsonResponse("NA");
			ekycVerification.setGstNo("NA");
			ekycVerification.setIfscCode("NA");
			ekycVerification.setIpAddress("NA");
			ekycVerification.setIsSignatoryVerifide('N');
			ekycVerification.setIsGstVerifide('N');
			ekycVerification.setIsVerifideAccount('N');
			ekycVerification.setIsAadharVerifide('N');
			ekycVerification.setLetLong("NA");
			ekycVerification.setMerchantId((long) 0);
			ekycVerification.setPanCompanyBussinessName("NA");
			ekycVerification.setPanCompanyName("NA");
			ekycVerification.setPanJsonResponse("NA");
			ekycVerification.setPanNoCompany("NA");
			ekycVerification.setRegisterBussinessName("N");
			ekycVerification.setRegisterDateCompany("NA");
			ekycVerification.setSignatoryNameOwnerPanNo("NA");
			ekycVerification.setSignatoryOwnerDesignation("NA");
			ekycVerification.setSignatoryPanOwnerName("NA");
			ekycVerification.setSubMerchantId((long) 0);

			ekycVerification = ekycVerificationRepository.save(ekycVerification);
			long ekyc_id = ekycVerification.getEkycId();

			map.put("ekyc_id", String.valueOf(ekyc_id));
			map.put("code", ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Save Email and Mobile");

			return map;
		}

		else if (optional.isPresent()) {

			EkycVerification ekycVerification = new EkycVerification();
			ekycVerification.setMobile(phone);
			ekycVerification.setEmail(email);
			ekycVerification.setType("Merchant");
			ekycVerification.setIsVerifidePanNO('0');
			ekycVerification.setIsVerifideAccount('0');
			ekycVerification.setIsGstVerifide('0');
			ekycVerification.setIsSignatoryVerifide('0');
			ekycVerification.setIsAadharVerifide('0');
			ekycVerification.setAadharJsonResponse("NA");
			ekycVerification.setAadharNo("NA");
			ekycVerification.setAadharRefId("NA");
			ekycVerification.setAccountJsonResponse("NA");
			ekycVerification.setAccountNo("NA");
			ekycVerification.setBankName("NA");
			ekycVerification.setBranchName("NA");
			ekycVerification.setSignatoryPanJsonResponse("NA");
			ekycVerification.setCinNo("NA");
			ekycVerification.setConsituteOfName("NA");
			ekycVerification.setCreationDate(timestamp);
			ekycVerification.setGstJsonResponse("NA");
			ekycVerification.setGstNo("NA");
			ekycVerification.setIfscCode("NA");
			ekycVerification.setIpAddress("NA");
			ekycVerification.setIsSignatoryVerifide('N');
			ekycVerification.setIsGstVerifide('N');
			ekycVerification.setIsVerifideAccount('N');
			ekycVerification.setIsAadharVerifide('N');
			ekycVerification.setLetLong("NA");
			ekycVerification.setMerchantId((long)0);
			ekycVerification.setPanCompanyBussinessName("NA");
			ekycVerification.setPanCompanyName("NA");
			ekycVerification.setPanJsonResponse("NA");
			ekycVerification.setPanNoCompany("NA");
			ekycVerification.setRegisterBussinessName("N");
			ekycVerification.setRegisterDateCompany("NA");
			ekycVerification.setSignatoryNameOwnerPanNo("NA");
			ekycVerification.setSignatoryOwnerDesignation("NA");
			ekycVerification.setSignatoryPanOwnerName("NA");
			ekycVerification.setSubMerchantId((long)0);
			ekycVerification = ekycVerificationRepository.save(ekycVerification);
			long ekyc_id = ekycVerification.getEkycId();

			map.put("ekyc_id", String.valueOf(ekyc_id));
			map.put("code", ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Save Email and Mobile");

			return map;

		} else if (partner.isPresent()) {

			EkycVerification ekycVerification = new EkycVerification();
			ekycVerification.setMobile(phone);
			ekycVerification.setEmail(email);
			ekycVerification.setType("Partner");
			ekycVerification.setIsVerifidePanNO('0');
			ekycVerification.setIsVerifideAccount('0');
			ekycVerification.setIsGstVerifide('0');
			ekycVerification.setIsSignatoryVerifide('0');
			ekycVerification.setIsAadharVerifide('0');
			ekycVerification.setAadharJsonResponse("NA");
			ekycVerification.setAadharNo("NA");
			ekycVerification.setAadharRefId("NA");
			ekycVerification.setAccountJsonResponse("NA");
			ekycVerification.setAccountNo("NA");
			ekycVerification.setBankName("NA");
			ekycVerification.setBranchName("NA");
			ekycVerification.setSignatoryPanJsonResponse("NA");
			ekycVerification.setCinNo("NA");
			ekycVerification.setConsituteOfName("NA");
			ekycVerification.setCreationDate(timestamp);
			ekycVerification.setGstJsonResponse("NA");
			ekycVerification.setGstNo("NA");
			ekycVerification.setIfscCode("NA");
			ekycVerification.setIpAddress("NA");
			ekycVerification.setIsSignatoryVerifide('N');
			ekycVerification.setIsGstVerifide('N');
			ekycVerification.setIsVerifideAccount('N');
			ekycVerification.setIsAadharVerifide('N');
			ekycVerification.setLetLong("NA");
			ekycVerification.setMerchantId((long)0);
			ekycVerification.setPanCompanyBussinessName("NA");
			ekycVerification.setPanCompanyName("NA");
			ekycVerification.setPanJsonResponse("NA");
			ekycVerification.setPanNoCompany("NA");
			ekycVerification.setRegisterBussinessName("N");
			ekycVerification.setRegisterDateCompany("NA");
			ekycVerification.setSignatoryNameOwnerPanNo("NA");
			ekycVerification.setSignatoryOwnerDesignation("NA");
			ekycVerification.setSignatoryPanOwnerName("NA");
			ekycVerification.setSubMerchantId((long)0);
			ekycVerification = ekycVerificationRepository.save(ekycVerification);
			long ekyc_id = ekycVerification.getEkycId();

			map.put("ekyc_id", String.valueOf(ekyc_id));
			map.put("code", ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Save Email and Mobile");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

			return map;

		} else {

			map.put(ResponseMessage.DESCRIPTION, "Not Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put("phone", phone);
			map.put("email", email);
			return map;

		}
	}

	@Override
	public Map<String, Object> checkMobileNo(String mobile) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();

		Optional<Merchants> optional = merchantsRepository.findByMerchantPhone(Encryption.encString(mobile));
		Optional<Partners> partner = partnersRepository.findByPartnerMobile(Encryption.encString(mobile));

		if (optional.isPresent() && partner.isPresent()) {

			String phone = Encryption.decString(partner.get().getPartnerMobile());
			String email = Encryption.decString(partner.get().getPartnerEmail());
			long partnerId = partner.get().getPartnerId();
			String partnerBusinessName = Encryption.decString(partner.get().getPartnerBusinessName());

			String otp = RandomNumberGenrator.generateWalletPin();

			SMSAPIImpl impl = new SMSAPIImpl();
			impl.registrationOTP(phone, partnerBusinessName, otp);

			map.put(ResponseMessage.DESCRIPTION, "Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put("phone", phone);
			map.put("email", email);
			map.put("otp", otp);
			map.put("partnerId", partnerId);
			map.put("subPartnerId", partnerId);
			map.put("partnerBussiessName-----", partnerBusinessName);

			return map;

		} else if (optional.isPresent()) {

			String phone = Encryption.decString(optional.get().getMerchantPhone());
			String email = Encryption.decString(optional.get().getMerchantEmail());
			long merchantId = optional.get().getMerchantId();
			String merchantBusinessName = Encryption.decString(optional.get().getMerchantBusinessName());

			String otp = RandomNumberGenrator.generateWalletPin();

			SMSAPIImpl impl = new SMSAPIImpl();
			impl.registrationOTP(phone, merchantBusinessName, otp);

			map.put(ResponseMessage.DESCRIPTION, "Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put("phone", phone);
			map.put("email", email);
			map.put("otp", otp);
			map.put("merchantId", merchantId);
			map.put("subMerchantId", merchantId);
			map.put("merchantBussiessName", merchantBusinessName);
			return map;

		} else if (partner.isPresent()) {

			String phone = Encryption.decString(partner.get().getPartnerMobile());
			String email = Encryption.decString(partner.get().getPartnerEmail());
			long partnerId = partner.get().getPartnerId();
			String partnerBusinessName = Encryption.decString(partner.get().getPartnerBusinessName());

			String otp = RandomNumberGenrator.generateWalletPin();

			SMSAPIImpl impl = new SMSAPIImpl();
			impl.registrationOTP(phone, partnerBusinessName, otp);

			map.put(ResponseMessage.DESCRIPTION, "Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put("phone", phone);
			map.put("email", email);
			map.put("otp", otp);
			map.put("partnerId", partnerId);
			map.put("subPartnerId", partnerId);
			map.put("partnerBussiessName", partnerBusinessName);
			return map;

		} else {
			map.put(ResponseMessage.DESCRIPTION, "Not Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put("phone", mobile);
			return map;
		}

	}

	@Override
	public Map<String, Object> checkEmail(String email) throws IOException {

		Map<String, Object> map = new HashMap<String, Object>();

		Optional<Merchants> optional = merchantsRepository.findByMerchantEmail(Encryption.encString(email));
		Optional<Partners> partner = partnersRepository.findByPartnerEmail(Encryption.encString(email));

		if (optional.isPresent() && partner.isPresent()) {

			String otp = RandomNumberGenrator.generateWalletPin();
			String res = emailAPIImpl.sendEmail(email, otp);

			String phone = Encryption.decString(partner.get().getPartnerMobile());
			email = Encryption.decString(partner.get().getPartnerEmail());
			long partnerId = partner.get().getPartnerId();
			String partnerBusinessName = Encryption.decString(partner.get().getPartnerBusinessName());

			map.put(ResponseMessage.DESCRIPTION, "Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put("phone", phone);
			map.put("email", email);
			map.put("otp", otp);
			map.put("partnerId", partnerId);
			map.put("subpartnerId", partnerId);
			map.put("partnerBussiessName", partnerBusinessName);

			return map;
		} else if (optional.isPresent()) {

			String otp = RandomNumberGenrator.generateWalletPin();
			String res = emailAPIImpl.sendEmail(email, otp);

			String phone = Encryption.decString(optional.get().getMerchantPhone());
			email = Encryption.decString(optional.get().getMerchantEmail());
			long merchantId = optional.get().getMerchantId();
			String merchantBusinessName = Encryption.decString(optional.get().getMerchantPhone());

			map.put(ResponseMessage.DESCRIPTION, "Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put("phone", phone);
			map.put("email", email);
			map.put("otp", otp);
			map.put("merchantId", merchantId);
			map.put("subMerchantId", merchantId);
			map.put("merchantBussiessName", merchantBusinessName);

			return map;

		} else if (partner.isPresent()) {

			String otp = RandomNumberGenrator.generateWalletPin();
			String res = emailAPIImpl.sendEmail(email, otp);

			String phone = Encryption.decString(partner.get().getPartnerMobile());
			email = Encryption.decString(partner.get().getPartnerEmail());
			long partnerId = partner.get().getPartnerId();
			String partnerBusinessName = Encryption.decString(partner.get().getPartnerBusinessName());

			map.put(ResponseMessage.DESCRIPTION, "Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put("phone", phone);
			map.put("email", email);
			map.put("otp", otp);
			map.put("partnerId", partnerId);
			map.put("subpartnerId", partnerId);
			map.put("partnerBussiessName", partnerBusinessName);

			return map;

		} else {
			map.put(ResponseMessage.DESCRIPTION, "Not Valid");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put("email", email);
			return map;
		}
	}

	@Override
	public EkycVerification findByEKycId(Long ekyc_id) {
		return ekycVerificationRepository.findById(ekyc_id).get();
	}

	@Override
	public EkycVerification findByEmailAndPhone(String email, String phone) {
		EkycVerification ekycVerification=null;
		try {
			ekycVerification= ekycVerificationRepository.findByEmailAndPhone(email, phone);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return ekycVerification;
	}

	@Override
	public Map<String, Object> updateEkyc(Long id, EkycMerchantRequest ekycMerchantRequest) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			EkycVerification p = ekycVerificationRepository.findById(id).get();
			if (p.getEkycId() == id) {

				Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());

				p.setEmail(ekycMerchantRequest.getEmail());
				p.setMobile(ekycMerchantRequest.getMobile());
				p.setPanCompanyBussinessName(ekycMerchantRequest.getPanCompanyBussinessName());
				p.setPanCompanyName(ekycMerchantRequest.getPanCompanyName());
				p.setPanNoCompany(ekycMerchantRequest.getPanNoCompany());
				p.setPanJsonResponse(ekycMerchantRequest.getPanJsonResponse());
				p.setIsVerifidePanNO(ekycMerchantRequest.getIsVerifidePanNO().charAt(0));
				p.setGstNo(ekycMerchantRequest.getGstNo());
				p.setConsituteOfName(ekycMerchantRequest.getConsituteOfName());
				p.setRegisterBussinessName(ekycMerchantRequest.getRegisterBussinessName());
				p.setGstJsonResponse(ekycMerchantRequest.getGstJsonResponse());
				p.setIsGstVerifide(ekycMerchantRequest.getIsGstVerifide().charAt(0));
				p.setCinNo(ekycMerchantRequest.getCinNo());
				p.setRegisterDateCompany(ekycMerchantRequest.getRegisterDateCompany());
				p.setSignatoryNameOwnerPanNo(ekycMerchantRequest.getSignatoryNameOwnerPanNo());
				p.setSignatoryPanJsonResponse(ekycMerchantRequest.getSignatoryPanJsonResponse());
				p.setSignatoryPanOwnerName(ekycMerchantRequest.getSignatoryPanOwnerName());
				p.setIsSignatoryVerifide(ekycMerchantRequest.getIsSignatoryVerifide().charAt(0));
				p.setSignatoryOwnerDesignation(ekycMerchantRequest.getSignatoryOwnerDesignation());
				p.setIfscCode(ekycMerchantRequest.getIfscCode());
				p.setAccountNo(ekycMerchantRequest.getAccountNo());
				p.setBranchName(ekycMerchantRequest.getBranchName());
				p.setBankName(ekycMerchantRequest.getBankName());
				p.setIsVerifideAccount(ekycMerchantRequest.getIsVerifideAccount().charAt(0));
				p.setAccountJsonResponse(ekycMerchantRequest.getAccountJsonResponse());
				p.setAadharNo(ekycMerchantRequest.getAadharNo());
				p.setAadharRefId(ekycMerchantRequest.getAadharRefId());
				p.setIsAadharVerifide(ekycMerchantRequest.getIsAadharVerifide().charAt(0));
				p.setAadharJsonResponse(ekycMerchantRequest.getAadharJsonResponse());
				p.setMerchantId(ekycMerchantRequest.getMerchantId());
				p.setSubMerchantId(ekycMerchantRequest.getSubMerchantId());
				p.setIpAddress(ekycMerchantRequest.getIpAddress());
				p.setLetLong(ekycMerchantRequest.getLetLong());
				p.setCreationDate(timestamp);

				ekycVerificationRepository.save(p);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Ekyc Details Updated Succefully");
				map.put("ekyc_details", p);
				return map;
			}
		} catch (NoSuchElementException e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Ekyc Id not found " + id);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.DESCRIPTION, "Ekyc Id not found " + id);
		return map;

	}
}
