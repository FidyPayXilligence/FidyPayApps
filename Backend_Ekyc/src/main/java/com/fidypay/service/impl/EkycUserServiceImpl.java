package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fidypay.dto.BulkEkycUserPayload;
import com.fidypay.dto.EkycUserRequestsResponse;
import com.fidypay.dto.EkycUserTempPayload;
import com.fidypay.encryption.Encryption;
import com.fidypay.encryption.EncryptionDataRequest;
import com.fidypay.entity.EkycUserTable;
import com.fidypay.entity.EkycUserTempDetails;
import com.fidypay.entity.EkycWorkflow;
import com.fidypay.entity.EkycWorkflowService;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.OTPVerification;
import com.fidypay.repo.EkycUserRepository;
import com.fidypay.repo.EkycUserTempDetailsRepository;
import com.fidypay.repo.EkycWorkflowRepository;
import com.fidypay.repo.EkycWorkflowServiceRepository;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.OTPVerificationRepository;
import com.fidypay.request.BulkEkycUsersRequest;
import com.fidypay.request.EKycUpdateRequest;
import com.fidypay.request.EKycWorkFlowTempRequest;
import com.fidypay.request.EkycRequest;
import com.fidypay.request.EkycRequests;
import com.fidypay.request.EkycUserFilterRequest;
import com.fidypay.request.EkycUserRequest;
import com.fidypay.service.EkycUserService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.constants.URLGenerater;
import com.fidypay.utils.constants.ValidateBulkEKyc;
import com.fidypay.utils.ex.BasicAuth;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.EMailService;
import com.fidypay.utils.ex.EmailAPIImpl;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.SMSAPIImpl;

