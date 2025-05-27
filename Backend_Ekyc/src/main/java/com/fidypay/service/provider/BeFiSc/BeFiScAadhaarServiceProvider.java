package com.fidypay.service.provider.BeFiSc;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.entity.EkycRequest;
import com.fidypay.exception.EkycProviderException;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.request.ValidateOtp;
import com.fidypay.service.provider.AadhaarServiceProvider;
import com.fidypay.utils.constants.ResponseMessage;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service("BeFiSc_Aadhaar")
public class BeFiScAadhaarServiceProvider implements AadhaarServiceProvider {

    private static final Logger logger = LoggerFactory
        .getLogger(BeFiScAadhaarServiceProvider.class);

    @Value("${fidypay.ekyc.befisc.authKey}")
    private String authKey;

    @Value("${fidypay.ekyc.befisc.aadhaar.xmlSendOtpUrl:https://aadhaar-xml-send-otp.befisc.com/}")
    private String sendOtpUrl;

    @Autowired
    private OkHttpClient client;

    private final ObjectMapper jsonMapper;

    private final EkycRequestRepository ekycRequestRepository;


    public BeFiScAadhaarServiceProvider(ObjectMapper objectMapper,
        EkycRequestRepository ekycRequestRepo) {
        this.jsonMapper = objectMapper;
        this.ekycRequestRepository = ekycRequestRepo;
    }

    @Override
    public Map<String, Object> generateOtp(String aadhaarNumber, long merchantId)
        throws EkycProviderException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aadhaar", aadhaarNumber);

        Timestamp trxnDate = Timestamp.from(Instant.now());

        String apiUrl = sendOtpUrl;

        Request request;
        try {
            request = buildPost(apiUrl, params);
        } catch (JsonProcessingException ex) {
            logger.error(
                "Error in buildPost({},{}): {}", apiUrl, params, ex.getLocalizedMessage(), ex
            );
            throw new EkycProviderException("Failed to build API request", ex);
        }

        String apiResponseStr;
        Map<String, Object> finalResponseMap = new HashMap<>();
        try {
            Response response = client.newCall(request).execute();
            apiResponseStr = response.body().string();
        } catch (IOException ex) {
            logger.error("IOException: {}", ex.getLocalizedMessage(), ex);

            finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            finalResponseMap.put(ResponseMessage.DESCRIPTION, "Error: " + ex.getLocalizedMessage());
            return finalResponseMap;
        }

        Map<String, Object> apiResponseMap;
        try {
            apiResponseMap = jsonMapper
                .readValue(apiResponseStr, new TypeReference<Map<String, Object>>() {
                });
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing API response: {}", ex.getLocalizedMessage(), ex);
            throw new EkycProviderException("Failed to parse API response", ex);
        }

        Integer responseStatus = Integer.valueOf("" + apiResponseMap.get("status"));
        String merchantTxnId = (String) apiResponseMap.get("txn_id");

        if (responseStatus == null) {
            finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            finalResponseMap.put(ResponseMessage.DESCRIPTION, "Error with no response status");
            finalResponseMap.put(ResponseMessage.MERCHANT_TXN_REF_ID, merchantTxnId);
            return finalResponseMap;
        }

        if (responseStatus == 1) {
            Map<String, Object> apiResult = (Map<String, Object>) apiResponseMap.get("result");
            String requestId = (String) apiResult.get("requestId");

            EkycRequest ekycRequest = ekycRequestRepository
                .save(new EkycRequest(merchantId, merchantTxnId, requestId, trxnDate));
            logger.info("ekycRequest: {}", ekycRequest);

            finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
            finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            finalResponseMap.put(ResponseMessage.DESCRIPTION, apiResult.get("message"));
            finalResponseMap.put(ResponseMessage.MERCHANT_TXN_REF_ID, requestId);
            return finalResponseMap;

        }

        String description;
        switch (responseStatus) {
            case 3:
                description = "Invalid Aadhaar Number";
                break;
            case 4:
                description = "Aadhaar number is not linked with any mobile number";
                break;
            case 5:
                description = "Aadhaar locked";
                break;
            case 7:
                description = "Other Aadhaar Problems";
                break;
            default:
                description = descriptionForCommonStatus(responseStatus);
        }

