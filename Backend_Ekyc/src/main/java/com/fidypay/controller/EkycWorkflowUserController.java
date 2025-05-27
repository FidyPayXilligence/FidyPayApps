package com.fidypay.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.dto.BulkEkycUserPayload;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.EkycWorkflowRepository;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.EKycUpdateRequest;
import com.fidypay.request.EKycWorkFlowTempRequest;
import com.fidypay.request.EkycRequest;
import com.fidypay.request.EkycRequests;
import com.fidypay.request.EkycUserFilterRequest;
import com.fidypay.request.EkycUserRequest;
import com.fidypay.service.EkycUserService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.AmazonClient;
import com.fidypay.utils.ex.EmailAPIImpl;
import com.fidypay.utils.ex.ExcelExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/workflowUser")
public class EkycWorkflowUserController {

    @Autowired
    private EkycUserService ekycUserService;

    @Autowired
    private MerchantInfoRepository merchantInfoRepository;

    @Autowired
    private MerchantsRepository merchantsRepository;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private EkycWorkflowRepository ekycWorkflowRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(EkycWorkflowUserController.class);

    @PostMapping("/saveEkycUserInfo")
    public Map<String, Object> saveEkycUserInfo(@RequestHeader(value = "Client-Id") String clientId,
                                                @RequestHeader("Client-Secret") String clientSecret, @Valid @RequestBody EkycRequest eKycRequest) {
        Map<String, Object> map = new HashMap<>();
        try {
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
            if (merchantInfo != null) {
                LOGGER.info("Inside Condition true");
                String businessName = Encryption.decString(merchantInfo.getMerchantBusinessName());
                return ekycUserService.saveEkycUserInfo(eKycRequest, merchantInfo.getMerchantId(), businessName);
            } else {
                LOGGER.info("Inside Condition false");
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Inside Condition exception");
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }


    @PostMapping("/resendNotification")
    public Map<String, Object> resendNotification(@RequestHeader(value = "Client-Id") String clientId,
                                                  @RequestHeader("Client-Secret") String clientSecret, @Valid @RequestParam("eKycUserId") Long eKycUserId) {
        Map<String, Object> map = new HashMap<>();
        try {
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
            if (merchantInfo != null) {
                LOGGER.info("Inside Condition true");
                String businessName = Encryption.decString(merchantInfo.getMerchantBusinessName());
                return ekycUserService.resendNotification(eKycUserId, merchantInfo.getMerchantId(), businessName);
            } else {
                LOGGER.info("Inside Condition false");
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Inside Condition exception");
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    @PostMapping("/findByEkycUserId")
    public Map<String, Object> findByEkycUserId(@RequestHeader(value = "Client-Id") String clientId,
                                                @RequestHeader("Client-Secret") String clientSecret, @RequestParam String ekycUserId) {
        Map<String, Object> map = new HashMap<>();
        try {
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
            if (merchantInfo != null) {
                LOGGER.info("Inside Condition true");
                return ekycUserService.findByEkycUserId(Long.valueOf(ekycUserId), merchantInfo.getMerchantId());
            } else {
                LOGGER.info("Inside Condition false");
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Inside Condition exception");
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    @PostMapping("/findAllUser")
    public Map<String, Object> findAllUser(@RequestHeader(value = "Client-Id") String clientId,
                                           @RequestHeader("Client-Secret") String clientSecret, @Valid @RequestBody EkycUserRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
            if (merchantInfo != null) {
                LOGGER.info("Inside Condition true");
                return ekycUserService.findAllUser(merchantInfo.getMerchantId(), request);
            } else {
                LOGGER.info("Inside Condition false");
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (IllegalArgumentException exception) {
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, "Please pass positive value on pageNo and pageSize");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Inside Condition exception");
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    @PostMapping("/updateIsDeleted")
    public Map<String, Object> getUpdateByEkycUserId(@RequestHeader(value = "Client-Id") String clientId,
                                                     @RequestHeader("Client-Secret") String clientSecret, @RequestParam long ekycUserId) {
        Map<String, Object> map = new HashMap<>();
        try {
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
            if (merchantInfo != null) {
                LOGGER.info("Inside Condition true");
                return ekycUserService.IsDeletByEkycUserId(ekycUserId, merchantInfo.getMerchantId());
            } else {
                LOGGER.info("Inside Condition false");
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Inside Condition exception");
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    @PostMapping("/isVerified")
    public Map<String, Object> isVerified(@RequestHeader(value = "Client-Id") String clientId,
                                          @RequestHeader("Client-Secret") String clientSecret, @RequestParam long ekycUserId,
                                          @RequestParam String isVerified) {
        Map<String, Object> map = new HashMap<>();
        try {
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
            if (merchantInfo != null) {
                LOGGER.info("Inside Condition true");
                return ekycUserService.updateIsVerified(ekycUserId, merchantInfo.getMerchantId(), isVerified);
            } else {
                LOGGER.info("Inside Condition false");
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Inside Condition exception");
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    @PostMapping("/findByNameAndMobileAndEmail")
    public Map<String, Object> findByNameAndMobileAndEmail(@RequestHeader(value = "Client-Id") String clientId,
                                                           @RequestHeader("Client-Secret") String clientSecret, @RequestBody EkycRequests ekycRequests) {
        Map<String, Object> map = new HashMap<>();
        try {
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
            if (merchantInfo != null) {
                LOGGER.info("Inside Condition true");
                return ekycUserService.findAllUserByRequest(merchantInfo.getMerchantId(), ekycRequests);
            } else {
                LOGGER.info("Inside Condition false");
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Inside Condition exception");
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    @PostMapping("/findByEkycWorkflowId")
    public Map<String, Object> findByEkycWorkflowId(@RequestHeader(value = "Client-Id") String clientId,
                                                    @RequestHeader("Client-Secret") String clientSecret, @RequestParam("ekycWorkflowId") String ekycWorkflowId) {

        Map<String, Object> response = new HashMap<String, Object>();

        try {
            LOGGER.info("clientSecret:- " + clientSecret);

            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

            if (merchantInfo != null) {
                long merchantId = merchantInfo.getMerchantId();
                LOGGER.info("merchantId:- " + merchantId);
                return ekycUserService.findByEkycWorkflowId(Long.valueOf(ekycWorkflowId), merchantId);

            } else {
                response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            }

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
        } catch (Exception e) {
            response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            //response.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
        }

        return response;
    }

    @ApiOperation(value = "sendOTPPhone Phone")
    @PostMapping("/{userUniqueId}/sendOTPPhone/{userMobile}")
    public Map<String, Object> sendOTPPhone(@PathVariable("userMobile") String userMobile,
                                            @PathVariable("userUniqueId") String userUniqueId) throws Exception {
        return ekycUserService.sendOTPPhone(userMobile, userUniqueId);

    }

    @ApiOperation(value = "otpVerification")
    @PostMapping("/{otp}/otpVerification/{otpToken}/{mId}")
    public Map<String, Object> otpVerification(@PathVariable("otp") String otp,
                                               @PathVariable("otpToken") String otpToken, @PathVariable("mId") long mId) throws Exception {
        return ekycUserService.otpVerification(otp, otpToken, mId);
    }

    @PostMapping("/updateServicesJson")
    public Map<String, Object> updateServicesJson(@RequestHeader(value = "Client-Id") String clientId,
                                                  @RequestHeader("Client-Secret") String clientSecret,
                                                  @RequestBody @Valid EKycUpdateRequest ekycUpdateRequest) {
        Map<String, Object> response = new HashMap<String, Object>();
        try {
            LOGGER.info("clientSecret:- " + clientSecret);

            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

            if (merchantInfo != null) {
                long merchantId = merchantInfo.getMerchantId();
                LOGGER.info("merchantId:- " + merchantId);
                return ekycUserService.updateServicesJson(ekycUpdateRequest, merchantId);

            } else {
                response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            }

        } catch (Exception e) {
            response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
        }

        return response;
    }

    @PostMapping("/searchEkycUserByFilter")
    public Map<String, Object> searchEkycUserByFilter(@RequestHeader(value = "Client-Id") String clientId,
                                                      @RequestHeader("Client-Secret") String clientSecret,
                                                      @RequestBody @Valid EkycUserFilterRequest ekycUserFilterRequest) {
        Map<String, Object> response = new HashMap<String, Object>();
        try {
            LOGGER.info("clientSecret:- " + clientSecret);

            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

            if (merchantInfo != null) {
                long merchantId = merchantInfo.getMerchantId();
                LOGGER.info("merchantId:- " + merchantId);
                return ekycUserService.ekycUserFilterDetails(ekycUserFilterRequest, merchantId);

            } else {
                response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            }

        } catch (Exception e) {
            response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
        }
        return response;
    }


    @PostMapping("/findByUserUniqueId")
    public Map<String, Object> findByUserUniqueId(@RequestParam("userUniqueId") String userUniqueId) {
        return ekycUserService.findByUserUniqueId(userUniqueId);
    }


    @GetMapping("/workflowExpiredScheduler")
    public Map<String, Object> workflowExpiredScheduler() {
        return ekycUserService.workflowExpiredScheduler();
    }


    @GetMapping("/AllApprovedByEkycUserId")
    public Map<String, Object> AllApprovedByEkycUserId(@RequestParam("ekycUserId") String ekycUserId) {
        return ekycUserService.AllApprovedByEkycUserId(Long.valueOf(ekycUserId));
    }

    @GetMapping("/AllRejectedByEkycUserId")
    public Map<String, Object> AllRejectedByEkycUserId(@RequestParam("ekycUserId") String ekycUserId) {
        return ekycUserService.AllRejectedByEkycUserId(Long.valueOf(ekycUserId));
    }


    //--------------------------------------------------------------------------


    @PostMapping("/saveBulkEkycUsers")
    public Map<String, Object> saveBulkEkycUsers(@RequestHeader(value = "Client-Id") String clientId,
                                                 @RequestHeader("Client-Secret") String clientSecret, @RequestParam("workflowUniqueId") String workflowUniqueId,
                                                 @RequestParam(name = "file", required = true) MultipartFile file, HttpServletResponse response)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        try {
            List<BulkEkycUserPayload> unVerifiedDataList = null;
            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');


            if (merchantInfo != null) {
                String logo = merchantInfo.getImageUrl();

                String TYPE = "text/csv";
                if (!TYPE.equals(file.getContentType())) {
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(ResponseMessage.DESCRIPTION, "File must be CSV format.");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                    return map;
                }


                if (!ekycWorkflowRepository.existsByWorkflowUniqueIdAndIsDeleted(workflowUniqueId, '0')) {
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(ResponseMessage.DESCRIPTION, "workflowUniqueId does not exist");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                    return map;
                }


                System.out.println("----Case SignDesk------");
                unVerifiedDataList = ekycUserService.saveBulkEkycUsers(
                        file, merchantInfo.getMerchantId(), clientId, workflowUniqueId);

                System.out.println("unVerifiedDataList " + unVerifiedDataList.size());

                if (unVerifiedDataList.isEmpty() || unVerifiedDataList.size() == 0) {
                    map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                    map.put(ResponseMessage.DESCRIPTION,
                            "File uploaded successfully.");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
                } else {
                    ExcelExporter excelExporter = new ExcelExporter();
                    File workbook = excelExporter.exportUnVerifiedMandateDataForEkycUsers(unVerifiedDataList);

                    String url = amazonClient.uploadFileV2ForBulk(workbook, merchantInfo.getMerchantId(), "Ekyc User");

                    System.out.println("url " + url);

                    Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

                    String mercchantEmail = Encryption.decString(merchants.getMerchantEmail());
                    String merchantBusinessName = Encryption.decString(merchants.getMerchantBusinessName());

                    EmailAPIImpl sendEmail = new EmailAPIImpl();
                    sendEmail.sendEmailForBulkEkycUsers(mercchantEmail, merchantBusinessName, url, logo);

                    map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                    map.put(ResponseMessage.DESCRIPTION,
                            "File uploaded successfully.");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
                }

            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
                map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            }
        } catch (Exception e) {
            map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
        }
        return map;
    }


    @PostMapping("/eKycWorkflowUserTempList")
    public Map<String, Object> eKycWorkflowUserTempList(@RequestHeader(value = "Client-Id") String clientId,
                                                        @RequestHeader("Client-Secret") String clientSecret,
                                                        @RequestBody @Valid EKycWorkFlowTempRequest eKycWorkFlowTempRequest) {
        Map<String, Object> response = new HashMap<String, Object>();
        try {
            LOGGER.info("clientSecret:- " + clientSecret);

            MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
                    Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

            if (merchantInfo != null) {
                long merchantId = merchantInfo.getMerchantId();
                LOGGER.info("merchantId:- " + merchantId);
                return ekycUserService.eKycWorkflowTempList(eKycWorkFlowTempRequest, merchantId);


            } else {
                response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
                response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
            }

        } catch (Exception e) {
            response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
            response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
        }

        return response;
    }
}
