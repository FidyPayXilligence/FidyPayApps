package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.ServiceProvider.Signzy.SignzyService;
import com.fidypay.config.EkycServiceResolver;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycRequest;
import com.fidypay.entity.EkycTransactionDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.repo.ServiceProvidersRepository;
import com.fidypay.request.Validate;
import com.fidypay.request.ValidateOtp;
import com.fidypay.service.AadhaarService;
import com.fidypay.service.provider.AadhaarServiceProvider;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.ValidateUtils;
import com.fidypay.utils.ex.Validations;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class AadhaarServiceImpl implements AadhaarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AadhaarServiceImpl.class);

    public static final String BASE_URL = "https://signzy.tech/api/v2/snoops";

    private final EkycServiceResolver ekycServiceResolver;

    @Autowired
    private WalletNotification walletNotification;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    @Autowired
    private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

    @Autowired
    private ServiceInfoRepository serviceInfoRepository;

    @Autowired
    private MerchantServiceRepository merchantServiceRepository;

    @Autowired
    private MerchantServiceChargeService chargeService;

    @Autowired
    private ServiceProvidersRepository serviceprovidersrepository;

    @Autowired
    private SignzyService signzyService;

    @Autowired
    private WalletService walletService;

    // @Autowired
    // private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

    private final Semaphore semaphore = new Semaphore(1);

    private final Lock lock = new ReentrantLock();


    public AadhaarServiceImpl(EkycServiceResolver ekycServiceResolver) {
        this.ekycServiceResolver = ekycServiceResolver;
    }

    @Override
    public synchronized Map<String, Object> saveDataForVerify(String accountNumber, long merchantId,
        double merchantWallet, String bussinessName, String email) {
        Map<String, Object> map = new HashMap<>();

        try {
            semaphore.acquire();
            lock.lock();
            // String serviceName = "Aadhar Verify";
            String serviceName = "Aadhaar Verify";
            LOGGER.info("Inside saveAdharData");

            String amount = "1";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);

            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: {}" + serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;

            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

                return map;
            }

            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
            long ekycRequestId = ekycRequest.getRequestId();

            // Bank bank = new Bank();
            // ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges,
            // merchantWallet, merchantId,
            // merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
            // serviceName, trxnRefId,
            // walletTxnRefNo, serviceName);
            // LOGGER.info("Name of Thread ----------------------- {}", test.getName());
            // test.start();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);

            map = signzyService.verifyAadhaar(accountNumber);
            LOGGER.info("map : {}", map);

            String serviceProvider = ResponseMessage.SIGNZY;
            ServiceProviders spInfo = serviceprovidersrepository
                .findBySpName(Encryption.encString(serviceProvider));
            Long serviceProviderId = spInfo.getServiceProviderId();

            String aadhar = accountNumber;
            aadhar = "XX" + accountNumber.substring(accountNumber.length() - 4);
            LOGGER.info("aadhar : {}", aadhar);

            EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository.save(
                new EkycTransactionDetails(
                    ekycRequestId, merchantId, merchantServiceId, ResponseMessage.STATUS_SUCCESS,
                    ResponseMessage.STATUS_SUCCESS, commission, charges, serviceName, trxnDate,
                    merchantTrxnRefId, trxnRefId, aadhar, 1L, serviceProviderId, '0'
                )
            );
            LOGGER.info("ekycTransactionDetails : {}", ekycTransactionDetails);

            // EkycTransactionDetails elk =
            // ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
            // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR VERIFY AADHAAR EKYC TRXN DETAILS : {}",
            // elk);

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return map;
    }

    @Override
    public synchronized Map<String, Object> saveDataForBasicVerify(String accountNumber,
        long merchantId, double merchantWallet, String bussinessName, String email) {

        Map<String, Object> map = new HashMap<>();
        try {
            semaphore.acquire();
            lock.lock();
            // String serviceName = "Aadhar Basic Verify";
            String serviceName = "Aadhaar Basic Verify";

            LOGGER.info("  Inside Basic saveAdharData  ");

            String amount = "3.00";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);

            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: {}", serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;

            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

                return map;
            }

            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
            long ekycRequestId = ekycRequest.getRequestId();

            // Bank bank = new Bank();
            // ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges,
            // merchantWallet, merchantId,
            // merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
            // serviceName, trxnRefId,
            // walletTxnRefNo, serviceName);
            // LOGGER.info("Name of Thread ----------------------- {}", test.getName());
            // test.start();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);

            if (Validations.isValidAdharNumber(accountNumber)) {
                map = signzyService.basicVerifyAadhaar(accountNumber);

                String aadhar = accountNumber;
                aadhar = "XX" + accountNumber.substring(accountNumber.length() - 4);
                LOGGER.info("aadhar : {}", aadhar);
                String serviceProvider = ResponseMessage.SIGNZY;
                ServiceProviders spInfo = serviceprovidersrepository
                    .findBySpName(Encryption.encString(serviceProvider));
                Long serviceProviderId = spInfo.getServiceProviderId();

                EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
                    .save(
                        new EkycTransactionDetails(
                            ekycRequestId, merchantId, merchantServiceId,
                            ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS,
                            commission, charges, serviceName, trxnDate, merchantTrxnRefId,
                            trxnRefId, aadhar, 1L, serviceProviderId, '0'
                        )
                    );

                LOGGER.info("ekycTransactionDetails {}", ekycTransactionDetails);
                // ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
                // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR BASIC VERIFY AADHAAR EKYC TRXN
                // DETAILS : {}",
                // ekycTransactionDetails);

            } else {
                String serviceProvider = ResponseMessage.SIGNZY;
                ServiceProviders spInfo = serviceprovidersrepository
                    .findBySpName(Encryption.encString(serviceProvider));
                Long serviceProviderId = spInfo.getServiceProviderId();
                EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
                    .save(
                        new EkycTransactionDetails(
                            ekycRequestId, merchantId, merchantServiceId,
                            ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS,
                            commission, charges, serviceName, trxnDate, merchantTrxnRefId,
                            trxnRefId, merchantTrxnRefId, 1L, serviceProviderId, '0'
                        )
                    );

                LOGGER.info("ekycTransactionDetails {}", ekycTransactionDetails);
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.AADHAR_NUMBER_INVALID);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return map;
    }

    @Override
    public synchronized Map<String, Object> saveDataForGetURL(long merchantId,
        double merchantWallet, String url, String bussinessName, String email) {
        Map<String, Object> map = new HashMap<>();

        try {
            semaphore.acquire();
            lock.lock();

            String serviceName = "Get URL For EAdhar";
            LOGGER.info(" Inside Get URL For EAdhar ");
            String amount = "1";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);

            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");
            LOGGER.info("walletTxnRefNo: {}", walletTxnRefNo);

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: " + serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;

            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return map;
            }

            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, "EAadharGetURL", "EAadharGetURL", trxnDate));
            long ekycRequestId = ekycRequest.getRequestId();

            // Bank bank = new Bank();
            // ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges,
            // merchantWallet, merchantId,
            // merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
            // serviceName, trxnRefId,
            // walletTxnRefNo, serviceName);
            // LOGGER.info("Name of Thread ----------------------- {}", test.getName());
            // test.start();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);

            map = signzyService.createUrlForDigilocker(url);
            String serviceProvider = ResponseMessage.SIGNZY;
            ServiceProviders spInfo = serviceprovidersrepository
                .findBySpName(Encryption.encString(serviceProvider));
            Long serviceProviderId = spInfo.getServiceProviderId();

            EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository.save(
                new EkycTransactionDetails(
                    ekycRequestId, merchantId, merchantServiceId, ResponseMessage.STATUS_SUCCESS,
                    ResponseMessage.STATUS_SUCCESS, commission, charges, serviceName, trxnDate,
                    merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L, serviceProviderId, '0'
                )
            );
            LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

            // EkycTransactionDetails elk =
            // ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
            // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR CREATE URL EKYC TRXN DETAILS : {}",
            // elk);
        }

        catch (JSONException e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return map;
    }

    @Override
    public synchronized Map<String, Object> eAdhar(String requestId, long merchantId,
        double merchantWallet, String bussinessName, String email) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            semaphore.acquire();
            lock.lock();
            String serviceName = "EAdhar";
            LOGGER.info("  Inside Get URL For EAdhar  ");

            String amount = "1";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);

            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: " + serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;

            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                responseMap
                    .put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return responseMap;
            }
            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, requestId, requestId, trxnDate));
            long ekycRequestId = ekycRequest.getRequestId();

            // Bank bank = new Bank();
            // ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges,
            // merchantWallet, merchantId,
            // merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
            // serviceName, trxnRefId,
            // walletTxnRefNo, serviceName);
            // LOGGER.info("Name of Thread ----------------------- " + test.getName());
            // test.start();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);

            responseMap = signzyService.eAadhar(requestId);

            String aadhar = requestId;
            aadhar = "XX" + requestId.substring(requestId.length() - 4);
            LOGGER.info("aadhar : {}", aadhar);

            String serviceProvider = ResponseMessage.SIGNZY;
            ServiceProviders spInfo = serviceprovidersrepository
                .findBySpName(Encryption.encString(serviceProvider));
            Long serviceProviderId = spInfo.getServiceProviderId();

            EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository.save(
                new EkycTransactionDetails(
                    ekycRequestId, merchantId, merchantServiceId, ResponseMessage.STATUS_SUCCESS,
                    ResponseMessage.STATUS_SUCCESS, commission, charges, serviceName, trxnDate,
                    merchantTrxnRefId, trxnRefId, aadhar, 1L, serviceProviderId, '0'
                )
            );
            LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

            // EkycTransactionDetails elkEkycTrxnDetails = ekycTransactionDetailsSearchRepository
            // .save(ekycTransactionDetails);
            // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR E-AADHAAR EKYC TRXN DETAILS : {}",
            // elkEkycTrxnDetails);

        }

        catch (JSONException e) {
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } catch (Exception e) {
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap
                .put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return responseMap;
    }

    @Override
    public synchronized Map<String, Object> getDetailsEAdhar(String requestId, long merchantId,
        double merchantWallet, String bussinessName, String email) {

        Map<String, Object> map = new HashMap<>();

        try {
            semaphore.acquire();
            lock.lock();

            String serviceName = "Get Details EAdhar";
            LOGGER.info("  Inside Get URL For EAdhar  ");
            String amount = "1";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);
            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: " + serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;

            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return map;
            }

            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, requestId, requestId, trxnDate));
            long ekycRequestId = ekycRequest.getRequestId();

            // Bank bank = new Bank();
            // ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges,
            // merchantWallet, merchantId,
            // merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
            // serviceName, trxnRefId,
            // walletTxnRefNo, serviceName);
            // LOGGER.info("Name of Thread ----------------------- " + test.getName());
            // test.start();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);

            map = signzyService.getDetails(requestId);
            String serviceProvider = ResponseMessage.SIGNZY;
            ServiceProviders spInfo = serviceprovidersrepository
                .findBySpName(Encryption.encString(serviceProvider));
            Long serviceProviderId = spInfo.getServiceProviderId();

            EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository.save(
                new EkycTransactionDetails(
                    ekycRequestId, merchantId, merchantServiceId, ResponseMessage.STATUS_SUCCESS,
                    ResponseMessage.STATUS_SUCCESS, commission, charges, serviceName, trxnDate,
                    merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L, serviceProviderId, '0'
                )
            );
            LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

            // EkycTransactionDetails elk =
            // ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
            // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR GET DETAILS EKYC TRXN DETAILS : {}",
            // elk);
        }

        catch (JSONException e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return map;
    }

    @Override
    public synchronized Map<String, Object> saveDataForGenerateOtp(String aadhaarNumber,
        long merchantId, double merchantWallet, String serviceName, String bussinessName,
        String email) {

        Map<String, Object> responseMap = new HashMap<>();

        try {
            semaphore.acquire();
            lock.lock();

            LOGGER.info(" Inside saveDataForGenerateOtp ");
            String amount = "0";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);
            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: " + serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;

            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                responseMap
                    .put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return responseMap;
            }

            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, merchantTrxnRefId, merchantTrxnRefId, trxnDate));

            long ekycRequestId = ekycRequest.getRequestId();

            LOGGER.info("ekycRequestId--->" + ekycRequestId);

            // Bank bank = new Bank();
            // ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges,
            // merchantWallet, merchantId,
            // merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
            // serviceName, trxnRefId,
            // walletTxnRefNo, serviceName);
            // LOGGER.info("Name of Thread ----------------------- " + test.getName());
            // test.start();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);

            AadhaarServiceProvider aadhaarServiceProvider = ekycServiceResolver.getServiceProvider(
                AadhaarServiceProvider.SERVICE_ID, AadhaarServiceProvider.class
            );
            responseMap = aadhaarServiceProvider.generateOtp(aadhaarNumber, merchantId);
            LOGGER.info("APiResponse: {}", responseMap);

            String serviceProvider = ResponseMessage.DECENTRO;
            ServiceProviders spInfo = serviceprovidersrepository
                .findBySpName(Encryption.encString(serviceProvider));

            Long serviceProviderId = spInfo.getServiceProviderId();

            LOGGER.info("serviceProviderId--->" + serviceProviderId);

            EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository.save(
                new EkycTransactionDetails(
                    ekycRequestId, merchantId, merchantServiceId, ResponseMessage.STATUS_SUCCESS,
                    ResponseMessage.STATUS_SUCCESS, commission, charges, serviceName, trxnDate,
                    merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L, serviceProviderId, '0'
                )
            );
            LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

            // EkycTransactionDetails elk =
            // ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
            // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR GENERATE OTP EKYC TRXN DETAILS : {}",
            // elk);

        } catch (Exception ex) {
            LOGGER.error("Exception in saveDataForGenerateOtp: {}", ex.getLocalizedMessage(), ex);
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap
                .put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return responseMap;
    }

    @Override
    public synchronized Map<String, Object> saveDataForAadharValidate(ValidateOtp validateotp,
        long merchantId, double merchantWallet, String bussinessName, String email) {

        Map<String, Object> responseMap = new HashMap<>();

        try {
            semaphore.acquire();
            lock.lock();

            String serviceName = "Validate Aadhaar";
            LOGGER.info(" Inside validateOtp ");
            String amount = "3.00";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);
            validateotp.setReference_id(merchantTrxnRefId);
            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: " + serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;

            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                responseMap
                    .put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return responseMap;
            }
            String valOtp = validateotp.getReference_id();

            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, valOtp, valOtp, trxnDate));
            long ekycRequestId = ekycRequest.getRequestId();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);
            
            // Retain old behavior for all existing integrations
            if (StringUtils.isEmpty(validateotp.getAadhaarNumber())) {
                responseMap = signzyService.validateOtp(validateotp);
            } else {
                AadhaarServiceProvider aadhaarServiceProvider = ekycServiceResolver
                    .getServiceProvider(
                        AadhaarServiceProvider.SERVICE_ID, AadhaarServiceProvider.class
                    );
                responseMap = aadhaarServiceProvider.validateOtp(validateotp);
            }
            LOGGER.info("APiResponse: {}", responseMap);

            String serviceProvider = ResponseMessage.DECENTRO;
            ServiceProviders spInfo = serviceprovidersrepository
                .findBySpName(Encryption.encString(serviceProvider));
            Long serviceProviderId = spInfo.getServiceProviderId();

            EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository.save(
                new EkycTransactionDetails(
                    ekycRequestId, merchantId, merchantServiceId, ResponseMessage.STATUS_SUCCESS,
                    ResponseMessage.STATUS_SUCCESS, commission, charges, serviceName, trxnDate,
                    merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L, serviceProviderId, '0'
                )
            );
            LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

            // EkycTransactionDetails elk =
            // ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
            // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR VALIDATE AADHAAR OTP EKYC TRXN DETAILS
            // : {}", elk);

        } catch (Exception e) {
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap
                .put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

        } finally {
            lock.unlock();
            semaphore.release();
        }
        return responseMap;
    }

    @Override
    public Map<String, Object> saveDataForValidate(Validate validate, long merchantId,
        double merchantWallet, String bussinessName, String email) {
        Map<String, Object> map = new HashMap<>();

        try {
            semaphore.acquire();
            lock.lock();

            map = ValidateUtils.validate(validate.getDocument_type(), validate.getId_number());

            if (!map.isEmpty()) {
                return map;
            }

            String serviceName = "Single KYC API";

            LOGGER.info("Inside validate");

            String amount = "3.00";
            double amtInDouble = Double.parseDouble(amount);

            String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
                + RandomNumberGenrator.randomNumberGenerate(16);
            validate.setReference_id(merchantTrxnRefId);
            String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
            String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

            ServiceInfo serviceInfo = serviceInfoRepository
                .findServiceByName(Encryption.encString(serviceName));
            Long serviceId = serviceInfo.getServiceId();

            MerchantService merchantsService = merchantServiceRepository
                .findByMerchantIdAndServiceId(merchantId, serviceId);
            Long merchantServiceId = merchantsService.getMerchantServiceId();
            String serviceType = merchantsService.getServiceType();
            LOGGER.info("serviceType: " + serviceType);
            double charges = 0.0;
            double commission = 0.0;

            switch (serviceType) {

                case "Charge":
                    charges = chargeService
                        .getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
                    LOGGER.info("charges: " + charges);
                    break;

                default:
                    charges = amtInDouble;
                    break;
            }

            String resWallet = walletNotification
                .checkWalletBalance(merchantWallet, bussinessName, email);

            if (merchantWallet < charges) {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return map;
            }
            String valOtp = validate.getReference_id();

            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, valOtp, valOtp, trxnDate));
            long ekycRequestId = ekycRequest.getRequestId();

            // Bank bank = new Bank();
            // ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges,
            // merchantWallet, merchantId,
            // merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
            // serviceName, trxnRefId,
            // walletTxnRefNo, serviceName);
            // LOGGER.info("Name of Thread ----------------------- " + test.getName());
            // test.start();

            WalletRequest debitRequest = new WalletRequest(
                merchantId, charges, "Debit", serviceName, serviceName, ekycRequestId, trxnRefId,
                merchantTrxnRefId, walletTxnRefNo
            );
            walletService.enqueueTransaction(debitRequest);

            map = signzyService.validate(validate);
            String serviceProvider = ResponseMessage.DECENTRO;
            ServiceProviders spInfo = serviceprovidersrepository
                .findBySpName(Encryption.encString(serviceProvider));
            Long serviceProviderId = spInfo.getServiceProviderId();

            EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository.save(
                new EkycTransactionDetails(
                    ekycRequestId, merchantId, merchantServiceId, ResponseMessage.STATUS_SUCCESS,
                    ResponseMessage.STATUS_SUCCESS, commission, charges, serviceName, trxnDate,
                    merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L, serviceProviderId, '0'
                )
            );
            LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

            // EkycTransactionDetails elk =
            // ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
            // LOGGER.info("ELASTICSEARCH DATA INSERTION FOR VALIDATE KYC EKYC TRXN DETAILS : {}",
            // elk);

            return map;
        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return map;
    }

    public String createUrlForDigilockerNew(String rId) throws Exception {

        String finalResponse = null;
        JSONObject jsonObj = new JSONObject();

        try {
            String responseLogin = new GSTServiceImpl()
                .login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            LOGGER.info("Login Response " + responseLogin);
            JSONObject jsonObject = new JSONObject(responseLogin);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");
            String redirectUrl = "https://rekyc.fidypay.com/redirect/" + rId + "";
            String requestStr = " {\r\n" + " \"task\": \"url\",\r\n" + " \"essentials\": {\r\n"
                + " \"signup\": true,\r\n" + " \"redirectUrl\": \"" + redirectUrl + "\"\r\n"
                + " }\r\n" + " }";

            LOGGER.info(" requestStr : " + requestStr);

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder()
                .url("https://signzy.tech/api/v2/patrons/" + userId + "/digilockers").post(body)
                .addHeader("Accept", "application/json").addHeader("Authorization", id).build();

            Response response = client.newCall(request).execute();
            finalResponse = response.body().string();

            LOGGER.info(" Response : " + finalResponse + " Request Id " + rId);
            return finalResponse;
        } catch (JSONException e) {
            jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
            jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            finalResponse = jsonObj.toString();
        } catch (Exception e) {
            jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            jsonObj
                .put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            finalResponse = jsonObj.toString();
        }
        return finalResponse;
    }

    // ELK Migrate the data from db to Elasticsearch Node/index
    // @Override
    // public Iterable<EkycTransactionDetails> migrateDataByDateRange(ElasticMigrationRequest
    // elasticMigrationRequest) {
    // LOGGER.debug("Request to data migration from db to elasticsearch");
    // List<EkycTransactionDetails> list = new ArrayList<>();
    //
    // Pageable paging = PageRequest.of(elasticMigrationRequest.getPageNo(),
    // elasticMigrationRequest.getPageSize(),
    // Sort.by("TRANSACTION_DATE").descending());
    //
    // String startDate = elasticMigrationRequest.getStartDate();
    // String endDate = elasticMigrationRequest.getEndDate();
    // String startTime = elasticMigrationRequest.getStartTime();
    // String endTime = elasticMigrationRequest.getEndTime();
    //
    // if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") ||
    // endTime.equals("null")
    // || endTime.equals("") || endTime.equals("0")) {
    // startDate = startDate + " 00.00.00.0";
    // endDate = endDate + " 23.59.59.9";
    // } else {
    // startDate = startDate + " " + startTime + ".00.0";
    // endDate = endDate + " " + endTime + ".00.0";
    // }
    //
    // List<EkycTransactionDetails> findAll =
    // ekycTransactionDetailsRepository.findByDateBetweenPKR(startDate,
    // endDate);
    // LOGGER.info("Total Records: " + findAll.stream().count());
    //
    // for (EkycTransactionDetails ekycTransactionDetails : findAll) {
    // EkycTransactionDetails ekyc = new EkycTransactionDetails();
    //
    // ekyc.setEkycTransactionId(ekycTransactionDetails.getEkycTransactionId());
    // ekyc.setRequestId(ekycTransactionDetails.getRequestId());
    // ekyc.setMerchantId(ekycTransactionDetails.getMerchantId());
    // ekyc.setMerchantServiceId(ekycTransactionDetails.getMerchantServiceId());
    // ekyc.setStatus(ekycTransactionDetails.getStatus());
    // ekyc.setApiStatus(ekycTransactionDetails.getApiStatus());
    // ekyc.setMerchantServiceCommision(ekycTransactionDetails.getMerchantServiceCommision());
    // ekyc.setMerchantServiceCharge(ekycTransactionDetails.getMerchantServiceCharge());
    // ekyc.setEkycServicename(ekycTransactionDetails.getEkycServicename());
    // ekyc.setCreationDate(ekycTransactionDetails.getCreationDate());
    // ekyc.setMerchantTransactionRefId(ekycTransactionDetails.getMerchantTransactionRefId());
    // ekyc.setTrxnRefId(ekycTransactionDetails.getTrxnRefId());
    // ekyc.seteKycId(ekycTransactionDetails.geteKycId());
    // ekyc.setTransactionStatusId(ekycTransactionDetails.getTransactionStatusId());
    // ekyc.setServiceProviderId(ekycTransactionDetails.getServiceProviderId());
    // ekyc.setIsReconcile(ekycTransactionDetails.getIsReconcile());
    // list.add(ekyc);
    // }
    // return ekycTransactionDetailsSearchRepository.saveAll(list);
    // }

    // // fetch all data from Elasticsearch index
    // @Override
    // public Iterable<EkycTransactionDetails> findAllEkycTxnFromElasticsearch() {
    // return ekycTransactionDetailsSearchRepository.findAll();
    // }
    //
    // // Reindexing Functionality
    // @Override
    // public void doIndex(int pageNo, int pageSize, String fromDate, String toDate) {
    // LOGGER.debug("Request to do elastic index on EkycTransactionDetails");
    // List<EkycTransactionDetails> data =
    // ekycTransactionDetailsRepository.findByDateRangeSortById(fromDate, toDate,
    // PageRequest.of(pageNo, pageSize));
    // if (!data.isEmpty()) {
    // ekycTransactionDetailsSearchRepository.saveAll(data);
    // }
    // }
}