        String responseMessage = (String) apiResponseMap.get("message");
        finalResponseMap.put("message", responseMessage);
        finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
        finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
        finalResponseMap.put(ResponseMessage.DESCRIPTION, description);
        finalResponseMap.put(ResponseMessage.MERCHANT_TXN_REF_ID, merchantTxnId);

        return finalResponseMap;
    }

    @Override
    public Map<String, Object> validateOtp(ValidateOtp validateotp) throws EkycProviderException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aadhaar", validateotp.getAadhaarNumber());
        params.put("referenceId", validateotp.getInitiation_transaction_id());
        params.put("otp", validateotp.getOtp());

        String validateOtpURL = "https://aadhaar-vintage.befisc.com/";
        String apiUrl = validateOtpURL;

        Request request;
        try {
            request = buildPost(apiUrl, params);
        } catch (JsonProcessingException ex) {
            logger.error(
                "Error in buildPost({},{}): {}", apiUrl, params, ex.getLocalizedMessage(), ex
            );
            throw new EkycProviderException("Failed to build API request", ex);
        }

        String apiResponseStr;
        Map<String, Object> finalResponseMap = new HashMap<>();
        try {
            Response response = client.newCall(request).execute();
            apiResponseStr = response.body().string();
        } catch (IOException ex) {
            logger.error("IOException: {}", ex.getLocalizedMessage(), ex);

            finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            finalResponseMap.put(ResponseMessage.DESCRIPTION, "Error: " + ex.getLocalizedMessage());
            return finalResponseMap;
        }

        Map<String, Object> apiResponseMap;
        try {
            apiResponseMap = jsonMapper
                .readValue(apiResponseStr, new TypeReference<Map<String, Object>>() {
                });
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing API response: {}", ex.getLocalizedMessage(), ex);
            throw new EkycProviderException("Failed to parse API response", ex);
        }

        Integer responseStatus = Integer.valueOf("" + apiResponseMap.get("status"));
        String merchantTxnId = (String) apiResponseMap.get("txn_id");

        if (responseStatus == null) {
            finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            finalResponseMap.put(ResponseMessage.DESCRIPTION, "Error with no response status");
            finalResponseMap.put(ResponseMessage.MERCHANT_TXN_REF_ID, merchantTxnId);
            return finalResponseMap;
        }

        if (responseStatus == 1) {

            Map<String, Object> apiResult = (Map<String, Object>) apiResponseMap.get("result");

            finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
            finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            finalResponseMap.put(ResponseMessage.DESCRIPTION, apiResult.get("message"));
            finalResponseMap.put(ResponseMessage.MERCHANT_TXN_REF_ID, merchantTxnId);
            finalResponseMap.put("requestId", apiResult.get("requestId"));
            return finalResponseMap;

        }

        String description;
        switch (responseStatus) {
            case 2:
                description = "Invalid OTP/referenceId";
                break;
            case 3:
                description = "OTP timed out";
                break;
            case 4:
                description = "OTP already submitted";
                break;
            default:
                description = descriptionForCommonStatus(responseStatus);
        }
        String responseMessage = (String) apiResponseMap.get("message");
        finalResponseMap.put("message", responseMessage);
        finalResponseMap.put(ResponseMessage.DESCRIPTION, description);
        finalResponseMap.put(ResponseMessage.MERCHANT_TXN_REF_ID, merchantTxnId);
        finalResponseMap.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
        finalResponseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);

        return finalResponseMap;
    }

    @Override
    public String getProviderId() {
        return "BeFiSc";
    }

    private Request buildPost(String requestUrl, Object bodyParams) throws JsonProcessingException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonMapper.writeValueAsString(bodyParams));
        return new Request.Builder().url(requestUrl).addHeader("authkey", authKey)
            .method("POST", body).build();
    }

    private String descriptionForCommonStatus(Integer status) {
        switch (status) {
            case 3:
                return "Invalid Aadhaar Number";
            case 4:
                return "Aadhaar number is not linked with any mobile number";
            case 5:
                return "Aadhaar locked";
            case 7:
                return "Other Aadhaar Problems";

            // These aren't HTTP Status codes. These are codes defined in API doc of BeFiSc
            case 401:
                return "Authkey missing or invalid";
            case 402:
                return "Your account does not have the required privilege to access this API";
            case 403:
                return "Request limit exceeded";
            case 301:
                return "Parameter missing";
            case 302:
                return "Source down";

            default:
                return "Unexpected API Error";
        }
    }

}
