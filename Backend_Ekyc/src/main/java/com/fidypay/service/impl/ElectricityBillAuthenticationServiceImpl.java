package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.Karza.KarzaService;
import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.dto.ServiceProvidersList;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycRequest;
import com.fidypay.entity.EkycTransactionDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.repo.ServiceProvidersRepository;
import com.fidypay.request.ElectricityBillRequest;
import com.fidypay.service.ElectricityBillAuthenticationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

@Service
public class ElectricityBillAuthenticationServiceImpl implements ElectricityBillAuthenticationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElectricityBillAuthenticationServiceImpl.class);

	@Autowired
	private WalletNotification walletNotification;

	@Autowired
	private KarzaService karzaService;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private EkycRequestRepository ekycRequestRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private WalletService walletService;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

//    @Autowired
//    private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@Override
	public Map<String, Object> electricityServiceProvidersList() {

		Map<String, Object> map = new HashMap<>();

		try {

			List<ServiceProvidersList> list = new ArrayList<>();

			list.add(new ServiceProvidersList("Southern Power Distribution Company of Andhra Pradesh Limited",
					"APSPDCL"));
			list.add(new ServiceProvidersList("Eastern Power Distribution Company of Andhra Pradesh Limited",
					"APEPDCL"));
			list.add(new ServiceProvidersList("Assam Power Distribution Company Limited", "APDCL"));
			list.add(new ServiceProvidersList("North Bihar Power Distribution Company Limited", "NBPDCL"));
			list.add(new ServiceProvidersList("South Bihar Power Distribution Company Limited", "SBPDCL"));
			list.add(new ServiceProvidersList("Calcutta Electric Supply Corporation Limited", "CESC"));
			list.add(new ServiceProvidersList("Chandigarh Electricity Department", "CH_ELEC"));
			list.add(new ServiceProvidersList("Chhattisgarh State Power Distribution Company Limited", "CSPDCL"));
			list.add(new ServiceProvidersList("Daman and Diu Electricity Department", "DAMAN_DIU"));
			list.add(new ServiceProvidersList("Tata Power Delhi Distribution", "TATA_DL"));
			list.add(new ServiceProvidersList("BSES Yamuna Power Ltd / BSES Rajdhani Power Ltd", "BSES_DL"));
			list.add(new ServiceProvidersList("Goa Electricity- for Tisvadi, Ponda & Verna", "GOA_ELEC"));
			list.add(new ServiceProvidersList("Goa Electricity- for others", "UPAY_GOA"));
			list.add(new ServiceProvidersList("Uttar Gujarat VIJ Company Ltd.", "UGVCL"));
			list.add(new ServiceProvidersList("Pashchim Gujarat VIJ Company Ltd.", "PGVCL"));
			list.add(new ServiceProvidersList("Madhya Gujarat VIJ Company Limited", "MGVCL"));
			list.add(new ServiceProvidersList("Dakshin Gujarat VIJ Company Limited", "DGVCL"));
			list.add(new ServiceProvidersList("Torrent Power Limited - Ahmedabad", "TORRENT_AHD"));
			list.add(new ServiceProvidersList("Torrent Power Limited - Surat", "TORRENT_SURAT"));
			list.add(new ServiceProvidersList("Torrent Power Limited - Agra", "TORRENT_AGRA"));
			list.add(new ServiceProvidersList("Torrent Power Limited - Bhiwandi", "TORRENT_BHIWANDI"));
			list.add(new ServiceProvidersList("Torrent Power Limited - Dahej", "TORRENT_DAHEJ"));
			list.add(new ServiceProvidersList("Dakshin Haryana Bijli Vitran Nigam", "DHBVN"));
			list.add(new ServiceProvidersList("Uttar Haryana Bijli Vitran Nigam", "UHBVN"));
			list.add(new ServiceProvidersList("Himachal Pradesh State Electricity Board Ltd", "HPSEB"));
			list.add(new ServiceProvidersList("Mangalore Electricity Supply Company Limited", "MESCOM"));
			list.add(new ServiceProvidersList("Bangalore Electricity Supply Company Ltd", "BESCOM"));
			list.add(new ServiceProvidersList("Chamundeshwari Electricity Supply Company Limited Mysore", "CESCOM"));
			list.add(new ServiceProvidersList("Gulbarga Electricity Supply Company Limited", "GESCOM"));
			list.add(new ServiceProvidersList("Hubli Electricity Supply Company Limited", "HESCOM"));
			list.add(new ServiceProvidersList("Kerala State Electricity Board", "KERALA"));
			list.add(new ServiceProvidersList("Madhya Pradesh Paschim Kshetra Vidyut Vitran Company Limited", "MPWZ"));
			list.add(new ServiceProvidersList("Madhya Pradesh Madhya Kshetra Vidyut Vitran Company Limited", "MPCZ"));
			list.add(new ServiceProvidersList("Madhya Pradesh Poorv Kshetra Vidyut Vitran Company Limited", "MPEZ"));
			list.add(new ServiceProvidersList("TATA-Power - Mumbai", "TATA_MUMBAI"));
			list.add(new ServiceProvidersList("MAHAVITARAN-Maharashtra State Electricity Distribution Co. Ltd (MSEDCL)",
					"MAHAVITRAN"));
			list.add(new ServiceProvidersList(
					"Brihanmumbai Electric Supply & Transport Undertaking for Greater Mumbai (BEST_MH)", "BEST_MH"));
			list.add(new ServiceProvidersList("Meghalaya Energy Corporation Limited", "MeECL"));
			list.add(new ServiceProvidersList("Supply Company of Orissa Limited (North, South, West, Central)",
					"ORISSA"));
			list.add(new ServiceProvidersList("Punjab State Power Corporation Limited", "PSPCL"));
			list.add(new ServiceProvidersList("Jaipur Vidhyut Vitran Nigam Ltd", "JAIPUR"));
			list.add(new ServiceProvidersList("Ajmer Vidhyut Vitran Nigam Ltd", "AJMER"));
			list.add(new ServiceProvidersList("Jodhpur Vidhyut Vitran Nigam Ltd", "JODHPUR"));
			list.add(new ServiceProvidersList(
					"Tamil Nadu Generation and Distribution Corporation Limited - Chennai_North", "CHENNAI_NORTH"));
			list.add(new ServiceProvidersList("Tamil Nadu Generation and Distribution Corporation Limited - Villupuram",
					"VILLUPURAM"));
			list.add(new ServiceProvidersList("Tamil Nadu Generation and Distribution Corporation Limited - Coimbatore",
					"COIMBATORE"));
			list.add(new ServiceProvidersList("Tamil Nadu Generation and Distribution Corporation Limited - Erode",
					"ERODE"));
			list.add(new ServiceProvidersList("Tamil Nadu Generation and Distribution Corporation Limited - Madurai",
					"MADURAI"));
			list.add(new ServiceProvidersList("Tamil Nadu Generation and Distribution Corporation Limited - Trichy",
					"TRICHY"));
			list.add(new ServiceProvidersList("Tamil Nadu Generation and Distribution Corporation Limited - Tirunelvel",
					"TIRUNELVEL"));
			list.add(new ServiceProvidersList("Tamil Nadu Generation and Distribution Corporation Limited - Vellore",
					"VELLORE"));
			list.add(new ServiceProvidersList(
					"Tamil Nadu Generation and Distribution Corporation Limited - Chennai_South", "CHENNAI_SOUTH"));
			list.add(
					new ServiceProvidersList("The Southern Power Distribution Company of Telangana Limited", "TSSPCL"));
			list.add(new ServiceProvidersList("Tripura State Electricity Corporation Limited", "TRIPURA"));
			list.add(new ServiceProvidersList("Kanpur Electricity Supply Company", "KESCO"));
			list.add(new ServiceProvidersList("UP Vidyut Vitran Nigam Limited (All Zones)", "UPPCL"));
			list.add(new ServiceProvidersList("WEST BENGAL STATE ELECTRICITY DISTRIBUTION COMPANY LIMITED", "WBENGAL"));

			JSONArray array = new JSONArray();
			array.put(list);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Service provider list");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("list", array.toList());

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> epfUANValidation(String uanNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = ResponseMessage.EPF_UAN_SERVICE;
			double amtInDouble = 3.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;
			}

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			LOGGER.info("serviceName " + serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

//            Bank bank = new Bank();
//            ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//                    merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//                    trxnRefId, walletTxnRefNo, serviceName);
//            LOGGER.info("Name of Thread -----------------------" + test.getName());
//            test.start();

			String serviceProvider = ResponseMessage.KARZA;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//            EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//            LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'EPF UAN VALIDATION' EKYC TRXN DETAILS : {}", elk);

			map = karzaService.epfUANValidationAPI(uanNumber);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> electricityBillAuthentication(ElectricityBillRequest electricityBillRequest,
			Long merchantId, double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			// String serviceName = "Electricity Bill Authentication";
			String serviceName = ResponseMessage.ELECTRICITY_SERVICE;
			double amtInDouble = 9.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;
			}

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			LOGGER.info("serviceName " + serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//            Bank bank = new Bank();
//            ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//                    merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//                    trxnRefId, walletTxnRefNo, serviceName);
//            LOGGER.info("Name of Thread -----------------------" + test.getName());
//            test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			String serviceProvider = ResponseMessage.KARZA;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//            EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//            LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'ELECTRICITY BILL AUTHENTICATION' EKYC TRXN DETAILS : {}",
//                    elk);

			map = karzaService.electricityBillAuthentication(electricityBillRequest.getConsumerId(),
					electricityBillRequest.getServiceProviderCode());

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}
}
