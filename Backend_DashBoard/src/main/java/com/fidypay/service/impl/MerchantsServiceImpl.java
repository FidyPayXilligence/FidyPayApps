package com.fidypay.service.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.YesBank.YesBankSubMerchant;
import com.fidypay.ServiceProvider.YesBank.SellerAggregator.EncryptionAndDecryptionProduction;
import com.fidypay.dto.ForgetPassword;
import com.fidypay.dto.MerchantEdit;
import com.fidypay.dto.MerchantLogin;
import com.fidypay.dto.SubMerchantDTOSeller;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantFloatManage;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.MerchantOutletTills;
import com.fidypay.entity.MerchantOutlets;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.MerchantServiceCharges;
import com.fidypay.entity.MerchantServiceCommission;
import com.fidypay.entity.MerchantServices;
import com.fidypay.entity.MerchantSubMerchantInfo;
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.MerchantTillTxn;
import com.fidypay.entity.MerchantTills;
import com.fidypay.entity.MerchantType;
import com.fidypay.entity.MerchantUser;
import com.fidypay.entity.MerchantWalletInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.OTPVerification;
import com.fidypay.entity.PartnerServices;
import com.fidypay.entity.Partners;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.CoreTransactionsRepository;
import com.fidypay.repo.MerchantFloatManageRepository;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantOutletTillsRepository;
import com.fidypay.repo.MerchantOutletsRepository;
import com.fidypay.repo.MerchantServiceChargesRepository;
import com.fidypay.repo.MerchantServiceCommissionRepository;
import com.fidypay.repo.MerchantServicesRepository;
import com.fidypay.repo.MerchantSubMerchantInfoRepository;
import com.fidypay.repo.MerchantTillTxnRepository;
import com.fidypay.repo.MerchantTillsRepository;
import com.fidypay.repo.MerchantTypeRepository;
import com.fidypay.repo.MerchantUserRepository;
import com.fidypay.repo.MerchantWalletInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.OTPVerificationRepository;
import com.fidypay.repo.PartnerServiceRepository;
import com.fidypay.repo.PartnersRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.AlternateEmailRequest;
import com.fidypay.request.ChangePasswordRequest;
import com.fidypay.request.FloatRequest;
import com.fidypay.request.ForgetPasswordRequest;
import com.fidypay.request.MerchantActiveRequest;
import com.fidypay.request.MerchantInfoRequest;
import com.fidypay.request.MerchantRegisterRequest;
import com.fidypay.request.MerchantSubMerchantRequest;
import com.fidypay.request.TransactionHistoryRequest;
import com.fidypay.response.MerchantChargesDeatilsResponse;
import com.fidypay.response.MerchantCommissionDeatilsResponse;
import com.fidypay.response.OTPVerificationResponse;
import com.fidypay.response.TransactionHistoryPayload;
import com.fidypay.service.LogInDetailsService;
import com.fidypay.service.MerchantInfoService;
import com.fidypay.service.MerchantsService;
import com.fidypay.service.MerchantsServiceService;
import com.fidypay.service.YBSellerAggregatorService;
import com.fidypay.utils.constants.EmailNotification;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.BasicAuth;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;
import com.fidypay.utils.ex.EmailAPIImpl;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.PartnerServiceValidate;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.SMSAPIImpl;
import com.fidypay.utils.ex.URLGenerater;
import com.fidypay.utils.ex.ValidateBankAccount;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class MerchantsServiceImpl implements MerchantsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantsServiceImpl.class);

	@Autowired
	private MerchantServiceChargesRepository merchantServiceChargesRepository;

	@Autowired
	private MerchantsServiceService merchantsServiceService;

	@Autowired
	private PartnerServiceRepository partnerServiceRepository;

	@Autowired
	private MerchantSubMerchantInfoRepository merchantSubMerchantInfoRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantTillsRepository merchantTillsRepository;

	@Autowired
	private MerchantTillTxnRepository merchantTillTxnRepository;

	@Autowired
	private MerchantFloatManageRepository merchantFloatManageRepository;

	@Autowired
	private MerchantOutletsRepository merchantOutletsRepository;

	@Autowired
	private MerchantOutletTillsRepository merchantOutletTillsRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServicesRepository merchantServicesRepository;

	@Autowired
	private MerchantTypeRepository merchantTypeRepository;

	@Autowired
	private MerchantWalletInfoRepository merchantWalletInfoRepository;

	@Autowired
	private CoreTransactionsRepository coreTransactionsRepository;

	@Autowired
	private PartnersRepository partnersRepository;

	@Autowired
	private YBSellerAggregatorService ybSellerAggregatorService;

	@Autowired
	private PartnerServiceValidate partnerServiceValidate;

	@Autowired
	private ValidateBankAccount validateBankAccount;

	@Autowired
	private MerchantInfoService merchantInfoService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private LogInDetailsService detailsService;

	@Autowired
	private EncryptionAndDecryptionProduction encryptionAndDecryptionProduction;

	@Autowired
	private URLGenerater urlGenerater;

	@Autowired
	private OTPVerificationRepository otpVerificationRepository;

	@Autowired
	private MerchantUserRepository merchantUserRepository;

	@Autowired
	private MerchantServiceCommissionRepository merchantServiceCommissionRepository;

	private static final String MERCHANT_USER_TYPE_SECONDARY = "secondary";

	private static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		// LOGGER.info("Time : " + dateFormat.format(date));
		return dateFormat.format(date).toString();
	}

	@Override
	public String findMerchantDetails(long merchantId) {
		JSONObject jsonObject = new JSONObject();
		String response = null;

		try {
			List list = merchantsRepository.findMerchantDetails(merchantId);
			Iterator it = list.iterator();
			String merchantFirstName = null;
			String merchantLastName = null;
			String merchantAddress = null;
			String merchantCity = null;
			String merchantState = null;
			String merchantPincode = null;
			String merchantBussinessName = null;
			String merchantPhone = null;
			String merchantEmail = null;

			while (it.hasNext()) {
				Object[] obj = (Object[]) it.next();
				merchantFirstName = Encryption.decString((String) obj[0]);
				merchantLastName = Encryption.decString((String) obj[1]);
				merchantAddress = Encryption.decString((String) obj[2]);
				merchantCity = (String) obj[3];
				merchantState = (String) obj[4];
				merchantPincode = (String) obj[5];
				merchantBussinessName = Encryption.decString((String) obj[6]);
				merchantEmail = Encryption.decString((String) obj[7]);
				merchantPhone = Encryption.decString((String) obj[8]);

			}
			jsonObject.put("merchantName", merchantFirstName + " " + merchantLastName);
			jsonObject.put("merchantBussinessName", merchantBussinessName);
			jsonObject.put("merchantPhone", merchantPhone);
			jsonObject.put("merchantEmail", merchantEmail);
			jsonObject.put("merchantAddress", merchantAddress);
			jsonObject.put("merchantPinCode", merchantPincode);
			jsonObject.put("merchantCity", merchantCity);
			jsonObject.put("merchantState", merchantState);
			response = jsonObject.toString();

		} catch (NullPointerException e) {
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			response = jsonObject.toString();
		} catch (Exception e) {
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		}

		return response;
	}

	@Override
	public String findMerchantUserDetails(long merchantId, long merchantUserId) {
		JSONObject jsonObject = new JSONObject();
		String response = null;

		try {
			MerchantUser merchantUser = merchantUserRepository.findByMerchantUserIdAndMerchantId(merchantUserId,
					merchantId);

			if (merchantUser == null) {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant user not exist");
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObject.toString();

				return response;
			}

			String merchantName = Encryption.decString(merchantUser.getMerchantUserName());
			String merchantUserKey = Encryption.decString(merchantUser.getMerchantUserKey());
			String merchantBussinessName = Encryption.decString(merchantUser.getMerchantBusinessName());
			String merchantPhone = Encryption.decString(merchantUser.getMerchantUserMobileNo());
			String merchantEmail = Encryption.decString(merchantUser.getMerchantUserEmail());

			jsonObject.put("merchantName", merchantName);
			jsonObject.put("merchantBussinessName", merchantBussinessName);
			jsonObject.put("merchantPhone", merchantPhone);
			jsonObject.put("merchantEmail", merchantEmail);
			jsonObject.put("merchantUserKey", merchantUserKey);
			response = jsonObject.toString();

		} catch (NullPointerException e) {
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

			response = jsonObject.toString();
		} catch (Exception e) {
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		}

		return response;
	}

	@Override
	public String checkPassword(ChangePasswordRequest changePasswordRequest, long merchantId) {
		JSONObject jsonObject = new JSONObject();
		String response = null;
		try {

			String password = changePasswordRequest.getOldPassword();
			String oldPassword = merchantsRepository.getPassword(merchantId);
			oldPassword = Encryption.decString(oldPassword);
			String newPassword = Encryption.decString(changePasswordRequest.getNewPassword());

			LOGGER.info("oldPassword  " + oldPassword);
			LOGGER.info("Password " + password);
			if (oldPassword == password || oldPassword.equals(password)) {
				if (oldPassword == newPassword || oldPassword.equals(newPassword)) {
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.OLD_AND_NEW_PASSWORD_SAME);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					response = jsonObject.toString();

				} else {
					LOGGER.info("password:" + newPassword);

					newPassword = Encryption.encString(newPassword);
					Merchants merchants = merchantsRepository.findById(merchantId).get();
					merchants.setMerchantPassword(newPassword);
					merchants = merchantsRepository.save(merchants);

					LOGGER.info("Merchant Password Updated");
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.PASSWORD_CHANGED_MESSAGE);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

					response = jsonObject.toString();

				}

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.OLD_PASSWORD_WROUNG);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObject.toString();

			}

		} catch (NullPointerException e) {
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		} catch (Exception e) {
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		}

		return response;

	}

	@Override
	public String loginDashboard(String email, String password) {
		JSONObject jsonObject = new JSONObject();
		String response = null;
		String getEmail = null;
		String getPassword = null;
		String clientSecret = null;
		String clientId = null;
		long merchantId = 0;
		String bussinessName = null;
		String firstName = null;
		BigInteger mTypeId = null;
		String upiQRCode = null;
		String merchantCity = null;
		try {
			Date date = new Date();
			Timestamp trxnDate = new Timestamp(date.getTime());

			String nDate = DateUtil.convertDateToStringWithTimeNew(trxnDate);

			email = Encryption.encString(email);
			List list = merchantsRepository.merchantLoginDashbaord(email);
			LOGGER.info("List " + list.size());
			if (list.size() == 0 || list.isEmpty()) {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, "Email_Id is Incorrect ");
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put("date", nDate);

				response = jsonObject.toString();

			} else {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					getEmail = Encryption.decString((String) obj[2]);
					getPassword = Encryption.decString((String) obj[1]);
					BigInteger merId = (BigInteger) obj[3];
					bussinessName = Encryption.decString((String) obj[4]);
					mTypeId = ((BigInteger) obj[5]);
					firstName = Encryption.decString((String) obj[6]);

					merchantId = merId.longValue();

					upiQRCode = (String) obj[7];
					merchantCity = (String) obj[8];

					MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(merchantId);
					String bName = Encryption.decString(merchantInfo.getMerchantBusinessName());
					bussinessName = bName;
					try {
						upiQRCode = (String) obj[7];
						merchantCity = (String) obj[8];

						if ((upiQRCode == null || upiQRCode.equals("NA"))) {
							upiQRCode = bName + ", " + merchantCity;
						}

					} catch (Exception e) {
						merchantCity = (String) obj[8];
						upiQRCode = bName + ", " + merchantCity;
					}

				}
				if (password.equals(getPassword) || password == getPassword) {

					String uniqueId = detailsService.saveLogInDetails(merchantId, "Merchant Dashboard");

					MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(merchantId);

					clientSecret = Encryption.decString(merchantInfo.getClientSecret());
					String mEmail = getEmail;
					firstName = Encryption.decString(merchantInfo.getUsername());
					clientId = Encryption.decString(String.valueOf(merchantInfo.getClientId()));
					jsonObject.put("clientId", clientId);
					jsonObject.put("clientSecret", clientSecret);
					jsonObject.put("email", mEmail);
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.LOGIN_SUCCESS_MESSAGE);
					jsonObject.put("date", nDate);
					jsonObject.put("merchantTypeId", mTypeId);
					jsonObject.put("bussinessName", bussinessName);
					jsonObject.put("firstName", firstName);
					jsonObject.put("loggedIn", true);
					jsonObject.put("upiQRCode", upiQRCode);
					jsonObject.put("loginUniqueId", uniqueId);
					response = jsonObject.toString();

				} else {
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.PASSWORD_INCORRECT_MESSAGE);
					jsonObject.put("date", nDate);
					jsonObject.put("loggedIn", false);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					response = jsonObject.toString();
				}
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		}

		return response;
	}

	@Override
	public Map<String, Object> checkMerchant(String email, String password) {
		Map<String, Object> map = new HashMap<>();
		try {

			email = Encryption.encString(email);
			String eEmail = email;
			password = Encryption.encString(password);
			String ePassword = password;

			Merchants merchants = merchantsRepository.findByEmail(eEmail);

			String mEmail = merchants.getMerchantEmail();
			String mPssword = merchants.getMerchantPassword();

			if (mEmail.equals(eEmail) && mPssword.equals(ePassword)) {

				map.put("message", "merchant is valid");
				map.put("status", "1");

			} else {
				map.put("message", "clientSecret & password is invalid");
				map.put("status", "2");
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put("message", "clientSecret & password is empty");
		}

		return map;
	}

	@Override
	public String addFloatByAdmin(String floatTo, double amount, double recAmount, String payMode, String desc,
			String txnType, char isRevert, long mId) {
		String response = null;
		try {
			long merchantId = 0;
			FloatRequest floatRequest = new FloatRequest();

			floatRequest.setAmount(amount);
			floatRequest.setDescription(desc);
			floatRequest.setFloatTo(floatTo);
			floatRequest.setIsReverted(isRevert);

			floatRequest.setPayMode(payMode);
			floatRequest.setReceivedAmt(recAmount);
			floatRequest.setTxnType(txnType);

			if (floatRequest != null) {

				if (floatRequest.getFloatTo().equals("merchantTill")) {
					long mTillId = mId;
					List<?> list = merchantTillsRepository.findByMERCHANTTILLID(mTillId);

					Iterator it = list.iterator();
					while (it.hasNext()) {
						Object[] o = (Object[]) it.next();
						amount = (double) o[0];
						merchantId = (long) (((BigInteger) o[2]).longValue());

					}
					response = saveTillInfo(floatRequest, mTillId, merchantId, amount);
					if ("success".equals(response)) {
						LOGGER.info(" Inside Sucees merchantTill");
						isRevert = '1';

						response = saveMerchantInfo(floatRequest, merchantId);

					}

				} else {

					merchantId = mId;

					response = saveMerchantInfo(floatRequest, mId);

				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String saveTillInfo(FloatRequest floatRequest, long mtillId, long merchantId, double amount) {
		String response = null;

		try {
			String trxnType = floatRequest.getTxnType();
			double getAmountByRequest = floatRequest.getAmount();
			String description = floatRequest.getDescription();
			String trxnRefId = new GenerateTrxnRefId().generateWalletRefId();
			Date date = new Date();
			Timestamp timestampDate = new Timestamp(date.getTime());
			double recivedAmount = floatRequest.getReceivedAmt();
			String payMode = floatRequest.getPayMode();
			char isRevert = floatRequest.getIsReverted();

			if (trxnType.equals("CR")) {
				amount = amount + getAmountByRequest;
			} else if (trxnType.equals("DR")) {
				amount = amount - getAmountByRequest;
			}
			amount = Encryption.round_to_decimal(amount, Encryption.decimalPlaces);
			MerchantTills merchantTills = merchantTillsRepository.findById(mtillId).get();
			merchantTills.setTillFloatAmount(amount);
			merchantTills = merchantTillsRepository.save(merchantTills);

			MerchantTillTxn merchantTillTxn = new MerchantTillTxn();
			merchantTillTxn.setMerchantTills(merchantTills);
			merchantTillTxn.setDescription(Encryption.encString(description));
			merchantTillTxn.setTxnType(trxnType);
			merchantTillTxn.setTxnRefNo(Encryption.encString(trxnRefId));
			merchantTillTxn.setTxnDate(timestampDate);
			merchantTillTxn.setTxnAmount(Encryption.encFloat(getAmountByRequest));
			merchantTillTxn.setReceivedAmount(Encryption.encFloat(recivedAmount));
			merchantTillTxn.setPayMode(Encryption.encString(payMode));
			merchantTillTxn.setFloatBalance(Encryption.encFloat(amount));
			merchantTillTxn.setIsReverted(isRevert);

			merchantTillTxn = merchantTillTxnRepository.save(merchantTillTxn);
			response = "success";
		} catch (Exception e) {
			response = "fail";

			e.printStackTrace();
		}

		return response;

	}

	@Override
	public String saveMerchantInfo(FloatRequest floatRequest, long merchantId) {
		String response = null;

		try {
			String trxnType = floatRequest.getTxnType();
			double getAmountByRequest = floatRequest.getAmount();
			String description = floatRequest.getDescription();
			String trxnRefId = new GenerateTrxnRefId().generateWalletRefId();
			Date date = new Date();
			Timestamp timestampDate = new Timestamp(date.getTime());
			double recivedAmount = floatRequest.getReceivedAmt();
			String payMode = floatRequest.getPayMode();
			char isRevert = floatRequest.getIsReverted();

			double amount = 0.0;
			double getDBAmount = 0.0;

			List<Merchants> mlist = merchantsRepository.findListByMerchantId(merchantId);
			for (Merchants m : mlist) {
				getDBAmount = m.getMerchantFloatAmount();
			}

			if (trxnType.equals("CR")) {
				amount = getDBAmount + getAmountByRequest;
			} else if (trxnType.equals("DR")) {
				amount = getDBAmount - getAmountByRequest;
			}
			amount = Encryption.round_to_decimal(amount, Encryption.decimalPlaces);

			Merchants merchants = merchantsRepository.findById(merchantId).get();
			merchants.setMerchantFloatAmount(amount);
			merchants = merchantsRepository.save(merchants);

			MerchantFloatManage floatManage = new MerchantFloatManage();
			floatManage.setMerchants(merchants);
			floatManage.setDescription(Encryption.encString(description));
			floatManage.setAmount(Encryption.encFloat(amount));
			floatManage.setManageDate(timestampDate);
			floatManage.setNewBalance(Encryption.encFloat(amount));
			floatManage.setPayMode(Encryption.encString(payMode));
			floatManage.setPreBalance(Encryption.encFloat(getDBAmount));
			floatManage.setReceivedAmount(Encryption.encFloat(recivedAmount));
			floatManage.setTxnRefNo(Encryption.encString(trxnRefId));
			floatManage.setTxnType(trxnType);
			floatManage.setIsReverted(isRevert);

			floatManage = merchantFloatManageRepository.save(floatManage);
			response = "success";
		} catch (Exception e) {
			response = "fail";

			e.printStackTrace();
		}

		return response;

	}

	@Override
	public String addMerchantByAdmin(String requestParam, String fname, String lname, String businessName, String gend,
			String email, String phone, String merchantPassword, String address, String city, String state,
			String zipCode, String country, String latitude, String longitude, String outletType, String tillPassword) {
		String response = "Fail";
		latitude = convertDecimal(Encryption.decString(latitude));
		longitude = convertDecimal(Encryption.decString(longitude));
		String coordinates = latitude + '&' + longitude;

		char gender = 0;
		gend = Encryption.decString(gend);
		if (gend != null) {
			gender = gend.charAt(0);
		}
		List<?> merchantList = merchantsRepository.findByEmailAndMobileNo(Encryption.decString(email),
				Encryption.decString(phone));

		if (merchantList.isEmpty()) {
			MerchantType merchantType = merchantTypeRepository.findByMerchantTypeId("1");
			LOGGER.info("fname : " + fname);

			response = saveRegisterMerchantInfo(fname, lname, gender, email, address, city, state, zipCode,
					merchantPassword, tillPassword, phone, businessName, merchantType, outletType, coordinates);

		}

		return response;
	}

	@Override
	public String saveRegisterMerchantInfo(String fname, String lname, char gender, String email, String address,
			String city, String state, String zipCode, String merchantPassword, String tillPassword, String phone,
			String businessName, MerchantType mType, String outletType, String coordinates) {
		String response = null;
		try {
			Date date = new Date();
			Timestamp trxnDate = new Timestamp(date.getTime());

			Merchants merchants = new Merchants();
			merchants.setMerchantFromdate(trxnDate);
			merchants.setIsMerchantActive('1');
			merchants.setIsMerchantEmailVerified('1');
			merchants.setIsMerchantPhoneVerified('1');
			merchants.setMerchantLoginCount('0');
			merchants.setIsBankDocVerified('0');
			merchants.setSecondSecQuestionId((long) 0);
			merchants.setMerchantFloatAmount(0.0);
			merchants.setMerchantSettlementFrequency("0");
			merchants.setMerchantCommission(new Long(0));

			merchants.setMerchantFirstname(fname);
			merchants.setMerchantLastname(lname);
			merchants.setMerchantEmail((email));
			merchants.setMerchantAddress1(address);
			merchants.setMerchantPassword((merchantPassword));
			merchants.setMerchantCity(Encryption.decString(city));
			merchants.setMerchantState(Encryption.decString(state));
			merchants.setMerchantCountry("India");
			merchants.setMerchantZipcode(Encryption.decString(zipCode));
			merchants.setGender(gender);
			merchants.setMerchantType(mType);
			merchants.setMerchantPhone((phone));
			merchants.setMerchantBusinessName((businessName));

			merchants = merchantsRepository.save(merchants);
			String tillCode = null;
			String outletCode = null;

			List<String> maxTillCodeList = merchantTillsRepository.findMaxTillCode();
			if (maxTillCodeList.contains(null)) {
				tillCode = "FP111111";
				LOGGER.info("tillCode : " + tillCode);
			} else {
				LOGGER.info("} else {");
				String inputString = maxTillCodeList.get(0);
				LOGGER.info("inputString" + inputString);
				int iTemp = Integer.parseInt(inputString.substring(2));
				tillCode = Integer.toString(++iTemp);
				String one = "FP";
				tillCode = one.concat(tillCode);
				LOGGER.info("tillCode : " + tillCode);
			}
			MerchantTills merchantTills = new MerchantTills();
			merchantTills.setMerchants(merchants);
			merchantTills.setTillFirstname(Encryption.decString(merchants.getMerchantFirstname()));
			merchantTills.setTillLastname(Encryption.decString(merchants.getMerchantLastname()));
			merchantTills.setTillEmail(Encryption.decString(merchants.getMerchantEmail()));
			merchantTills.setTillPhone(Encryption.decString(merchants.getMerchantPhone()));
			merchantTills.setTillPassword(Encryption.decString(merchants.getMerchantPassword()));
			merchantTills.setTillAddress1(Encryption.decString(merchants.getMerchantAddress1()));
			merchantTills.setTillAddress2(Encryption.decString(merchants.getMerchantAddress2()));
			merchantTills.setTillPassword(Encryption.decString(tillPassword));
			merchantTills.setTillCity(merchants.getMerchantCity());
			merchantTills.setTillState(merchants.getMerchantState());
			merchantTills.setTillCountry(merchants.getMerchantCountry());
			merchantTills.setTillZipcode(merchants.getMerchantZipcode());
			merchantTills.setTillCode(tillCode);
			merchantTills.setTillIsVerified('1');
			merchantTills.setTillIsActive('1');
			merchantTills.setTillFromdate(trxnDate);
			merchantTills.setTillIsSupervisor('0');

			merchantTills = merchantTillsRepository.save(merchantTills);

			List<String> merchantOutletList = merchantOutletsRepository.findByMaxOutletCode();
			if (merchantOutletList.contains(null)) {
				LOGGER.info("if (outletList.contains(null)) {");
				outletCode = "HUT111111";
				LOGGER.info("outletCode : " + outletCode);
			} else {
				LOGGER.info("} else {");
				String inputString = merchantOutletList.get(0);
				int iTemp = Integer.parseInt(inputString.substring(3));
				outletCode = Integer.toString(++iTemp);
				String one = "HUT";
				outletCode = one.concat(outletCode);
				LOGGER.info("outletCode : " + outletCode);
			}

			MerchantOutlets merchantOutlets = new MerchantOutlets();
			merchantOutlets.setOutletType(Encryption.decString(outletType));
			merchantOutlets.setOutletCoordinates(coordinates);
			merchantOutlets.setMerchants(merchants);
			merchantOutlets.setOutletContactName((Encryption.decString(merchants.getMerchantFirstname()) + " "
					+ Encryption.decString(merchants.getMerchantLastname())));

			String add = Encryption.decString(merchants.getMerchantAddress1());

			if (null != Encryption.decString(merchants.getMerchantAddress2())) {
				add = add + " " + Encryption.decString(merchants.getMerchantAddress2());
			}

			merchantOutlets.setOutletAddress(add);
			merchantOutlets.setOutletCity(merchants.getMerchantCity());
			merchantOutlets.setOutletState(merchants.getMerchantState());
			merchantOutlets.setOutletCountry(merchants.getMerchantCountry());
			merchantOutlets.setOutletZipcode(merchants.getMerchantZipcode());
			merchantOutlets.setOutletFromdate(trxnDate);
			merchantOutlets.setOutletIsVerified('1');
			merchantOutlets.setOutletIsActie('1');
			merchantOutlets.setOutletCode(outletCode);
			merchantOutlets.setOutletContactNumber(Encryption.decString(merchants.getMerchantPhone()));

			merchantOutlets = merchantOutletsRepository.save(merchantOutlets);

			long tillId = merchantTills.getMerchantTillId();
			merchantTills.setMerchantTillId(tillId);

			MerchantOutletTills merchantOutletTills = new MerchantOutletTills();
			merchantOutletTills.setMerchantTills(merchantTills);

			long outletId = merchantOutlets.getMerchantOutletId();
			merchantOutlets.setMerchantOutletId(outletId);
			merchantOutletTills.setMerchantOutlets(merchantOutlets);
			merchantOutletTills.setOutletTillIsActive('1');

			merchantOutletTills = merchantOutletTillsRepository.save(merchantOutletTills);

			List<ServiceInfo> serviceInfoList = serviceInfoRepository.findByService();
			Iterator<ServiceInfo> iterator = serviceInfoList.iterator();
			while (iterator.hasNext()) {
				ServiceInfo serviceInfo = (ServiceInfo) iterator.next();

				MerchantServices merchantServices = new MerchantServices();

				merchantServices.setServiceInfo(serviceInfo);
				merchantServices.setMerchants(merchants);
				merchantServices.setMerchantOutlets(merchantOutlets);
				merchantServices.setCommision(70.00);
				merchantServices = merchantServicesRepository.save(merchantServices);

			}

			response = "SUCCESS;" + merchants.getMerchantId();

		} catch (Exception e) {
			response = "fail";

			e.printStackTrace();
		}

		return response;
	}

	public String convertDecimal(String tempVar) {
		LOGGER.info("~~~~~~~~~convertDecimal~~~~~~~~~~~");

		try {
			LOGGER.info("convert to Decimal : " + tempVar);
			double d = Double.parseDouble(tempVar);
			DecimalFormat df = new DecimalFormat("#.#####");
			tempVar = df.format(d);
			LOGGER.info("Converted to Decimal : " + tempVar);
		} catch (Exception e) {
			LOGGER.error("Exception : " + e);
		}
		return tempVar;
	}

	@Override
	public Map<String, Object> merchantLoginWithOTP(String mobileNo) {
		Map<String, Object> map = new HashMap<>();
		try {

			Date date = new Date();
			Timestamp trxnDate = new Timestamp(date.getTime());
			String nDate = DateUtil.convertDateToStringWithTimeNew(trxnDate);

			LOGGER.info("nDate: " + nDate);

			String mNo = Encryption.encString(mobileNo);

			LOGGER.info("Mobile " + mobileNo);
			Merchants merchants = merchantsRepository.findByMobileNo(mNo);
			String flowId = "608b9ce415f29c42d12904e9";
			Long mMerchantId = merchants.getMerchantId();
			String mMobileNo = Encryption.decString(merchants.getMerchantPhone());

			if (mobileNo.equals(mMobileNo)) {

				String cId = String.valueOf(mMerchantId);
				String clientId = Encryption.encString(cId);
				String clientSecret = merchants.getMerchantEmail();
				String mEmail = Encryption.decString(clientSecret);

				String mBussinessName = Encryption.decString(merchants.getMerchantBusinessName());
				String agentCode = merchants.getMerchantVatId();

				String otp = RandomNumberGenrator.generateWalletPin();

				SMSAPIImpl impl = new SMSAPIImpl();
				impl.registrationOTP(Encryption.decString(mMobileNo),
						Encryption.decString(merchants.getMerchantFirstname()), otp);

				MerchantSubMerchantInfo info = merchantSubMerchantInfoRepository.findByMerchantId(mMerchantId);
				String vpa = info.getSubMerchantAdditionalInfo();

				String mFirstName = Encryption.decString(merchants.getMerchantFirstname());
				String mLastName = Encryption.decString(merchants.getMerchantLastname());

				BasicAuth basicAuth = new BasicAuth();
				String authorization = basicAuth.createEncodedText(mFirstName,
						Encryption.decString(merchants.getMerchantPassword()));

				map.put("code", ResponseMessage.SUCCESS);
				map.put("description", "Login Successfully");
				map.put("lastName", mLastName);
				map.put("clientId", clientId);
				map.put("mobile", Encryption.decString(merchants.getMerchantPhone()));
				map.put("bussinessName", mBussinessName);
				map.put("otp", otp);
				map.put("clientSecret", clientSecret);
				map.put("authorization", "Basic " + authorization);
				map.put("firstName", mFirstName);
				map.put("email", mEmail);
				map.put("vpa", vpa);
				map.put("merchantId", mMerchantId);
			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Mobile Number is Incorrect");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put("date", trxnDate);
			}
		} catch (Exception e) {
			LOGGER.info("error " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

		}
		return map;
	}

	@Override
	public Map<String, Object> checkMobileNo(String mobileNo) {
		Map<String, Object> map = new HashMap<>();
		try {
			Merchants mMobile = merchantsRepository.findByMobileNo(Encryption.encString(mobileNo));
			if (mMobile == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_NOT_REGISTERED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_ALREADY_REGISTERED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> checkEmailId(String emailid) {
		Map<String, Object> map = new HashMap<>();
		try {
			Merchants mEmail = merchantsRepository.findByEmail(Encryption.encString(emailid));
			if (mEmail == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAILID_NOT_REGISTERED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAILID_ALREADY_REGISTERED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	// ----For LIVE-----------
//	@Override
//	public Map<String, Object> merchantSubMerchantRegister(MerchantSubMerchantRequest merchantSubMerchantRequest,
//			MerchantType merchantType, String partnerKey) {
//		Map<String, Object> map = new HashMap<>();
//		JSONParser parser = new JSONParser();
//		String subMerchantVPA = null;
//		Long merchantId = null;
//		String cId = null;
//		String callBackUrl = null;
//		String redirectURL = null;
//		String domesticCallBackUrl = null;
//		String subMerchantreponse = null;
//		try {
//			String serviceName = "Merchant Registration UPI VA";
//
//			if (!partnerServiceValidate.checkServiceExistOrNot(merchantType.getMerchantTypeId(), serviceName)) {
//				LOGGER.info("-------------- CheckServiceExistOrNot  ---------------------------");
//
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//				return map;
//			}
//
//			String action = "C";
//			String merchnatBussiessName = merchantSubMerchantRequest.getMerchnatBussiessName();
//			String merchantVirtualAddress = "NA";
//
//			String requestUrl1 = "NA";
//			String panNo = merchantSubMerchantRequest.getPanNo();
//			String contactEmail = merchantSubMerchantRequest.getContactEmail();
//			String gstn = merchantSubMerchantRequest.getGstn();
//			String merchantBussinessType = merchantSubMerchantRequest.getMerchantBussinessType();
//			String perDayTxnCount = "9999";
//			String perDayTxnLmt = "9999";
//			String perDayTxnAmt = "9999";
//			String mobile = merchantSubMerchantRequest.getMobile();
//			String address = merchantSubMerchantRequest.getAddress();
//			String state = merchantSubMerchantRequest.getState();
//			String city = merchantSubMerchantRequest.getCity();
//			String pinCode = merchantSubMerchantRequest.getPinCode();
//			String subMerchantId = "NA";
//			String MCC = merchantSubMerchantRequest.getMCC();
//			String firstName = merchantSubMerchantRequest.getFirstName();
//			String lastName = merchantSubMerchantRequest.getLastName();
//			String password = merchantSubMerchantRequest.getPassword();
//			String bankName = merchantSubMerchantRequest.getMerchantBankName();
//			String accountNumber = merchantSubMerchantRequest.getAccountNumber();
//			String bankBranch = merchantSubMerchantRequest.getBankBranch();
//			String ifsc = merchantSubMerchantRequest.getIfsc();
//			// String merchantGenre = merchantSubMerchantRequest.getMerchantGenre();
//
//			String dob = merchantSubMerchantRequest.getDob();
//			String doi = merchantSubMerchantRequest.getDoi();
//			String alternateAddress = merchantSubMerchantRequest.getAlternateAddress();
//			String longitude = merchantSubMerchantRequest.getLongitude();
//			String latitude = merchantSubMerchantRequest.getLatitude();
//
//			String merchantGenre = "OFFLINE";
//			String agentCode = "NA";
//
//			Merchants mMobile = merchantsRepository
//					.findByMobileNo(Encryption.encString(merchantSubMerchantRequest.getMobile()));
//
//			Merchants mEmail = merchantsRepository
//					.findByEmail(Encryption.encString(merchantSubMerchantRequest.getContactEmail()));
//
//			LOGGER.info("mMobile:" + mMobile);
//			if (mMobile == null) {
//
//				LOGGER.info("mEmail: " + mEmail);
//				if (mEmail == null) {
//
//					Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());
//
//					String stateName = getState(state);
//
//					if (merchnatBussiessName == "" || action == "" || merchantVirtualAddress == "" || requestUrl1 == ""
//							|| panNo == "" || contactEmail == "" || perDayTxnCount == "" || merchantBussinessType == ""
//							|| perDayTxnLmt == "" || perDayTxnAmt == "" || pinCode == "" || MCC == null
//							|| merchnatBussiessName == null || action == null || merchantVirtualAddress == null
//							|| requestUrl1 == null || panNo == null || contactEmail == null || perDayTxnCount == null
//							|| merchantBussinessType == null || perDayTxnLmt == null || perDayTxnAmt == null
//							|| pinCode == null || MCC == null || firstName == null || lastName == null
//							|| password == null || bankName == null || accountNumber == null || ifsc == null
//							|| merchantGenre == null) {
//						map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
//						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
//						LOGGER.info("Bad Request");
//
//					} else {
//						String turnOverType = "LARGE";
//						String partnerRefrenceNumber = YesBankSubMerchant.generateRandomMerchantId();
//						String actionC = "ADD_PARTNER_SELLER";
//						String vpa = "NA";
//						String sellerStatus = "NA";
//						String qrString = "NA";
//
//						String subMerchantId2 = "AP" + GenerateTrxnRefId.getAlphaNumericString();
//
//						SubMerchantDTOSeller subMerchantDTOSeller = new SubMerchantDTOSeller();
//						subMerchantDTOSeller.setAction(actionC);
//						subMerchantDTOSeller.setMerchantBussiessName(merchnatBussiessName);
//						subMerchantDTOSeller.setPanNo(panNo);
//						subMerchantDTOSeller.setContactEmail(contactEmail);
//						subMerchantDTOSeller.setGstn(gstn);
//						subMerchantDTOSeller.setPerDayTxnCount(perDayTxnCount);
//						subMerchantDTOSeller.setMerchantBussinessType(merchantBussinessType);
//						subMerchantDTOSeller.setPerDayTxnLmt(perDayTxnLmt);
//						subMerchantDTOSeller.setPerDayTxnAmt(perDayTxnAmt);
//						subMerchantDTOSeller.setMobile(mobile);
//						subMerchantDTOSeller.setAddress(address);
//						subMerchantDTOSeller.setState(state);
//						subMerchantDTOSeller.setCity(city);
//						subMerchantDTOSeller.setPinCode(pinCode);
//						subMerchantDTOSeller.setSubMerchantId(subMerchantId);
//						subMerchantDTOSeller.setMCC(MCC);
//						subMerchantDTOSeller.setSubMerchantBankName(bankName);
//						subMerchantDTOSeller.setSubMerchantBankAccount(accountNumber);
//						subMerchantDTOSeller.setSubMerchantIfscCode(ifsc);
//						subMerchantDTOSeller.setMerchantGenre(merchantGenre);
//						subMerchantDTOSeller.setName(firstName + " " + lastName);
//						subMerchantDTOSeller.setSubMerchantBankBranch(bankBranch);
//						subMerchantDTOSeller.setMerchantVirtualAddress(merchantVirtualAddress);
//
//						subMerchantDTOSeller.setAlternateAddress(alternateAddress);
//						subMerchantDTOSeller.setLatitude(latitude);
//						subMerchantDTOSeller.setLongitude(longitude);
//						subMerchantDTOSeller.setDob(dob);
//						subMerchantDTOSeller.setDoi(doi);
//
//						String sellerCreationReq = "{\r\n" + "	\"partnerReferenceNo\": \"" + partnerRefrenceNumber
//								+ "\",\r\n" + "	\"actionName\": \"" + actionC + "\",\r\n" + "	\"partnerKey\": \""
//								+ partnerKey + "\",\r\n" + "	\"p1\": \"" + merchnatBussiessName + "\",\r\n"
//								+ "	\"p2\": \"" + firstName + " " + lastName + "\",\r\n" + "	\"p3\": \""
//								+ subMerchantId2 + "\",\r\n" + "	\"p4\": \"" + mobile + "\",\r\n" + "	\"p5\": \""
//								+ contactEmail + "\",\r\n" + "	\"p6\": \"" + MCC + "\",\r\n" + "	\"p7\": \""
//								+ turnOverType + "\",\r\n" + "	\"p8\": \"" + merchantGenre + "\",\r\n" + "	\"p9\": \""
//								+ merchantBussinessType + "\",\r\n" + "	\"p10\": \"" + city + "\",\r\n" + "	\"p11\": \""
//								+ city + "\",\r\n" + "	\"p12\": \"" + state + "\",\r\n" + "	\"p13\": \"" + pinCode
//								+ "\",\r\n" + "	\"p14\": \"" + panNo + "\",\r\n" + "	\"p15\": \"" + gstn + "\",\r\n"
//								+ "	\"p16\": \"" + accountNumber + "\",\r\n" + "	\"p17\": \"" + ifsc + "\",\r\n"
//								+ "	\"p18\": \"" + latitude + "\",\r\n" + "	\"p19\": \"" + longitude + "\",\r\n"
//								+ "	\"p20\": \"" + address + "\",\r\n" + "	\"p21\": \"" + alternateAddress + "\",\r\n"
//								+ "	\"p22\": \"\",\n" + "	\"p23\": \"\",\n" + "	\"p24\": \"\",\n"
//								+ "	\"p25\": \"\",\n" + "	\"p26\": \"" + dob + "\",\n" + "	\"p27\": \"" + doi
//								+ "\",\n" + "	\"p28\": \"\"\n" + "\n" + "}";
//
//						String sellerCreationResp = encryptionAndDecryptionProduction.getEncDec(sellerCreationReq,
//								partnerKey);
//						LOGGER.info("  sellerCreationResp -- " + sellerCreationResp);
//
//						JSONObject bankDetailJSON = new JSONObject();
//						bankDetailJSON.put("subMerchantIfscCode", ifsc);
//						bankDetailJSON.put("subMerchantBankName", bankName);
//						bankDetailJSON.put("subMerchantBankAccount", accountNumber);
//						String bankDetails = bankDetailJSON.toString();
//
//						org.json.JSONObject jsonObject = new org.json.JSONObject(sellerCreationResp);
//						String status = jsonObject.getString("status");
//						String responseCode = jsonObject.getString("responseCode");
//						String responseMessage = jsonObject.getString("responseMessage");
//
//						if (status.equalsIgnoreCase("SUCCESS") || responseCode.equals("00")) {
//							String yphubName = jsonObject.getString("ypHubUsername");
//							String sellerIdentifier = jsonObject.getString("sellerIdentifier");
//							LOGGER.info("  sellerIdentifier -- " + sellerIdentifier);
//
//							// Fetch QR
//
//							String fetchRequest = "{\r\n" + "\"requestId\": \"1\",\r\n"
//									+ "\"actionName\": \"FETCH_QR\",\r\n" + "\"partnerKey\": \"" + partnerKey
//									+ "\",\r\n" + "\"p1\": \"" + sellerIdentifier + "\"\r\n" + "}";
//							LOGGER.info("  fetchRequest -- " + fetchRequest);
//
//							String fetchResponse = encryptionAndDecryptionProduction.getEncDec(fetchRequest,
//									partnerKey);
//
//							LOGGER.info("  fetchResponse -- " + fetchResponse);
//
//							org.json.JSONObject jsonObj = new org.json.JSONObject(fetchResponse);
//
//							String fetchStatus = jsonObj.getString("status");
//							String fetchResponseCode = jsonObj.getString("responseCode");
//							String fetchResponseMessage = jsonObj.getString("responseMessage");
//							LOGGER.info("  fetchStatus -- " + fetchStatus);
//
//							if (fetchStatus.equalsIgnoreCase("SUCCESS") || fetchResponseCode.equals("00")) {
//								LOGGER.info("  fetchStatus -- " + fetchStatus);
//
//								vpa = jsonObj.getString("vpa");
//								qrString = jsonObj.getString("qrString");
//								sellerStatus = jsonObj.getString("sellerStatus");
//
//								subMerchantVPA = vpa;
//
//								Merchants merchants = new Merchants();
//								merchants.setMerchantFromdate(trxnDate);
//								merchants.setIsMerchantActive('1');
//								merchants.setIsMerchantEmailVerified('1');
//								merchants.setIsMerchantPhoneVerified('1');
//								merchants.setMerchantLoginCount('0');
//								merchants.setIsBankDocVerified('0');
//								merchants.setSecondSecQuestionId((long) 0);
//								merchants.setMerchantFloatAmount(0.0);
//								merchants.setMerchantSettlementFrequency("0");
//								merchants.setMerchantCommission(new Long(0));
//								merchants.setMerchantNationality(partnerKey);
//								merchants.setMerchantFirstname(Encryption.encString(firstName));
//								merchants.setMerchantLastname(Encryption.encString(lastName));
//								merchants.setMerchantEmail(Encryption.encString(contactEmail));
//								merchants.setMerchantAddress1(Encryption.encString(address));
//								merchants.setMerchantAddress2(Encryption.encString(address));
//								merchants.setMerchantPassword(Encryption.encString(password));
//								merchants.setMerchantCity(city);
//								merchants.setMerchantState(stateName);
//								merchants.setMerchantCountry("India");
//								merchants.setMerchantZipcode(pinCode);
//								merchants.setGender('M');
//								merchants.setMerchantType(merchantType);
//								merchants.setMerchantPhone(Encryption.encString(mobile));
//								merchants.setMerchantBusinessName(Encryption.encString(merchnatBussiessName));
//
//								// Set Alternate Email
//								Partners partners = partnersRepository.finByPartnerBussinessName(
//										Encryption.encString(merchantType.getMerchantTypeName()));
//
//								if (partners == null) {
//									String altEmail = "NULL";
//
//									callBackUrl = "NULL";
//									redirectURL = "NULL";
//									domesticCallBackUrl = "NULL";
//								} else {
//									String altEmail = partners.getPartnerAlternateEmail();
//
//									org.json.JSONObject object = new org.json.JSONObject(altEmail);
//
//									callBackUrl = object.getString("callBackUrl");
//									redirectURL = object.getString("redirectURL");
//									domesticCallBackUrl = object.getString("domesticCallBackUrl");
//								}
//								LOGGER.info("callBackUrl: " + callBackUrl);
//								LOGGER.info("redirectURL: " + redirectURL);
//								LOGGER.info("domesticCallBackUrl: " + domesticCallBackUrl);
//
//								merchants.setMerchantAccountNo(Encryption.encString(accountNumber));
//								merchants.setMerchantBankName(bankName);
//								merchants.setMerchantBankCode(Encryption.encString(ifsc));
//								merchants.setMerchantBankBranch(bankBranch);
//
//								AlternateEmailRequest alternateEmailRequest = new AlternateEmailRequest();
//								alternateEmailRequest.setCallBackUrl(callBackUrl);
//								alternateEmailRequest.setRedirectURL(redirectURL);
//								alternateEmailRequest.setDomesticCallBackUrl(domesticCallBackUrl);
//								alternateEmailRequest.setPgMerchantId(sellerIdentifier);
//								alternateEmailRequest.setMCC(MCC);
//
//								String altJson = new Gson().toJson(alternateEmailRequest);
//
//								merchants.setMerchantAlternateEmail(altJson);
//
//								merchants = merchantsRepository.save(merchants);
//								LOGGER.info("Merchant Table Saved");
//
//								// ------Create virtual
//								// account-----------------------------------------------------
//								String vaIfsc = null;
//								String vaNumber = null;
//								String res = getVirtualAccountNumber(merchants.getMerchantId(),
//										merchantSubMerchantRequest.getMobile());
//
//								LOGGER.info("res: " + res);
//
//								org.json.JSONObject object = new org.json.JSONObject(res);
//
//								String status2 = object.getString("status");
//								if (status2.equals("Success")) {
//
//									vaIfsc = object.getString("VA_Ifsc");
//									vaNumber = object.getString("VA_Number");
//								}
//
//								MerchantWalletInfo merchantWalletInfo = new MerchantWalletInfo();
//								merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString(vaNumber));
//
//								merchantWalletInfo.setMerchWalletFromDate(trxnDate);
//								merchantWalletInfo.setMerchWalletToDate(trxnDate);
//								merchantWalletInfo.setMerchWalletAmount(0.0);
//								merchantWalletInfo.setMerchPinLastChangedDate(trxnDate);
//								merchantWalletInfo.setIsIdProofVerified('Y');
//								merchantWalletInfo.setIsAddressProofVerified('Y');
//								merchantWalletInfo.setIsMerchKycComplete('Y');
//								merchantWalletInfo.setIsMerchWalletActive('Y');
//								merchantWalletInfo.setIsItaxVerified('Y');
//								merchantWalletInfo.setIsPermitVerified('Y');
//								merchantWalletInfo.setIsExtraDocVerified('Y');
//								merchantWalletInfo.setMerchWalletPin(vaIfsc);
//
//								merchantWalletInfo.setMerchants(merchants);
//								merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);
//
//								LOGGER.info("Merchant Wallet Info Table Saved");
//
//								merchantId = merchants.getMerchantId();
//								cId = String.valueOf(merchantId);
//
//								List<PartnerServices> partnerServicesList = partnerServiceRepository
//										.findAllPartnerServiceByPartnerId(partners.getPartnerId());
//								LOGGER.info("partner service list " + partnerServicesList.size());
//								if (partnerServicesList.size() > 0) {
//									for (PartnerServices partnerServices : partnerServicesList) {
//										// save merchant service
//										LOGGER.info("ServiceId: " + partnerServices.getServiceId());
//
//										ServiceInfo serviceInfo = serviceInfoRepository
//												.findById(partnerServices.getServiceId()).get();
//										String sName = Encryption.decString(serviceInfo.getServiceName());
//
//										if ((sName.equals("Merchant Registration")
//												|| sName.equals("Merchant Registration VA")
//												|| sName.equals("Merchant Registration UPI VA"))) {
//											LOGGER.info("Inside Non Assigned Services");
//										} else {
//											MerchantService merchantService = merchantsServiceService
//													.assignMerchantService(partnerServices.getPartnerServiceId(),
//															partnerServices.getAmc(), partnerServices.getOtc(),
//															merchants.getMerchantId(),
//															partnerServices.getSerrviceProviderId(),
//															partnerServices.getServiceId(),
//															partnerServices.getSubscriptionAmount(),
//															partnerServices.getSubscriptionCycle(),
//															partnerServices.getServiceType());
//										}
//									}
//								}
//
//								LOGGER.info("merchantId: " + merchantId);
//
//								String name = firstName + " " + lastName;
//
//								MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(
//										merchantId, action, vpa, bankDetails, sellerIdentifier, sellerCreationResp,
//										sellerCreationReq, partnerKey, qrString, fetchStatus, sellerStatus, trxnDate,
//										merchnatBussiessName, new Gson().toJson(subMerchantDTOSeller), name, mobile,
//										contactEmail, panNo, MCC, gstn, "Yes Bank");
//
//								org.json.simple.JSONObject jsonBankDetails = new org.json.simple.JSONObject();
//								jsonBankDetails.put("subMerchantIfscCode", ifsc);
//								jsonBankDetails.put("subMerchantBankName", bankName);
//								jsonBankDetails.put("subMerchantBankAccount", accountNumber);
//
//								String clientId = Encryption.encString(cId);
//								String clientSecret = merchants.getMerchantEmail();
//								String merchantFirstName = Encryption.decString(merchants.getMerchantFirstname());
//								String merchantLastName = Encryption.decString(merchants.getMerchantLastname());
//
//								BasicAuth basicAuth = new BasicAuth();
//
//								String authorization = basicAuth.createEncodedText(merchantFirstName,
//										Encryption.decString(merchants.getMerchantPassword()));
//
//								String userId = merchantFirstName.toUpperCase()
//										+ merchantLastName.substring(0, 3).toUpperCase() + merchantId;
//								MerchantInfoRequest merchantInfoRequest = new MerchantInfoRequest();
//								merchantInfoRequest.setBankIdJson("NA");
//								merchantInfoRequest.setBankIdUpi("NA");
//								merchantInfoRequest.setBbpsCallbackUrl("NA");
//								merchantInfoRequest.setClientId(clientId);
//								merchantInfoRequest.setClientSecret(clientSecret);
//								merchantInfoRequest.seteCollectCorpId("NA");
//								merchantInfoRequest.seteCollectNotifyUrl("NA");
//								merchantInfoRequest.seteCollectValidateUrl("NA");
//								merchantInfoRequest.seteNachCallbackUrl("NA");
//								merchantInfoRequest.seteNachRedirectUrl("NA");
//								merchantInfoRequest.setImageUrl("NA");
//								merchantInfoRequest.setMerchantBussinessName(
//										Encryption.decString(merchants.getMerchantBusinessName()));
//								merchantInfoRequest.setMerchantId(merchants.getMerchantId());
//								merchantInfoRequest.setPartnerKeyUpi("NA");
//								merchantInfoRequest.setPassword(Encryption.decString(merchants.getMerchantPassword()));
//								merchantInfoRequest.setPayoutCallbackUrl(domesticCallBackUrl);
//								merchantInfoRequest.setPgCallbackUrl("NA");
//								merchantInfoRequest.setPgRedirectUrl(redirectURL);
//								merchantInfoRequest.setUpiCallbackUrl(callBackUrl);
//								merchantInfoRequest.setUsername(Encryption.decString(merchants.getMerchantFirstname()));
//								merchantInfoService.saveMerchantInfo(merchantInfoRequest);
//								LOGGER.info("Save Merchant Info Table Data");
//
//								map.put("userId", userId);
//								map.put("virtualAccountNo",
//										Encryption.decString(merchantWalletInfo.getMerchWalletAccountNo()));
//								map.put("ifscCode", merchantWalletInfo.getMerchWalletPin());
//								map.put("clientId", clientId);
//								map.put("clientSecret", clientSecret);
//								map.put("authorization", "Basic " + authorization);
//								map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//								map.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
//								map.put("subMerchantVPA", subMerchantVPA);
//								map.put("qrString", qrString);
//								map.put("status", "Success");
//
//							} else {
//								vpa = jsonObj.getString("vpa");
//								qrString = jsonObj.getString("qrString");
//								sellerStatus = jsonObj.getString("sellerStatus");
//
//								subMerchantVPA = vpa;
//
//								Merchants merchants = new Merchants();
//								merchants.setMerchantFromdate(trxnDate);
//								merchants.setIsMerchantActive('1');
//								merchants.setIsMerchantEmailVerified('1');
//								merchants.setIsMerchantPhoneVerified('1');
//								merchants.setMerchantLoginCount('0');
//								merchants.setIsBankDocVerified('0');
//								merchants.setSecondSecQuestionId((long) 0);
//								merchants.setMerchantFloatAmount(0.0);
//								merchants.setMerchantSettlementFrequency("0");
//								merchants.setMerchantCommission(new Long(0));
//
//								merchants.setMerchantFirstname(Encryption.encString(firstName));
//								merchants.setMerchantLastname(Encryption.encString(lastName));
//								merchants.setMerchantEmail(Encryption.encString(contactEmail));
//								merchants.setMerchantAddress1(Encryption.encString(address));
//								merchants.setMerchantAddress2(Encryption.encString(address));
//								merchants.setMerchantPassword(Encryption.encString(password));
//								merchants.setMerchantCity(city);
//								merchants.setMerchantState(stateName);
//								merchants.setMerchantCountry("India");
//								merchants.setMerchantZipcode(pinCode);
//								merchants.setGender('M');
//								merchants.setMerchantType(merchantType);
//								merchants.setMerchantPhone(Encryption.encString(mobile));
//								merchants.setMerchantBusinessName(Encryption.encString(merchnatBussiessName));
//
//								// Set Alternate Email
//								Partners partners = partnersRepository.finByPartnerBussinessName(
//										Encryption.encString(merchantType.getMerchantTypeName()));
//
//								if (partners == null) {
//									String altEmail = "NULL";
//
//									callBackUrl = "NULL";
//									redirectURL = "NULL";
//									domesticCallBackUrl = "NULL";
//								} else {
//									String altEmail = partners.getPartnerAlternateEmail();
//
//									org.json.JSONObject object = new org.json.JSONObject(altEmail);
//
//									callBackUrl = object.getString("callBackUrl");
//									redirectURL = object.getString("redirectURL");
//									domesticCallBackUrl = object.getString("domesticCallBackUrl");
//								}
//								LOGGER.info("callBackUrl: " + callBackUrl);
//								LOGGER.info("redirectURL: " + redirectURL);
//								LOGGER.info("domesticCallBackUrl: " + domesticCallBackUrl);
//
//								merchants.setMerchantAccountNo(Encryption.encString(accountNumber));
//								merchants.setMerchantBankName(bankName);
//								merchants.setMerchantBankCode(Encryption.encString(ifsc));
//								merchants.setMerchantBankBranch(bankBranch);
//
//								AlternateEmailRequest alternateEmailRequest = new AlternateEmailRequest();
//								alternateEmailRequest.setCallBackUrl(callBackUrl);
//								alternateEmailRequest.setRedirectURL(redirectURL);
//								alternateEmailRequest.setDomesticCallBackUrl(domesticCallBackUrl);
//								alternateEmailRequest.setPgMerchantId(sellerIdentifier);
//								alternateEmailRequest.setMCC(MCC);
//
//								String altJson = new Gson().toJson(alternateEmailRequest);
//								merchants.setMerchantAlternateEmail(altJson);
//								merchants = merchantsRepository.save(merchants);
//								LOGGER.info("Merchant Table Saved");
//
//								// ------Create virtual
//								// account-----------------------------------------------------
//								String vaIfsc = null;
//								String vaNumber = null;
//								String res = getVirtualAccountNumber(merchants.getMerchantId(),
//										merchantSubMerchantRequest.getMobile());
//
//								LOGGER.info("res: " + res);
//
//								org.json.JSONObject object = new org.json.JSONObject(res);
//
//								String status2 = object.getString("status");
//								if (status2.equals("Success")) {
//
//									vaIfsc = object.getString("VA_Ifsc");
//									vaNumber = object.getString("VA_Number");
//								}
//
//								MerchantWalletInfo merchantWalletInfo = new MerchantWalletInfo();
//								merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString(vaNumber));
//								merchantWalletInfo.setMerchWalletFromDate(trxnDate);
//								merchantWalletInfo.setMerchWalletToDate(trxnDate);
//								merchantWalletInfo.setMerchWalletAmount(0.0);
//								merchantWalletInfo.setMerchPinLastChangedDate(trxnDate);
//								merchantWalletInfo.setIsIdProofVerified('Y');
//								merchantWalletInfo.setIsAddressProofVerified('Y');
//								merchantWalletInfo.setIsMerchKycComplete('Y');
//								merchantWalletInfo.setIsMerchWalletActive('Y');
//								merchantWalletInfo.setIsItaxVerified('Y');
//								merchantWalletInfo.setIsPermitVerified('Y');
//								merchantWalletInfo.setIsExtraDocVerified('Y');
//								merchantWalletInfo.setMerchWalletPin(vaIfsc);
//
//								merchantWalletInfo.setMerchants(merchants);
//								merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);
//
//								LOGGER.info("Merchant Wallet Info Table Saved");
//
//								merchantId = merchants.getMerchantId();
//								cId = String.valueOf(merchantId);
//
//								LOGGER.info("merchantId: " + merchantId);
//
//								merchantWalletInfo.setMerchants(merchants);
//								merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);
//
//								LOGGER.info("Merchant Wallet Info Table Saved");
//
//								String name = firstName + " " + lastName;
//								MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(
//										merchantId, action, vpa, bankDetails, sellerIdentifier, sellerCreationResp,
//										sellerCreationReq, partnerKey, qrString, fetchStatus, sellerStatus, trxnDate,
//										merchnatBussiessName, new Gson().toJson(subMerchantDTOSeller), name, mobile,
//										contactEmail, panNo, MCC, gstn, "Yes Bank");
//
//								org.json.simple.JSONObject jsonBankDetails = new org.json.simple.JSONObject();
//								jsonBankDetails.put("subMerchantIfscCode", ifsc);
//								jsonBankDetails.put("subMerchantBankName", bankName);
//								jsonBankDetails.put("subMerchantBankAccount", accountNumber);
//
//								String clientId = Encryption.encString(cId);
//								String clientSecret = merchants.getMerchantEmail();
//								String merchantFirstName = Encryption.decString(merchants.getMerchantFirstname());
//								String merchantLastName = Encryption.decString(merchants.getMerchantLastname());
//
//								BasicAuth basicAuth = new BasicAuth();
//
//								String authorization = basicAuth.createEncodedText(merchantFirstName,
//										Encryption.decString(merchants.getMerchantPassword()));
//
//								String userId = merchantFirstName.toUpperCase()
//										+ merchantLastName.substring(0, 3).toUpperCase() + merchantId;
//								MerchantInfoRequest merchantInfoRequest = new MerchantInfoRequest();
//								merchantInfoRequest.setBankIdJson("NA");
//								merchantInfoRequest.setBankIdUpi("NA");
//								merchantInfoRequest.setBbpsCallbackUrl("NA");
//								merchantInfoRequest.setClientId(clientId);
//								merchantInfoRequest.setClientSecret(clientSecret);
//								merchantInfoRequest.seteCollectCorpId("NA");
//								merchantInfoRequest.seteCollectNotifyUrl("NA");
//								merchantInfoRequest.seteCollectValidateUrl("NA");
//								merchantInfoRequest.seteNachCallbackUrl("NA");
//								merchantInfoRequest.seteNachRedirectUrl("NA");
//								merchantInfoRequest.setImageUrl("NA");
//								merchantInfoRequest.setMerchantBussinessName(
//										Encryption.decString(merchants.getMerchantBusinessName()));
//								merchantInfoRequest.setMerchantId(merchants.getMerchantId());
//								merchantInfoRequest.setPartnerKeyUpi("NA");
//								merchantInfoRequest.setPassword(Encryption.decString(merchants.getMerchantPassword()));
//								merchantInfoRequest.setPayoutCallbackUrl(domesticCallBackUrl);
//								merchantInfoRequest.setPgCallbackUrl("NA");
//								merchantInfoRequest.setPgRedirectUrl(redirectURL);
//								merchantInfoRequest.setUpiCallbackUrl(callBackUrl);
//								merchantInfoRequest.setUsername(Encryption.decString(merchants.getMerchantFirstname()));
//								merchantInfoService.saveMerchantInfo(merchantInfoRequest);
//								LOGGER.info("Save Merchant Info Table Data");
//
//								map.put("userId", userId);
//								map.put("virtualAccountNo",
//										Encryption.decString(merchantWalletInfo.getMerchWalletAccountNo()));
//								map.put("ifscCode", merchantWalletInfo.getMerchWalletPin());
//								map.put("clientId", clientId);
//								map.put("clientSecret", clientSecret);
//								map.put("authorization", "Basic " + authorization);
//								map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//								map.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
//								map.put("subMerchantVPA", subMerchantVPA);
//								map.put("qrString", qrString);
//								map.put("status", "Success");
//							}
//
//						}
//
//						else {
//
//							map.put(ResponseMessage.DESCRIPTION, responseMessage);
//							map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//							map.put("status", "Failed");
//						}
//					}
//
//				}
//
//				else {
//					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//					map.put(ResponseMessage.DESCRIPTION, "Merchant Email Id Already Registered");
//				}
//
//			}
//
//			else {
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, "Merchant Mobile Number Already Registered");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
//			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
//		}
//		return map;
//	}

	// --- For UAT-----------------------------
	@Override
	public Map<String, Object> merchantSubMerchantRegister(MerchantSubMerchantRequest merchantSubMerchantRequest,
			MerchantType merchantType, String partnerKey) {
		Map<String, Object> map = new HashMap<>();
		JSONParser parser = new JSONParser();
		String subMerchantVPA = null;
		Long merchantId = null;
		String cId = null;
		String callBackUrl = null;
		String redirectURL = null;
		String domesticCallBackUrl = null;
		String subMerchantreponse = null;
		try {
			String serviceName = "Merchant Registration UPI VA";

			if (!partnerServiceValidate.checkServiceExistOrNot(merchantType.getMerchantTypeId(), serviceName)) {
				LOGGER.info("-------------- CheckServiceExistOrNot  ---------------------------");

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (validateBankAccount
					.verifyIfscCode(merchantSubMerchantRequest.getIfsc()) == ResponseMessage.INVALID_IFSCCODE) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "IFSC code not valid");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			String action = "C";
			String merchnatBussiessName = merchantSubMerchantRequest.getMerchnatBussiessName();
			String merchantVirtualAddress = "NA";

			String requestUrl1 = "NA";
			String panNo = merchantSubMerchantRequest.getPanNo();
			String contactEmail = merchantSubMerchantRequest.getContactEmail().toLowerCase();
			String gstn = merchantSubMerchantRequest.getGstn();
			String merchantBussinessType = merchantSubMerchantRequest.getMerchantBussinessType();
			String perDayTxnCount = "9999";
			String perDayTxnLmt = "9999";
			String perDayTxnAmt = "9999";
			String mobile = merchantSubMerchantRequest.getMobile();
			String address = merchantSubMerchantRequest.getAddress();
			String state = merchantSubMerchantRequest.getState();
			String city = merchantSubMerchantRequest.getCity();
			String pinCode = merchantSubMerchantRequest.getPinCode();
			String subMerchantId = "NA";
			String MCC = merchantSubMerchantRequest.getMCC();
			String firstName = merchantSubMerchantRequest.getFirstName();
			String lastName = merchantSubMerchantRequest.getLastName();
			String password = merchantSubMerchantRequest.getPassword();
			String bankName = merchantSubMerchantRequest.getMerchantBankName();
			String accountNumber = merchantSubMerchantRequest.getAccountNumber();
			String bankBranch = merchantSubMerchantRequest.getBankBranch();
			String ifsc = merchantSubMerchantRequest.getIfsc();
			// String merchantGenre = merchantSubMerchantRequest.getMerchantGenre();

			String dob = merchantSubMerchantRequest.getDob();
			String doi = merchantSubMerchantRequest.getDoi();
			String alternateAddress = merchantSubMerchantRequest.getAlternateAddress();
			String longitude = merchantSubMerchantRequest.getLongitude();
			String latitude = merchantSubMerchantRequest.getLatitude();

			String merchantGenre = "OFFLINE";
			String agentCode = "NA";

			Merchants mMobile = merchantsRepository
					.findByMobileNo(Encryption.encString(merchantSubMerchantRequest.getMobile()));

			Merchants mEmail = merchantsRepository.findByEmail(Encryption.encString(contactEmail));

			LOGGER.info("mMobile:" + mMobile);
			if (mMobile == null) {

				LOGGER.info("mEmail: " + mEmail);
				if (mEmail == null) {

					Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());

					String stateName = getState(state);

					if (merchnatBussiessName == "" || action == "" || merchantVirtualAddress == "" || requestUrl1 == ""
							|| panNo == "" || contactEmail == "" || perDayTxnCount == "" || merchantBussinessType == ""
							|| perDayTxnLmt == "" || perDayTxnAmt == "" || pinCode == "" || MCC == null
							|| merchnatBussiessName == null || action == null || merchantVirtualAddress == null
							|| requestUrl1 == null || panNo == null || contactEmail == null || perDayTxnCount == null
							|| merchantBussinessType == null || perDayTxnLmt == null || perDayTxnAmt == null
							|| pinCode == null || MCC == null || firstName == null || lastName == null
							|| password == null || bankName == null || accountNumber == null || ifsc == null
							|| merchantGenre == null) {
						map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
						LOGGER.info("Bad Request");

					} else {
						String turnOverType = "LARGE";
						String partnerRefrenceNumber = YesBankSubMerchant.generateRandomMerchantId();
						String actionC = "ADD_PARTNER_SELLER";
						String vpa = "NA";
						String sellerStatus = "NA";
						String qrString = "NA";

						String subMerchantId2 = "AP" + GenerateTrxnRefId.getNumericString(8);

						SubMerchantDTOSeller subMerchantDTOSeller = new SubMerchantDTOSeller();
						subMerchantDTOSeller.setAction(actionC);
						subMerchantDTOSeller.setMerchantBussiessName(merchnatBussiessName);
						subMerchantDTOSeller.setPanNo(panNo);
						subMerchantDTOSeller.setContactEmail(contactEmail);
						subMerchantDTOSeller.setGstn(gstn);
						subMerchantDTOSeller.setPerDayTxnCount(perDayTxnCount);
						subMerchantDTOSeller.setMerchantBussinessType(merchantBussinessType);
						subMerchantDTOSeller.setPerDayTxnLmt(perDayTxnLmt);
						subMerchantDTOSeller.setPerDayTxnAmt(perDayTxnAmt);
						subMerchantDTOSeller.setMobile(mobile);
						subMerchantDTOSeller.setAddress(address);
						subMerchantDTOSeller.setState(state);
						subMerchantDTOSeller.setCity(city);
						subMerchantDTOSeller.setPinCode(pinCode);
						subMerchantDTOSeller.setSubMerchantId(subMerchantId);
						subMerchantDTOSeller.setMCC(MCC);
						subMerchantDTOSeller.setSubMerchantBankName(bankName);
						subMerchantDTOSeller.setSubMerchantBankAccount(accountNumber);
						subMerchantDTOSeller.setSubMerchantIfscCode(ifsc);
						subMerchantDTOSeller.setMerchantGenre(merchantGenre);
						subMerchantDTOSeller.setName(firstName + " " + lastName);
						subMerchantDTOSeller.setSubMerchantBankBranch(bankBranch);
						subMerchantDTOSeller.setMerchantVirtualAddress(merchantVirtualAddress);

						subMerchantDTOSeller.setAlternateAddress(alternateAddress);
						subMerchantDTOSeller.setLatitude(latitude);
						subMerchantDTOSeller.setLongitude(longitude);
						subMerchantDTOSeller.setDob(dob);
						subMerchantDTOSeller.setDoi(doi);

						String sellerCreationReq = "{\r\n" + "	\"partnerReferenceNo\": \"" + partnerRefrenceNumber
								+ "\",\r\n" + "	\"actionName\": \"" + actionC + "\",\r\n" + "	\"partnerKey\": \""
								+ partnerKey + "\",\r\n" + "	\"p1\": \"" + merchnatBussiessName + "\",\r\n"
								+ "	\"p2\": \"" + firstName + " " + lastName + "\",\r\n" + "	\"p3\": \""
								+ subMerchantId2 + "\",\r\n" + "	\"p4\": \"" + mobile + "\",\r\n" + "	\"p5\": \""
								+ contactEmail + "\",\r\n" + "	\"p6\": \"" + MCC + "\",\r\n" + "	\"p7\": \""
								+ turnOverType + "\",\r\n" + "	\"p8\": \"" + merchantGenre + "\",\r\n" + "	\"p9\": \""
								+ merchantBussinessType + "\",\r\n" + "	\"p10\": \"" + city + "\",\r\n" + "	\"p11\": \""
								+ city + "\",\r\n" + "	\"p12\": \"" + state + "\",\r\n" + "	\"p13\": \"" + pinCode
								+ "\",\r\n" + "	\"p14\": \"" + panNo + "\",\r\n" + "	\"p15\": \"" + gstn + "\",\r\n"
								+ "	\"p16\": \"" + accountNumber + "\",\r\n" + "	\"p17\": \"" + ifsc + "\",\r\n"
								+ "	\"p18\": \"" + latitude + "\",\r\n" + "	\"p19\": \"" + longitude + "\",\r\n"
								+ "	\"p20\": \"" + address + "\",\r\n" + "	\"p21\": \"" + alternateAddress + "\",\r\n"
								+ "	\"p22\": \"\",\n" + "	\"p23\": \"\",\n" + "	\"p24\": \"\",\n"
								+ "	\"p25\": \"\",\n" + "	\"p26\": \"" + dob + "\",\n" + "	\"p27\": \"" + doi
								+ "\",\n" + "	\"p28\": \"\"\n" + "\n" + "}";

//						String sellerCreationResp = encryptionAndDecryptionProduction.getEncDec(sellerCreationReq,
//								partnerKey);

						String sellerCreationResp = "NA";
						LOGGER.info("  sellerCreationResp -- " + sellerCreationResp);

						JSONObject bankDetailJSON = new JSONObject();
						bankDetailJSON.put("subMerchantIfscCode", ifsc);
						bankDetailJSON.put("subMerchantBankName", bankName);
						bankDetailJSON.put("subMerchantBankAccount", accountNumber);
						String bankDetails = bankDetailJSON.toString();

//						org.json.JSONObject jsonObject = new org.json.JSONObject(sellerCreationResp);
//						String status = jsonObject.getString("status");
//						String responseCode = jsonObject.getString("responseCode");
//						String responseMessage = jsonObject.getString("responseMessage");

						String status = "SUCCESS";
						String responseCode = "00";
						String responseMessage = "Seller has been added successfully";

						if (status.equalsIgnoreCase("SUCCESS") || responseCode.equals("00")) {
//							String yphubName = jsonObject.getString("ypHubUsername");
//							String sellerIdentifier = jsonObject.getString("sellerIdentifier");

							String yphubName = "FPYS.SI00001";
							String sellerIdentifier = "SI00001";

							LOGGER.info("  sellerIdentifier -- " + sellerIdentifier);

							// Fetch QR

							String fetchRequest = "{\r\n" + "\"requestId\": \"1\",\r\n"
									+ "\"actionName\": \"FETCH_QR\",\r\n" + "\"partnerKey\": \"" + partnerKey
									+ "\",\r\n" + "\"p1\": \"" + sellerIdentifier + "\"\r\n" + "}";
							LOGGER.info("  fetchRequest -- " + fetchRequest);

//							String fetchResponse = encryptionAndDecryptionProduction.getEncDec(fetchRequest,
//									partnerKey);
							String fetchResponse = "NA";
							LOGGER.info("  fetchResponse -- " + fetchResponse);

//							org.json.JSONObject jsonObj = new org.json.JSONObject(fetchResponse);

//							String fetchStatus = jsonObj.getString("status");
//							String fetchResponseCode = jsonObj.getString("responseCode");
//							String fetchResponseMessage = jsonObj.getString("responseMessage");

							String fetchStatus = "SUCCESS";
							String fetchResponseCode = "00";
							String fetchResponseMessage = "NA";

							LOGGER.info("  fetchStatus -- " + fetchStatus);

							if (fetchStatus.equalsIgnoreCase("SUCCESS") || fetchResponseCode.equals("00")) {
								LOGGER.info("  fetchStatus -- " + fetchStatus);

//								vpa = jsonObj.getString("vpa");
//								qrString = jsonObj.getString("qrString");
//								sellerStatus = jsonObj.getString("sellerStatus");

								vpa = "yespay.fpyssi00003@yesbankltd";
								qrString = "upi://pay?mc=4121&pa=yespay.fpyssi00001@yesbankltd&pn=FPTEST01";
								sellerStatus = "ACTIVE";

								subMerchantVPA = vpa;

								Merchants merchants = new Merchants();
								merchants.setMerchantFromdate(trxnDate);
								merchants.setIsMerchantActive('1');
								merchants.setIsMerchantEmailVerified('1');
								merchants.setIsMerchantPhoneVerified('1');
								merchants.setMerchantLoginCount('0');
								merchants.setIsBankDocVerified('0');
								merchants.setSecondSecQuestionId((long) 0);
								merchants.setMerchantFloatAmount(0.0);
								merchants.setMerchantSettlementFrequency("0");
								merchants.setMerchantCommission(new Long(0));
								merchants.setMerchantNationality(partnerKey);
								merchants.setMerchantFirstname(Encryption.encString(firstName));
								merchants.setMerchantLastname(Encryption.encString(lastName));
								merchants.setMerchantEmail(Encryption.encString(contactEmail));
								merchants.setMerchantAddress1(Encryption.encString(address));
								merchants.setMerchantAddress2(Encryption.encString(address));
								merchants.setMerchantPassword(Encryption.encString(password));
								merchants.setMerchantCity(city);
								merchants.setMerchantState(stateName);
								merchants.setMerchantCountry("India");
								merchants.setMerchantZipcode(pinCode);
								merchants.setGender('M');
								merchants.setMerchantType(merchantType);
								merchants.setMerchantPhone(Encryption.encString(mobile));
								merchants.setMerchantBusinessName(Encryption.encString(merchnatBussiessName));

								// Set Alternate Email
								Partners partners = partnersRepository.finByPartnerBussinessName(
										Encryption.encString(merchantType.getMerchantTypeName()));

								if (partners == null) {
									String altEmail = "NULL";

									callBackUrl = "NULL";
									redirectURL = "NULL";
									domesticCallBackUrl = "NULL";
								} else {
									String altEmail = partners.getPartnerAlternateEmail();

									org.json.JSONObject object = new org.json.JSONObject(altEmail);

									callBackUrl = object.getString("callBackUrl");
									redirectURL = object.getString("redirectURL");
									domesticCallBackUrl = object.getString("domesticCallBackUrl");
								}
								LOGGER.info("callBackUrl: " + callBackUrl);
								LOGGER.info("redirectURL: " + redirectURL);
								LOGGER.info("domesticCallBackUrl: " + domesticCallBackUrl);

								merchants.setMerchantAccountNo(Encryption.encString(accountNumber));
								merchants.setMerchantBankName(bankName);
								merchants.setMerchantBankCode(Encryption.encString(ifsc));
								merchants.setMerchantBankBranch(bankBranch);

								AlternateEmailRequest alternateEmailRequest = new AlternateEmailRequest();
								alternateEmailRequest.setCallBackUrl(callBackUrl);
								alternateEmailRequest.setRedirectURL(redirectURL);
								alternateEmailRequest.setDomesticCallBackUrl(domesticCallBackUrl);
								alternateEmailRequest.setPgMerchantId(sellerIdentifier);
								alternateEmailRequest.setMCC(MCC);

								String altJson = new Gson().toJson(alternateEmailRequest);

								merchants.setMerchantAlternateEmail(altJson);

								merchants = merchantsRepository.save(merchants);
								LOGGER.info("Merchant Table Saved");

								// ------Create virtual
								// account-----------------------------------------------------
								String vaIfsc = null;
								String vaNumber = null;
								String res = getVirtualAccountNumber(merchants.getMerchantId(),
										merchantSubMerchantRequest.getMobile());

								LOGGER.info("res: " + res);

								org.json.JSONObject object = new org.json.JSONObject(res);

								String status2 = object.getString("status");
								if (status2.equals("Success")) {

									vaIfsc = object.getString("VA_Ifsc");
									vaNumber = object.getString("VA_Number");
								}

								MerchantWalletInfo merchantWalletInfo = new MerchantWalletInfo();
								merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString(vaNumber));

								merchantWalletInfo.setMerchWalletFromDate(trxnDate);
								merchantWalletInfo.setMerchWalletToDate(trxnDate);
								merchantWalletInfo.setMerchWalletAmount(0.0);
								merchantWalletInfo.setMerchPinLastChangedDate(trxnDate);
								merchantWalletInfo.setIsIdProofVerified('Y');
								merchantWalletInfo.setIsAddressProofVerified('Y');
								merchantWalletInfo.setIsMerchKycComplete('Y');
								merchantWalletInfo.setIsMerchWalletActive('Y');
								merchantWalletInfo.setIsItaxVerified('Y');
								merchantWalletInfo.setIsPermitVerified('Y');
								merchantWalletInfo.setIsExtraDocVerified('Y');
								merchantWalletInfo.setMerchWalletPin(vaIfsc);

								merchantWalletInfo.setMerchants(merchants);
								merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);

								LOGGER.info("Merchant Wallet Info Table Saved");

								merchantId = merchants.getMerchantId();
								cId = String.valueOf(merchantId);

								List<PartnerServices> partnerServicesList = partnerServiceRepository
										.findAllPartnerServiceByPartnerId(partners.getPartnerId());
								LOGGER.info("partner service list " + partnerServicesList.size());
								if (partnerServicesList.size() > 0) {
									for (PartnerServices partnerServices : partnerServicesList) {
										// save merchant service
										LOGGER.info("ServiceId: " + partnerServices.getServiceId());

										ServiceInfo serviceInfo = serviceInfoRepository
												.findById(partnerServices.getServiceId()).get();
										String sName = Encryption.decString(serviceInfo.getServiceName());

										if ((sName.equals("Merchant Registration")
												|| sName.equals("Merchant Registration VA")
												|| sName.equals("Merchant Registration UPI VA"))) {
											LOGGER.info("Inside Non Assigned Services");
										} else {
											MerchantService merchantService = merchantsServiceService
													.assignMerchantService(partnerServices.getPartnerServiceId(),
															partnerServices.getAmc(), partnerServices.getOtc(),
															merchants.getMerchantId(),
															partnerServices.getSerrviceProviderId(),
															partnerServices.getServiceId(),
															partnerServices.getSubscriptionAmount(),
															partnerServices.getSubscriptionCycle(),
															partnerServices.getServiceType());
										}
									}
								}

								LOGGER.info("merchantId: " + merchantId);

								String name = firstName + " " + lastName;

								MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(
										merchantId, action, vpa, bankDetails, sellerIdentifier, sellerCreationResp,
										sellerCreationReq, partnerKey, qrString, fetchStatus, sellerStatus, trxnDate,
										merchnatBussiessName, new Gson().toJson(subMerchantDTOSeller), name, mobile,
										contactEmail, panNo, MCC, gstn, "Yes Bank");

								org.json.simple.JSONObject jsonBankDetails = new org.json.simple.JSONObject();
								jsonBankDetails.put("subMerchantIfscCode", ifsc);
								jsonBankDetails.put("subMerchantBankName", bankName);
								jsonBankDetails.put("subMerchantBankAccount", accountNumber);

								String clientId = Encryption.encString(cId);
								String clientSecret = merchants.getMerchantEmail();
								String merchantFirstName = Encryption.decString(merchants.getMerchantFirstname());
								String merchantLastName = Encryption.decString(merchants.getMerchantLastname());

								BasicAuth basicAuth = new BasicAuth();

								String authorization = basicAuth.createEncodedText(merchantFirstName,
										Encryption.decString(merchants.getMerchantPassword()));

								String userId = merchantFirstName.toUpperCase()
										+ merchantLastName.substring(0, 3).toUpperCase() + merchantId;

								MerchantInfoRequest merchantInfoRequest = new MerchantInfoRequest();
								merchantInfoRequest.setBankIdJson("NA");
								merchantInfoRequest.setBankIdUpi("NA");
								merchantInfoRequest.setBbpsCallbackUrl("NA");
								merchantInfoRequest.setClientId(clientId);
								merchantInfoRequest.setClientSecret(clientSecret);
								merchantInfoRequest.seteCollectCorpId("NA");
								merchantInfoRequest.seteCollectNotifyUrl("NA");
								merchantInfoRequest.seteCollectValidateUrl("NA");
								merchantInfoRequest.seteNachCallbackUrl("NA");
								merchantInfoRequest.seteNachRedirectUrl("NA");
								merchantInfoRequest.setImageUrl("NA");
								merchantInfoRequest.setMerchantBussinessName(
										Encryption.decString(merchants.getMerchantBusinessName()));
								merchantInfoRequest.setMerchantId(merchants.getMerchantId());
								merchantInfoRequest.setPartnerKeyUpi("NA");
								merchantInfoRequest.setPassword(Encryption.decString(merchants.getMerchantPassword()));
								merchantInfoRequest.setPayoutCallbackUrl(domesticCallBackUrl);
								merchantInfoRequest.setPgCallbackUrl("NA");
								merchantInfoRequest.setPgRedirectUrl(redirectURL);
								merchantInfoRequest.setUpiCallbackUrl(callBackUrl);
								merchantInfoRequest.setUsername(Encryption.decString(merchants.getMerchantFirstname()));
								merchantInfoService.saveMerchantInfo(merchantInfoRequest);
								LOGGER.info("Save Merchant Info Table Data");

								map.put("userId", userId);
								map.put("virtualAccountNo",
										Encryption.decString(merchantWalletInfo.getMerchWalletAccountNo()));
								map.put("ifscCode", merchantWalletInfo.getMerchWalletPin());
								map.put("clientId", clientId);
								map.put("clientSecret", clientSecret);
								map.put("authorization", "Basic " + authorization);
								map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
								map.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
								map.put("subMerchantVPA", subMerchantVPA);
								map.put("qrString", qrString);
								map.put("status", "Success");

							} else {
//								vpa = jsonObj.getString("vpa");
//								qrString = jsonObj.getString("qrString");
//								sellerStatus = jsonObj.getString("sellerStatus");

								vpa = "yespay.fpyssi00003@yesbankltd";
								qrString = "upi://pay?mc=4121&pa=yespay.fpyssi00001@yesbankltd&pn=FPTEST01";
								sellerStatus = "ACTIVE";

								subMerchantVPA = vpa;

								Merchants merchants = new Merchants();
								merchants.setMerchantFromdate(trxnDate);
								merchants.setIsMerchantActive('1');
								merchants.setIsMerchantEmailVerified('1');
								merchants.setIsMerchantPhoneVerified('1');
								merchants.setMerchantLoginCount('0');
								merchants.setIsBankDocVerified('0');
								merchants.setSecondSecQuestionId((long) 0);
								merchants.setMerchantFloatAmount(0.0);
								merchants.setMerchantSettlementFrequency("0");
								merchants.setMerchantCommission(new Long(0));

								merchants.setMerchantFirstname(Encryption.encString(firstName));
								merchants.setMerchantLastname(Encryption.encString(lastName));
								merchants.setMerchantEmail(Encryption.encString(contactEmail));
								merchants.setMerchantAddress1(Encryption.encString(address));
								merchants.setMerchantAddress2(Encryption.encString(address));
								merchants.setMerchantPassword(Encryption.encString(password));
								merchants.setMerchantCity(city);
								merchants.setMerchantState(stateName);
								merchants.setMerchantCountry("India");
								merchants.setMerchantZipcode(pinCode);
								merchants.setGender('M');
								merchants.setMerchantType(merchantType);
								merchants.setMerchantPhone(Encryption.encString(mobile));
								merchants.setMerchantBusinessName(Encryption.encString(merchnatBussiessName));

								// Set Alternate Email
								Partners partners = partnersRepository.finByPartnerBussinessName(
										Encryption.encString(merchantType.getMerchantTypeName()));

								if (partners == null) {
									String altEmail = "NULL";

									callBackUrl = "NULL";
									redirectURL = "NULL";
									domesticCallBackUrl = "NULL";
								} else {
									String altEmail = partners.getPartnerAlternateEmail();

									org.json.JSONObject object = new org.json.JSONObject(altEmail);

									callBackUrl = object.getString("callBackUrl");
									redirectURL = object.getString("redirectURL");
									domesticCallBackUrl = object.getString("domesticCallBackUrl");
								}
								LOGGER.info("callBackUrl: " + callBackUrl);
								LOGGER.info("redirectURL: " + redirectURL);
								LOGGER.info("domesticCallBackUrl: " + domesticCallBackUrl);

								merchants.setMerchantAccountNo(Encryption.encString(accountNumber));
								merchants.setMerchantBankName(bankName);
								merchants.setMerchantBankCode(Encryption.encString(ifsc));
								merchants.setMerchantBankBranch(bankBranch);

								AlternateEmailRequest alternateEmailRequest = new AlternateEmailRequest();
								alternateEmailRequest.setCallBackUrl(callBackUrl);
								alternateEmailRequest.setRedirectURL(redirectURL);
								alternateEmailRequest.setDomesticCallBackUrl(domesticCallBackUrl);
								alternateEmailRequest.setPgMerchantId(sellerIdentifier);
								alternateEmailRequest.setMCC(MCC);

								String altJson = new Gson().toJson(alternateEmailRequest);
								merchants.setMerchantAlternateEmail(altJson);
								merchants = merchantsRepository.save(merchants);
								LOGGER.info("Merchant Table Saved");

								// ------Create virtual
								// account-----------------------------------------------------
								String vaIfsc = null;
								String vaNumber = null;
								String res = getVirtualAccountNumber(merchants.getMerchantId(),
										merchantSubMerchantRequest.getMobile());

								LOGGER.info("res: " + res);

								org.json.JSONObject object = new org.json.JSONObject(res);

								String status2 = object.getString("status");
								if (status2.equals("Success")) {

									vaIfsc = object.getString("VA_Ifsc");
									vaNumber = object.getString("VA_Number");
								}

								MerchantWalletInfo merchantWalletInfo = new MerchantWalletInfo();
								merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString(vaNumber));
								merchantWalletInfo.setMerchWalletFromDate(trxnDate);
								merchantWalletInfo.setMerchWalletToDate(trxnDate);
								merchantWalletInfo.setMerchWalletAmount(0.0);
								merchantWalletInfo.setMerchPinLastChangedDate(trxnDate);
								merchantWalletInfo.setIsIdProofVerified('Y');
								merchantWalletInfo.setIsAddressProofVerified('Y');
								merchantWalletInfo.setIsMerchKycComplete('Y');
								merchantWalletInfo.setIsMerchWalletActive('Y');
								merchantWalletInfo.setIsItaxVerified('Y');
								merchantWalletInfo.setIsPermitVerified('Y');
								merchantWalletInfo.setIsExtraDocVerified('Y');
								merchantWalletInfo.setMerchWalletPin(vaIfsc);

								merchantWalletInfo.setMerchants(merchants);
								merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);

								LOGGER.info("Merchant Wallet Info Table Saved");

								merchantId = merchants.getMerchantId();
								cId = String.valueOf(merchantId);

								LOGGER.info("merchantId: " + merchantId);

								merchantWalletInfo.setMerchants(merchants);
								merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);

								LOGGER.info("Merchant Wallet Info Table Saved");

								String name = firstName + " " + lastName;
								MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(
										merchantId, action, vpa, bankDetails, sellerIdentifier, sellerCreationResp,
										sellerCreationReq, partnerKey, qrString, fetchStatus, sellerStatus, trxnDate,
										merchnatBussiessName, new Gson().toJson(subMerchantDTOSeller), name, mobile,
										contactEmail, panNo, MCC, gstn, "Yes Bank");

								org.json.simple.JSONObject jsonBankDetails = new org.json.simple.JSONObject();
								jsonBankDetails.put("subMerchantIfscCode", ifsc);
								jsonBankDetails.put("subMerchantBankName", bankName);
								jsonBankDetails.put("subMerchantBankAccount", accountNumber);

								String clientId = Encryption.encString(cId);
								String clientSecret = merchants.getMerchantEmail();
								String merchantFirstName = Encryption.decString(merchants.getMerchantFirstname());
								String merchantLastName = Encryption.decString(merchants.getMerchantLastname());

								BasicAuth basicAuth = new BasicAuth();

								String authorization = basicAuth.createEncodedText(merchantFirstName,
										Encryption.decString(merchants.getMerchantPassword()));

								String userId = merchantFirstName.toUpperCase()
										+ merchantLastName.substring(0, 3).toUpperCase() + merchantId;

								MerchantInfoRequest merchantInfoRequest = new MerchantInfoRequest();
								merchantInfoRequest.setBankIdJson("NA");
								merchantInfoRequest.setBankIdUpi("NA");
								merchantInfoRequest.setBbpsCallbackUrl("NA");
								merchantInfoRequest.setClientId(clientId);
								merchantInfoRequest.setClientSecret(clientSecret);
								merchantInfoRequest.seteCollectCorpId("NA");
								merchantInfoRequest.seteCollectNotifyUrl("NA");
								merchantInfoRequest.seteCollectValidateUrl("NA");
								merchantInfoRequest.seteNachCallbackUrl("NA");
								merchantInfoRequest.seteNachRedirectUrl("NA");
								merchantInfoRequest.setImageUrl("NA");
								merchantInfoRequest.setMerchantBussinessName(
										Encryption.decString(merchants.getMerchantBusinessName()));
								merchantInfoRequest.setMerchantId(merchants.getMerchantId());
								merchantInfoRequest.setPartnerKeyUpi("NA");
								merchantInfoRequest.setPassword(Encryption.decString(merchants.getMerchantPassword()));
								merchantInfoRequest.setPayoutCallbackUrl(domesticCallBackUrl);
								merchantInfoRequest.setPgCallbackUrl(callBackUrl);
								merchantInfoRequest.setPgRedirectUrl(redirectURL);
								merchantInfoRequest.setUpiCallbackUrl("NA");
								merchantInfoRequest.setUsername(Encryption.decString(merchants.getMerchantFirstname()));
								merchantInfoService.saveMerchantInfo(merchantInfoRequest);
								LOGGER.info("Save Merchant Info Table Data");

								map.put("userId", userId);
								map.put("virtualAccountNo",
										Encryption.decString(merchantWalletInfo.getMerchWalletAccountNo()));
								map.put("ifscCode", merchantWalletInfo.getMerchWalletPin());
								map.put("clientId", clientId);
								map.put("clientSecret", clientSecret);
								map.put("authorization", "Basic " + authorization);
								map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
								map.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
								map.put("subMerchantVPA", subMerchantVPA);
								map.put("qrString", qrString);
								map.put("status", "Success");
							}

						}

						else {
							map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
							map.put(ResponseMessage.DESCRIPTION, responseMessage);
							map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
							map.put("status", "Failed");
						}
					}

				}

				else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Merchant Email Id Already Registered");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}

			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Mobile Number Already Registered");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public String merchantEdit(MerchantEdit merchantEdit) {
		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {
			if (merchantEdit != null) {
				List<Merchants> list = merchantsRepository.findAll();

				for (Merchants merchants : list) {

					Long merchantId = merchants.getMerchantId();
					String firstName = Encryption.decString(merchants.getMerchantFirstname());
					String lastName = Encryption.decString(merchants.getMerchantLastname());

					String mUserId = firstName + lastName.substring(0, 3) + merchantId;

					String merchantuserId = mUserId.toUpperCase();

					String userId = merchantEdit.getUserId().toUpperCase();

					if (userId.equals(merchantuserId)) {

						if (merchantEdit.getFirstName() != null && !merchantEdit.getFirstName().equals("")) {

							merchants.setMerchantFirstname(Encryption.encString(merchantEdit.getFirstName()));
							merchants = merchantsRepository.save(merchants);

							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getLastName() != null && !merchantEdit.getLastName().equals("")) {

							merchants.setMerchantLastname(Encryption.encString(merchantEdit.getLastName()));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getBusinessName() != null && !merchantEdit.getBusinessName().equals("")) {

							merchants.setMerchantBusinessName(Encryption.encString(merchantEdit.getBusinessName()));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getGender() != null && !merchantEdit.getGender().equals("")) {

							merchants.setGender(merchantEdit.getGender().charAt(0));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getEmail() != null && !merchantEdit.getEmail().equals("")) {

							merchants.setMerchantEmail(Encryption.encString(merchantEdit.getEmail()));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getPhone() != null && !merchantEdit.getPhone().equals("")) {

							merchants.setMerchantPhone(Encryption.encString(merchantEdit.getPhone()));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getMerchantPassword() != null
								&& !merchantEdit.getMerchantPassword().equals("")) {

							merchants.setMerchantPassword(Encryption.encString(merchantEdit.getMerchantPassword()));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}

						if (merchantEdit.getAddress1() != null && !merchantEdit.getAddress1().equals("")) {

							merchants.setMerchantAddress1(Encryption.encString(merchantEdit.getAddress1()));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getAddress2() != null && !merchantEdit.getAddress2().equals("")) {

							merchants.setMerchantAddress2(Encryption.encString(merchantEdit.getAddress2()));
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getCity() != null && !merchantEdit.getCity().equals("")) {

							merchants.setMerchantCity(merchantEdit.getCity());
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getState() != null && !merchantEdit.getState().equals("")) {

							merchants.setMerchantState(merchantEdit.getState());
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						if (merchantEdit.getZipCode() != null && !merchantEdit.getZipCode().equals("")) {

							merchants.setMerchantZipcode(merchantEdit.getZipCode());
							merchants = merchantsRepository.save(merchants);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant updated successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

						}
						break;
					} else {
						jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						jsonObject.put(ResponseMessage.DESCRIPTION, "UserId is incorrect");
						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						finalResponse = jsonObject.toString();

					}

				}
			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				finalResponse = jsonObject.toString();
			}

			return finalResponse;
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = jsonObject.toString();
		}
		return finalResponse;
	}

	@Override
	public String merchantDelete(String userId) {
		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {
			if (userId != null) {
				List<Merchants> list = merchantsRepository.findAll();

				for (Merchants merchants : list) {

					Long merchantId = merchants.getMerchantId();
					String firstName = Encryption.decString(merchants.getMerchantFirstname());
					String lastName = Encryption.decString(merchants.getMerchantLastname());

					String mUserId = firstName + lastName.substring(0, 3) + merchantId;

					String merchantuserId = mUserId.toUpperCase();

					String userIdz = userId.toUpperCase();

					if (userIdz.equals(merchantuserId)) {

						merchants.setIsMerchantActive('0');
						merchants = merchantsRepository.save(merchants);

						jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant deleted successfully");
						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
						finalResponse = jsonObject.toString();
						break;

					} else {
						jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						jsonObject.put(ResponseMessage.DESCRIPTION, "UserId is incorrect");
						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						finalResponse = jsonObject.toString();
						// break;

					}

				}
			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				finalResponse = jsonObject.toString();
			}

			return finalResponse;
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = jsonObject.toString();
		}
		return finalResponse;
	}

	@Override
	public String sendOTP(String userId) {

		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {
			if (userId != null) {
				List<Merchants> list = merchantsRepository.findAll();

				for (Merchants merchants : list) {

					Long merchantId = merchants.getMerchantId();
					String firstName = Encryption.decString(merchants.getMerchantFirstname());
					String lastName = Encryption.decString(merchants.getMerchantLastname());

					String mUserId = firstName + lastName.substring(0, 3) + merchantId;

					LOGGER.info("mUserId " + mUserId);

					String merchantuserId = mUserId.toUpperCase();

					LOGGER.info("merchantuserId " + merchantuserId);

					String userIdz = userId.toUpperCase();

					if (userIdz.equals(merchantuserId)) {

						String merchantMobile = merchants.getMerchantPhone();
						String otp = RandomNumberGenrator.generateWalletPin();

						SMSAPIImpl impl = new SMSAPIImpl();
						impl.registrationOTP(Encryption.decString(merchantMobile), firstName, otp);

						jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						jsonObject.put(ResponseMessage.DESCRIPTION, "OTP Generated Sucessfully");
						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
						jsonObject.put("otp", otp);
						finalResponse = jsonObject.toString();
						break;
					} else {
						jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						jsonObject.put(ResponseMessage.DESCRIPTION, "UserId is incorrect");
						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						finalResponse = jsonObject.toString();

					}

				}
			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				finalResponse = jsonObject.toString();
			}

			return finalResponse;
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = jsonObject.toString();
		}
		return finalResponse;

	}

	@Override
	public String forgetPassword(ForgetPassword forgetPassword) {
		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {
			if (forgetPassword != null) {
				List<Merchants> list = merchantsRepository.findAll();

				for (Merchants merchants : list) {

					Long merchantId = merchants.getMerchantId();
					String firstName = Encryption.decString(merchants.getMerchantFirstname());
					String lastName = Encryption.decString(merchants.getMerchantLastname());
					String mPassword = Encryption.decString(merchants.getMerchantPassword());

					String mUserId = firstName + lastName.substring(0, 3) + merchantId;

					String merchantuserId = mUserId.toUpperCase();

					String userId = forgetPassword.getUserId().toUpperCase();
					String password = forgetPassword.getOldPassword();

					String timeStamp = getCurrentTime();
					if (userId.equals(merchantuserId)) {
						if (password.equals(mPassword)) {

							merchants.setMerchantPassword(Encryption.encString(forgetPassword.getNewPassword()));
							merchants = merchantsRepository.save(merchants);

							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant password changed successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();
							break;
						} else {
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Password is incorrect");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
							finalResponse = jsonObject.toString();
							break;
						}

					} else {
						jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						jsonObject.put(ResponseMessage.DESCRIPTION, "UserId is incorrect");
						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						finalResponse = jsonObject.toString();

					}

				}
			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				finalResponse = jsonObject.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = jsonObject.toString();
		}

		return finalResponse;
	}

	@Override
	public String merchantLogin(MerchantLogin merchantLogin) {
		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {
			if (merchantLogin != null) {

				List<Merchants> list = merchantsRepository.findAll();

				for (Merchants merchants : list) {

					Long merchantId = merchants.getMerchantId();
					String firstName = Encryption.decString(merchants.getMerchantFirstname());
					String lastName = Encryption.decString(merchants.getMerchantLastname());
					String mPassword = Encryption.decString(merchants.getMerchantPassword());

					String mUserId = firstName + lastName.substring(0, 3) + merchantId;

					String merchantuserId = mUserId.toUpperCase();

					String userId = merchantLogin.getUserId().toUpperCase();
					String password = merchantLogin.getPassword();

					String timeStamp = getCurrentTime();

					if (userId.equals(merchantuserId)) {

						if (password.equals(mPassword)) {

							jsonObject.put("timeStamp", timeStamp);
							jsonObject.put("userId", userId);
							jsonObject.put("loggedIn", true);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant login successfully");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
							finalResponse = jsonObject.toString();

							break;
						} else {
							jsonObject.put("timeStamp", timeStamp);
							jsonObject.put("loggedIn", false);
							jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
							jsonObject.put(ResponseMessage.DESCRIPTION, "Password is incorrect");
							jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
							finalResponse = jsonObject.toString();
							break;
						}

					} else {
						jsonObject.put("timeStamp", timeStamp);
						jsonObject.put("loggedIn", false);
						jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						jsonObject.put(ResponseMessage.DESCRIPTION, "UserId is incorrect");
						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						finalResponse = jsonObject.toString();

					}
				}

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				finalResponse = jsonObject.toString();
			}

			return finalResponse;
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = jsonObject.toString();
		}
		return finalResponse;
	}

//	@Override
//	public String merchantRegister(MerchantRegisterRequest merchantRegisterRequest, MerchantType merchantType) {
//		String finalResponse = null;
//		JSONObject jsonObject = new JSONObject();
//		try {
//			String vaNumber = null;
//			String vaIfsc = null;
//			char gender = 0;
//			String gen = Encryption.decString(merchantRegisterRequest.getGender());
//			if (gen != null) {
//				gender = gen.charAt(0);
//			}
//
//			String altEmail = null;
//			String serviceName = "Merchant Registration VA";
//
//			if (!partnerServiceValidate.checkServiceExistOrNot(merchantType.getMerchantTypeId(), serviceName)) {
//				LOGGER.info("-------------- CheckServiceExistOrNot  ---------------------------");
//
//				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
//				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//				finalResponse = jsonObject.toString();
//				return finalResponse;
//			}
//
//			if (merchantRegisterRequest != null) {
//				merchantRegisterRequest.setMerchantTypeId(merchantType.getMerchantTypeId());
//				merchantRegisterRequest.setOutletType("Shop");
//				merchantRegisterRequest.setTillPassword(merchantRegisterRequest.getMerchantPassword());
//				merchantRegisterRequest.setLatitude("22.7544° N");
//				merchantRegisterRequest.setLongitude("75.8668° E");
//
//				merchantRegisterRequest.setMerchantBankAccountNo("NA");
//				merchantRegisterRequest.setMerchantBankBranch("NA");
//				merchantRegisterRequest.setMerchantBankIfscCode("NA");
//				merchantRegisterRequest.setMerchantBankName("NA");
//
//				String merchantDJSON = new Gson().toJson(merchantRegisterRequest);
//
//				String jsonRequest = merchantDJSON;
//
//				Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
//
//				Merchants merchants = new Merchants();
//				merchants.setMerchantFromdate(trxnDate);
//				merchants.setIsMerchantActive('1');
//				merchants.setIsMerchantEmailVerified('1');
//				merchants.setIsMerchantPhoneVerified('1');
//				merchants.setMerchantLoginCount('0');
//				merchants.setIsBankDocVerified('0');
//				merchants.setSecondSecQuestionId((long) 0);
//				merchants.setMerchantFloatAmount(0.0);
//				merchants.setMerchantSettlementFrequency("0");
//				merchants.setMerchantCommission(new Long(0));
//
//				merchants.setMerchantFirstname(Encryption.encString(merchantRegisterRequest.getFirstName()));
//				merchants.setMerchantLastname(Encryption.encString(merchantRegisterRequest.getLastName()));
//				merchants.setMerchantEmail(Encryption.encString(merchantRegisterRequest.getEmail().toLowerCase()));
//				merchants.setMerchantAddress1(Encryption.encString(merchantRegisterRequest.getAddress1()));
//				merchants.setMerchantAddress2(Encryption.encString(merchantRegisterRequest.getAddress2()));
//				merchants.setMerchantPassword(Encryption.encString(merchantRegisterRequest.getMerchantPassword()));
//				merchants.setMerchantCity(merchantRegisterRequest.getCity());
//				merchants.setMerchantState(merchantRegisterRequest.getState());
//				merchants.setMerchantCountry("India");
//				merchants.setMerchantZipcode(merchantRegisterRequest.getZipCode());
//				merchants.setGender(gender);
//				merchants.setMerchantType(merchantType);
//				merchants.setMerchantPhone(Encryption.encString(merchantRegisterRequest.getPhone()));
//				merchants.setMerchantBusinessName(Encryption.encString(merchantRegisterRequest.getBusinessName()));
//
//				merchants
//						.setMerchantAccountNo(Encryption.encString(merchantRegisterRequest.getMerchantBankAccountNo()));
//				merchants.setMerchantBankName(merchantRegisterRequest.getMerchantBankName());
//				merchants.setMerchantBankCode(Encryption.encString(merchantRegisterRequest.getMerchantBankIfscCode()));
//				merchants.setMerchantBankBranch(merchantRegisterRequest.getMerchantBankBranch());
//
//				merchants = merchantsRepository.save(merchants);
//
//				LOGGER.info("Merchant Table Saved");
//
//				String res = getVirtualAccountNumber(merchants.getMerchantId(), merchantRegisterRequest.getPhone());
//
//				LOGGER.info("res: " + res);
//
//				org.json.JSONObject object = new org.json.JSONObject(res);
//
//				String status = object.getString("status");
//				if (status.equals("Success")) {
//
//					vaIfsc = object.getString("VA_Ifsc");
//					vaNumber = object.getString("VA_Number");
//				} else {
//					String description = object.getString("description");
//
//					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//					jsonObject.put(ResponseMessage.DESCRIPTION, description);
//					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//					finalResponse = jsonObject.toString();
//					return finalResponse;
//
//				}
//				LOGGER.info("vaNumber: " + vaNumber);
//
//				MerchantWalletInfo merchantWalletInfo = new MerchantWalletInfo();
//				// merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString("FIDYPY"+merchantRegisterRequest.getPhone()));
//				merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString(vaNumber));
//				merchantWalletInfo.setMerchants(merchants);
//				merchantWalletInfo.setMerchWalletFromDate(trxnDate);
//				merchantWalletInfo.setMerchWalletToDate(trxnDate);
//				merchantWalletInfo.setMerchWalletAmount(0.0);
//				merchantWalletInfo.setMerchPinLastChangedDate(trxnDate);
//				merchantWalletInfo.setIsIdProofVerified('Y');
//				merchantWalletInfo.setIsAddressProofVerified('Y');
//				merchantWalletInfo.setIsMerchKycComplete('Y');
//				merchantWalletInfo.setIsMerchWalletActive('Y');
//				merchantWalletInfo.setIsItaxVerified('Y');
//				merchantWalletInfo.setIsPermitVerified('Y');
//				merchantWalletInfo.setIsExtraDocVerified('Y');
//				merchantWalletInfo.setMerchWalletPin(vaIfsc);
//				merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);
//
//				LOGGER.info("Merchant Wallet Info Table Saved");
//
//				String merchWalletAccountNo = Encryption.decString(merchantWalletInfo.getMerchWalletAccountNo());
//				Long merchantId = merchants.getMerchantId();
//				String merchantFirstName = Encryption.decString(merchants.getMerchantFirstname());
//				String merchantLastName = Encryption.decString(merchants.getMerchantLastname());
//
//				// Set Alternate Email
//				Partners partners = partnersRepository
//						.finByPartnerBussinessName(Encryption.encString(merchantType.getMerchantTypeName()));
//
//				if (partners == null) {
//					altEmail = "NULL";
//				} else {
//					altEmail = partners.getPartnerAlternateEmail();
//				}
//
//				List<PartnerServices> partnerServicesList = partnerServiceRepository
//						.findAllPartnerServiceByPartnerId(partners.getPartnerId());
//				LOGGER.info("partner service list " + partnerServicesList.size());
//				if (partnerServicesList.size() > 0) {
//					for (PartnerServices partnerServices : partnerServicesList) {
//						// save merchant service
//						LOGGER.info("ServiceId: " + partnerServices.getServiceId());
//
//						ServiceInfo serviceInfo = serviceInfoRepository.findById(partnerServices.getServiceId()).get();
//						String sName = Encryption.decString(serviceInfo.getServiceName());
//
//						if ((sName.equals("Merchant Registration") || sName.equals("Merchant Registration VA")
//								|| sName.equals("Merchant Registration UPI VA"))) {
//							LOGGER.info("Inside Non Assigned Services");
//						} else {
//							MerchantService merchantService = merchantsServiceService.assignMerchantService(
//									partnerServices.getPartnerServiceId(), partnerServices.getAmc(),
//									partnerServices.getOtc(), merchants.getMerchantId(),
//									partnerServices.getSerrviceProviderId(), partnerServices.getServiceId(),
//									partnerServices.getSubscriptionAmount(), partnerServices.getSubscriptionCycle(),
//									partnerServices.getServiceType());
//						}
//
//					}
//				}
//
//				// Merchants merchants = merchantsRepository.findById(merchantId).get();
//
//				String clientId = Encryption.encString(String.valueOf(merchantId));
//				String clientSecret = merchants.getMerchantEmail();
//
//				BasicAuth basicAuth = new BasicAuth();
//
//				String authorization = basicAuth.createEncodedText(merchantFirstName,
//						Encryption.decString(merchants.getMerchantPassword()));
//
//				String userId = merchantFirstName.toUpperCase() + merchantLastName.substring(0, 3).toUpperCase()
//						+ merchantId;
//
//				merchants.setMerchantAlternateEmail(altEmail);
//				merchants = merchantsRepository.save(merchants);
//
//				MerchantInfoRequest merchantInfoRequest = new MerchantInfoRequest();
//				merchantInfoRequest.setBankIdJson("NA");
//				merchantInfoRequest.setBankIdUpi("NA");
//				merchantInfoRequest.setBbpsCallbackUrl("NA");
//				merchantInfoRequest.setClientId(clientId);
//				merchantInfoRequest.setClientSecret(clientSecret);
//				merchantInfoRequest.seteCollectCorpId("NA");
//				merchantInfoRequest.seteCollectNotifyUrl("NA");
//				merchantInfoRequest.seteCollectValidateUrl("NA");
//				merchantInfoRequest.seteNachCallbackUrl("NA");
//				merchantInfoRequest.seteNachRedirectUrl("NA");
//				merchantInfoRequest.setImageUrl("NA");
//				merchantInfoRequest.setMerchantBussinessName(Encryption.decString(merchants.getMerchantBusinessName()));
//				merchantInfoRequest.setMerchantId(merchants.getMerchantId());
//				merchantInfoRequest.setPartnerKeyUpi("NA");
//				merchantInfoRequest.setPassword(Encryption.decString(merchants.getMerchantPassword()));
//				merchantInfoRequest.setPayoutCallbackUrl("NA");
//				merchantInfoRequest.setPgCallbackUrl("NA");
//				merchantInfoRequest.setPgRedirectUrl("NA");
//				merchantInfoRequest.setUpiCallbackUrl("NA");
//				merchantInfoRequest.setUsername(Encryption.decString(merchants.getMerchantFirstname()));
//				merchantInfoService.saveMerchantInfo(merchantInfoRequest);
//				LOGGER.info("Save Merchant Info Table Data");
//
//				jsonObject.put("userId", userId);
//				jsonObject.put("virtualAccountNo", merchWalletAccountNo);
//				// jsonObject.put("ifscCode", "YESB0CMSNOC");
//				jsonObject.put("ifscCode", vaIfsc);
//				jsonObject.put("clientId", clientId);
//				jsonObject.put("clientSecret", clientSecret);
//				jsonObject.put("authorization", "Basic " + authorization);
//				jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//				jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
//				finalResponse = jsonObject.toString();
//
//			} else {
//				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
//				finalResponse = jsonObject.toString();
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
//			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
//			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
//
//			finalResponse = jsonObject.toString();
//		}
//		return finalResponse;
//	}

	@Override
	public String merchantRegister(MerchantRegisterRequest merchantRegisterRequest, MerchantType merchantType) {
		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {
			String vaNumber = null;
			String vaIfsc = null;
			char gender = 0;
			String gen = Encryption.decString(merchantRegisterRequest.getGender());
			if (gen != null) {
				gender = gen.charAt(0);
			}

			String altEmail = null;
			String serviceName = "Merchant Registration VA";

			if (!partnerServiceValidate.checkServiceExistOrNot(merchantType.getMerchantTypeId(), serviceName)) {
				LOGGER.info("-------------- CheckServiceExistOrNot ---------------------------");

				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				finalResponse = jsonObject.toString();
				return finalResponse;
			}

			if (merchantRegisterRequest != null) {

				Partners partners = partnersRepository
						.finByPartnerBussinessName(Encryption.encString(merchantType.getMerchantTypeName()));

				long merchantId = saveMerchantDetails(merchantRegisterRequest.getFirstName(),
						merchantRegisterRequest.getLastName(), merchantRegisterRequest.getEmail(),
						merchantRegisterRequest.getAddress1(), merchantRegisterRequest.getAddress2(),
						merchantRegisterRequest.getMerchantPassword(), merchantRegisterRequest.getCity(),
						merchantRegisterRequest.getState(), merchantRegisterRequest.getZipCode(), gender,
						merchantType.getMerchantTypeId(), merchantRegisterRequest.getPhone(),
						merchantRegisterRequest.getBusinessName(), merchantRegisterRequest.getMerchantBankAccountNo(),
						merchantRegisterRequest.getMerchantBankName(),
						merchantRegisterRequest.getMerchantBankIfscCode(),
						merchantRegisterRequest.getMerchantBankBranch(), partners.getAuthUserId(),
						partners.getVerticalRegions());

				LOGGER.info("merchantId: " + merchantId);

				String res = getVirtualAccountNumber(merchantId, merchantRegisterRequest.getPhone());

				LOGGER.info("res: " + res);

				org.json.JSONObject object = new org.json.JSONObject(res);

				String status = object.getString("status");
				if (status.equals("Success")) {

					vaIfsc = object.getString("VA_Ifsc");
					vaNumber = object.getString("VA_Number");
				} else {
					String description = object.getString("description");

					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.DESCRIPTION, description);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					finalResponse = jsonObject.toString();
					return finalResponse;

				}
				LOGGER.info("vaNumber: " + vaNumber);

				String merchWalletAccountNo = saveMerchantWalletInfoDetails(merchantId, vaNumber, vaIfsc);

				merchWalletAccountNo = Encryption.decString(merchWalletAccountNo);

				String merchantFirstName = merchantRegisterRequest.getFirstName();
				String merchantLastName = merchantRegisterRequest.getLastName();

				// Set Alternate Email

				if (partners == null) {
					altEmail = "NULL";
				} else {
					altEmail = partners.getPartnerAlternateEmail();
				}

				List<PartnerServices> partnerServicesList = partnerServiceRepository
						.findAllPartnerServiceByPartnerId(partners.getPartnerId());
				LOGGER.info("partner service list " + partnerServicesList.size());
				if (partnerServicesList.size() > 0) {
					for (PartnerServices partnerServices : partnerServicesList) {
						// save merchant service
						LOGGER.info("ServiceId: " + partnerServices.getServiceId());

						ServiceInfo serviceInfo = serviceInfoRepository.findById(partnerServices.getServiceId()).get();
						String sName = Encryption.decString(serviceInfo.getServiceName());

						if ((sName.equals("Merchant Registration") || sName.equals("Merchant Registration VA")
								|| sName.equals("Merchant Registration UPI VA"))) {
							LOGGER.info("Inside Non Assigned Services");
						} else {
							MerchantService merchantService = merchantsServiceService.assignMerchantService(
									partnerServices.getPartnerServiceId(), partnerServices.getAmc(),
									partnerServices.getOtc(), merchantId, partnerServices.getSerrviceProviderId(),
									partnerServices.getServiceId(), partnerServices.getSubscriptionAmount(),
									partnerServices.getSubscriptionCycle(), partnerServices.getServiceType());
						}

					}
				}

				Merchants merchants = merchantsRepository.findById(merchantId).get();

				String clientId = Encryption.encString(String.valueOf(merchantId));
				String clientSecret = merchants.getMerchantEmail();

				BasicAuth basicAuth = new BasicAuth();

				String authorization = basicAuth.createEncodedText(merchantFirstName,
						merchantRegisterRequest.getMerchantPassword());

				String userId = merchantFirstName.toUpperCase() + merchantLastName.substring(0, 3).toUpperCase()
						+ merchantId;

				merchants.setMerchantAlternateEmail(altEmail);
				merchants = merchantsRepository.save(merchants);

				String url = ResponseMessage.MERCHANT_DASHBOARD_URL;
				res = EmailAPIImpl.sendEmailForMerchantOnboarding(merchantRegisterRequest.getEmail(),
						merchantRegisterRequest.getFirstName(), "Merchant",
						merchantRegisterRequest.getMerchantPassword(), url, merchantRegisterRequest.getEmail(),
						ResponseMessage.MERCHANT_ENVIRONMENT);

				jsonObject.put("userId", userId);
				jsonObject.put("virtualAccountNo", merchWalletAccountNo);
				jsonObject.put("ifscCode", vaIfsc);
				jsonObject.put("clientId", clientId);
				jsonObject.put("clientSecret", clientSecret);
				jsonObject.put("authorization", "Basic " + authorization);
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				jsonObject.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
				finalResponse = jsonObject.toString();

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
				finalResponse = jsonObject.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			finalResponse = jsonObject.toString();
		}
		return finalResponse;
	}

	public String saveMerchantUser(long merchantId, String firstName, String bussinessName, String email, String mobile,
			String password) {
		String res = null;
		try {
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			MerchantUser merchantUser = new MerchantUser();
			merchantUser.setDate(trxnDate);
			merchantUser.setIsActive('1');
			merchantUser.setMerchantBusinessName(Encryption.encString(bussinessName));
			merchantUser.setMerchantId(merchantId);
			merchantUser.setMerchantUserEmail(Encryption.encString(email));
			merchantUser.setMerchantUserMobileNo(Encryption.encString(mobile));
			merchantUser.setMerchantUserName(Encryption.encString(firstName));
			merchantUser.setMerchantUserType("primary");
			merchantUser.setMerchantUserPassword(Encryption.encString(password));
			merchantUser.setUpdatePasswordDate(trxnDate);
			merchantUser = merchantUserRepository.save(merchantUser);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return res;
	}

	public String saveMerchantInfoDetails(Long merchantId, String bussinessName, String clientId, String clientSecret,
			String merchantFirstName, String password) {
		String res = null;
		try {
			Map<String, Object> mapMerchanInfo = new HashMap<>();

			MerchantInfoRequest infoRequest = new MerchantInfoRequest(merchantId, bussinessName, "NA", "NA", "NA", "NA",
					"NA", "NA", "NA", "NA", "NA", "NA", clientId, clientSecret, merchantFirstName, password, "NA", "NA",
					"NA", "NA", "NA", "NA");

			mapMerchanInfo = merchantInfoService.saveMerchantInfo(infoRequest);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	public String saveMerchantWalletInfoDetails(long merchantId, String vaNumber, String vaIfsc) {
		String res = null;
		try {
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			Merchants merchants = merchantsRepository.findById(merchantId).get();

			MerchantWalletInfo merchantWalletInfo = new MerchantWalletInfo();

			merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString(vaNumber));
			merchantWalletInfo.setMerchants(merchants);
			merchantWalletInfo.setMerchWalletFromDate(trxnDate);
			merchantWalletInfo.setMerchWalletToDate(trxnDate);
			merchantWalletInfo.setMerchWalletAmount(0.0);
			merchantWalletInfo.setMerchPinLastChangedDate(trxnDate);
			merchantWalletInfo.setIsIdProofVerified('Y');
			merchantWalletInfo.setIsAddressProofVerified('Y');
			merchantWalletInfo.setIsMerchKycComplete('Y');
			merchantWalletInfo.setIsMerchWalletActive('Y');
			merchantWalletInfo.setIsItaxVerified('Y');
			merchantWalletInfo.setIsPermitVerified('Y');
			merchantWalletInfo.setIsExtraDocVerified('Y');
			merchantWalletInfo.setMerchWalletPin(vaIfsc);
			merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);

			LOGGER.info("Merchant Wallet Info Table Saved");

			res = merchantWalletInfo.getMerchWalletAccountNo();

			return res;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	public Long saveMerchantDetails(String firstName, String lastName, String email, String address1, String address2,
			String password, String city, String state, String zipcode, char gender, Long merchantTypeId, String phone,
			String bussinessName, String accountNumber, String bankName, String ifsc, String bankBranch,
			long authUserId, String verticalRegions) {
		long merchantId = 0L;
		try {
			MerchantType merchantType = merchantTypeRepository.findById(merchantTypeId).get();

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			Merchants merchants = new Merchants();
			merchants.setMerchantFromdate(trxnDate);
			merchants.setIsMerchantActive('1');
			merchants.setIsMerchantEmailVerified('1');
			merchants.setIsMerchantPhoneVerified('1');
			merchants.setMerchantLoginCount('0');
			merchants.setIsBankDocVerified('0');
			merchants.setSecondSecQuestionId((long) 0);
			merchants.setMerchantFloatAmount(0.0);
			merchants.setMerchantSettlementFrequency("0");
			merchants.setMerchantCommission(0L);

			merchants.setMerchantFirstname(Encryption.encString(firstName));
			merchants.setMerchantLastname(Encryption.encString(lastName));
			merchants.setMerchantEmail(Encryption.encString(email));
			merchants.setMerchantAddress1(Encryption.encString(address1));
			merchants.setMerchantAddress2(Encryption.encString(address2));
			merchants.setMerchantPassword(Encryption.encString(password));
			merchants.setMerchantCity(city);
			merchants.setMerchantState(state);
			merchants.setMerchantCountry("India");
			merchants.setMerchantZipcode(zipcode);
			merchants.setGender(gender);
			merchants.setMerchantType(merchantType);
			merchants.setMerchantPhone(Encryption.encString(phone));
			merchants.setMerchantBusinessName(Encryption.encString(bussinessName));

			merchants.setMerchantAccountNo(Encryption.encString(accountNumber));
			merchants.setMerchantBankName(bankName);
			merchants.setMerchantBankCode(Encryption.encString(ifsc));
			merchants.setMerchantBankBranch(bankBranch);
			merchants.setAuthUserId(authUserId);
			merchants.setVerticalRegions(verticalRegions);
			merchants = merchantsRepository.save(merchants);

			LOGGER.info("Merchant Table Saved");

			merchantId = merchants.getMerchantId();

			LOGGER.info("merchantId:" + merchantId);

			saveMerchantUser(merchantId, firstName, bussinessName, email, phone, password);

			String strMerchantBusinessName = merchants.getMerchantBusinessName();
			String clientId = String.valueOf(merchantId);
			clientId = Encryption.encString(clientId);
			String clientSecret = merchants.getMerchantEmail();
			String merchantFirstName = merchants.getMerchantFirstname();
			password = merchants.getMerchantPassword();
			password = Encryption.decString(password);
			merchantFirstName = Encryption.decString(merchantFirstName);
			strMerchantBusinessName = Encryption.decString(strMerchantBusinessName);

			String merchantInfo = saveMerchantInfoDetails(merchantId, bussinessName, clientId, clientSecret,
					merchantFirstName, password);

			return merchantId;
		} catch (Exception e) {
			e.printStackTrace();
			merchantId = 0L;
		}

		return merchantId;
	}

	private String getVirtualAccountNumber(Long merchantId, String phone) {
		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "");
			Request request = new Request.Builder()
					.url(ResponseMessage.CREATE_VIRTUAL_ACCOUNT_URL + merchantId + "/rblVirtualAccount/" + phone)
					.method("POST", body).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			finalResponse = response.body().string();

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = jsonObject.toString();
		}
		return finalResponse;
	}

	@Override
	public Map<String, Object> transactionHistory(TransactionHistoryRequest transactionHistoryRequest,
			String clientId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TransactionHistoryPayload> activityList = new ArrayList<TransactionHistoryPayload>();
		try {

			int totalRecord = 0;
			List<?> details = null;
			List<?> details2 = null;
			List<?> totalDetails = null;

			LOGGER.info("clientId: " + clientId);
			Long merchantId = Long.parseLong(clientId);

			String startDate = transactionHistoryRequest.getStartDate() + " 00.00.00.0";
			String endDate = transactionHistoryRequest.getEndDate() + " 23.59.59.9";
			String serviceName = transactionHistoryRequest.getServiceName();

			LOGGER.info("startDate: " + startDate);
			LOGGER.info("endDate: " + endDate);
			LOGGER.info("serviceName: " + Encryption.encString(serviceName));

			Pageable pageable = PageRequest.of(transactionHistoryRequest.getPageNo(),
					transactionHistoryRequest.getPageSize());

			details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateForFastag(
					Encryption.encString(serviceName), merchantId, startDate, endDate, pageable);
			totalRecord = coreTransactionsRepository.countByMerchantIdAndStartDateToEndDateForFastag(
					Encryption.encString(serviceName), merchantId, startDate, endDate);

			LOGGER.info("List Size: " + details.size());
			int i = 0;

			Iterator it = details.iterator();
			while (it.hasNext()) {
				i++;
				Object[] object = (Object[]) it.next();

				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String tamount = amount1.format(Encryption.decFloat((Double) object[0]));
				Timestamp trxndate = (Timestamp) object[1];
				String trxnRefId = Encryption.decString((String) object[2]);
				String trxnServicefIdentifier = Encryption.decString((String) object[3]);
				String merchantTrxnRefId = (String) object[4];
				String spRefId = (String) object[5];
				String operatorRefNo = (String) object[6];
				String sName = Encryption.decString((String) object[7]);
				String transactionStatus = (String) object[8];

				BigInteger trxnIdBig = (BigInteger) (BigInteger) object[9];
				Long trxnId = trxnIdBig.longValue();

				String pcTrxnMsg = (String) object[10];
				String resMsg = (String) object[11];

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(resMsg);
				JSONObject data = (JSONObject) obj;

				JSONObject customerParam = (JSONObject) data.get("customerParam");

				String paramValue = null;
				if (customerParam == null) {
					paramValue = "NA";
				}

				else {
					String optional2 = customerParam.toString();

					String cParam = optional2.toString();
					LOGGER.info("cParam: " + cParam);

					String valueString = null;
					org.json.JSONObject objectS = new org.json.JSONObject(cParam);

					Iterator<String> keys = objectS.keys();
					while (keys.hasNext()) {
						String keyValue = (String) keys.next();
						valueString = objectS.getString(keyValue);

						LOGGER.info("Value: " + valueString);
					}

					paramValue = valueString;
				}

				LOGGER.info("Trxn Id : " + trxnId);
				LOGGER.info("spRefId : " + spRefId);
				LOGGER.info("pcTrxnMsg : " + pcTrxnMsg);

				LOGGER.info("--------------------------------------------------");
				details2 = coreTransactionsRepository.checkTransactionstatus(pcTrxnMsg);
				Iterator it2 = details2.iterator();
				while (it2.hasNext()) {

					Object[] object2 = (Object[]) it2.next();

					BigInteger tId = (BigInteger) (BigInteger) object2[0];
					Long trxnId2 = tId.longValue();

					String status = (String) object2[1];

					LOGGER.info("trxnId2 : " + trxnId2);
					LOGGER.info("status  : " + status);

					TransactionHistoryPayload payLoad = new TransactionHistoryPayload();
					payLoad.setsNo(i);
					payLoad.setCustomerAccountNo("NA");
					payLoad.setCustomerName("NA");
					payLoad.setMERCHANT_TRXN_REF_ID(merchantTrxnRefId);
					payLoad.setMerchnatBussiessName("NA");
					payLoad.setNAME("NA");
					payLoad.setOPERATOR_REF_NO(operatorRefNo);
					payLoad.setSERVICE_NAME(serviceName);
					payLoad.setSP_REFERENCE_ID(spRefId);
					payLoad.setSTATUS_NAME(transactionStatus);
					payLoad.setUpiStatus(status);
					payLoad.setTRXN_AMOUNT(tamount);
					payLoad.setTRXN_DATE(trxndate.toString());
					payLoad.setTRXN_REF_ID(trxnRefId);
					payLoad.setTRXN_SERVICE_IDENTIFIER(trxnServicefIdentifier);
					activityList.add(payLoad);

				}
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.TRANSACTION_HISTORY_LIST);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("transactionHistoryList", activityList);
			map.put("totalRecord", totalRecord);

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public String checkPanNumber(String panNo) {
		String finalResponse = null;
		JSONObject jsonObject = new JSONObject();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "");
			Request request = new Request.Builder().url("https://api.fidypay.com/pg/pan/fetchPan/" + panNo)
					.method("POST", body).addHeader("Client-Id", ResponseMessage.CLIENT_ID)
					.addHeader("Client-Secret", ResponseMessage.CLIENT_SECRET)
					.addHeader("Authorization", ResponseMessage.AUTHORIZATION)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String res = response.body().string();
			finalResponse = res;

			return finalResponse;
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = jsonObject.toString();

		}
		return finalResponse;
	}

	public static String getState(String stateCode) {
		String response = "";
		String stateName = "";
		if (stateCode.equals("1")) {
			stateName = "Andhra Pradesh";
		} else if (stateCode.equals("2")) {
			stateName = "Arunachal Pradesh";
		}

		else if (stateCode.equals("3")) {
			stateName = "Assam";
		}

		else if (stateCode.equals("4")) {
			stateName = "Bihar";
		}

		else if (stateCode.equals("5")) {
			stateName = "Chhattisgarh";
		}

		else if (stateCode.equals("6")) {
			stateName = "Goa";
		}

		else if (stateCode.equals("7")) {
			stateName = "Gujarat";
		}

		else if (stateCode.equals("8")) {
			stateName = "Haryana";
		}

		else if (stateCode.equals("9")) {
			stateName = "Himachal Pradesh";
		}

		else if (stateCode.equals("10")) {
			stateName = "Jammu and Kashmir";
		}

		else if (stateCode.equals("11")) {
			stateName = "Jharkhand";
		}

		else if (stateCode.equals("12")) {
			stateName = "Karnataka";
		}

		else if (stateCode.equals("13")) {
			stateName = "Kerala";
		}

		else if (stateCode.equals("14")) {
			stateName = "Madhya Pradesh";
		}

		else if (stateCode.equals("15")) {
			stateName = "Maharashtra";
		}

		else if (stateCode.equals("16")) {
			stateName = "Manipur";
		}

		else if (stateCode.equals("17")) {
			stateName = "Meghalaya";
		}

		else if (stateCode.equals("18")) {
			stateName = "Mizoram";
		}

		else if (stateCode.equals("19")) {
			stateName = "Nagaland";
		}

		else if (stateCode.equals("20")) {
			stateName = "Odisha";
		}

		else if (stateCode.equals("21")) {
			stateName = "Punjab";
		}

		else if (stateCode.equals("22")) {
			stateName = "Rajasthan";
		}

		else if (stateCode.equals("23")) {
			stateName = "Sikkim";
		}

		else if (stateCode.equals("24")) {
			stateName = "Tamil Nadu";
		}

		else if (stateCode.equals("25")) {
			stateName = "Telangana";
		}

		else if (stateCode.equals("26")) {
			stateName = "Tripura";
		}

		else if (stateCode.equals("27")) {
			stateName = "Uttarakhand";
		}

		else if (stateCode.equals("28")) {
			stateName = "Uttar Pradesh";
		} else if (stateCode.equals("29")) {
			stateName = "West Bengal";
		} else if (stateCode.equals("30")) {
			stateName = "Andaman and Nicobar Islands";
		} else if (stateCode.equals("31")) {
			stateName = "Chandigarh";
		} else if (stateCode.equals("32")) {
			stateName = "Dadra and Nagar Haveli";
		} else if (stateCode.equals("33")) {
			stateName = "Daman and Diu";
		} else if (stateCode.equals("34")) {
			stateName = "Delhi";
		} else if (stateCode.equals("35")) {
			stateName = "Lakshadweep";
		} else if (stateCode.equals("36")) {
			stateName = "Puducherry";
		}

		response = stateName;

		return response;
	}

//	@SuppressWarnings("unused")
//	@Override
//	public Map<String, Object> merchantRegisterAndAssignService(long mId, MerchantRegisterRequest merchantRegister) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			String serviceName = "Merchant Registration VA";
//
//			String altEmail = null;
//			char gender = 0;
//			String gen = Encryption.decString(merchantRegister.getGender());
//			if (gen != null) {
//				gender = gen.charAt(0);
//			}
//
//			MerchantType merchantType = merchantTypeRepository.findById(mId).get();
//
//			Optional<Merchants> optional = merchantsRepository.findByEmailAndPhone(
//					Encryption.encString(merchantRegister.getEmail()),
//					Encryption.encString(merchantRegister.getPhone()));
//
//			if (!partnerServiceValidate.checkServiceExistOrNot(mId, serviceName)) {
//				LOGGER.info("-------------- CheckServiceExistOrNot  ---------------------------");
//
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//				return map;
//			}
//
//			if (optional.isPresent()) {
//				if (optional.get().getIsMerchantActive() == '1') {
//					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//					map.put(ResponseMessage.DESCRIPTION, "Merchant Email Id or Mobile Number Already Registered");
//					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//					return map;
//				} else {
//
//					Long merchantId = optional.get().getMerchantId();
//					String clientId = Encryption.encString(String.valueOf(merchantId));
//					String clientSecret = optional.get().getMerchantEmail();
//					String merchantFirstName = Encryption.decString(optional.get().getMerchantFirstname());
//					String merchantLastName = Encryption.decString(optional.get().getMerchantLastname());
//					String password = Encryption.decString(optional.get().getMerchantPassword());
//					String merchantBusinessName = Encryption.decString(optional.get().getMerchantBusinessName());
//					String authorization = BasicAuth.createEncodedText(merchantFirstName,
//							Encryption.decString(password));
//
//					Merchants merchants = merchantsRepository.findById(merchantId).get();
//					merchants.setIsMerchantActive('1');
//					merchants = merchantsRepository.save(merchants);
//
//					MerchantWalletInfo walletInfo = merchantWalletInfoRepository
//							.findByMerchantId(merchants.getMerchantId());
//
//					String userId = merchantFirstName.toUpperCase() + merchantLastName.substring(0, 3).toUpperCase()
//							+ merchantId;
//					String merchWalletAccountNo = Encryption.decString(walletInfo.getMerchWalletAccountNo());
//					String ifscCode = walletInfo.getMerchWalletPin();
//
//					MerchantInfoRequest merchantInfoRequest = new MerchantInfoRequest();
//					merchantInfoRequest.setBankIdJson("NA");
//					merchantInfoRequest.setBankIdUpi("NA");
//					merchantInfoRequest.setBbpsCallbackUrl("NA");
//					merchantInfoRequest.setClientId(clientId);
//					merchantInfoRequest.setClientSecret(clientSecret);
//					merchantInfoRequest.seteCollectCorpId("NA");
//					merchantInfoRequest.seteCollectNotifyUrl("NA");
//					merchantInfoRequest.seteCollectValidateUrl("NA");
//					merchantInfoRequest.seteNachCallbackUrl("NA");
//					merchantInfoRequest.seteNachRedirectUrl("NA");
//					merchantInfoRequest.setImageUrl("NA");
//					merchantInfoRequest
//							.setMerchantBussinessName(Encryption.decString(merchants.getMerchantBusinessName()));
//					merchantInfoRequest.setMerchantId(merchants.getMerchantId());
//					merchantInfoRequest.setPartnerKeyUpi("NA");
//					merchantInfoRequest.setPassword(Encryption.decString(merchants.getMerchantPassword()));
//					merchantInfoRequest.setPayoutCallbackUrl("NA");
//					merchantInfoRequest.setPgCallbackUrl("NA");
//					merchantInfoRequest.setPgRedirectUrl("NA");
//					merchantInfoRequest.setUpiCallbackUrl("NA");
//					merchantInfoRequest.setUsername(Encryption.decString(merchants.getMerchantFirstname()));
//					merchantInfoService.saveMerchantInfo(merchantInfoRequest);
//					LOGGER.info("Save Merchant Info Table Data");
//
//					map.put("merchantBusinessName", merchantBusinessName);
//					map.put("userId", userId);
//					map.put("virtualAccountNo", merchWalletAccountNo);
//					// map.put("ifscCode", "RATN0000100");
//					map.put("ifscCode", ifscCode);
//					map.put("clientId", clientId);
//					map.put("clientSecret", clientSecret);
//					map.put("authorization", "Basic " + authorization);
//					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//					map.put(ResponseMessage.DESCRIPTION, "Merchant Already Registered and is Active now");
//					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
//					return map;
//				}
//			}
//
//			else {
//
//				if (merchantsRepository.existsByMerchantPhone(Encryption.encString(merchantRegister.getPhone()))) {
//
//					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PHONE_ALREADY_ACTIVATED);
//					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//					return map;
//				}
//
//				if (merchantsRepository.existsByMerchantEmail(Encryption.encString(merchantRegister.getEmail()))) {
//					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_ACTIVATED);
//					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//					return map;
//				}
//
////				MerchantType merchantType = merchantTypeRepository.findById(mId).get();
//				LOGGER.info("Partner name " + merchantType.getMerchantTypeName());
//				Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());
//
//				Merchants merchants = new Merchants();
//				merchants.setMerchantFromdate(trxnDate);
//				merchants.setIsMerchantActive('1');
//				merchants.setIsMerchantEmailVerified('1');
//				merchants.setIsMerchantPhoneVerified('1');
//				merchants.setMerchantLoginCount('0');
//				merchants.setIsBankDocVerified('0');
//				merchants.setSecondSecQuestionId((long) 0);
//				merchants.setMerchantFloatAmount(0.0);
//				merchants.setMerchantSettlementFrequency("0");
//				merchants.setMerchantCommission(0L);
//
//				merchants.setMerchantFirstname(Encryption.encString(merchantRegister.getFirstName()));
//				merchants.setMerchantLastname(Encryption.encString(merchantRegister.getLastName()));
//				merchants.setMerchantEmail(Encryption.encString(merchantRegister.getEmail()));
//				merchants.setMerchantAddress1(Encryption.encString(merchantRegister.getAddress1()));
//				merchants.setMerchantAddress2(Encryption.encString(merchantRegister.getAddress2()));
//				merchants.setMerchantPassword(Encryption.encString(merchantRegister.getMerchantPassword()));
//				merchants.setMerchantCity(merchantRegister.getCity());
//				merchants.setMerchantState(merchantRegister.getState());
//				merchants.setMerchantCountry("India");
//				merchants.setMerchantZipcode(merchantRegister.getZipCode());
//				merchants.setGender(gender);
//				merchants.setMerchantType(merchantType);
//				merchants.setMerchantPhone(Encryption.encString(merchantRegister.getPhone()));
//				merchants.setMerchantBusinessName(Encryption.encString(merchantRegister.getBusinessName()));
//
//				merchants.setMerchantAccountNo(Encryption.encString(merchantRegister.getMerchantBankAccountNo()));
//				merchants.setMerchantBankName(merchantRegister.getMerchantBankName());
//				merchants.setMerchantBankCode(Encryption.encString(merchantRegister.getMerchantBankIfscCode()));
//				merchants.setMerchantBankBranch(merchantRegister.getMerchantBankBranch());
//
//				merchants = merchantsRepository.save(merchants);
//
//				LOGGER.info("Merchant Table Saved");
//
//				String res = getVirtualAccountNumber(merchants.getMerchantId(), merchantRegister.getPhone());
//
//				org.json.JSONObject object = new org.json.JSONObject(res);
//
//				String status = object.getString("status");
//				String vaNumber = null;
//				String vaIfsc = null;
//				if (status.equals("Success")) {
//					vaNumber = object.getString("VA_Number");
//
//					vaIfsc = object.getString("VA_Ifsc");
//				} else {
//					String description = object.getString("description");
//
//					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//					map.put(ResponseMessage.DESCRIPTION, description);
//					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//
//					return map;
//
//				}
//				LOGGER.info("vaNumber: " + vaNumber);
//
//				MerchantWalletInfo merchantWalletInfo = new MerchantWalletInfo();
////				merchantWalletInfo
////						.setMerchWalletAccountNo(Encryption.encString("FIDYPY" + merchantRegister.getPhone()));
//
//				merchantWalletInfo.setMerchWalletAccountNo(Encryption.encString(vaNumber));
//				merchantWalletInfo.setMerchants(merchants);
//				merchantWalletInfo.setMerchWalletFromDate(trxnDate);
//				merchantWalletInfo.setMerchWalletToDate(trxnDate);
//				merchantWalletInfo.setMerchWalletAmount(0.0);
//				merchantWalletInfo.setMerchPinLastChangedDate(trxnDate);
//				merchantWalletInfo.setIsIdProofVerified('Y');
//				merchantWalletInfo.setIsAddressProofVerified('Y');
//				merchantWalletInfo.setIsMerchKycComplete('Y');
//				merchantWalletInfo.setIsMerchWalletActive('Y');
//				merchantWalletInfo.setIsItaxVerified('Y');
//				merchantWalletInfo.setIsPermitVerified('Y');
//				merchantWalletInfo.setIsExtraDocVerified('Y');
//				merchantWalletInfo.setMerchWalletPin(vaIfsc);
//				merchantWalletInfo = merchantWalletInfoRepository.save(merchantWalletInfo);
//
//				LOGGER.info("Merchant Wallet Info Table Saved");
//
//				Partners partners = partnersRepository
//						.finByPartnerBussinessName(Encryption.encString(merchantType.getMerchantTypeName()));
//				long partnerId = partners.getPartnerId();
//				if (partners == null) {
//					altEmail = "NULL";
//				} else {
//					altEmail = partners.getPartnerAlternateEmail();
//				}
//
//				merchants.setMerchantAlternateEmail(altEmail);
//				merchants = merchantsRepository.save(merchants);
//
//				List<PartnerServices> partnerServicesList = partnerServiceRepository
//						.findAllPartnerServiceByPartnerId(partnerId);
//				LOGGER.info("partner service list " + partnerServicesList.size());
//				if (partnerServicesList.size() > 0) {
//					for (PartnerServices partnerServices : partnerServicesList) {
//						// save merchant service
//
//						LOGGER.info("ServiceId: " + partnerServices.getServiceId());
//
//						ServiceInfo serviceInfo = serviceInfoRepository.findById(partnerServices.getServiceId()).get();
//						String sName = Encryption.decString(serviceInfo.getServiceName());
//
//						if ((sName.equals("Merchant Registration") || sName.equals("Merchant Registration VA")
//								|| sName.equals("Merchant Registration UPI VA"))) {
//							LOGGER.info("Inside Non Assigned Services");
//						} else {
//							MerchantService merchantService = merchantsServiceService.assignMerchantService(
//									partnerServices.getPartnerServiceId(), partnerServices.getAmc(),
//									partnerServices.getOtc(), merchants.getMerchantId(),
//									partnerServices.getSerrviceProviderId(), partnerServices.getServiceId(),
//									partnerServices.getSubscriptionAmount(), partnerServices.getSubscriptionCycle(),
//									partnerServices.getServiceType());
//
//							LOGGER.info("Inside Merchant Services");
//						}
//
//					}
//				}
//				Long merchantId = merchants.getMerchantId();
//				String clientId = Encryption.encString(String.valueOf(merchantId));
//				String clientSecret = merchants.getMerchantEmail();
//				String merchantFirstName = Encryption.decString(merchants.getMerchantFirstname());
//				String password = Encryption.decString(merchants.getMerchantPassword());
//				String merchantLastName = Encryption.decString(merchants.getMerchantLastname());
//				String merchantBusinessName = Encryption.decString(merchants.getMerchantBusinessName());
//				String userId = merchantFirstName.toUpperCase() + merchantLastName.substring(0, 3).toUpperCase()
//						+ merchantId;
//
//				String authorization = BasicAuth.createEncodedText(merchantFirstName, Encryption.decString(password));
//				String merchWalletAccountNo = Encryption.decString(merchantWalletInfo.getMerchWalletAccountNo());
//				// String ifscCode= merchantWalletInfo.getMerchWalletPin();
//
//				MerchantInfoRequest merchantInfoRequest = new MerchantInfoRequest();
//				merchantInfoRequest.setBankIdJson("NA");
//				merchantInfoRequest.setBankIdUpi("NA");
//				merchantInfoRequest.setBbpsCallbackUrl("NA");
//				merchantInfoRequest.setClientId(clientId);
//				merchantInfoRequest.setClientSecret(clientSecret);
//				merchantInfoRequest.seteCollectCorpId("NA");
//				merchantInfoRequest.seteCollectNotifyUrl("NA");
//				merchantInfoRequest.seteCollectValidateUrl("NA");
//				merchantInfoRequest.seteNachCallbackUrl("NA");
//				merchantInfoRequest.seteNachRedirectUrl("NA");
//				merchantInfoRequest.setImageUrl("NA");
//				merchantInfoRequest.setMerchantBussinessName(Encryption.decString(merchants.getMerchantBusinessName()));
//				merchantInfoRequest.setMerchantId(merchants.getMerchantId());
//				merchantInfoRequest.setPartnerKeyUpi("NA");
//				merchantInfoRequest.setPassword(Encryption.decString(merchants.getMerchantPassword()));
//				merchantInfoRequest.setPayoutCallbackUrl("NA");
//				merchantInfoRequest.setPgCallbackUrl("NA");
//				merchantInfoRequest.setPgRedirectUrl("NA");
//				merchantInfoRequest.setUpiCallbackUrl("NA");
//				merchantInfoRequest.setUsername(Encryption.decString(merchants.getMerchantFirstname()));
//				merchantInfoService.saveMerchantInfo(merchantInfoRequest);
//				LOGGER.info("Save Merchant Info Table Data");
//
//				map.put("merchantBusinessName", merchantBusinessName);
//				map.put("userId", userId);
//				map.put("virtualAccountNo", merchWalletAccountNo);
//				// map.put("ifscCode", "YESB0CMSNOC");
//				map.put("ifscCode", vaIfsc);
//				map.put("clientId", clientId);
//				map.put("clientSecret", clientSecret);
//				map.put("authorization", "Basic " + authorization);
//				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//				map.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
//				return map;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
//			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
//			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//		}
//		return map;
//	}

	@SuppressWarnings("unused")
	@Override
	public Map<String, Object> merchantRegisterAndAssignService(long mId, MerchantRegisterRequest merchantRegister,
			long authUserId) {
		Map<String, Object> map = new HashMap<>();
		try {
			String serviceName = "Merchant Registration VA";

			String altEmail = null;
			char gender = 0;
			String gen = Encryption.decString(merchantRegister.getGender());
			if (gen != null) {
				gender = gen.charAt(0);
			}

			MerchantType merchantType = merchantTypeRepository.findById(mId).get();

			Optional<Merchants> optional = merchantsRepository.findByEmailAndPhone(
					Encryption.encString(merchantRegister.getEmail()),
					Encryption.encString(merchantRegister.getPhone()));

			if (!partnerServiceValidate.checkServiceExistOrNot(mId, serviceName)) {
				LOGGER.info("-------------- CheckServiceExistOrNot ---------------------------");

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (optional.isPresent()) {
				if (optional.get().getIsMerchantActive() == '1') {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Merchant Email Id or Mobile Number Already Registered");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				} else {

					Long merchantId = optional.get().getMerchantId();
					String clientId = Encryption.encString(String.valueOf(merchantId));
					String clientSecret = optional.get().getMerchantEmail();
					String merchantFirstName = Encryption.decString(optional.get().getMerchantFirstname());
					String merchantLastName = Encryption.decString(optional.get().getMerchantLastname());
					String password = Encryption.decString(optional.get().getMerchantPassword());
					String merchantBusinessName = Encryption.decString(optional.get().getMerchantBusinessName());
					String authorization = BasicAuth.createEncodedText(merchantFirstName,
							Encryption.decString(password));

					Merchants merchants = merchantsRepository.findById(merchantId).get();
					merchants.setIsMerchantActive('1');
					merchants = merchantsRepository.save(merchants);

					MerchantWalletInfo walletInfo = merchantWalletInfoRepository
							.findByMerchantIdd(merchants.getMerchantId());

					String userId = merchantFirstName.toUpperCase() + merchantLastName.substring(0, 3).toUpperCase()
							+ merchantId;
					String merchWalletAccountNo = Encryption.decString(walletInfo.getMerchWalletAccountNo());
					String ifscCode = walletInfo.getMerchWalletPin();

					String merchantInfo = saveMerchantInfoDetails(merchantId, merchants.getMerchantBusinessName(),
							clientId, clientSecret, merchantFirstName, password);

					map.put("merchantBusinessName", merchantBusinessName);
					map.put("userId", userId);
					map.put("virtualAccountNo", merchWalletAccountNo);
					map.put("ifscCode", ifscCode);
					map.put("clientId", clientId);
					map.put("clientSecret", clientSecret);
					map.put("authorization", "Basic " + authorization);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "Merchant Already Registered and is Active now");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					return map;
				}
			}

			else {

				if (merchantsRepository.existsByMerchantPhone(Encryption.encString(merchantRegister.getPhone()))) {

					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PHONE_ALREADY_ACTIVATED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				if (merchantsRepository.existsByMerchantEmail(Encryption.encString(merchantRegister.getEmail()))) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_ACTIVATED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				Partners partners = partnersRepository
						.finByPartnerBussinessName(Encryption.encString(merchantType.getMerchantTypeName()));
				long partnerId = partners.getPartnerId();

				long merchantId = saveMerchantDetails(merchantRegister.getFirstName(), merchantRegister.getLastName(),
						merchantRegister.getEmail(), merchantRegister.getAddress1(), merchantRegister.getAddress2(),
						merchantRegister.getMerchantPassword(), merchantRegister.getCity(), merchantRegister.getState(),
						merchantRegister.getZipCode(), gender, merchantType.getMerchantTypeId(),
						merchantRegister.getPhone(), merchantRegister.getBusinessName(),
						merchantRegister.getMerchantBankAccountNo(), merchantRegister.getMerchantBankName(),
						merchantRegister.getMerchantBankIfscCode(), merchantRegister.getMerchantBankBranch(),
						partners.getAuthUserId(), partners.getVerticalRegions());

				String res = getVirtualAccountNumber(merchantId, merchantRegister.getPhone());

				org.json.JSONObject object = new org.json.JSONObject(res);

				String status = object.getString("status");
				String vaNumber = null;
				String vaIfsc = null;
				if (status.equals("Success")) {
					vaNumber = object.getString("VA_Number");

					vaIfsc = object.getString("VA_Ifsc");
				} else {
					String description = object.getString("description");

					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, description);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

					return map;

				}
				LOGGER.info("vaNumber: " + vaNumber);

				String merchWalletAccountNo = saveMerchantWalletInfoDetails(merchantId, vaNumber, vaIfsc);

				if (partners == null) {
					altEmail = "NULL";
				} else {
					altEmail = partners.getPartnerAlternateEmail();
				}

				Merchants merchants = merchantsRepository.findById(merchantId).get();
				merchants.setMerchantAlternateEmail(altEmail);
				merchants = merchantsRepository.save(merchants);

				List<PartnerServices> partnerServicesList = partnerServiceRepository
						.findAllPartnerServiceByPartnerId(partnerId);
				LOGGER.info("partner service list " + partnerServicesList.size());
				if (partnerServicesList.size() > 0) {
					for (PartnerServices partnerServices : partnerServicesList) {
						// save merchant service

						LOGGER.info("ServiceId: " + partnerServices.getServiceId());

						ServiceInfo serviceInfo = serviceInfoRepository.findById(partnerServices.getServiceId()).get();
						String sName = Encryption.decString(serviceInfo.getServiceName());

						if ((sName.equals("Merchant Registration") || sName.equals("Merchant Registration VA")
								|| sName.equals("Merchant Registration UPI VA"))) {
							LOGGER.info("Inside Non Assigned Services");
						} else {
							MerchantService merchantService = merchantsServiceService.assignMerchantService(
									partnerServices.getPartnerServiceId(), partnerServices.getAmc(),
									partnerServices.getOtc(), merchants.getMerchantId(),
									partnerServices.getSerrviceProviderId(), partnerServices.getServiceId(),
									partnerServices.getSubscriptionAmount(), partnerServices.getSubscriptionCycle(),
									partnerServices.getServiceType());

							LOGGER.info("Inside Merchant Services");
						}

					}
				}
				merchantId = merchants.getMerchantId();
				String clientId = Encryption.encString(String.valueOf(merchantId));
				String clientSecret = merchants.getMerchantEmail();
				String merchantFirstName = Encryption.decString(merchants.getMerchantFirstname());
				String password = Encryption.decString(merchants.getMerchantPassword());
				String merchantLastName = Encryption.decString(merchants.getMerchantLastname());
				String merchantBusinessName = Encryption.decString(merchants.getMerchantBusinessName());
				String userId = merchantFirstName.toUpperCase() + merchantLastName.substring(0, 3).toUpperCase()
						+ merchantId;

				String authorization = BasicAuth.createEncodedText(merchantFirstName, Encryption.decString(password));
				merchWalletAccountNo = Encryption.decString(merchWalletAccountNo);

				map.put("merchantBusinessName", merchantBusinessName);
				map.put("userId", userId);
				map.put("virtualAccountNo", merchWalletAccountNo);
				map.put("ifscCode", vaIfsc);
				map.put("clientId", clientId);
				map.put("clientSecret", clientSecret);
				map.put("authorization", "Basic " + authorization);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Registered Successfully");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				return map;
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> merchantDeactive(@Valid MerchantActiveRequest merchantActiveRequest) {
		Map<String, Object> map = new HashMap<>();
		try {

			Merchants merchants = merchantsRepository.findByMerchantEmailAndPhone(
					Encryption.encString(merchantActiveRequest.getEmail()),
					Encryption.encString(merchantActiveRequest.getPhone()));
			LOGGER.info("merchants " + merchants);
			if (merchants != null) {

				merchants.setIsMerchantActive('0');
				merchants = merchantsRepository.save(merchants);

				MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(merchants.getMerchantId());
				merchantInfo.setIsMerchantActive('0');
				merchantInfo = merchantInfoRepository.save(merchantInfo);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Merchant deactivated Successfully");
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Email or Phone Number does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> findBBPSCommissionRate(long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {
			List<Object[]> list = merchantServicesRepository.findByMerchantIdToServiceCommission(merchantId);

			List<MerchantCommissionDeatilsResponse> responseList = new ArrayList<>();
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
			if (list.size() != 0) {
				for (Object[] object : list) {

					long mServiceId = ((Number) object[0]).longValue();
					long mServiceCommissionStart = ((Number) object[1]).longValue();
					long mServiceCommissionEnd = ((Number) object[2]).longValue();
					double mServiceCommissionRate = ((Number) object[3]).doubleValue();
					long mID = ((Number) object[4]).longValue();
					long serviceId = ((Number) object[5]).longValue();

					ServiceInfo serviceInfo = serviceInfoRepository.findByMerchantServiceId(serviceId);
					String serviceName = serviceInfo.getServiceName();

					MerchantCommissionDeatilsResponse response = new MerchantCommissionDeatilsResponse();
					List<MerchantServiceCommission> merchantServiceCommissionlist = merchantServiceCommissionRepository
							.findByMerchantserviceId(mServiceId);
					for (MerchantServiceCommission merchantServiceCommission : merchantServiceCommissionlist) {

						response.setType(
								Encryption.decString(merchantServiceCommission.getMerchantServiceCommissionType()));
					}

					response.setMerchantId(mID);
					response.setMerchantServiceId(mServiceId);
					response.setMerchantServiceCommissionStart(mServiceCommissionStart);
					response.setMerchantServiceCommissionEnd(mServiceCommissionEnd);
					response.setMerchantServiceCommissionRate(
							Double.parseDouble(amountFormate.format(mServiceCommissionRate)));
					response.setServiceName(Encryption.decString(serviceName));
					responseList.add(response);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put("data", responseList);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				}
			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Commission not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> verifyMerchantEmail(String email) {
		Map<String, Object> map = new HashMap<>();
		try {

			Optional<MerchantUser> merchantUser = merchantUserRepository
					.findByMerchantUserEmail(Encryption.encString(email));

			if (merchantUser.isPresent()) {

				if (merchantUser.get().getIsActive() == '0') {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
					map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					return map;
				}

				Optional<OTPVerification> otpVerificationVerify = otpVerificationRepository.findByBankId(email,
						"lGfsIVVIFsb+2MILfsXXAA==", merchantUser.get().getMerchantId());

				if (otpVerificationVerify.isPresent()) {
					Timestamp currentTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
					long transactionTimeValidate = DateAndTime.compareTwoTimeStamps(currentTime,
							otpVerificationVerify.get().getCreationDate());

					if (transactionTimeValidate <= 300) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION,
								"The link has already been generated. You can generate a new one after 5 minutes.");
						map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
						return map;
					}
				}

				String token = RandomNumberGenrator.generateToken(merchantUser.get().getMerchantId());
				String url = ResponseMessage.FORGET_PASSWORD_URL + token;
				String shortUrl = urlGenerater.generateShortUrl(url);
				String res = EmailNotification.sendEmailForForgetPassword(email, shortUrl);
				String trxnDate = DateAndTime.getCurrentTimeInIST();

				OTPVerification otpVerification = new OTPVerification();
				otpVerification.setBankId(email);
				otpVerification.setCreationDate(Timestamp.valueOf(trxnDate));
				otpVerification.setMerchantBankAccountNumber(Encryption.encString("NA"));
				otpVerification.setMerchantBankIfsc(Encryption.encString("NA"));
				otpVerification.setMerchantId(merchantUser.get().getMerchantId());
				otpVerification.setOtp("0");
				otpVerification.setOtpRefId(token);

				otpVerification = otpVerificationRepository.save(otpVerification);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//		r		map.put(ResponseMessage.DESCRIPTION,
//						"A link has been sent to the registered mail to change the password.");
				map.put(ResponseMessage.DESCRIPTION, "If your register you will get the forget password link over the mail.");
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				//map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAILID_NOT_REGISTERED);
				map.put(ResponseMessage.DESCRIPTION, "If your register you will get the forget password link over the mail.");
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	@Override
	public Map<String, Object> forgetMerchantPassowrd(ForgetPasswordRequest forgetPasswordRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			OTPVerification otpVerification = otpVerificationRepository.findByToken(forgetPasswordRequest.getToken());

			if (otpVerification != null) {

				if (otpVerification.getOtp().equalsIgnoreCase("1")) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Forgot password link already used.");
					map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					return map;
				}

				Timestamp currentTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
				long transactionTimeValidate = DateAndTime.compareTwoTimeStamps(currentTime,
						otpVerification.getCreationDate());

				if (transactionTimeValidate >= 270) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Password link expired");
					map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					return map;
				}

				Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
				MerchantUser merchantUser = merchantUserRepository
						.findByMerchantUserEmailForPassword(Encryption.encString(otpVerification.getBankId()));

				String userType = merchantUser.getMerchantUserType();

				if (merchantUser.getIsActive() == '0') {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
					map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					return map;
				}
				
				if (Encryption.decString(merchantUser.getMerchantUserPassword()).equals(forgetPasswordRequest.getPassword())) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "The new password should not be the same as the old password.");
					map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					return map;
				}

				if (userType.equals(MERCHANT_USER_TYPE_SECONDARY)) {
					merchantUser.setMerchantUserPassword(Encryption.encString(forgetPasswordRequest.getPassword()));
					merchantUser.setLoginCount(0L);
					merchantUser.setUpdatePasswordDate(trxnDate);
					merchantUser.setMerchantUserKey("NA");
					merchantUser = merchantUserRepository.save(merchantUser);
				} else {
					merchantUser.setMerchantUserPassword(Encryption.encString(forgetPasswordRequest.getPassword()));
					merchantUser.setLoginCount(0L);
					merchantUser.setUpdatePasswordDate(trxnDate);
					merchantUser.setMerchantUserKey("NA");
					merchantUser = merchantUserRepository.save(merchantUser);

					Merchants merchants = merchantsRepository.findById(merchantUser.getMerchantId()).get();
					merchants.setMerchantPassword(Encryption.encString(forgetPasswordRequest.getPassword()));
					merchants = merchantsRepository.save(merchants);
				}
				otpVerification.setOtp("1");
				otpVerificationRepository.save(otpVerification);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Merchant password changed successfully");
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "token does not exist");
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			}

		} catch (Exception e) {

			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	@Override
	public Map<String, Object> findKYcChargesRate(long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {
			List<Object[]> list = merchantServicesRepository.findByMerchantIdToEkycServiceCharges(merchantId);

			List<MerchantChargesDeatilsResponse> responseList = new ArrayList<>();
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

			if (list.size() != 0) {
				for (Object[] object : list) {

					long mServiceId = ((Number) object[0]).longValue();
					long mServiceCommissionStart = ((Number) object[1]).longValue();
					long mServiceCommissionEnd = ((Number) object[2]).longValue();
					double mServiceCommissionRate = ((Number) object[3]).doubleValue();
					long mID = ((Number) object[4]).longValue();
					long serviceId = ((Number) object[5]).longValue();

					ServiceInfo serviceInfo = serviceInfoRepository.findByMerchantServiceId(serviceId);
					String serviceName = serviceInfo.getServiceName();

					MerchantChargesDeatilsResponse response = new MerchantChargesDeatilsResponse();
					List<MerchantServiceCharges> merchantServiceChargeslist = merchantServiceChargesRepository
							.findByMerchantServiceId(mServiceId);
					for (MerchantServiceCharges merchantServiceCharges : merchantServiceChargeslist) {

						response.setType(Encryption.decString(merchantServiceCharges.getMerchantServiceChargeType()));
					}
					response.setMerchantId(mID);
					response.setMerchantServiceId(mServiceId);
					response.setMerchantServiceChargesStart(mServiceCommissionStart);
					response.setMerchantServiceChargesEnd(mServiceCommissionEnd);
					response.setMerchantServiceChargesRate(
							Double.parseDouble(amountFormate.format(mServiceCommissionRate)));
					response.setServiceName(Encryption.decString(serviceName));
					responseList.add(response);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DATA, responseList);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				}
			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Commission not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> findENachChargesRate(long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {
			List<Object[]> list = merchantServicesRepository.findByMerchantIdToENachServiceCharges(merchantId);

			List<MerchantChargesDeatilsResponse> responseList = new ArrayList<>();
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

			if (list.size() != 0) {
				for (Object[] object : list) {

					long mServiceId = ((Number) object[0]).longValue();
					long mServiceCommissionStart = ((Number) object[1]).longValue();
					long mServiceCommissionEnd = ((Number) object[2]).longValue();
					double mServiceCommissionRate = ((Number) object[3]).doubleValue();
					long mID = ((Number) object[4]).longValue();
					long serviceId = ((Number) object[5]).longValue();

					ServiceInfo serviceInfo = serviceInfoRepository.findByMerchantServiceId(serviceId);
					String serviceName = serviceInfo.getServiceName();

					MerchantChargesDeatilsResponse response = new MerchantChargesDeatilsResponse();
					List<MerchantServiceCharges> merchantServiceChargeslist = merchantServiceChargesRepository
							.findByMerchantServiceId(mServiceId);
					for (MerchantServiceCharges merchantServiceCharges : merchantServiceChargeslist) {

						response.setType(Encryption.decString(merchantServiceCharges.getMerchantServiceChargeType()));
					}

					response.setMerchantId(mID);
					response.setMerchantServiceId(mServiceId);
					response.setMerchantServiceChargesStart(mServiceCommissionStart);
					response.setMerchantServiceChargesEnd(mServiceCommissionEnd);
					response.setMerchantServiceChargesRate(
							Double.parseDouble(amountFormate.format(mServiceCommissionRate)));
					response.setServiceName(Encryption.decString(serviceName));
					responseList.add(response);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DATA, responseList);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				}
			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Commission not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;

	}

	// payin
	@Override
	public Map<String, Object> findPayinChargesRate(long merchantId) {

		Map<String, Object> map = new HashMap<>();

		try {
			List<Object[]> list = merchantServicesRepository.findByMerchantIdToPayinServiceCharges(merchantId);

			List<MerchantChargesDeatilsResponse> responseList = new ArrayList<>();
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

			if (list.size() != 0) {
				for (Object[] object : list) {

					long mServiceId = ((Number) object[0]).longValue();
					long mServiceCommissionStart = ((Number) object[1]).longValue();
					long mServiceCommissionEnd = ((Number) object[2]).longValue();
					double mServiceCommissionRate = ((Number) object[3]).doubleValue();
					long mID = ((Number) object[4]).longValue();
					long serviceId = ((Number) object[5]).longValue();
					ServiceInfo serviceInfo = serviceInfoRepository.findByMerchantServiceId(serviceId);
					String serviceName = serviceInfo.getServiceName();

					MerchantChargesDeatilsResponse response = new MerchantChargesDeatilsResponse();
					List<MerchantServiceCharges> merchantServiceChargeslist = merchantServiceChargesRepository
							.findByMerchantServiceId(mServiceId);
					for (MerchantServiceCharges merchantServiceCharges : merchantServiceChargeslist) {

						response.setType(Encryption.decString(merchantServiceCharges.getMerchantServiceChargeType()));
					}
					response.setMerchantId(mID);
					response.setMerchantServiceId(mServiceId);
					response.setMerchantServiceChargesStart(mServiceCommissionStart);
					response.setMerchantServiceChargesEnd(mServiceCommissionEnd);
					response.setMerchantServiceChargesRate(
							Double.parseDouble(amountFormate.format(mServiceCommissionRate)));
					response.setServiceName(Encryption.decString(serviceName));
					responseList.add(response);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DATA, responseList);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				}
			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Commission not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;

	}

	// payout
	@Override
	public Map<String, Object> findPayOutChargesRate(long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {
			List<Object[]> list = merchantServicesRepository.findByMerchantIdToServiceCharges(merchantId);

			List<MerchantChargesDeatilsResponse> responseList = new ArrayList<>();
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

			if (list.size() != 0) {
				for (Object[] object : list) {

					long mServiceId = ((Number) object[0]).longValue();
					long mServiceCommissionStart = ((Number) object[1]).longValue();
					long mServiceCommissionEnd = ((Number) object[2]).longValue();
					double mServiceCommissionRate = ((Number) object[3]).doubleValue();
					long mID = ((Number) object[4]).longValue();
					long serviceId = ((Number) object[5]).longValue();

					ServiceInfo serviceInfo = serviceInfoRepository.findByMerchantServiceId(serviceId);
					String serviceName = serviceInfo.getServiceName();

					MerchantChargesDeatilsResponse response = new MerchantChargesDeatilsResponse();
					List<MerchantServiceCharges> merchantServiceChargeslist = merchantServiceChargesRepository
							.findByMerchantServiceId(mServiceId);
					for (MerchantServiceCharges merchantServiceCharges : merchantServiceChargeslist) {

						response.setType(Encryption.decString(merchantServiceCharges.getMerchantServiceChargeType()));
					}

					response.setMerchantId(mID);
					response.setMerchantServiceId(mServiceId);
					response.setMerchantServiceChargesStart(mServiceCommissionStart);
					response.setMerchantServiceChargesEnd(mServiceCommissionEnd);
					response.setMerchantServiceChargesRate(
							Double.parseDouble(amountFormate.format(mServiceCommissionRate)));
					response.setServiceName(Encryption.decString(serviceName));
					responseList.add(response);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DATA, responseList);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				}
			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Commission not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	// pg
	@Override
	public Map<String, Object> findPgChargesRate(long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {
			List<Object[]> list = merchantServicesRepository.findByMerchantIdToPgServiceCharges(merchantId);
			List<MerchantChargesDeatilsResponse> responseList = new ArrayList<>();
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

			if (list.size() != 0) {
				for (Object[] object : list) {

					long mServiceId = ((Number) object[0]).longValue();
					long mServiceCommissionStart = ((Number) object[1]).longValue();
					long mServiceCommissionEnd = ((Number) object[2]).longValue();
					double mServiceCommissionRate = ((Number) object[3]).doubleValue();
					long mID = ((Number) object[4]).longValue();
					long serviceId = ((Number) object[5]).longValue();

					ServiceInfo serviceInfo = serviceInfoRepository.findByMerchantServiceId(serviceId);
					String serviceName = serviceInfo.getServiceName();

					MerchantChargesDeatilsResponse response = new MerchantChargesDeatilsResponse();
					List<MerchantServiceCharges> merchantServiceChargeslist = merchantServiceChargesRepository
							.findByMerchantServiceId(mServiceId);
					for (MerchantServiceCharges merchantServiceCharges : merchantServiceChargeslist) {

						response.setType(Encryption.decString(merchantServiceCharges.getMerchantServiceChargeType()));
					}
					response.setMerchantId(mID);
					response.setMerchantServiceId(mServiceId);
					response.setMerchantServiceChargesStart(mServiceCommissionStart);
					response.setMerchantServiceChargesEnd(mServiceCommissionEnd);
					response.setMerchantServiceChargesRate(
							Double.parseDouble(amountFormate.format(mServiceCommissionRate)));
					response.setServiceName(Encryption.decString(serviceName));
					responseList.add(response);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DATA, responseList);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				}
			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant Commission not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> getOTPDetails() {

		Map<String, Object> map = new HashMap<>();
		List<OTPVerificationResponse> otpResponseList = new ArrayList<>();
		try {
			List<OTPVerification> list = otpVerificationRepository.findLastFiveOTP();

			AtomicInteger atomic = new AtomicInteger(1);

			for (OTPVerification otpVerification : list) {

				OTPVerificationResponse otpVerificationResponse = new OTPVerificationResponse();
				otpVerificationResponse.setsNO(atomic.getAndIncrement());

				otpVerificationResponse.setMerchantId(otpVerification.getMerchantId());

				String date = DateAndTime.dateFormatReports(otpVerification.getCreationDate().toString());
				otpVerificationResponse.setCreationDate(date);

				otpVerificationResponse.setOtp(otpVerification.getOtp());
				otpVerificationResponse.setOtpRefId(otpVerification.getOtpRefId());
				otpVerificationResponse.setBankId(otpVerification.getBankId());
				otpVerificationResponse
						.setMerchantBankIfsc(Encryption.decString(otpVerification.getMerchantBankIfsc()));
				otpVerificationResponse.setMerchantBankAccountNumber(
						Encryption.decString(otpVerification.getMerchantBankAccountNumber()));

				otpResponseList.add(otpVerificationResponse);
			}
			map.put("OTPVerificationDetails", otpResponseList);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public List<MerchantCommissionDeatilsResponse> findBBPSCommissionRateExcel(long merchantId) {
		List<MerchantCommissionDeatilsResponse> responseList = new ArrayList<>();
		try {
			List<Object[]> list = merchantServicesRepository.findByMerchantIdToServiceCommission(merchantId);

			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
			if (!list.isEmpty()) {
				for (Object[] object : list) {

					long mServiceId = ((Number) object[0]).longValue();
					long mServiceCommissionStart = ((Number) object[1]).longValue();
					long mServiceCommissionEnd = ((Number) object[2]).longValue();
					double mServiceCommissionRate = ((Number) object[3]).doubleValue();
					long mID = ((Number) object[4]).longValue();
					long serviceId = ((Number) object[5]).longValue();

					ServiceInfo serviceInfo = serviceInfoRepository.findByMerchantServiceId(serviceId);
					String serviceName = serviceInfo.getServiceName();

					MerchantCommissionDeatilsResponse response = new MerchantCommissionDeatilsResponse();
					List<MerchantServiceCommission> merchantServiceCommissionlist = merchantServiceCommissionRepository
							.findByMerchantserviceId(mServiceId);
					for (MerchantServiceCommission merchantServiceCommission : merchantServiceCommissionlist) {

						response.setType(
								Encryption.decString(merchantServiceCommission.getMerchantServiceCommissionType()));
					}

					response.setMerchantId(mID);
					response.setMerchantServiceId(mServiceId);
					response.setMerchantServiceCommissionStart(mServiceCommissionStart);
					response.setMerchantServiceCommissionEnd(mServiceCommissionEnd);
					response.setMerchantServiceCommissionRate(
							Double.parseDouble(amountFormate.format(mServiceCommissionRate)));
					response.setServiceName(Encryption.decString(serviceName));
					responseList.add(response);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return responseList;
	}

}