@Service
public class EkycUserServiceImpl extends JdbcDaoSupport implements EkycUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EkycUserServiceImpl.class);

    @Autowired
    private EkycUserRepository EkycUserRepository;

    @Autowired
    private EkycWorkflowRepository ekycWorkflowRepository;

    @Autowired
    private OTPVerificationRepository otpVerificationRepository;

    @Autowired
    private EmailAPIImpl emailAPIImpl;

    @Autowired
    private EkycWorkflowServiceRepository ekycWorkflowServiceRepository;

    @Autowired
    private MerchantsRepository merchantsRepository;

    @Autowired
    private MerchantInfoRepository merchantInfoRepository;

    @Autowired
    private EkycUserTempDetailsRepository ekycUserTempDetailsRepository;

    @Autowired
    private EMailService apiImpl;

    @Autowired
    DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }


    @Override
    public Map<String, Object> saveEkycUserInfo(EkycRequest eKycRequest, long merchantId, String businessName)
            throws Exception {

        LOGGER.info("Inside saveEkycUserInfo");

        Map<String, Object> map = new HashMap<>();

        String userName = eKycRequest.getUserName();
        String userEmail = eKycRequest.getUserEmail();
        String mobile = eKycRequest.getUsermobile();
        long workflowId = Long.valueOf(eKycRequest.getEkycWorkflowId());
        String isNotification = eKycRequest.getIsNotification();
        char serviceCount = eKycRequest.getServiceCount().charAt(0);

        Timestamp timeStamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

        UUID randomUUID = UUID.randomUUID();
        String userUniqueId = merchantId + "WFL"
                + randomUUID.toString().replaceAll("-", "").substring(0, 15).toUpperCase();

        if (EkycUserRepository.existsByEkycWorkflowIdAndUserMobileAndIsDeleted(workflowId, Encryption.encString(mobile),
                '0')) {

            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.USER_ALREADY_REGISTERED);
            return map;

        }

        EkycWorkflow ekycWorkflow = ekycWorkflowRepository.findByWorkflowId(workflowId);

        if (ekycWorkflow != null) {
            if (ekycWorkflow.getIsDeleted() == '1') {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.WORKFLOW_DELETED_MESSAGE);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return map;
            } else {
                EkycUserTable ekycUserTable = new EkycUserTable();
                ekycUserTable.setCreationDate(timeStamp);
                ekycUserTable.setIsDeleted('0');
                ekycUserTable.setIsVerified('0');
                ekycUserTable.setMerchantId(merchantId);
                ekycUserTable.setEkycWorkflowId(ekycWorkflow.getEkycWorkflowId());
                ekycUserTable.setUserUniqueId(userUniqueId);
                ekycUserTable.setUserName(Encryption.encString(userName));
                ekycUserTable.setUserEmail(Encryption.encString(userEmail));
                ekycUserTable.setUserMobile(Encryption.encString(mobile));
                ekycUserTable.setWorkflowName(ekycWorkflow.getWorkflowName());
                ekycUserTable.setServicesJson(ekycWorkflow.getServices());
                ekycUserTable.setActivationDate(timeStamp);
                ekycUserTable.setServiceCount(serviceCount);

                EkycUserRepository.save(ekycUserTable);

                String link = ResponseMessage.LINK_WORKFLOW + ekycUserTable.getUserUniqueId();
                // Email
                if (isNotification.equalsIgnoreCase("2")) {
                    String sendEmail = emailAPIImpl.sendEmailEKycWorkFlow(userEmail, businessName, link, userName);
                }

                // Message
                SMSAPIImpl impl = new SMSAPIImpl();
                URLGenerater urlGenerater = new URLGenerater();
                String url = urlGenerater.generateShortUrl(link);
                impl.workFlowLink(mobile, businessName, url);

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SUCCESSFULLY_REGISTERED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            }
        } else {
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    @Override
    public Map<String, Object> resendNotification(Long eKycUserId, long merchantId, String businessName) {
        LOGGER.info("Inside resendNotification");

        Map<String, Object> map = new HashMap<>();
        try {
            String userEmail = null;
            String userName = null;
            String mobile = null;

            EkycUserTable ekycUserTable = EkycUserRepository.findById(eKycUserId).get();

            if (ekycUserTable != null) {

                userEmail = Encryption.decString(ekycUserTable.getUserEmail());
                mobile = Encryption.decString(ekycUserTable.getUserMobile());
                userName = Encryption.decString(ekycUserTable.getUserName());
                String link = ResponseMessage.LINK_WORKFLOW + ekycUserTable.getUserUniqueId();
                // Email
                if (userEmail != null && !userEmail.equals("NA")) {
                    LOGGER.info("EMAIL " + userEmail);
                    String sendEmail = emailAPIImpl.sendEmailEKycWorkFlow(userEmail, businessName, link, userName);
                }
                LOGGER.info("mobile " + mobile);
                // Message
                SMSAPIImpl impl = new SMSAPIImpl();
                URLGenerater urlGenerater = new URLGenerater();
                String url = urlGenerater.generateShortUrl(link);
                impl.workFlowLink(mobile, businessName, url);

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, "Notification sent successfully");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, "User does not exist");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    @Override
    public Map<String, Object> findByEkycUserId(long ekycUserId, long merchantId) throws Exception {

        LOGGER.info("Inside findByEkycUserId");
        String ekycWorkflowName = "NA";
        Map<String, Object> map = new HashMap<>();

        EkycUserTable EkycUserTable = EkycUserRepository.findByEkycUserId(ekycUserId, merchantId);

        if (EkycUserTable != null) {

            EkycUserRequestsResponse response = new EkycUserRequestsResponse();

            String date = DateAndTime.formatDate(EkycUserTable.getCreationDate().toString());

            LOGGER.info("EkycWorkflowId: " + EkycUserTable.getEkycWorkflowId());
            EkycWorkflow ekycWorkflow = ekycWorkflowRepository.findByWorkflowId(EkycUserTable.getEkycWorkflowId());

            if (ekycWorkflow != null) {
                ekycWorkflowName = ekycWorkflow.getWorkflowName();
            }

            LOGGER.info("ekycWorkflowName: " + ekycWorkflowName);

            response.setCreationDate(date);
            response.setIsVerified(EkycUserTable.getIsVerified());
            response.setMerchantId(EkycUserTable.getMerchantId());
            response.setMobile(Encryption.decString(EkycUserTable.getUserMobile()));
            response.setUserEmail(Encryption.decString(EkycUserTable.getUserEmail()));
            response.setUserName(Encryption.decString(EkycUserTable.getUserName()));
            response.setEkycUserId(EkycUserTable.getEkycUserId());
            response.setEkycWorkflowId(EkycUserTable.getEkycWorkflowId());
            response.setUserUniqueId(EkycUserTable.getUserUniqueId());
            response.setWorkflowName(ekycWorkflowName);
            response.setServicesJson(EkycUserTable.getServicesJson());
            response.setServiceCount(String.valueOf(EkycUserTable.getServiceCount()));

            map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
            map.put(ResponseMessage.DATA, response);

            return map;
        }

        map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

        return map;
    }

    @Override
    public Map<String, Object> findAllUser(long merchantId, EkycUserRequest request) {

        LOGGER.info("Inside findAllUser");

        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Pageable pageble = PageRequest.of(request.getPageNo(), request.getPageSize());
            List<EkycUserTable> list = new ArrayList<EkycUserTable>();
            List<EkycUserRequestsResponse> listEkycUserResponse = new ArrayList<EkycUserRequestsResponse>();
            Page<EkycUserTable> page = null;

            if (pageble != null) {
                page = EkycUserRepository.findAllByPage(merchantId, pageble);
            }

            list = page.getContent();
            if (!list.isEmpty()) {

                list.forEach(response -> {
                    String date;
                    try {
                        date = DateAndTime.formatDate(response.getCreationDate().toString());

                        EkycUserRequestsResponse setResponse = new EkycUserRequestsResponse();
                        EkycWorkflow ekycWorkflow = ekycWorkflowRepository
                                .findByWorkflowId(response.getEkycWorkflowId());
                        setResponse.setCreationDate(date);
                        setResponse.setWorkflowName(ekycWorkflow.getWorkflowName());
                        setResponse.setIsVerified(response.getIsVerified());
                        setResponse.setMerchantId(response.getMerchantId());
                        setResponse.setMobile(Encryption.decString(response.getUserMobile()));
                        setResponse.setUserEmail(Encryption.decString(response.getUserEmail()));
                        setResponse.setUserName(Encryption.decString(response.getUserName()));
                        setResponse.setEkycUserId(response.getEkycUserId());
                        setResponse.setEkycWorkflowId(response.getEkycWorkflowId());
                        setResponse.setUserUniqueId(response.getUserUniqueId());
                        setResponse.setServicesJson(response.getServicesJson());
                        setResponse.setServiceCount(String.valueOf(response.getServiceCount()));

                        listEkycUserResponse.add(setResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
                map.put(ResponseMessage.DATA, listEkycUserResponse);
                map.put("currentPage", page.getNumber());
                map.put("totalItems", page.getTotalElements());
                map.put("totalPages", page.getTotalPages());
            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

        }
        return map;

    }

    @Override
    public Map<String, Object> IsDeletByEkycUserId(long ekycUserId, long merchantId) throws Exception {

        LOGGER.info("Inside IsDeletByEkycUserId");

        Map<String, Object> map = new HashMap<>();

        EkycUserTable EkycUserTable = EkycUserRepository.findByEkycUserId(ekycUserId, merchantId);
        if (EkycUserTable != null) {

            if (EkycUserTable.getIsDeleted() == '0') {

                EkycUserTable.setIsDeleted('1');
                EkycUserRepository.save(EkycUserTable);
            }

            map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DELETE);
            return map;

        } else {
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
        }
        return map;

    }

    @Override
    public Map<String, Object> updateIsVerified(long ekycUserId, long merchantId, String isVerified) {
        Map<String, Object> map = new HashMap<>();
        try {
            LOGGER.info("Inside updateIsVerified");

            Timestamp timeStamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            if (!isVerified.equals("0") && !isVerified.equals("1") && !isVerified.equals("2")) {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, "Please pass 0,1 and 2 on isVerified parameter");
                return map;
            }

            EkycUserTable ekycUserTable = EkycUserRepository.findByEkycUserId(ekycUserId, merchantId);

            if (ekycUserTable != null) {
                List<EkycWorkflowService> list = ekycWorkflowServiceRepository.findByUserId(ekycUserId);

                for (EkycWorkflowService ekycWorkflowService : list) {

                    String fileName = ekycWorkflowService.getDocumentId();
                    char isVer = ekycWorkflowService.getIsVerified();

                    if (isVer == '0') {
                        map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                        map.put(ResponseMessage.DESCRIPTION, fileName + " not verified");
                        return map;
                    }
                }

                if (ekycUserTable.getIsVerified() == '0' || ekycUserTable.getIsVerified() == '2') {
                    char ch = isVerified.charAt(0);

                    ekycUserTable.setIsVerified(ch);
                    ekycUserTable.setActivationDate(timeStamp);

                    EkycUserRepository.save(ekycUserTable);

                    map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                    map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                    map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATED);
                } else {
                    map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                    map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                    map.put(ResponseMessage.DESCRIPTION, "Document already verified");
                }

            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
            }
        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
        }
        return map;
    }


    @Override
    public Map<String, Object> findAllUserByRequest(long merchantId, EkycRequests ekycRequests) {

        LOGGER.info("Inside findAllUserByRequest");

        Map<String, Object> map = new HashMap<>();
        try {
            Pageable pageble = PageRequest.of(ekycRequests.getPageNo(), ekycRequests.getPageSize());
            String name = ekycRequests.getUserName();
            String mobile = ekycRequests.getUserMobile();
            String email = ekycRequests.getUserEmail();
            String workflowName = ekycRequests.getWorkflowName();

            List<EkycUserTable> list = new ArrayList<>();
            Page<EkycUserTable> page = null;
            List<EkycUserRequestsResponse> ekycUserRequestsResponse = new ArrayList<>();

            if ((!name.equals("") || !name.equals("0") || !name.equals("NA"))
                    && (email.equals("") || email.equals("0") || email.equals("NA"))
                    && (mobile.equals("") || mobile.equals("0") || mobile.equals("NA"))
                    && (workflowName.equals("") || workflowName.equals("0") || workflowName.equals("NA"))) {
                LOGGER.info("name " + name);
                page = EkycUserRepository.findByName(Encryption.encString(name), merchantId, pageble);
            } else if ((!mobile.equals("") || !mobile.equals("0") || !mobile.equals("NA"))
                    && (name.equals("") || name.equals("0") || name.equals("NA"))
                    && (email.equals("") || email.equals("0") || email.equals("NA"))
                    && (workflowName.equals("") || workflowName.equals("0") || workflowName.equals("NA"))) {

                LOGGER.info("mobile");
                page = EkycUserRepository.findByUserMobile(Encryption.encString(mobile), merchantId, pageble);

            } else if ((!email.equals("") || !email.equals("0") || !email.equals("NA"))
                    && (name.equals("") || name.equals("0") || name.equals("NA"))
                    && (mobile.equals("") || mobile.equals("0") || mobile.equals("NA"))
                    && (workflowName.equals("") || workflowName.equals("0") || workflowName.equals("NA"))) {

                LOGGER.info("email");
                page = EkycUserRepository.findByEmail(Encryption.encString(email), merchantId, pageble);

            } else if ((!workflowName.equals("") || !workflowName.equals("0") || !workflowName.equals("NA"))
                    && (name.equals("") || name.equals("0") || name.equals("NA"))
                    && (mobile.equals("") || mobile.equals("0") || mobile.equals("NA"))
                    && (email.equals("") || email.equals("0") || email.equals("NA"))) {

                LOGGER.info("workflowName");
                page = EkycUserRepository.findByWorkflowName(workflowName, merchantId, pageble);

            }

            list = page.getContent();
            if (!list.isEmpty()) {

                list.forEach(response -> {

                    try {
                        String date = DateAndTime.formatDate(response.getCreationDate().toString());
                        ;
                        EkycUserRequestsResponse setResponse = new EkycUserRequestsResponse();

                        EkycWorkflow ekycWorkflow = ekycWorkflowRepository
                                .findByWorkflowId(response.getEkycWorkflowId());
                        setResponse.setWorkflowName(ekycWorkflow.getWorkflowName());
                        setResponse.setCreationDate(date);
                        setResponse.setIsVerified(response.getIsVerified());
                        setResponse.setMerchantId(response.getMerchantId());
                        setResponse.setMobile(Encryption.decString(response.getUserMobile()));
                        setResponse.setUserEmail(Encryption.decString(response.getUserEmail()));
                        setResponse.setUserName(Encryption.decString(response.getUserName()));
                        setResponse.setEkycUserId(response.getEkycUserId());
                        setResponse.setEkycWorkflowId(response.getEkycWorkflowId());
                        setResponse.setUserUniqueId(response.getUserUniqueId());
                        setResponse.setServicesJson(response.getServicesJson());
                        setResponse.setServiceCount(String.valueOf(response.getServiceCount()));

                        ekycUserRequestsResponse.add(setResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
                map.put(ResponseMessage.DATA, ekycUserRequestsResponse);
                map.put("currentPage", page.getNumber());
                map.put("totalItems", page.getTotalElements());
                map.put("totalPages", page.getTotalPages());
            } else {

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
            }
        } catch (NullPointerException e) {
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } catch (Exception e) {
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;

    }

    @Override
    public Map<String, Object> findByEkycWorkflowId(long ekycWorkflowId, long merchantId) throws Exception {

        LOGGER.info("Inside findByEkycWorkflowId");

        Map<String, Object> response = new HashMap<String, Object>();

        List<EkycUserTable> userDetailsList = EkycUserRepository.findByEkycWorkflowId(ekycWorkflowId, merchantId);

        List<EkycUserRequestsResponse> userResponseList = new ArrayList<EkycUserRequestsResponse>();

        if (!userDetailsList.isEmpty()) {

            userDetailsList.forEach(responseData -> {

                try {

                    String date = DateAndTime.formatDate(responseData.getCreationDate().toString());
                    EkycUserRequestsResponse userResponse = new EkycUserRequestsResponse();

                    EkycWorkflow ekycWorkflow = ekycWorkflowRepository
                            .findByWorkflowId(responseData.getEkycWorkflowId());

                    userResponse.setWorkflowName(ekycWorkflow.getWorkflowName());
                    userResponse.setCreationDate(date);
                    userResponse.setIsVerified(responseData.getIsVerified());
                    userResponse.setMerchantId(responseData.getMerchantId());
                    userResponse.setMobile(Encryption.decString(responseData.getUserMobile()));
                    userResponse.setUserEmail(Encryption.decString(responseData.getUserEmail()));
                    userResponse.setUserName(Encryption.decString(responseData.getUserName()));
                    userResponse.setEkycUserId(responseData.getEkycUserId());
                    userResponse.setEkycWorkflowId(responseData.getEkycWorkflowId());
                    userResponse.setUserUniqueId(responseData.getUserUniqueId());
                    userResponse.setServicesJson(responseData.getServicesJson());

                    userResponseList.add(userResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
            response.put(ResponseMessage.DATA, userResponseList);

            return response;
        }

        response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
        response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
        return response;
    }

    @Override
    public Map<String, Object> sendOTPPhone(String userMobile, String userUniqueId) throws Exception {

        LOGGER.info("Inside sendOTPPhone");

        Map<String, Object> map = new HashMap<>();

        EkycUserTable optional = EkycUserRepository.findByUserUniqueId(userUniqueId);

        if (optional != null) {

            if (optional.getIsVerified() == '1') {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, "Document already uploaded");
                return map;
            }

            if (!EkycUserRepository.existsByUserUniqueIdAndUserMobile(userUniqueId, Encryption.encString(userMobile))) {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_NOT_EXIST);
                return map;
            }

            long ekycWorkflowId = optional.getEkycWorkflowId();

            EkycWorkflow workflowdetails = ekycWorkflowRepository.findByWorkflowId(ekycWorkflowId);

            if (workflowdetails.getIsDeleted() == '1') {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.WORKFLOW_DELETED_MESSAGE);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                return map;
            }

            UUID randomUUID = UUID.randomUUID();
            String otpToken = "WFLOTP" + randomUUID.toString().replaceAll("-", "").substring(0, 18).toUpperCase();

            String otp = RandomNumberGenrator.generateWalletPin();
            Merchants merchants = merchantsRepository.findByMerchantId(workflowdetails.getMerchantId());
            String merchantBusinessName = Encryption.decString(merchants.getMerchantBusinessName());
            Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
            otpToken = otpToken + otp;
            String workflowToken = workflowdetails.getWorkflowUniqueId();
            String userWorkflowToken = optional.getUserUniqueId();
            long merchantId = optional.getMerchantId();
            SMSAPIImpl impl = new SMSAPIImpl();
            impl.registrationOTP(userMobile, merchantBusinessName, otp);

            OTPVerification otpVerification = otpVerificationRepository.save(new OTPVerification(merchantId, trxnDate,
                    otp, otpToken, "NA", Encryption.encString(workflowToken), userWorkflowToken));

            map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            map.put(ResponseMessage.DESCRIPTION, "OTP Generated Successfully");
            map.put("otpToken", otpVerification.getOtpRefId());
            map.put("workflowToken", workflowToken);
            map.put("userWorkflowToken", userWorkflowToken);
            map.put("mId", merchantId);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            return map;
        }

        map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        map.put(ResponseMessage.DESCRIPTION, ResponseMessage.USER_UNIQUE_ID_NOT_EXIST);
        return map;
    }

    @Override
    public Map<String, Object> otpVerification(String otp, String otpToken, long merchantId) throws Exception {

        LOGGER.info("Inside otpVerification");

        Map<String, Object> map = new HashMap<>();

        Optional<OTPVerification> otpVerification = otpVerificationRepository.findOtpANDOtpRefId(merchantId, otp,
                otpToken);

        if (!otpVerification.isPresent()) {

            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_FAILED);
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            return map;
        }

        Timestamp currentTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

        long transactionTimeValidate = DateAndTime.compareTwoTimeStamps(currentTime,
                otpVerification.get().getCreationDate());

        if (transactionTimeValidate >= 180) {
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_EXPIRED);
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            return map;
        }

        String workflowToken = Encryption.decString(otpVerification.get().getMerchantBankIfsc());
        String userWorkflowToken = Encryption.decString(otpVerification.get().getMerchantBankAccountNumber());
        long merchantId1 = otpVerification.get().getMerchantId();

        EkycWorkflow ekycWorkflow = ekycWorkflowRepository.findByWorkflowUniqueId(workflowToken);

        String services = ekycWorkflow.getServices().toString();

        if (ekycWorkflow.getIsDeleted() == '1') {
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.WORKFLOW_DELETED_MESSAGE);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            return map;
        }

        EkycUserTable ekycUser = EkycUserRepository.findByUserUniqueId(userWorkflowToken);
        long userId = ekycUser.getEkycUserId();
        List<String> ekycUserServiceName = ekycWorkflowServiceRepository.findByUserIdService(userId);


        MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(merchantId1);

        String cId = Encryption.decString(merchantInfo.getClientId());
        String csId = Encryption.decString(merchantInfo.getClientSecret());
        String userName = Encryption.decString(merchantInfo.getUsername());
        String password = Encryption.decString(merchantInfo.getPassword());

        String basicAuth = BasicAuth.createEncodedText(userName, password);

        String logo = merchantInfo.getImageUrl();

        if (!logo.equalsIgnoreCase("NA")) {
            logo = ResponseMessage.LOGO_URL + logo;
        }


        JSONObject object = new JSONObject();
        object.put("Client-Id", cId);
        object.put("Client-Secret", csId);
        object.put("Authorization", basicAuth);


        //String ekyc=Encryption.encString(object.toString());
        String ekyc = EncryptionDataRequest.encrypt(object.toString());

        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
        map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
        map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VALID_OTP_MERTRXNREFID);
        map.put("workflowToken", workflowToken);
        map.put("userWorkflowToken", userWorkflowToken);
        map.put("mId", merchantId1);
        map.put("services", services);
        map.put("ekycUserServiceName", ekycUserServiceName);
        map.put("ekycUserId", ekycUser.getEkycUserId());
        map.put("ekyc", ekyc);
        map.put("logo", logo);

        map.put("days", ekycWorkflow.getDays());
        map.put("description", ekycWorkflow.getDescription());
        map.put("workflowName", ekycWorkflow.getWorkflowName());
        map.put("workflowUniqueId", ekycWorkflow.getWorkflowUniqueId());

        return map;
    }

    @Override
    public Map<String, Object> updateServicesJson(EKycUpdateRequest ekycUpdateRequest, long merchantId) {
        Map<String, Object> map = new HashMap<>();
        try {
            EkycUserTable ekycUser = EkycUserRepository.findByEkycUserId(ekycUpdateRequest.getEkycUserId(), merchantId);

            if (ekycUser != null) {

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

                String serviceList = objectMapper.writeValueAsString(ekycUpdateRequest.getServicesJson());

                ekycUser.setServicesJson(serviceList);
                ekycUser = EkycUserRepository.save(ekycUser);

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, "Services Json updated successfully");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, "Ekyc usserId does not exist");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    @Override
    public Map<String, Object> ekycUserFilterDetails(@Valid EkycUserFilterRequest ekycUserFilterRequest,
                                                     long merchantId) {
        Map<String, Object> map = new HashMap<>();
        try {

            String query = "SELECT EKYC_USER_ID,CREATION_DATE,IS_DELETED,IS_VERIFIED,MERCHANT_ID,USER_MOBILE,USER_EMAIL,USER_NAME,EKYC_WORKFLOW_ID,USER_UNIQUE_ID,WORKFLOW_NAME,ACTIVATION_DATE,SERVICES_JSON,SERVICE_COUNT FROM EKYC_USER";
            List<String> conditions = new ArrayList<>();

            if (ekycUserFilterRequest.getUserEmail() != null && !ekycUserFilterRequest.getUserEmail().equals("")
                    && !ekycUserFilterRequest.getUserEmail().isEmpty()) {
                conditions.add("USER_EMAIL = '" + Encryption.encString(ekycUserFilterRequest.getUserEmail()) + "'");

            }
            if (ekycUserFilterRequest.getUserMobile() != null && !ekycUserFilterRequest.getUserMobile().equals("")
                    && !ekycUserFilterRequest.getUserMobile().isEmpty()) {
                conditions.add("USER_MOBILE = '" + Encryption.encString(ekycUserFilterRequest.getUserMobile()) + "'");

            }

            if (ekycUserFilterRequest.getEkycWorkflowId() != null && ekycUserFilterRequest.getEkycWorkflowId() != 0) {
                conditions.add("EKYC_WORKFLOW_ID  ='" + ekycUserFilterRequest.getEkycWorkflowId() + "'");

            }


            if (ekycUserFilterRequest.getUserName() != null && !ekycUserFilterRequest.getUserName().equals("")
                    && !ekycUserFilterRequest.getUserName().isEmpty()) {
                conditions.add("USER_NAME = '" + Encryption.encString(ekycUserFilterRequest.getUserName()) + "'");

                LOGGER.info("USER_NAME ='" + Encryption.encString(ekycUserFilterRequest.getUserName()) + "'");
            }


            query = query + " where " + conditions.stream().collect(Collectors.joining(" and "));
            LOGGER.info(query);
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query);
            List<EkycUserRequestsResponse> result = new ArrayList<EkycUserRequestsResponse>();


            if (rows.stream().count() > 0) {

                for (Map<String, Object> row : rows) {
                    EkycUserRequestsResponse ekycUserRequestsResponse = new EkycUserRequestsResponse();


                    String isVerified = (String) row.get("IS_VERIFIED");

                    String serviceCount = (String) row.get("SERVICE_COUNT");


                    String ewId = (String) row.get("EKYC_WORKFLOW_ID");

                    ekycUserRequestsResponse.setEkycUserId((Long) row.get("EKYC_USER_ID"));
                    ekycUserRequestsResponse.setEkycWorkflowId(Long.valueOf(ewId));
                    ekycUserRequestsResponse.setIsVerified(isVerified.charAt(0));
                    ekycUserRequestsResponse.setMobile(Encryption.decString((String) row.get("USER_MOBILE")));
                    ekycUserRequestsResponse.setServicesJson((String) row.get("SERVICES_JSON"));
                    ekycUserRequestsResponse.setUserEmail(Encryption.decString((String) row.get("USER_EMAIL")));
                    ekycUserRequestsResponse.setUserName(Encryption.decString((String) row.get("USER_NAME")));
                    ekycUserRequestsResponse.setUserUniqueId(Encryption.decString((String) row.get("USER_UNIQUE_ID")));
                    ekycUserRequestsResponse.setWorkflowName(Encryption.decString((String) row.get("WORKFLOW_NAME")));
                    ekycUserRequestsResponse.setMerchantId((Long) row.get("MERCHANT_ID"));

                    LocalDateTime date = (LocalDateTime) row.get("CREATION_DATE");
                    ekycUserRequestsResponse.setCreationDate(DateAndTime.dateFormatForPartner2(date.toString()));

                    ekycUserRequestsResponse.setServiceCount(serviceCount);
                    result.add(ekycUserRequestsResponse);
                }


                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put("code", ResponseMessage.SUCCESS);
                map.put("data", result);
            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
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
    public Map<String, Object> findByUserUniqueId(String userUniqueId) {
        Map<String, Object> map = new HashMap<>();
        try {
            EkycUserTable ekycUser = EkycUserRepository.findByEkycUserUniqueId(userUniqueId);

            if (ekycUser != null) {

                String userMobile = Encryption.decString(ekycUser.getUserMobile());
                String userEMail = Encryption.decString(ekycUser.getUserEmail());
                String userName = Encryption.decString(ekycUser.getUserName());
                String serviceJson = ekycUser.getServicesJson();


                EkycWorkflow ekycWorkflow = ekycWorkflowRepository.findById(ekycUser.getEkycWorkflowId()).get();

                String workFlowName = ekycWorkflow.getWorkflowName();
                String days = ekycWorkflow.getDays();
                String description = ekycWorkflow.getDescription();
                //String imageUrl=ekycWorkflow.getImageUrl();
                String kycType = ekycWorkflow.getKycType();
                //String url="https://fidylogoandqrimages.s3.ap-south-1.amazonaws.com/"+imageUrl;

                MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(ekycUser.getMerchantId());
                String logo = merchantInfo.getImageUrl();

                LOGGER.info("logo:" + logo);

                String url = "NA";
                if (!logo.equalsIgnoreCase("NA")) {
                    url = ResponseMessage.LOGO_URL + logo;
                }


                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, "EKYC User details");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put("userMobile", userMobile);
                map.put("userEMail", userEMail);
                map.put("userName", userName);
                map.put("serviceJson", serviceJson);
                map.put("workFlowName", workFlowName);
                map.put("days", days);
                map.put("description", description);
                map.put("imageUrl", url);
                map.put("kycType", kycType);

            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, "UserUniqueId does not exist");
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
    public Map<String, Object> workflowExpiredScheduler() {
        Map<String, Object> map = new HashMap<>();
        try {

            List<EkycUserTable> list = EkycUserRepository.findAllVerifiedUsers();
            LOGGER.info("list: " + list.size());

            for (EkycUserTable ekycUserTable : list) {

                String actDate = ekycUserTable.getActivationDate().toString();
                LOGGER.info("actDate: " + actDate);

                EkycWorkflow ekycWorkflow = ekycWorkflowRepository.findById(ekycUserTable.getEkycWorkflowId()).get();

                String days = ekycWorkflow.getDays();

                if (days.equals("NA")) {
                    days = "0";
                }

                LOGGER.info("days: " + days);

                LocalDate date = ekycUserTable.getActivationDate().toLocalDateTime().toLocalDate();

                //LocalDate newDate = date.minusDays(Long.valueOf(days));

                LocalDate newDate = date.plusDays(Long.valueOf(days));

                LocalDate currentDate = LocalDate.now();

                LOGGER.info("date: " + date);
                LOGGER.info("newDate: " + newDate);
                LOGGER.info("currentDate: " + currentDate);

//				 long daysDifference = ChronoUnit.DAYS.between(newDate, currentDate); 
                long daysDifference = ChronoUnit.DAYS.between(date, newDate);
                LOGGER.info("daysDifference: " + daysDifference);

                String userEmail = Encryption.decString(ekycUserTable.getUserEmail());
                String userName = Encryption.decString(ekycUserTable.getUserName());
                String businessName = Encryption.decString(ekycUserTable.getUserName());

                LocalTime localTime = LocalTime.MIDNIGHT;
                LocalDateTime dateTime = LocalDateTime.of(newDate, localTime);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

                // Format the LocalDateTime object using the formatter
                String formattedDate = dateTime.format(formatter);

                MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(ekycWorkflow.getMerchantId());
                String logo = Encryption.decString(merchantInfo.getImageUrl());
                if (!logo.equalsIgnoreCase("NA")) {
                    logo = ResponseMessage.LOGO_URL + logo;
                }

                if (daysDifference <= 5) {

                    LOGGER.info("----------You kyc is expired in: " + daysDifference + "days,Please Re Kyc your details-------------");

                    String link = ResponseMessage.LINK_WORKFLOW + ekycUserTable.getUserUniqueId();
                    // Email
                    if (userEmail != null && !userEmail.equals("NA")) {
                        String sendEmail = apiImpl.sendEmailForReKycVerification(userName, userEmail, formattedDate, link, logo, businessName);
                        LOGGER.info("----------sendEmail:-------------" + sendEmail);
                    }
                }
            }

            map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            map.put(ResponseMessage.DESCRIPTION, "Notification send successfully");
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    @Override
    public Map<String, Object> AllApprovedByEkycUserId(Long ekycUserId) {
        Map<String, Object> map = new HashMap<>();
        try {
            EkycUserTable ekycUser = EkycUserRepository.findById(ekycUserId).get();

            if (ekycUser != null) {

                String sCount = String.valueOf(ekycUser.getServiceCount());

                LOGGER.info("sCount: " + sCount);

                Integer serviceCount = Integer.valueOf(sCount);

                List<EkycWorkflowService> list = ekycWorkflowServiceRepository.findByEkycUserIdForVerified(ekycUserId, ekycUser.getMerchantId());
                if (list.size() != 0) {

                    Integer isVerified = 0;
                    for (EkycWorkflowService ekycWorkflowService : list) {

                        if (ekycWorkflowService.getIsVerified() == '1')
                            isVerified = isVerified + 1;

                    }

                    LOGGER.info("serviceCount: " + serviceCount);
                    LOGGER.info("isVerified: " + isVerified);

                    if (serviceCount == isVerified) {
                        ekycUser.setIsVerified('1');
                        ekycUser = EkycUserRepository.save(ekycUser);

                        map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                        map.put(ResponseMessage.DESCRIPTION, "e-KYC user approved successfully!");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                    } else {
                        map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                        map.put(ResponseMessage.DESCRIPTION, "For every document, each step must be completed and approved individually before the entire document can be approved.");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                    }
                } else {
                    List<EkycWorkflowService> list2 = ekycWorkflowServiceRepository.findByEkycUserIdForReKyc(ekycUserId, ekycUser.getMerchantId());


                    Integer isVerified = 0;
                    for (EkycWorkflowService ekycWorkflowService : list2) {

                        if (ekycWorkflowService.getIsVerified() == '1')
                            isVerified = isVerified + 1;

                    }

                    LOGGER.info("serviceCount: " + serviceCount);
                    LOGGER.info("isVerified: " + isVerified);

                    if (serviceCount == isVerified) {
                        ekycUser.setIsVerified('1');
                        ekycUser = EkycUserRepository.save(ekycUser);

                        map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                        map.put(ResponseMessage.DESCRIPTION, "ekycUser verified successfully");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                    } else {
                        map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                        map.put(ResponseMessage.DESCRIPTION, "For every document, each step must be completed and approved individually before the entire document can be approved.");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                    }
                }
            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, "ekycUserId does not exist");
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
    public Map<String, Object> AllRejectedByEkycUserId(Long ekycUserId) {
        Map<String, Object> map = new HashMap<>();
        try {
            EkycUserTable ekycUser = EkycUserRepository.findById(ekycUserId).get();

            if (ekycUser != null) {

                String sCount = String.valueOf(ekycUser.getServiceCount());

                LOGGER.info("sCount: " + sCount);

                Integer serviceCount = Integer.valueOf(sCount);

                List<EkycWorkflowService> list = ekycWorkflowServiceRepository.findByEkycUserIdForVerified(ekycUserId, ekycUser.getMerchantId());
                if (list.size() != 0) {

                    Integer isVerified = 0;
                    for (EkycWorkflowService ekycWorkflowService : list) {

                        if (ekycWorkflowService.getIsVerified() == '2')
                            isVerified = isVerified + 1;
                    }

                    if (isVerified <= serviceCount) {
                        ekycUser.setIsVerified('2');
                        ekycUser = EkycUserRepository.save(ekycUser);

                        map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                        map.put(ResponseMessage.DESCRIPTION, "e-KYC user rejected successfully!");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                    } else {
                        map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                        map.put(ResponseMessage.DESCRIPTION, "For every document, each step must be completed and rejected individually before the entire document can be rejected.");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                    }
                } else {
                    List<EkycWorkflowService> list2 = ekycWorkflowServiceRepository.findByEkycUserIdForReKyc(ekycUserId, ekycUser.getMerchantId());


                    Integer isVerified = 0;
                    for (EkycWorkflowService ekycWorkflowService : list2) {

                        if (ekycWorkflowService.getIsVerified() == '2')
                            isVerified = isVerified + 1;

                    }

                    //if(serviceCount==isVerified) {
                    if (isVerified <= serviceCount) {
                        ekycUser.setIsVerified('2');
                        ekycUser = EkycUserRepository.save(ekycUser);

                        map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                        map.put(ResponseMessage.DESCRIPTION, "ekycUser verified successfully");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                    } else {
                        map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                        map.put(ResponseMessage.DESCRIPTION, "For every document, each step must be completed and rejected individually before the entire document can be rejected.");
                        map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                    }
                }
            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, "ekycUserId does not exist");
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
    public List<BulkEkycUserPayload> saveBulkEkycUsers(MultipartFile file, long merchantId, String clientId, String workflowUniqueId) {
        List<BulkEkycUserPayload> unVerifiedDataList = new ArrayList<BulkEkycUserPayload>();
        try {
            Timestamp timestamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

            List<BulkEkycUsersRequest> uploadrequest = ValidateBulkEKyc.convertToModel(file,
                    BulkEkycUsersRequest.class);

            LOGGER.info("list size-----" + uploadrequest.size());

            if (uploadrequest.size() > 0) {

//				Map<String, String> validationMap = new HashMap<String, String>();

                JSONArray validationMap = null;
                for (BulkEkycUsersRequest p : uploadrequest) {

                    LOGGER.info("inside for -----");

                    if (ValidateBulkEKyc.isRowEmpty(p)) {
                        continue;
                    }

                    ValidateBulkEKyc kyc = new ValidateBulkEKyc();

                    validationMap = kyc.validate(p);

                    LOGGER.info("validationMap -----" + validationMap.toString());


                    if (validationMap.isEmpty()) {

                        EkycUserTempDetails enachTempDetails = new EkycUserTempDetails();


                        EkycWorkflow workflow = ekycWorkflowRepository.findByWorkflowUniqueId(workflowUniqueId);


                        LOGGER.info("workflow.getServices() -----" + workflow.getServices());

                        JSONArray array = new JSONArray(workflow.getServices());

                        EkycRequest ekycRequest = new EkycRequest();
                        ekycRequest.setServiceCount(String.valueOf(array.length()));
                        ekycRequest.setIsNotification("2");
                        ekycRequest.setEkycWorkflowId(String.valueOf(workflow.getEkycWorkflowId()));
                        ekycRequest.setUserEmail(p.getUserEmail());
                        ekycRequest.setUsermobile(p.getUserMobile());
                        ekycRequest.setUserName(p.getUserName());

                        Merchants merchants = merchantsRepository.findById(merchantId).get();

                        String businessName = Encryption.decString(merchants.getMerchantBusinessName());

                        Map<String, Object> res = saveEkycUserInfo(ekycRequest, merchantId, businessName);


                        LOGGER.info("res: -----" + res);

                        // Access individual elements from the map
                        String code = (String) res.get("code");
                        String description = (String) res.get("description");

                        LOGGER.info("code: -----" + code);
                        LOGGER.info("description: -----" + description);
                        if (code.equals("0x0202")) {

//								validationMap.put("description", description);
                            validationMap.put(description);

                            BulkEkycUserPayload reportpayload = new BulkEkycUserPayload();
                            reportpayload.setUserEmail(p.getUserEmail());
                            reportpayload.setUserMobile(p.getUserMobile());
                            reportpayload.setUserName(p.getUserName());
                            reportpayload.setWorkflowUniqueId(workflowUniqueId);
//							reportpayload.setReason(ValidateBulkEKyc.convertWithIteration(validationMap));
                            reportpayload.setReason(validationMap.toString());
                            unVerifiedDataList.add(reportpayload);

                            enachTempDetails.setIsVerified('0');

                            enachTempDetails.setReason(validationMap.toString());
                        } else {
                            enachTempDetails.setIsVerified('1');
                            enachTempDetails.setReason("NA");
                        }


                        enachTempDetails.setCreationDate(timestamp);

                        enachTempDetails.setMerchantId(merchantId);
                        enachTempDetails.setServiceCount(String.valueOf(array.length()).charAt(0));
                        enachTempDetails.setUserEmail(p.getUserEmail());
                        enachTempDetails.setUserMobile(p.getUserMobile());
                        enachTempDetails.setUserName(p.getUserName());
                        enachTempDetails.setWorkflowUniqueId(workflowUniqueId);
                        enachTempDetails.setReason(validationMap.toString());
                        enachTempDetails = ekycUserTempDetailsRepository.save(enachTempDetails);

                    } else {

                        EkycUserTempDetails enachTempDetails = new EkycUserTempDetails();

                        enachTempDetails.setCreationDate(timestamp);
                        enachTempDetails.setIsVerified('0');
                        enachTempDetails.setMerchantId(merchantId);
                        enachTempDetails.setServiceCount(Character.valueOf('0'));
                        enachTempDetails.setUserEmail(p.getUserEmail());
                        enachTempDetails.setUserMobile(p.getUserMobile());
                        enachTempDetails.setUserName(p.getUserName());
                        enachTempDetails.setWorkflowUniqueId(workflowUniqueId);
//							enachTempDetails.setReason(ValidateBulkEKyc.convertWithIteration(validationMap));
                        enachTempDetails.setReason(validationMap.toString());
                        enachTempDetails = ekycUserTempDetailsRepository.save(enachTempDetails);


                        BulkEkycUserPayload reportpayload = new BulkEkycUserPayload();
                        reportpayload.setUserEmail(p.getUserEmail());
                        reportpayload.setUserMobile(p.getUserMobile());
                        reportpayload.setUserName(p.getUserName());
                        reportpayload.setWorkflowUniqueId(workflowUniqueId);
                        //	reportpayload.setReason(ValidateBulkEKyc.convertWithIteration(validationMap));
                        reportpayload.setReason(validationMap.toString());
                        unVerifiedDataList.add(reportpayload);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unVerifiedDataList;
    }

    @Override
    public Map<String, Object> eKycWorkflowTempList(@Valid EKycWorkFlowTempRequest eKycWorkFlowTempRequest,
                                                    long merchantId) {
        Map<String, Object> map = new HashMap<>();
        List<EkycUserTempPayload> eList = new ArrayList<>();
        try {
            Pageable pageable = PageRequest.of(eKycWorkFlowTempRequest.getPageNo(), eKycWorkFlowTempRequest.getPageSize());


            String startDate = eKycWorkFlowTempRequest.getStartDate() + " 00:00:00.0";
            String endDate = eKycWorkFlowTempRequest.getEndDate() + " 23:59:59.9";
            String ekycWorkflowId = eKycWorkFlowTempRequest.getEkycWorkflowId();


            List<EkycUserTempDetails> list = null;
            int totalRecords = 0;
            if ((ekycWorkflowId == null || ekycWorkflowId.equals(""))) {

                list = ekycUserTempDetailsRepository.findByStartDateAndEndDate(merchantId, startDate, endDate, pageable);

                totalRecords = ekycUserTempDetailsRepository.countByStartDateAndEndDate(merchantId, startDate, endDate);
            } else {
                list = ekycUserTempDetailsRepository.findByStartDateAndEndDateAndEkycWorkflowId(merchantId, startDate, endDate, ekycWorkflowId, pageable);

                totalRecords = ekycUserTempDetailsRepository.countByStartDateAndEndDateAndEkycWorkflowId(merchantId, startDate, endDate, ekycWorkflowId);

            }


            if (list.size() != 0) {

                int i = 0;
                for (EkycUserTempDetails ekycUserTempDetails : list) {
                    i++;


                    String email = ekycUserTempDetails.getUserEmail();
                    String mobile = ekycUserTempDetails.getUserMobile();
                    String userName = ekycUserTempDetails.getUserName();


                    if (ekycUserTempDetails.getUserEmail().equals("")) {
                        email = "NA";
                    }


                    if (ekycUserTempDetails.getUserMobile().equals("")) {
                        mobile = "NA";
                    }


                    if (ekycUserTempDetails.getUserName().equals("")) {
                        userName = "NA";
                    }


                    EkycUserTempPayload ekycUserTempPayload = new EkycUserTempPayload();
                    ekycUserTempPayload.setsNo(i);
                    ekycUserTempPayload.setCreationDate(DateAndTime.dateFormatForPartner3(ekycUserTempDetails.getCreationDate().toString()));
                    ekycUserTempPayload.setEkycUserTempId(ekycUserTempDetails.getEkycUserTempId());
                    ekycUserTempPayload.setIsVerified(String.valueOf(ekycUserTempDetails.getIsVerified()));
                    ekycUserTempPayload.setMerchantId(ekycUserTempDetails.getMerchantId());
                    ekycUserTempPayload.setUserEmail(email);
                    ekycUserTempPayload.setUserMobile(mobile);
                    ekycUserTempPayload.setUserName(userName);
                    ekycUserTempPayload.setWorkflowUniqueId(ekycUserTempDetails.getWorkflowUniqueId());

                    LOGGER.info("ekycUserTempDetails.getReason(): -----" + ekycUserTempDetails.getReason());

                    ekycUserTempPayload.setReason(ekycUserTempDetails.getReason().toString());

                    eList.add(ekycUserTempPayload);
                }

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, "ekycUser temp list");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put("data", eList);
                map.put("totalRecords", totalRecords);

            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
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

}
