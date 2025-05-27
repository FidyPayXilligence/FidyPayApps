package com.fidypay.ServiceProvider.Signzy;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.ServiceProvider.Decentro.DecentroUtils;
import com.fidypay.ServiceProvider.Karza.ESignServices;
import com.fidypay.entity.EkycRequest;
import com.fidypay.exception.EkycProviderException;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.request.CreditBureauReportRequest;
import com.fidypay.request.CustomerDataRequest;
import com.fidypay.request.FetchPassportRequest;
import com.fidypay.request.GenerateOtpRequest;
import com.fidypay.request.Validate;
import com.fidypay.request.ValidateOtp;
import com.fidypay.request.VerifyPassportRequest;
import com.fidypay.service.impl.AadhaarServiceImpl;
import com.fidypay.service.impl.GSTServiceImpl;
import com.fidypay.service.impl.PanServiceImpl;
import com.fidypay.service.provider.AadhaarServiceProvider;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service("Signzy_Aadhaar")
public class SignzyService implements AadhaarServiceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignzyService.class);

    private static final String SIGNZY_ID = "SIGNZY2019098";

    private static final String USER_NAME = "fidypay_test";

    private static final String USER_PASSWORD = "6zvWtnSar8dTjPDwr8dv";

    private static final String LOGIN_URL = "https://preproduction.signzy.tech/api/v2/patrons/login";

    private static final String CLIENT_ID = "fidypay_staging";

    private static final String CLIENT_SECRET = "dRr5IqBh5L4wQ2GYmst5iAxYLcWCLXEN";

    private static final String MODULE_SECRET = "giANewDVjwDIpSlth1qlctJV9jLP6P9j";

    private static final String PROVIDER_SECRET = "GshuYWHlmjy5gtLsKxKVKjE7ZtpBztHm";

    private static final String CREDIT_BUREAU_URL = "https://in.staging.decentro.tech/v2/financial_services/credit_bureau/credit_report/summary";

    private static final String FETCH_CUSTOMER_DETAILS_URL = "https://in.staging.decentro.tech/v2/financial_services/data/pull";

    private static final String FETCH_CREDIT_SCORE_URL = "https://a2fl3hr40d.execute-api.ap-south-1.amazonaws.com/equifax/cir";

    public static final String AADHAAR_BASE_URL = "https://signzy.tech/api/v2/snoops";
    // UAT
    // public static final String USERNAME = "Fidypay_test";
    // public static final String PASSWORD = "juSff9ET#vzc@Rc*";
    // public static final String BASE_URL =
    // "https://preproduction.signzy.tech/api/v2/patrons/";

    // LIVE
    public static final String USERNAME = "fidypay_prod";

    public static final String PASSWORD = "u4wwVbDFy2xYMrbRU8xs";

    public static final String BASE_URL = "https://signzy.tech/api/v2/patrons/";

    @Autowired
    private EkycRequestRepository ekycRequestRepository;


    @Override
    public String getProviderId() {
        return "Signzy";
    }

    public String login(String username, String password) throws Exception {
        // For Production Credentials
        // https://signzy.tech/api/v2/patrons/login

        String requestStr = "{\r\n" + "    \"username\": \"" + username + "\",\r\n"
            + "    \"password\": \"" + password + "\"\r\n" + "  }";
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestStr);
        Request request = new Request.Builder().url(BASE_URL + "login").method("POST", body)
            .addHeader("Content-Type", "application/json").build();
        Response response = client.newCall(request).execute();
        String finalResposne = response.body().string();
        return finalResposne;
    }

    public Map<String, Object> identityCardObjectForINDIVIDUALPAN() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        String responseLogin = new GSTServiceImpl()
            .login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
        System.out.println("Login Response " + responseLogin);
        JSONObject jsonObject = new JSONObject(responseLogin);
        String id = jsonObject.getString("id");
        String userId = jsonObject.getString("userId");

        String requestStr = "{\r\n" + "              \"type\": \"individualPan\",\r\n"
            + "              \"email\": \"admin@signzy.com\",\r\n"
            + "              \"callbackUrl\": \"https://prebuild.com/system\",\r\n"
            + "              \"images\": [\r\n"
            + "                \"https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80\"\r\n"
            + "              ]\r\n" + "            }";

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestStr);
        Request request = new Request.Builder()
            .url(GSTServiceImpl.BASE_URL + userId + "/identities").method("POST", body)
            .addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();
        Response response = client.newCall(request).execute();
        String finalResponse = response.body().string();
        System.out.println("Identity Response " + finalResponse);
        JSONObject jsonObjectIdentity = new JSONObject(finalResponse);
        String idIdentity = jsonObjectIdentity.getString("id");
        String accessTokenIdentity = jsonObjectIdentity.getString("accessToken");

        LOGGER.info("Inside Identity idIdentity " + idIdentity);
        LOGGER.info("Inside Identity authorization " + accessTokenIdentity);

        map.put("accessToken", accessTokenIdentity);
        map.put("authorization", id);
        map.put("idIdentity", idIdentity);
        return map;
    }

    public Map<String, Object> gstinSearch(String gSTIN, String merchantTrxnRefId) {
        Map<String, Object> map = new HashedMap<>();
        String finalResponse = null;
        try {
            String loginResponse = new GSTServiceImpl().login(USERNAME, PASSWORD);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            String requestStr = " {\r\n" + "    \"task\" : \"gstinSearch\",\r\n"
                + "    \"essentials\": {\r\n" + "        \"gstin\": \"" + gSTIN + "\"\r\n"
                + "    }\r\n" + "  }\r\n" + "";

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder().url(BASE_URL + userId + "/gstns")
                .method("POST", body).addHeader("Authorization", id)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();
            finalResponse = response.body().string();

            LOGGER.info("response: " + finalResponse);

            org.json.JSONObject resultJsonObject = new org.json.JSONObject(finalResponse);

            org.json.JSONObject resultObject = new org.json.JSONObject();

            if (!resultJsonObject.has("error")) {

                org.json.JSONObject result = resultJsonObject.getJSONObject("result");
                // org.json.JSONObject gstnDetailed = result.getJSONObject("gstnDetailed");
                // org.json.JSONArray gstnRecords = result.getJSONArray("gstnRecords");
                // org.json.JSONObject recordObject = gstnRecords.getJSONObject(0);
                //
                // resultObject.put("gstin", gSTIN);
                // resultObject.put("email", recordObject.get("emailId"));
                // resultObject.put("address", gstnDetailed.get("principalPlaceAddress"));
                // resultObject.put("mobileNumber", recordObject.get("mobNum"));
                // resultObject.put("natureOfBusinessAtAddress", "NA");
                // resultObject.put("stateJurisdiction", gstnDetailed.get("stateJurisdiction"));
                // resultObject.put("taxpayerType", gstnDetailed.get("taxPayerType"));
                // resultObject.put("registrationDate", gstnDetailed.get("registrationDate"));
                // resultObject.put("constitutionOfBusiness",
                // gstnDetailed.get("constitutionOfBusiness"));
                // resultObject.put("gstnStatus", gstnDetailed.get("gstinStatus"));
                // resultObject.put("legalName", gstnDetailed.get("legalNameOfBusiness"));
                // resultObject.put("centralJurisdiction", gstnDetailed.get("centreJurisdiction"));
                // resultObject.put("pan", "NA");
                // resultObject.put("tradeName", gstnDetailed.get("legalNameOfBusiness"));

                map = ESignServices.setResponse(
                    ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
                    ResponseMessage.DATA_SUCCESS
                );

                // map.put("merchantTxnRefId", merchantTrxnRefId);
                map.put("result", result.toMap());
                return map;
            } else {
                // map = ESignServices.setResponse(ResponseMessage.FAILED,
                // ResponseMessage.API_STATUS_FAILED,
                // ResponseMessage.DATA_NOT_FOUND);
                map = ESignServices.setResponse(
                    ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS, "Invalid GSTIN."
                );
                // map.put("gstin", gSTIN);
                // map.put("merchantTxnRefId", merchantTrxnRefId);
            }

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    public Map<String, Object> gstinDetailsSearch(String GSTIN) {
        Map<String, Object> map = new HashedMap<>();
        try {
            String finalResponse = null;

            String loginResponse = login(USERNAME, PASSWORD);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            String requestStr = " {\r\n" + "    \"task\" : \"detailedGstinSearch\",\r\n"
                + "    \"essentials\": {\r\n" + "        \"gstin\": \"" + GSTIN + "\"\r\n"
                + "    }\r\n" + "  }\r\n" + "";

            LOGGER.info("requestStr: " + requestStr);
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder().url(BASE_URL + userId + "/gstns")
                .method("POST", body).addHeader("Authorization", id)
                .addHeader("Content-Type", "application/json").build();

            LOGGER.info("request: " + request);

            Response response = client.newCall(request).execute();
            finalResponse = response.body().string();

            LOGGER.info("response: " + finalResponse);

            ObjectMapper objectMapper = new ObjectMapper();

            map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            map = objectMapper.readValue(finalResponse, Map.class);

            return map;

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    public Map<String, Object> gstSearchCompanyName(String companyName) {
        Map<String, Object> map = new HashedMap<>();
        try {
            String finalResponse = null;
            String loginResponse = login(USERNAME, PASSWORD);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            String requestStr = " {\r\n" + "    \"task\" : \"companyNameToGst\",\r\n"
                + "    \"essentials\": {\r\n" + "        \"companyName\": \"" + companyName
                + "\"\r\n" + "    }\r\n" + "  }\r\n" + "";

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder().url(BASE_URL + userId + "/gstns")
                .method("POST", body).addHeader("Authorization", id)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();
            finalResponse = response.body().string();

            LOGGER.info("request: " + request);
            LOGGER.info("response: " + finalResponse);

            JSONObject js = new JSONObject(finalResponse);
            // JSONObject jsonResponse = new JSONObject();

            ObjectMapper objectMapper = new ObjectMapper();
            map = objectMapper.readValue(finalResponse, Map.class);

            // if (js.has("error")) {
            // JSONObject error = js.getJSONObject("error");
            // String message = error.getString("message");
            //
            // map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            // map.put(ResponseMessage.DESCRIPTION, message);
            // map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            // }

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    public Map<String, Object> fetchPan(@Valid String number, String merchantTrxnRefId) {
        Map<String, Object> map = new HashMap<>();
        try {
            String finalResponse = null;
            String loginResponse = login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            LOGGER.info("Login  : " + loginResponse);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            JSONObject object = new JSONObject();
            object.put("task", "fetch");

            JSONObject essentials = new JSONObject();
            essentials.put("number", number);
            object.put("essentials", essentials);

            String req = object.toString();

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, req);
            Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
                .method("POST", body).addHeader("Authorization", id)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();

            String results = response.body().string();
            int code = response.code();
            LOGGER.info("Response : " + results);
            LOGGER.info("code : " + response.code());
            JSONObject responseJson = new JSONObject(results);

            if (code == 200) {
                JSONObject resultJson = responseJson.getJSONObject("result");
                String name = resultJson.getString("name");
                String panStatus = resultJson.getString("panStatus");

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put("name", name);
                map.put("panStatus", panStatus);
                map.put("panNumber", number);
                map.put("merchantTxnRefId", merchantTrxnRefId);
                return map;
            } else {
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put("panNumber", number);
                map.put("merchantTxnRefId", merchantTrxnRefId);
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

    public Map<String, Object> fetchPanV2(@Valid String number) {
        Map<String, Object> map = new HashedMap<>();
        try {
            String finalResponse = null;
            String loginResponse = new PanServiceImpl()
                .login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            LOGGER.info("id: " + id);

            LOGGER.info("userId: " + userId);

            JSONObject object = new JSONObject();

            JSONArray array = new JSONArray();

            array.add(0, "1");

            object.put("task", array);

            JSONObject essentials = new JSONObject();
            essentials.put("number", number);
            object.put("essentials", essentials);

            String req = object.toString();

            LOGGER.info("req: " + req);

            String url = GSTServiceImpl.BASE_URL + userId + "/panv2";

            LOGGER.info("url----->" + url);

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, req);
            Request request = new Request.Builder().url(url).method("POST", body)
                .addHeader("Authorization", id).addHeader("Content-Type", "application/json")
                .build();
            Response response = client.newCall(request).execute();

            String results = response.body().string();

            LOGGER.info("api response: " + results);

            try {
                JSONObject responseJson = new JSONObject(results);
                JSONObject resultJson = responseJson.getJSONObject("result");
                resultJson.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                resultJson.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
                resultJson.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

                finalResponse = resultJson.toString();

                ObjectMapper objectMapper = new ObjectMapper();
                map = objectMapper.readValue(finalResponse, Map.class);
                LOGGER.info("map:" + map);
            } catch (Exception ex) {
                JSONObject responseJson = new JSONObject(results);
                JSONObject resultJ = responseJson.getJSONObject("error");

                String message = resultJ.getString("message");

                JSONObject resultJson = new JSONObject();
                resultJson.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                resultJson.put(ResponseMessage.DESCRIPTION, message);
                resultJson.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

                finalResponse = resultJson.toString();

                ObjectMapper objectMapper = new ObjectMapper();
                map = objectMapper.readValue(finalResponse, Map.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    public Map<String, Object> fetchPanV2New(@Valid String number) {
        Map<String, Object> map = new HashedMap<>();
        try {
            String finalResponse = null;
            String loginResponse = login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            JSONObject object = new JSONObject();

            JSONArray array = new JSONArray();

            array.add(0, "1");

            object.put("task", array);

            JSONObject essentials = new JSONObject();
            essentials.put("number", number);
            object.put("essentials", essentials);

            String req = object.toString();

            System.out.println("req: " + req);

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, req);
            Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
                .method("POST", body).addHeader("Authorization", id)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();

            String results = response.body().string();
            JSONObject responseJson = new JSONObject(results);
            JSONObject resultJson = responseJson.getJSONObject("result");
            finalResponse = resultJson.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            map = objectMapper.readValue(finalResponse, Map.class);
            LOGGER.info("map:" + map);

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    public Map<String, Object> panCompliance(@Valid String panNumber) {
        Map<String, Object> map = new HashedMap<>();
        try {
            String finalResponse = null;
            String loginResponse = login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            JSONObject object = new JSONObject();

            JSONArray array = new JSONArray();

            array.add(0, "2");

            object.put("task", array);

            JSONObject essentials = new JSONObject();
            essentials.put("number", panNumber);
            object.put("essentials", essentials);

            String req = object.toString();

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, req);
            Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
                .method("POST", body).addHeader("Authorization", id)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();

            System.out.println("json req : " + req);

            System.out.println("request : " + request);
            String results = response.body().string();
            System.out.println("results : " + results);
            JSONObject responseJson = new JSONObject(results);
            JSONObject resultJson = responseJson.getJSONObject("result");
            finalResponse = resultJson.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            map = objectMapper.readValue(finalResponse, Map.class);
            LOGGER.info("map:" + map);

        }

        catch (JSONException e) {
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONTACT_TECH_SUPPORT);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    public Map<String, Object> fetchCompliance(@Valid String panNumber) {
        Map<String, Object> map = new HashedMap<>();
        try {
            String finalResponse = null;
            String loginResponse = login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            JSONObject jsonObject = new JSONObject(loginResponse);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            JSONObject object = new JSONObject();

            JSONArray array = new JSONArray();

            array.add(0, "1");
            array.add(1, "2");
            object.put("task", array);

            JSONObject essentials = new JSONObject();
            essentials.put("number", panNumber);
            object.put("essentials", essentials);

            String req = object.toString();

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, req);
            Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
                .method("POST", body).addHeader("Authorization", id)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();

            String results = response.body().string();

            System.out.println("results : " + results);
            JSONObject responseJson = new JSONObject(results);
            JSONObject resultJson = responseJson.getJSONObject("result");
            finalResponse = resultJson.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            map = objectMapper.readValue(finalResponse, Map.class);
            LOGGER.info("map:" + map);

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        return map;
    }

    public Map<String, Object> panAadharLinkStatus(String adharNumber, String panNumber) {
        Map<String, Object> map = new HashMap<>();
        try {
            String finalResponse = null;
            LOGGER.info("UID : " + adharNumber + " pans " + panNumber);
            Map<String, Object> mapU = identityCardObjectForINDIVIDUALPAN();
            String accessToken = (String) mapU.get("accessToken");
            String authorization = (String) mapU.get("authorization");
            String idIdentity = (String) mapU.get("idIdentity");

            LOGGER.info("Inside Verify Aadhaar accessToken " + accessToken);
            LOGGER.info("Inside Verify Aadhaar authorization " + authorization);
            LOGGER.info("Inside Verify Aadhaar idIdentity " + idIdentity);

            String requestStr = "{\r\n" + "      \"service\":\"Identity\",\r\n"
                + "      \"itemId\":\"" + idIdentity + "\",\r\n"
                + "      \"task\":\"panAadhaarLinkStatus\",\r\n" + "      \"accessToken\":\""
                + accessToken + "\",\r\n" + "      \"essentials\":{\r\n" + "          \"number\":\""
                + panNumber + "\",\r\n" + "          \"uid\":\"" + adharNumber + "\"\r\n"
                + "        }\r\n" + "      }";

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder().url(AadhaarServiceImpl.BASE_URL)
                .method("POST", body).addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json").build();

            Response response = client.newCall(request).execute();

            String results = response.body().string();

            LOGGER.info("Response: " + results);

            JSONObject jsonObject = new JSONObject(results);

            if (jsonObject.has("response")) {
                JSONObject responseJson = jsonObject.getJSONObject("response");
                JSONObject resultJson = responseJson.getJSONObject("result");
                finalResponse = resultJson.toString();
            } else {
                finalResponse = results;
            }

            LOGGER.info("Response : " + finalResponse);
            ObjectMapper objectMapper = new ObjectMapper();
            map = objectMapper.readValue(finalResponse, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    // --------------------- Passport
    // Service---------------------------------------------

    public Map<String, Object> identityCardObjectForPassport() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        String responseLogin = new GSTServiceImpl()
            .login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
        LOGGER.info("Login Response " + responseLogin);
        JSONObject jsonObject = new JSONObject(responseLogin);
        String id = jsonObject.getString("id");
        String userId = jsonObject.getString("userId");

        String requestStr = "{\r\n" + "              \"type\": \"passport\",\r\n"
            + "              \"email\": \"admin@signzy.com\",\r\n"
            + "              \"callbackUrl\": \"https://prebuild.com/system\",\r\n"
            + "              \"images\": [\r\n"
            + "                \"https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80\"\r\n"
            + "              ]\r\n" + "            }";

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestStr);
        Request request = new Request.Builder()
            .url(GSTServiceImpl.BASE_URL + userId + "/identities").method("POST", body)
            .addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();
        Response response = client.newCall(request).execute();
        String finalResponse = response.body().string();
        LOGGER.info("Identity Response " + finalResponse);
        JSONObject jsonObjectIdentity = new JSONObject(finalResponse);
        String idIdentity = jsonObjectIdentity.getString("id");
        String accessTokenIdentity = jsonObjectIdentity.getString("accessToken");

        LOGGER.info("Inside Identity idIdentity " + idIdentity);
        LOGGER.info("Inside Identity authorization " + accessTokenIdentity);

        map.put("accessToken", accessTokenIdentity);
        map.put("authorization", id);
        map.put("idIdentity", idIdentity);
        return map;
    }

    public Map<String, Object> fetchPassport(@Valid FetchPassportRequest fetchPassportRequest) {
        Map<String, Object> map = new HashMap<>();
        try {
            String finalResponse = null;
            Map<String, Object> mapU = identityCardObjectForPassport();
            String accessToken = (String) mapU.get("accessToken");
            String authorization = (String) mapU.get("authorization");
            String idIdentity = (String) mapU.get("idIdentity");

            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(fetchPassportRequest.getDob());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dob = formatter.format(date1);
            LOGGER.info("dob: " + dob);

            String requstStr = "{\r\n" + "      \"service\":\"Identity\",\r\n"
                + "      \"itemId\":\"" + idIdentity + "\",\r\n" + "      \"task\":\"fetch\",\r\n"
                + "      \"accessToken\":\"" + accessToken + "\",\r\n"
                + "      \"essentials\":{\r\n" + "            \"fileNumber\":\""
                + fetchPassportRequest.getFileNumber() + "\",\r\n" + "            \"dob\":\"" + dob
                + "\"\r\n" + "        }\r\n" + "     }";

            LOGGER.info("Json Request: " + requstStr);

            OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requstStr);
            Request request = new Request.Builder().url("https://signzy.tech/api/v2/snoops")
                .method("POST", body).addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();

            String results = response.body().string();

            LOGGER.info("results: " + results);

            Object obj = null;
            JSONParser parser = new JSONParser();
            obj = parser.parse(results);
            org.json.simple.JSONObject jsonObjectt = (org.json.simple.JSONObject) obj;

            org.json.simple.JSONObject responseJson = (org.json.simple.JSONObject) jsonObjectt
                .get("response");

            if (responseJson != null) {
                org.json.simple.JSONObject resultJson = (org.json.simple.JSONObject) responseJson
                    .get("result");
                finalResponse = resultJson.toString();

                String fileNumber = (String) resultJson.get("fileNumber");
                String givenName = (String) resultJson.get("givenName");
                String surname = (String) resultJson.get("surname");
                String typeOfApplication = (String) resultJson.get("typeOfApplication");
                String applicationReceivedOnDate = (String) resultJson
                    .get("applicationReceivedOnDate");
                String name = (String) resultJson.get("name");
                String dob2 = (String) resultJson.get("dob");

                org.json.simple.JSONObject object = new org.json.simple.JSONObject();
                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, "Passport details");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put("fileNumber", fileNumber);
                map.put("givenName", givenName);
                map.put("surname", surname);
                map.put("typeOfApplication", typeOfApplication);
                map.put("applicationReceivedOnDate", applicationReceivedOnDate);
                map.put("name", name);
                map.put("dob", dob2);
            } else {

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, "Please Pass Valid File Number and DOB");
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }
        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    public Map<String, Object> verifyPassport(VerifyPassportRequest verifyPassportRequest) {
        Map<String, Object> map = new HashMap<>();
        try {
            String finalResponse = null;
            Map<String, Object> mapU = identityCardObjectForPassport();
            String accessToken = (String) mapU.get("accessToken");
            String authorization = (String) mapU.get("authorization");
            String idIdentity = (String) mapU.get("idIdentity");

            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(verifyPassportRequest.getDob());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dob = formatter.format(date1);
            LOGGER.info("dob: " + dob);

            String requstStr = "{\r\n" + "      \"service\":\"Identity\",\r\n"
                + "      \"itemId\":\"" + idIdentity + "\",\r\n"
                + "      \"task\":\"verification\",\r\n" + "      \"accessToken\":\"" + accessToken
                + "\",\r\n" + "      \"essentials\":{\r\n" + "            \"fileNumber\":\""
                + verifyPassportRequest.getFileNumber() + "\",\r\n" + "            \"dob\":\"" + dob
                + "\",\r\n" + "            \"name\":\"" + verifyPassportRequest.getName()
                + "\",\r\n" + "            \"fuzzy\" : \"true\"\r\n" + "        }\r\n" + "     }";

            LOGGER.info("Json Request: " + requstStr);

            OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requstStr);
            Request request = new Request.Builder().url("https://signzy.tech/api/v2/snoops")
                .method("POST", body).addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();

            String results = response.body().string();

            LOGGER.info("Response:" + results);

            Object obj = null;
            JSONParser parser = new JSONParser();
            obj = parser.parse(results);
            org.json.simple.JSONObject jsonObjectt = (org.json.simple.JSONObject) obj;

            org.json.simple.JSONObject responseJson = (org.json.simple.JSONObject) jsonObjectt
                .get("response");

            if (responseJson != null) {
                org.json.simple.JSONObject resultJson = (org.json.simple.JSONObject) responseJson
                    .get("result");
                resultJson.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                resultJson.put(ResponseMessage.DESCRIPTION, "Verify passport details");
                resultJson.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                finalResponse = resultJson.toString();
            } else {
                org.json.simple.JSONObject error = (org.json.simple.JSONObject) jsonObjectt
                    .get("error");

                Long statusCode = (Long) error.get("statusCode");
                String message = (String) error.get("message");

                org.json.simple.JSONObject object = new org.json.simple.JSONObject();
                object.put("statusCode", statusCode);
                object.put("message", message);
                object.put(ResponseMessage.DESCRIPTION, message);
                object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                finalResponse = object.toString();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            map = objectMapper.readValue(finalResponse, Map.class);

        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    // ----------------------VoterId Service-------------------------------------

    public Map<String, Object> identityCardObjectForVoterId() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        String responseLogin = login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
        LOGGER.info("Login Response " + responseLogin);
        JSONObject jsonObject = new JSONObject(responseLogin);
        String id = jsonObject.getString("id");
        String userId = jsonObject.getString("userId");

        String requestStr = "{\r\n" + "              \"type\": \"voterid\",\r\n"
            + "              \"email\": \"admin@signzy.com\",\r\n"
            + "              \"callbackUrl\": \"https://prebuild.com/system\",\r\n"
            + "              \"images\": [\r\n"
            + "                \"https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80\"\r\n"
            + "              ]\r\n" + "            }";

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestStr);
        Request request = new Request.Builder()
            .url(GSTServiceImpl.BASE_URL + userId + "/identities").method("POST", body)
            .addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();
        Response response = client.newCall(request).execute();
        String finalResponse = response.body().string();
        LOGGER.info("Identity Response " + finalResponse);
        JSONObject jsonObjectIdentity = new JSONObject(finalResponse);
        String idIdentity = jsonObjectIdentity.getString("id");
        String accessTokenIdentity = jsonObjectIdentity.getString("accessToken");

        LOGGER.info("Inside Identity idIdentity " + idIdentity);
        LOGGER.info("Inside Identity authorization " + accessTokenIdentity);

        map.put("accessToken", accessTokenIdentity);
        map.put("authorization", id);
        map.put("idIdentity", idIdentity);
        return map;
    }

    public Map<String, Object> verifyVoter(String epicNumber, String voterName, String state) {
        Map<String, Object> map = new HashMap<>();
        try {
            String finalResponse = null;
            System.out
                .println("  voter : " + epicNumber + " voterName " + voterName + " state " + state);
            Map<String, Object> mapU = identityCardObjectForVoterId();
            String accessToken = (String) mapU.get("accessToken");
            String authorization = (String) mapU.get("authorization");
            String idIdentity = (String) mapU.get("idIdentity");

            String requstStr = "{\r\n" + "      \"service\":\"Identity\",\r\n"
                + "      \"itemId\":\"" + idIdentity + "\",\r\n" + "      \"accessToken\": \""
                + accessToken + "\",\r\n" + "      \"task\":\"verification\",\r\n"
                + "      \"essentials\":\r\n" + "      {\r\n" + "        \"epicNumber\":\""
                + epicNumber + "\",\r\n" + "        \"name\":\"" + voterName + "\",\r\n"
                + "        \"state\":\"" + state + "\"\r\n" + "      }\r\n" + "    }";

            // System.out.println("Json Request: "+requstStr);

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requstStr);
            Request request = new Request.Builder().url("https://signzy.tech/api/v2/snoops/")
                .method("POST", body).addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();

            LOGGER.info("Json Request: -----------------------" + requstStr);

            LOGGER.info("Request: -----------------------" + request);
            String results = response.body().string();
            LOGGER.info(" results -----------------------" + results);

            Object obj = null;
            JSONParser parser = new JSONParser();
            obj = parser.parse(results);
            org.json.simple.JSONObject jsonObjectt = (org.json.simple.JSONObject) obj;

            org.json.simple.JSONObject responseJson = (org.json.simple.JSONObject) jsonObjectt
                .get("response");

            if (responseJson != null) {
                org.json.simple.JSONObject resultJson = (org.json.simple.JSONObject) responseJson
                    .get("result");

                String message = (String) resultJson.get("message");
                boolean verification = (boolean) resultJson.get("verification");

                JSONObject object = new JSONObject();
                object.put("message", message);
                // object.put(ResponseMessage.DESCRIPTION, message);
                object.put("verification", verification);
                object.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

                finalResponse = object.toString();
            } else {
                org.json.simple.JSONObject error = (org.json.simple.JSONObject) jsonObjectt
                    .get("error");

                Long statusCode = (Long) error.get("statusCode");
                String message = (String) error.get("message");

                JSONObject object = new JSONObject();

                object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                object.put("statusCode", statusCode);
                // object.put(ResponseMessage.DESCRIPTION,message);
                object.put("message", message);
                finalResponse = object.toString();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            map = objectMapper.readValue(finalResponse, Map.class);
        } catch (Exception e) {
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map;
    }

    // ----------------------------Credit Score
    // Service-------------------------------------

    public Map<String, Object> creditBureauReport(
        CreditBureauReportRequest creditBureauReportRequest, String merchantTrxnRefId)
        throws Exception {

        LOGGER.info("Inside creditBureauReport ----------------------- ");

        String responseBody = null;

        Map<String, Object> map = new HashMap<>();

        String date = DateAndTime.formatDate1(creditBureauReportRequest.getDob());

        String jsonRequest = "\r\n{\r\n  \"consent\": true,\r\n  \"consent_purpose\": \"For bank verification purpose\",\r\n  \"reference_id\": \""
            + merchantTrxnRefId + "\",\r\n  \"name\": \"" + creditBureauReportRequest.getName()
            + "\",\r\n  \"date_of_birth\": \"" + date + "\",\r\n  \"address_type\": \""
            + creditBureauReportRequest.getAddressType() + "\",\r\n  \"address\": \""
            + creditBureauReportRequest.getAddress() + "\",\r\n  \"pincode\": \""
            + creditBureauReportRequest.getPincode() + "\",\r\n  \"mobile\": \""
            + creditBureauReportRequest.getMobile() + "\",\r\n  \"inquiry_purpose\": \""
            + creditBureauReportRequest.getInquiryPurpose() + "\",\r\n  \"document_type\": \""
            + creditBureauReportRequest.getDocumentType() + "\",\r\n  \"document_id\": \""
            + creditBureauReportRequest.getDocumentId() + "\"\r\n}\r\n";

        try {

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonRequest);

            Request request = new Request.Builder().url(CREDIT_BUREAU_URL).method("POST", body)
                .addHeader("accept", "application/json").addHeader("client_id", CLIENT_ID)
                .addHeader("client_secret", CLIENT_SECRET)
                .addHeader("content-type", "application/json")
                .addHeader("module_secret", MODULE_SECRET)
                .addHeader("provider_secret", PROVIDER_SECRET).build();
            Response response = client.newCall(request).execute();

            responseBody = response.body().string();

            LOGGER.info("responseBody : {}", responseBody);

            JSONObject apiResponse = new JSONObject(responseBody);
            String status = apiResponse.getString("status");

            if (status.equalsIgnoreCase("SUCCESS")) {

                String trxnRefId = apiResponse.getString("decentroTxnId");
                String message = apiResponse.getString("message");
                JSONObject data = apiResponse.getJSONObject("data");
                JSONObject cCRResponse = data.getJSONObject("cCRResponse");
                cCRResponse.remove("status");

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, message);
                map.put("merchantTrxnRefId", trxnRefId);
                map.put(ResponseMessage.DATA, cCRResponse.toMap());

            } else if (status.equalsIgnoreCase("FAILURE")) {

                String trxnRefId = apiResponse.getString("decentroTxnId");
                String message = apiResponse.getString("message");

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, message);
                map.put("merchantTrxnRefId", trxnRefId);
            }

        } catch (Exception e) {
            LOGGER.info("Inside catch block ----------------------- ");
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_FAILED);
            return map;
        }
        return map;
    }

    public Map<String, Object> fetchCustomerDetails(CustomerDataRequest customerDataRequest,
        String merchantTrxnRefId) {

        LOGGER.info("Inside fetchCustomerDetails ----------------------- ");

        String responseBody = null;

        Map<String, Object> map = new HashMap<>();

        try {

            String jsonRequest = "\r\n{\r\n  \"consent\": true,\r\n  \"reference_id\": \""
                + merchantTrxnRefId
                + "\",\r\n  \"consent_purpose\": \"Fetching Data for Testing\",\r\n  \"name\": \""
                + customerDataRequest.getName() + "\",\r\n  \"mobile\": \""
                + customerDataRequest.getMobile() + "\",\r\n  \"document_type\": \""
                + customerDataRequest.getDocumentType() + "\",\r\n  \"id_value\": \""
                + customerDataRequest.getId_value() + "\"\r\n}\r\n";

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonRequest);

            Request request = new Request.Builder().url(FETCH_CUSTOMER_DETAILS_URL)
                .method("POST", body).addHeader("accept", "application/json")
                .addHeader("client_id", CLIENT_ID).addHeader("client_secret", CLIENT_SECRET)
                .addHeader("content-type", "application/json")
                .addHeader("module_secret", MODULE_SECRET)
                .addHeader("provider_secret", PROVIDER_SECRET).build();

            Response response = client.newCall(request).execute();

            responseBody = response.body().string();

            LOGGER.info("responseBody : {}", responseBody);

            JSONObject apiResponse = new JSONObject(responseBody);
            String status = apiResponse.getString("status");

            if (status.equalsIgnoreCase("SUCCESS")) {

                String trxnRefId = apiResponse.getString("decentroTxnId");
                JSONObject data = apiResponse.getJSONObject("data");

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
                map.put("merchantTrxnRefId", trxnRefId);
                map.put(ResponseMessage.DATA, data.toMap());

            } else if (status.equalsIgnoreCase("FAILURE")) {

                String trxnRefId = apiResponse.getString("decentroTxnId");
                String message = apiResponse.getString("message");

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, message);
                map.put("merchantTrxnRefId", trxnRefId);
            }

        } catch (Exception e) {
            LOGGER.info("Inside catch block ----------------------- ");
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_FAILED);
            return map;
        }
        return map;
    }

    public Map<String, Object> fetchCreditScore(CustomerDataRequest customerDataRequest,
        String merchantTrxnRefId) {

        LOGGER.info("Inside fetchCreditScore ----------------------- ");

        String responseBody = null;

        Map<String, Object> map = new HashMap<>();

        try {

            String jsonRequest = "{\n" + "    \"InquiryPurpose\": \"16\",\n"
                + "    \"TransactionAmount\": \"100\",\n" + "    \"FirstName\": \""
                + customerDataRequest.getName() + "\",\n" + "    \"MiddleName\": \"\",\n"
                + "    \"LastName\": \"\",\n" + "    \"InquiryPhones\": [\n" + "        {\n"
                + "            \"seq\": \"1\",\n" + "            \"Number\": \""
                + customerDataRequest.getMobile() + "\",\n" + "            \"PhoneType\": [\n"
                + "                \"M\"\n" + "            ]\n" + "        }\n" + "    ],\n"
                + "    \"IDDetails\": [\n" + "        {\n" + "            \"seq\": \"1\",\n"
                + "            \"IDType\": \"t\",\n" + "            \"IDValue\": \""
                + customerDataRequest.getId_value() + "\"\n" + "        }\n" + "    ]\n" + "}";

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonRequest);
            Request request = new Request.Builder().url(FETCH_CREDIT_SCORE_URL).method(
                "POST", body
            ).addHeader("Content-Type", "application/json").addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKQU1CT1BBWSIsImlhdCI6MTcxMjgxMTEwMjY5NX0.RXA-3QzT3N2xKGxqWJUBOkTz7btocNheKBGIJ-37EiM"
            ).build();
            Response response = client.newCall(request).execute();

            responseBody = response.body().string();

            LOGGER.info("responseBody : {}", responseBody);

            JSONObject apiResponse = new JSONObject(responseBody);
            JSONObject CCRResponse = apiResponse.getJSONObject("CCRResponse");
            org.json.JSONArray CIRReportDataLst = CCRResponse.getJSONArray("CIRReportDataLst");
            JSONObject error = CIRReportDataLst.getJSONObject(0);
            if (!error.has("Error")) {
                //
                // String trxnRefId = apiResponse.getString("decentroTxnId");
                // JSONObject data = apiResponse.getJSONObject("data");
                //
                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
                map.put("merchantTrxnRefId", merchantTrxnRefId);
                map.put(ResponseMessage.DATA, apiResponse.toMap());
                //
            } else {
                //
                // String trxnRefId = apiResponse.getString("decentroTxnId");
                // org.json.JSONArray CIRReportDataLst =
                // CCRResponse.getJSONArray("CIRReportDataLst");
                // JSONObject index = CIRReportDataLst.getJSONObject(0);
                // JSONObject error = index.getJSONObject("Error");
                JSONObject index = error.getJSONObject("Error");
                String errorDesc = index.getString("ErrorDesc");

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, errorDesc);
                map.put("merchantTrxnRefId", merchantTrxnRefId);
            }

        } catch (Exception e) {
            LOGGER.info("Inside catch block ----------------------- ");
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_FAILED);
            return map;
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> rcValidate(String vehicleNumber) {

        Map<String, Object> map = new HashMap<>();

        String responseBody = null;

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(
                mediaType,
                "{\r\n    \"username\": \"" + USER_NAME + "\",\r\n    \"password\": \""
                    + USER_PASSWORD + "\"\r\n}"
            );

            LOGGER.info("Body ----------------------- " + body);
            Request request = new Request.Builder().url(LOGIN_URL).method("POST", body)
                .addHeader("Content-Type", "application/json").build();

            LOGGER.info("Request ----------------------- " + request);

            Response response = client.newCall(request).execute();

            responseBody = response.body().string();

            LOGGER.info("Response ----------------------- " + responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> readValue = objectMapper.readValue(responseBody, Map.class);

            String authorization = (String) readValue.get("id");
            String userId = (String) readValue.get("userId");

            String vehicleRegistrationsurl = "https://preproduction.signzy.tech/api/v2/patrons/"
                + userId + "/vehicleregistrations";

            LOGGER
                .info("vehicleRegistrationsurl ----------------------- " + vehicleRegistrationsurl);

            OkHttpClient client1 = new OkHttpClient().newBuilder().build();
            MediaType mediaType1 = MediaType.parse("application/json");
            RequestBody body1 = RequestBody.create(
                mediaType1,
                "{\r\n    \"task\": \"detailedSearch\",\r\n    \"essentials\": {\r\n        \"vehicleNumber\": \""
                    + vehicleNumber
                    + "\",\r\n        \"blacklistCheck\": \"true\"\r\n    },\r\n    \"signzyID\": \""
                    + SIGNZY_ID + "\"\r\n}"
            );

            LOGGER.info("vehicleRegistrationsrequest ----------------------- " + body1);
            Request request1 = new Request.Builder().url(vehicleRegistrationsurl)
                .method("POST", body1).addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json").build();
            Response response1 = client1.newCall(request1).execute();

            LOGGER.info("vehicleRegistrationsrequest ----------------------- " + request1);

            responseBody = response1.body().string();

            LOGGER.info("Response1 ----------------------- " + responseBody);

            int code = response1.code();

            LOGGER.info("code ----------------------- " + code);

            ObjectMapper objectMapper1 = new ObjectMapper();
            Map<String, Object> readValue1 = objectMapper1.readValue(responseBody, Map.class);

            if (code == 400 || code == 404 || code == 403) {

                Map<String, Object> error = (Map<String, Object>) readValue1.get("error");

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                map.put(ResponseMessage.DESCRIPTION, error.get("message"));
                return map;
            }

            Map<String, Object> result = (Map<String, Object>) readValue1.get("result");
            result.remove("status");
            LOGGER.info("result ----------------------- " + result);
            map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_SUCCESS);
            map.put("Data", result);

        } catch (IOException e) {
            LOGGER.info("Inside catch block ----------------------- ");
            LOGGER.error("IOException: {}", e);
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_FAILED);
            return map;
        }

        return map;

    }

    // ------Aaadhar Service---------------------------------

    public Map<String, Object> identityCardObject() throws Exception {

        Map<String, Object> map = new HashMap<>();

        String responseLogin = login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
        LOGGER.info("Login Response  {} ", responseLogin);
        JSONObject jsonObject = new JSONObject(responseLogin);
        String id = jsonObject.getString("id");
        String userId = jsonObject.getString("userId");

        String requestStr = "{\r\n" + "              \"type\": \"aadhaar\",\r\n"
            + "              \"email\": \"admin@signzy.com\",\r\n"
            + "              \"callbackUrl\": \"https://prebuild.com/system\",\r\n"
            + "              \"images\": [\r\n"
            + "                \"https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80\"\r\n"
            + "              ]\r\n" + "            }";

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestStr);
        Request request = new Request.Builder()
            .url(GSTServiceImpl.BASE_URL + userId + "/identities").method("POST", body)
            .addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();
        Response response = client.newCall(request).execute();
        String finalResponse = response.body().string();
        LOGGER.info("Identity Response " + finalResponse);
        JSONObject jsonObjectIdentity = new JSONObject(finalResponse);
        String idIdentity = jsonObjectIdentity.getString("id");
        String accessTokenIdentity = jsonObjectIdentity.getString("accessToken");

        LOGGER.info("Inside Identity idIdentity " + idIdentity);
        LOGGER.info("Inside Identity authorization " + accessTokenIdentity);

        map.put("accessToken", accessTokenIdentity);
        map.put("authorization", id);
        map.put("idIdentity", idIdentity);
        return map;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> verifyAadhaar(String uid) throws JsonProcessingException {

        Map<String, Object> map1 = new HashMap<>();
        String finalResponse = null;
        Map<String, Object> mapObject = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> map = new SignzyService().identityCardObject();
            String accessToken = (String) map.get("accessToken");
            String authorization = (String) map.get("authorization");
            String idIdentity = (String) map.get("idIdentity");

            LOGGER.info("Inside Verify Aadhaar accessToken " + accessToken);
            LOGGER.info("Inside Verify Aadhaar authorization " + authorization);
            LOGGER.info("Inside Verify Aadhaar idIdentity " + idIdentity);

            String requestStr = "            {\r\n" + "      \"service\":\"Identity\",\r\n"
                + "      \"itemId\":\"" + idIdentity + "\",\r\n"
                + "      \"task\":\"verifyAadhaar\",\r\n" + "      \"accessToken\":\"" + accessToken
                + "\",\r\n" + "      \"essentials\":{\r\n" + "          \"uid\":\"" + uid + "\"\r\n"
                + "        }\r\n" + "      }";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestStr, headers);
            ResponseEntity<String> responseEntity = restTemplate
                .exchange(AADHAAR_BASE_URL, HttpMethod.POST, entity, String.class);
            LOGGER.info("responseEntity: {}", responseEntity.getBody());
            finalResponse = responseEntity.getBody();

            LOGGER.info("Inside Verify Aadhaar finalResponse " + finalResponse);
            mapObject = objectMapper.readValue(finalResponse, Map.class);
            LOGGER.info("mapObject: {}", mapObject);

            Map<String, Object> resultMap = (Map<String, Object>) mapObject.get("response");
            LOGGER.info("resultMap: {} ", resultMap);
            LOGGER.info("resultMap: {} ", resultMap.get("result"));
            map1.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            map1.put(ResponseMessage.DESCRIPTION, "Verify Aadhaar details");
            map1.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            map1.put("result", resultMap.get("result"));

        } catch (HttpClientErrorException.BadRequest ex) {

            LOGGER.error("Exception: {}", ex);
            String responseBody = ex.getResponseBodyAsString();
            mapObject = objectMapper.readValue(responseBody, Map.class);
            Map<String, String> error = (Map<String, String>) mapObject.get("error");
            LOGGER.info("error: " + error);
            String message = error.get("message");
            LOGGER.info("message: " + message);
            if (message.contains("Task not supported".trim())) {
                message = ResponseMessage.CONTACT_TECH_SUPPORT;
            }
            map1.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map1.put(ResponseMessage.DESCRIPTION, message);
            map1.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }

        catch (NullPointerException e) {
            LOGGER.error("NullPointerException: {}", e);
            map1.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map1.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
            map1.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } catch (Exception e) {
            LOGGER.error("Exception: {}", e);
            map1.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map1.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            map1.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return map1;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> basicVerifyAadhaar(String uid) {
        Map<String, Object> responseMap = new HashMap<>();
        String finalResponse = null;

        try {
            Map<String, Object> map = new SignzyService().identityCardObject();
            String accessToken = (String) map.get("accessToken");
            String authorization = (String) map.get("authorization");
            String idIdentity = (String) map.get("idIdentity");

            String requestStr = "{\r\n" + "	\"service\": \"Identity\",\r\n" + "	\"itemId\": \""
                + idIdentity + "\",\r\n" + "	\"task\": \"basicVerifyAadhaar\",\r\n"
                + "	\"accessToken\": \"" + accessToken + "\",\r\n" + "	\"essentials\": {\r\n"
                + "		\"uid\": \"" + uid + "\"\r\n" + "	}\r\n" + "}";

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder().url(AADHAAR_BASE_URL).method("POST", body)
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json").build();

            LOGGER.info("requestStr: {}", requestStr);
            LOGGER.info("request: {}", request);
            Response response = client.newCall(request).execute();
            finalResponse = response.body().string();

            LOGGER.info("response: {}", finalResponse);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> readValue = mapper.readValue(finalResponse, Map.class);
            LOGGER.info("readValue: {}", readValue);

            Map<String, Object> responseJson = (Map<String, Object>) readValue.get("response");
            LOGGER.info("responseJson: {}", responseJson);
            if (responseJson != null) {
                Map<String, Object> result = (Map<String, Object>) responseJson.get("result");
                String aadhaarNumber = (String) result.get("aadhaarNumber");
                boolean value = (boolean) result.get("verified");
                String verified = String.valueOf(value);
                responseMap.put("aadhaarNumber", aadhaarNumber);
                responseMap.put("verified", verified);
                responseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                responseMap.put(ResponseMessage.DESCRIPTION, verified);
                responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

            } else {
                Map<String, Object> error = (Map<String, Object>) readValue.get("error");
                Long statusCode = ((Number) readValue.get("statusCode")).longValue();
                String message = (String) error.get("message");

                if (message.contains("Task not supported".trim())) {
                    message = ResponseMessage.CONTACT_TECH_SUPPORT;
                }

                responseMap.put("statusCode", statusCode);
                responseMap.put("message", message);
                responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                responseMap.put(ResponseMessage.DESCRIPTION, message);
                responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            }

        } catch (NullPointerException e) {
            LOGGER.error("error: {}", e);
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } catch (Exception e) {
            LOGGER.error("error: {}", e);
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap
                .put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

        }
        return responseMap;
    }

    // @SuppressWarnings("unchecked")
    // public Map<String, Object> createUrlForDigilocker() {
    // Map<String, Object> responseMap = new HashMap<>();
    // String finalResponse = null;
    // try {
    // String responseLogin = new GSTServiceImpl().login(GSTServiceImpl.USERNAME,
    // GSTServiceImpl.PASSWORD);
    // LOGGER.info("Login Response {} ", responseLogin);
    // JSONObject jsonObject = new JSONObject(responseLogin);
    // String id1 = jsonObject.getString("id");
    // String userId = jsonObject.getString("userId");
    //
    // String requestStr = "{\r\n" + " \"task\": \"url\",\r\n" + " \"essentials\": {\r\n"
    // + " \"signup\": \"true\"\r\n" + " }\r\n" + "}";
    // LOGGER.info(" requestStr{] : ", requestStr);
    //
    // OkHttpClient client = new OkHttpClient();
    // MediaType mediaType = MediaType.parse("application/json");
    // RequestBody body = RequestBody.create(mediaType, requestStr);
    // Request request = new Request.Builder().url("https://signzy.tech/api/v2/patrons/" +
    // userId + "/digilockers")
    // .post(body).addHeader("Accept", "application/json").addHeader("Authorization",
    // id1).build();
    //
    // Response response = client.newCall(request).execute();
    // finalResponse = response.body().string();
    // LOGGER.info(" finalResponse{} : ", finalResponse);
    //
    // ObjectMapper mapperObj = new ObjectMapper();
    // Map<String, String> responseObj = mapperObj.readValue(finalResponse, Map.class);
    // LOGGER.info("responseObj.get(\"essentials\"): {}", responseObj.get("essentials"));
    //
    // responseMap.put("essentials", responseObj.get("essentials"));
    // responseMap.put("id", responseObj.get("id"));
    // responseMap.put("patronId", responseObj.get("patronId"));
    // responseMap.put("task", responseObj.get("task"));
    // responseMap.put("result", responseObj.get("result"));
    // responseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
    // responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
    // responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
    //
    // } catch (JSONException e) {
    // LOGGER.error("error: {}", e);
    // responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
    // responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
    // responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
    // } catch (Exception e) {
    // LOGGER.error("error: {}", e);
    // responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
    // responseMap.put(ResponseMessage.DESCRIPTION,
    // ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
    // responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
    // }
    // return responseMap;
    // }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createUrlForDigilocker(String redirectUrl) {
        Map<String, Object> responseMap = new HashMap<>();
        String finalResponse = null;
        try {

            String responseLogin = new GSTServiceImpl()
                .login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            LOGGER.info("Login Response " + responseLogin);
            JSONObject jsonObject = new JSONObject(responseLogin);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");
            // String redirectUrl = "https://rekyc.fidypay.com/redirect/" + rId + "";

            // String requestStr = " {\r\n" + " \"task\": \"url\",\r\n" + " \"essentials\": {\r\n"
            // + " \"signup\": true,\r\n" + " \"redirectUrl\": \"" + redirectUrl + "\"\r\n" + "
            // }\r\n" + " }";

            String requestStr = " {\n" + "    \"task\": \"url\",\n" + "    \"essentials\": {\n"
                + "        \"signup\": true,\n" + "        \"redirectUrl\": \"" + redirectUrl
                + "\",\n" + "        \"redirectTime\": \"1\"\n" + "    }\n" + "}";

            LOGGER.info(" requestStr : " + requestStr);

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder()
                .url("https://signzy.tech/api/v2/patrons/" + userId + "/digilockers").post(body)
                .addHeader("Accept", "application/json").addHeader("Authorization", id).build();

            Response response = client.newCall(request).execute();
            finalResponse = response.body().string();

            LOGGER.info(" finalResponse{} : ", finalResponse);

            ObjectMapper mapperObj = new ObjectMapper();
            Map<String, String> responseObj = mapperObj.readValue(finalResponse, Map.class);
            LOGGER.info("responseObj.get(\"essentials\"): {}", responseObj.get("essentials"));

            responseMap.put("essentials", responseObj.get("essentials"));
            responseMap.put("id", responseObj.get("id"));
            responseMap.put("patronId", responseObj.get("patronId"));
            responseMap.put("task", responseObj.get("task"));
            responseMap.put("result", responseObj.get("result"));
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

        } catch (JSONException e) {
            LOGGER.error("error: {}", e);
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        } catch (Exception e) {
            LOGGER.error("error: {}", e);
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap
                .put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return responseMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> eAadhar(String requestId) {

        Map<String, Object> responseMap = new HashMap<>();
        String finalResponse = null;
        Map<String, Object> responseObject = null;

        try {
            String responseLogin = new GSTServiceImpl()
                .login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
            LOGGER.info("Login Response " + responseLogin);
            JSONObject jsonObject = new JSONObject(responseLogin);
            String id = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            String requestStr = "{\r\n" + "	\"task\": \"getEadhaar\",\r\n"
                + "	\"essentials\": {\r\n" + "		\"requestId\": \"" + requestId + "\"\r\n"
                + "	}\r\n" + "\r\n" + "}";
            LOGGER.info(" requestStr : " + requestStr);

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestStr);
            Request request = new Request.Builder()
                .url("https://signzy.tech/api/v2/patrons/" + userId + "/digilockers").post(body)
                .addHeader("Accept", "application/json").addHeader("Authorization", id).build();

            Response response = client.newCall(request).execute();
            finalResponse = response.body().string();
            LOGGER.info(" Response : {}", finalResponse);

            ObjectMapper mapperObj = new ObjectMapper();
            responseObject = mapperObj.readValue(finalResponse, Map.class);
            LOGGER.info("responseObject: {}", responseObject);
            Map<String, Object> result = (Map<String, Object>) responseObject.get("result");
            LOGGER.info("result: {}", result);
            result.remove("x509Data");
            LOGGER.info("result : {}", result);
            responseMap.put("uid", result.get("uid"));
            responseMap.put("address", result.get("address"));
            responseMap.put("gender", result.get("gender"));
            responseMap.put("dob", result.get("dob"));
            responseMap.put("name", result.get("name"));
            responseMap.put("photo", result.get("photo"));
            responseMap.put("splitAddress", result.get("splitAddress"));
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

        } catch (NullPointerException e) {
            LOGGER.error("error: {}", e);
            if (responseObject != null) {

                Map<String, Object> map = (Map<String, Object>) responseObject.get("error");
                String message = (String) map.get("message");

                if (message != null && message.contains("Task not supported".trim())) {
                    message = ResponseMessage.CONTACT_TECH_SUPPORT;
                    responseMap.put("message", message);
                    responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    responseMap.put(ResponseMessage.DESCRIPTION, message);
                    responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                    return responseMap;
                }

                String description = ResponseMessage.SERVICE_NOT_AVILABLE;

                if (message != null && message.contains("INVALID reqId".trim())) {
                    description = message;
                }
                responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                responseMap.put(ResponseMessage.DESCRIPTION, description);
                responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                responseMap.put("error", map);
            }
        } catch (Exception e) {
            LOGGER.error("error: {}", e);
            responseMap.put("error", responseObject != null ? responseObject.get("error") : "");
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            responseMap
                .put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
        }
        return responseMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDetails(String requestId) throws Exception {

        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> result = null;
        Map<String, Object> responseObject = null;

        String responseLogin = new GSTServiceImpl()
            .login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
        LOGGER.info("Login Response " + responseLogin);
        JSONObject jsonObject = new JSONObject(responseLogin);
        String id = jsonObject.getString("id");
        String userId = jsonObject.getString("userId");

        String requestStr = "{\r\n" + "	\"task\": \"getDetails\",\r\n" + "	\"essentials\": {\r\n"
            + "		\"requestId\": \"" + requestId + "\"\r\n" + "	}\r\n" + "\r\n" + "}";
        LOGGER.info(" requestStr : " + requestStr);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestStr);
        Request request = new Request.Builder()
            .url("https://signzy.tech/api/v2/patrons/" + userId + "/digilockers").post(body)
            .addHeader("Accept", "application/json").addHeader("Authorization", id).build();

        Response response = client.newCall(request).execute();
        String finalResponse = response.body().string();
        LOGGER.info(" Response : " + finalResponse);
        ObjectMapper objMapper = new ObjectMapper();

        responseObject = objMapper.readValue(finalResponse, Map.class);
        LOGGER.info("responseObject : {}", responseObject);
        result = (Map<String, Object>) responseObject.get("result");
        LOGGER.info("result : {}", result);
        if (result != null) {
            responseMap.put("essentials", responseObject.get("essentials"));
            responseMap.put("id", responseObject.get("id"));
            responseMap.put("patronId", responseObject.get("patronId"));
            responseMap.put("task", responseObject.get("task"));
            responseMap.put("result", result);
            responseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
            responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
        } else {
            Map<String, Object> map = (Map<String, Object>) responseObject.get("error");
            String message = (String) map.get("message");

            if (message != null && message.contains("Task not supported".trim())) {
                message = ResponseMessage.CONTACT_TECH_SUPPORT;
                responseMap.put("message", message);
                responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                responseMap.put(ResponseMessage.DESCRIPTION, message);
                return responseMap;
            }

            responseMap.put("error", map);
            responseMap.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            responseMap.put(ResponseMessage.DESCRIPTION, message);
        }
        return responseMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> generateOtp(String aadhaarNumber, long merchantId)
        throws EkycProviderException {
        Map<String, Object> map = new HashMap<>();

        String responseBody = "";
        Map<String, Object> mapResponse = null;
        GenerateOtpRequest generateotprequest = new GenerateOtpRequest();
        Timestamp trxnDate = null;
        try {
            trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
        } catch (ParseException ex) {
            // Such an error may never arise in practice
            // but log & proceed seems the only meaningful thing to do if it ever happens
            LOGGER.error(
                "Error converting IST time to java.sql.Timestamp: {}", ex.getLocalizedMessage(), ex
            );
        }
        generateotprequest.setAadhaar_number(aadhaarNumber);
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, new Gson().toJson(generateotprequest));
            LOGGER.info("request : {}", body);

            Request request = new Request.Builder()
                .url(DecentroUtils.DECENTRO_API_BASE_URL + "v2/kyc/aadhaar/otp")
                .method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
                .addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
                .addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
                .addHeader("Content-Type", "application/json").build();

            LOGGER.info("Client-Id: " + DecentroUtils.DECENTRO_CLIENT_ID);
            LOGGER.info("Client-Secret: " + DecentroUtils.DECENTRO_CLIENT_SECRET);

            LOGGER.info("body: " + new Gson().toJson(generateotprequest));

            LOGGER.info("request: " + request);

            Response response = client.newCall(request).execute();
            responseBody = response.body().string();

            LOGGER.info("responseBody: " + responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            mapResponse = objectMapper.readValue(responseBody, Map.class);
            LOGGER.info("mapResponse: " + mapResponse);
            String result = (String) mapResponse.get("status");
            LOGGER.info("result: " + result);
            String message = (String) mapResponse.get("message");
            LOGGER.info("message: " + message);
            String merchantTxnId = (String) mapResponse.get("decentroTxnId");
            LOGGER.info("merchantTxnId: " + merchantTxnId);

            if (result != null && result.contains("SUCCESS")) {
                EkycRequest ekycRequest = ekycRequestRepository
                    .save(new EkycRequest(merchantId, merchantTxnId, merchantTxnId, trxnDate));
                LOGGER.info("ekycRequest: {}", ekycRequest);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, message);
                map.put("merchantTxnRefId", merchantTxnId);

            } else if (message.contains("Your IP address is not allowed")) {
                map.put("message", message);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONTACT_TECH_SUPPORT);
            } else if (message.contains("No response received from the underlying provider")
                || message.contains("Task not supported")) {
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONTACT_TECH_SUPPORT);
                map.put("merchantTxnRefId", merchantTxnId);
            } else {
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, message);
                map.put("merchantTxnRefId", merchantTxnId);

            }
            return map;
        } catch (JSONException e) {
            LOGGER.error("JSONException: {}", e);
        } catch (IOException e) {
            LOGGER.error("IOException: {}", e);
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> validateOtp(ValidateOtp validateotp) {

        Map<String, Object> map = new HashMap<>();

        String responseBody = "";
        Map<String, Object> responseObject = null;

        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, new Gson().toJson(validateotp));

            Request request = new Request.Builder()
                .url(DecentroUtils.DECENTRO_API_BASE_URL + "v2/kyc/aadhaar_connect/otp/validate")
                .method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
                .addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
                .addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
                .addHeader("Content-Type", "application/json").build();

            Response response = client.newCall(request).execute();
            responseBody = response.body().string();

            LOGGER.info("responseBody: " + responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            responseObject = objectMapper.readValue(responseBody, Map.class);

            String result = (String) responseObject.get("status");
            String message = (String) responseObject.get("message");

            if (result != null && result.contains("SUCCESS")) {
                Map<String, Object> mapData = (Map<String, Object>) responseObject.get("data");

                String aadhaarReferenceNumber = (String) mapData.get("aadhaarReferenceNumber");

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, message);
                map.put("merchantReferenceNumber", aadhaarReferenceNumber);
                map.put("merchantProofOfIdentity", mapData.get("proofOfIdentity"));
                map.put("merchantProofOfAddress", mapData.get("proofOfAddress"));

            } else {
                if (message.contains("Your IP address is not allowed")) {
                    map.put("message", message);
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONTACT_TECH_SUPPORT);
                } else if (message.contains("The provider for this session was not found.")
                    || message.contains("Task not supported")) {

                    String merchantTxnId = (String) responseObject.get("decentroTxnId");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(
                        ResponseMessage.DESCRIPTION,
                        "The session has been expired or maybe you are putting the wrong OTP. Please generate new OTP and try again."
                    );
                    map.put("merchantTxnRefId", merchantTxnId);

                } else if (message.contains("UIDAI responded with an unexpected response")) {
                    String merchantTxnId = (String) responseObject.get("decentroTxnId");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(
                        ResponseMessage.DESCRIPTION,
                        "Maybe you are putting the wrong OTP. Please generate new OTP and try again."
                    );
                    map.put("merchantTxnRefId", merchantTxnId);
                } else if (message.contains("No response received from the underlying provider")) {
                    String merchantTxnId = (String) responseObject.get("decentroTxnId");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONTACT_TECH_SUPPORT);
                    map.put("merchantTxnRefId", merchantTxnId);
                } else {
                    String merchantTxnId = (String) responseObject.get("decentroTxnId");
                    map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(ResponseMessage.DESCRIPTION, message);
                    map.put("merchantTxnRefId", merchantTxnId);
                }

            }
        } catch (JSONException e) {
            LOGGER.info("JSONException: {}", e);
        }

        catch (IOException e) {
            LOGGER.info("IOException: {}", e);
        }

        return map;
    }

    @SuppressWarnings({
        "unchecked"
    })
    public Map<String, Object> validate(Validate validate) {

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> mapResponse = null;
        String responseBody = "";
        String message = "";
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, new Gson().toJson(validate));

            LOGGER.info("Body ----------------------- " + new Gson().toJson(validate));

            Request request = new Request.Builder()
                .url(DecentroUtils.DECENTRO_API_BASE_URL + "kyc/public_registry/validate")
                .method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
                .addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
                .addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
                .addHeader("Content-Type", "application/json").build();

            LOGGER.info("request ----------------------- " + request);

            Response response = client.newCall(request).execute();
            responseBody = response.body().string();

            LOGGER.info("responseBody ----------------------- " + responseBody);

            if (responseBody == null || responseBody.isEmpty()) {
                map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                map.put(
                    ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION
                );
                return map;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            mapResponse = objectMapper.readValue(responseBody, Map.class);

            // message = (String) mapResponse.get("message");

            try {
                message = (String) mapResponse.get("message");
            } catch (Exception e) {
                message = ResponseMessage.DATA_NOT_FOUND;
            }

            if (message == null) {
                message = ResponseMessage.DATA_NOT_FOUND;
            }

            if (message != null && message.equals("Your IP address is not allowed")) {
                map.put("message", message);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONTACT_TECH_SUPPORT);
                return map;
            }

            String kycStatus = (String) mapResponse.get("kycStatus");

            if (validate.getDocument_type().equals("DRIVING_LICENSE")
                && kycStatus.equals("UNKNOWN")) {

                Map<String, Object> error = (Map<String, Object>) mapResponse.get("error");

                try {
                    LOGGER.info("Inside try: ");
                    message = (String) error.get("message");
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("Inside catch: ");
                    message = ResponseMessage.DATA_NOT_FOUND;
                }

                if (message == null) {
                    message = ResponseMessage.DATA_NOT_FOUND;
                }

                if (message.contains("Task not supported")) {
                    message = "Task not supported";
                }

                LOGGER.info("message: " + message);

                String merchantTxnId = (String) error.get("decentroTxnId");

                // if(merchantTxnId==null) {
                // merchantTxnId="NA";
                // }

                if (message.contains("Data not found.")) {
                    map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                    map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                    map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
                    return map;
                }

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
                map.put("merchantTxnId", merchantTxnId);
                map.put(ResponseMessage.DESCRIPTION, message);
                return map;

            }

            if (kycStatus.equals("SUCCESS")) {
                Map<String, Object> kycResult = (Map<String, Object>) mapResponse.get("kycResult");
                kycResult.remove("status");
                kycResult.remove("dateOfBirth");
                String merchantTxnId = (String) mapResponse.get("decentroTxnId");
                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put("merchantKycResult", kycResult);
                map.put("merchantTxnId", merchantTxnId);
                map.put(
                    ResponseMessage.DESCRIPTION,
                    validate.getDocument_type() + " details retrived successfully."
                );
                return map;
            }

            if (kycStatus.equals("UNKNOWN") || kycStatus.equals("FAILURE")) {

                String merchantTxnId = (String) mapResponse.get("decentroTxnId");

                map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
                map.put("merchantTxnId", merchantTxnId);
                map.put(
                    ResponseMessage.DESCRIPTION,
                    "No records found for the given " + validate.getDocument_type() + " ID."
                );
                return map;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            String merchantTxnId = (String) mapResponse.get("decentroTxnId");
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, "Please enter valid documentIdNumber.");
            map.put("merchantTxnId", merchantTxnId);
            LOGGER.error("JSONException: {}", e);
        }

        catch (SocketTimeoutException e) {
            e.printStackTrace();
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, "Please try again.");
            LOGGER.error("SocketTimeoutException: {}", e);
        }

        catch (NullPointerException e) {
            e.printStackTrace();
            LOGGER.error("Exception: {}", e);
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
        }

        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Exception: {}", e);
            map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
        }

        return map;
    }

    public Map<String, Object> GSTINSearchByPanAndState(String pan) {
        Map<String, Object> map = new HashMap<>();
        try {

            String loginResponse = FaceMatcherAndImageLivenessService.login();

            LOGGER.info("Login : " + loginResponse);

            JSONObject jsonObject = new JSONObject(loginResponse);

            String authorization = jsonObject.getString("id");
            String userId = jsonObject.getString("userId");

            String searchByPanAndStateUrl = BASE_URL + userId + "/gstns";

            String apiRequest = "{\"task\":\"panSearch\",\"essentials\":{\"panNumber\":\"" + pan
                + "\",\"state\":\"\",\"email\":\"\"}}";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("Authorization", authorization);

            HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
            ResponseEntity<String> response = restTemplate
                .exchange(searchByPanAndStateUrl, HttpMethod.POST, entity, String.class);
            String apiResponse = response.getBody();

            LOGGER.info("apiResponse: {}", apiResponse);

            JSONObject resultJsonObject = new JSONObject(apiResponse);

            if (resultJsonObject.has("result")) {

                JSONObject result = resultJsonObject.getJSONObject("result");

                LOGGER.info("result ----------------------- {}", result);

                map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
                map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
                map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
                map.put(ResponseMessage.DATA, result.toMap());
            }

        } catch (HttpClientErrorException e) {
            String responeString = e.getResponseBodyAsString();
            JSONObject errorJsonObject = new JSONObject(responeString);

            JSONObject error = errorJsonObject.getJSONObject("error");

            LOGGER.info("error ----------------------- {}", error);

            String message = error.getString("message");
            map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
            map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
            map.put(ResponseMessage.DESCRIPTION, message);

        } catch (Exception e) {
            LOGGER.info("Exception: {}", e);
            map = ESignServices.setResponse(
                ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
                ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION
            );
        }
        return map;
    }

}
