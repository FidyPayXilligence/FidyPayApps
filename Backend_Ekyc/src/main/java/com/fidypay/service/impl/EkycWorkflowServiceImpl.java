package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.dto.EkycWorkflowServiceResponse;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycUserTable;
import com.fidypay.entity.EkycWorkflow;
import com.fidypay.entity.EkycWorkflowService;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.EkycUserRepository;
import com.fidypay.repo.EkycWorkflowRepository;
import com.fidypay.repo.EkycWorkflowServiceRepository;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.EkycWorkflowServiceRequest;
import com.fidypay.request.KycFailedRequest;
import com.fidypay.service.EkycWorkflowServiceSer;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.constants.URLGenerater;
import com.fidypay.utils.ex.AmazonClient;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.EMailService;
import com.fidypay.utils.ex.SMSAPIImpl;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class EkycWorkflowServiceImpl implements EkycWorkflowServiceSer {

	private static final Logger LOGGER = LoggerFactory.getLogger(EkycWorkflowServiceImpl.class);

	public static final String USERNAME = "fidypay_prod";
	public static final String PASSWORD = "u4wwVbDFy2xYMrbRU8xs";
	public static final String BASE_URL = "https://signzy.tech/api/v2/patrons/";

	@Autowired
	private EkycWorkflowServiceRepository ekycWorkflowServiceRepository;

	@Autowired
	private EkycUserRepository ekycUserRepository;

	@Autowired
	private AmazonClient amazonClient;

	@Autowired
	private EkycWorkflowRepository ekycWorkflowRepository;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;
	
	@Autowired
	private EMailService apiImpl;
	
	@Override
	public Map<String, Object> findByEkycUserId(long ekycUserId, long merchantId) {

		LOGGER.info("Inside findByEkycUserId");

		Map<String, Object> response = new HashMap<String, Object>();

		List<EkycWorkflowService> workflowServiceList = ekycWorkflowServiceRepository.findByEkycUserId(ekycUserId,
				merchantId);

		List<EkycWorkflowServiceResponse> serviceList = new ArrayList<EkycWorkflowServiceResponse>();

		if (!workflowServiceList.isEmpty()) {

			workflowServiceList.forEach(responseData -> {

				try {
					String date = DateAndTime.formatDate(responseData.getCreationDate().toString());

					EkycWorkflowServiceResponse serviceResponse = new EkycWorkflowServiceResponse();
					serviceResponse.setEkycWorkflowServiceId(responseData.getEkycWorkflowServiceId());
					serviceResponse.setCreationDate(date);
					serviceResponse.setMerchantId(responseData.getMerchantId());
					serviceResponse.setIsVerified(responseData.getIsVerified());
					serviceResponse.setEkycWorkflowId(responseData.getEkycWorkflowId());
					serviceResponse.setEkycUserId(responseData.getEkycUserId());
					serviceResponse.setDocumentResponse(responseData.getDocumentResponse());
					serviceResponse.setServiceId(responseData.getServiceId());
					serviceResponse.setServiceName(responseData.getServiceName());
					serviceResponse.setDocumentId(responseData.getDocumentId());
					serviceResponse.setDocumentRequest(responseData.getDocumentRequest());
					serviceResponse.setServiceUniqueId(responseData.getServiceUniqueId());
					serviceResponse.setDocumentJson(responseData.getDocumentJSon());
					serviceResponse.setTitle(responseData.getTitle());
					serviceResponse.setRejectReason(responseData.getRejectReason());
					
					serviceList.add(serviceResponse);

				} catch (Exception e) {
					e.printStackTrace();
				}

			});

			response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
			response.put(ResponseMessage.DATA, serviceList);

			return response;
		}

		response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return response;
	}

	@Override
	public Map<String, Object> documentUpload(String workflowToken, String userWorkflowToken, String fileName,
			@NotNull MultipartFile file, String optional, String optional2, MultipartFile file2) {

		LOGGER.info("Inside documentUpload");

		Map<String, Object> map = new HashMap<String, Object>();
		try {

			if (file != null) {
				String extension = FilenameUtils.getExtension(file.getOriginalFilename());
				LOGGER.info("extension " + extension);
				if (!extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("pdf")
						&& !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("png")) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FILE_FORMAT);
					return map;
				}
			}

			if (fileName.equals("AADHAAR") || fileName.equals("PAN") || fileName.equals("GST")
					|| fileName.equals("DRIVING LICENSE") || fileName.equals("VOTER ID") || fileName.equals("PASSPORT")
					|| fileName.equals("BANK ACCOUNT") || fileName.equals("REGISTRATION CERTIFICATE")
					|| fileName.equals("FACE MATCH") || fileName.equals("IMAGE LIVENESS")) {

				EkycWorkflow ekycWorkflow = ekycWorkflowRepository.findByWorkflowUniqueId(workflowToken);

				if (ekycWorkflow == null) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.WORKFLOW_UNIQUE_ID_NOT_EXIST);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				EkycUserTable userTable = ekycUserRepository.findByUserUniqueId(userWorkflowToken);

				if (userTable == null) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.USER_UNIQUE_ID_NOT_EXIST);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				Long ekycUserId = userTable.getEkycUserId();
				Long ekycWorkflowId = ekycWorkflow.getEkycWorkflowId();
				Long merchantId = userTable.getMerchantId();

				if (ekycWorkflowServiceRepository.existsByEkycWorkflowIdAndEkycUserIdAndMerchantIdAndDocumentId(
						ekycWorkflowId, ekycUserId, merchantId, fileName)) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DUPLICATE_FILENAME);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				UUID randomUUID = UUID.randomUUID();
				String serviceUniqueId = merchantId + "SU"
						+ randomUUID.toString().replaceAll("-", "").substring(0, 15).toUpperCase();

				String ifsc = "NA";
				String url = "NA";

				LOGGER.info("fileName " + fileName);

				switch (fileName) {
				case "GST":
					url = optional;
					LOGGER.info("Inside GST");
					break;
				case "BANK ACCOUNT":
					LOGGER.info("Bank Account");
					url = optional;
					ifsc = optional2;
					break;
				case "PASSPORT":
					LOGGER.info("Passport");
					url = optional;
					ifsc = optional2;
					break;
				case "AADHAAR":
					LOGGER.info("AADHAAR");
					url = amazonClient.uploadFile(file, merchantId, fileName);
					ifsc = amazonClient.uploadFile(file2, merchantId, fileName);
					break;
				case "FACE MATCH":
					LOGGER.info("FACE MATCH");
					url = amazonClient.uploadFile(file, merchantId, fileName);
					ifsc = amazonClient.uploadFile(file2, merchantId, fileName);
					break;
				case "PAN":
					LOGGER.info("PAN");
					url = amazonClient.uploadFile(file, merchantId, fileName);
					break;
				case "DRIVING LICENSE":
					LOGGER.info("DRIVING LICENSE");
					url = amazonClient.uploadFile(file, merchantId, fileName);
					break;
				case "VOTER ID":
					LOGGER.info("VOTER ID");
					url = optional;
					//ifsc = amazonClient.uploadFile(file2, merchantId, fileName);
					break;
				case "IMAGE LIVENESS":
					LOGGER.info("IMAGE LIVENESS");
					url = amazonClient.uploadFile(file, merchantId, fileName);
					ifsc = amazonClient.uploadFile(file2, merchantId, fileName);
					break;
				case "REGISTRATION CERTIFICATE":
					LOGGER.info("REGISTRATION CERTIFICATE");
					url = optional;
					//ifsc = amazonClient.uploadFile(file2, merchantId, fileName);
					break;
				default:
					LOGGER.info("INVALID DOCUMENT TYPE");
					break;
				}

				if (fileName.equalsIgnoreCase("GST")) {

				} else if (fileName.equalsIgnoreCase("BANK ACCOUNT")) {

				} else if (fileName.equalsIgnoreCase("PASSPORT")) {

				} else {
					LOGGER.info("Inside Else upload files");

					if (!file2.isEmpty()) {

					}
               }

				EkycWorkflowService ekycWorkflowService = new EkycWorkflowService();
				ekycWorkflowService.setCreationDate(Timestamp.valueOf(DateAndTime.getCurrentTimeInIST()));
				ekycWorkflowService.setDocumentId(fileName);
				ekycWorkflowService.setDocumentRequest(ifsc);
				ekycWorkflowService.setDocumentResponse(url);
				ekycWorkflowService.setEkycUserId(ekycUserId);
				ekycWorkflowService.setEkycWorkflowId(ekycWorkflowId);
				ekycWorkflowService.setIsVerified('0');
				ekycWorkflowService.setMerchantId(merchantId);
				ekycWorkflowService.setServiceId(0L);
				ekycWorkflowService.setServiceName(fileName);
				ekycWorkflowService.setServiceUniqueId(serviceUniqueId);
				ekycWorkflowService.setDocumentJSon("NA");
				ekycWorkflowService = ekycWorkflowServiceRepository.save(ekycWorkflowService);

				LOGGER.info("Update Ekyc Workflow service Table");

				userTable.setIsVerified('2');
				userTable = ekycUserRepository.save(userTable);
				LOGGER.info("Update User Table");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DOCUMENT_UPLOADED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.WORKFLOW_UNIQUE_ID, workflowToken);
				map.put(ResponseMessage.USER_UNIQUE_ID, userWorkflowToken);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DOCUMENT);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> updateById(long ekycWorkflowServiceId, long merchantId, String isVerified,String rejectReason) {
		Map<String, Object> response = new HashMap<String, Object>(); 
		try {
        	 
			LOGGER.info("Inside updateById");

			if (!isVerified.equalsIgnoreCase("1") && !isVerified.equalsIgnoreCase("0") && !isVerified.equalsIgnoreCase("2")) {
				response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, "Invalid isVerified Please enter 0,1 or 2");
				return response;
			}
			

			EkycWorkflowService workflowServiceDetails = ekycWorkflowServiceRepository
					.findByEkycWorkflowServiceIdAndMerchantId(ekycWorkflowServiceId, merchantId);

			
			
//			EkycUserTable ekycUserTable = ekycUserRepository.findByEkycUserId(workflowServiceDetails.getEkycUserId(), merchantId);
//			List<EkycWorkflowService> elist = ekycWorkflowServiceRepository.findByEkycUserId(ekycUserTable.getEkycUserId(), ekycUserTable.getMerchantId());
//			
//			if(elist.size()!= ekycUserTable.getServiceCount()) {
//				response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//				response.put(ResponseMessage.DESCRIPTION, "All document does not uploaded");
//				return response;
//			}
			
		
			if (workflowServiceDetails != null) {
				
				LOGGER.info("Inside workflowServiceDetails");

				char ch = isVerified.charAt(0);
				workflowServiceDetails.setIsVerified(ch);
				
				response.put(ResponseMessage.DESCRIPTION, "Document approved successfully!");
				
				if(isVerified.equalsIgnoreCase("2")) {
				workflowServiceDetails.setRejectReason(rejectReason);
				
				response.put(ResponseMessage.DESCRIPTION, "Document rejected successfully!");
				}
				
				
				ekycWorkflowServiceRepository.save(workflowServiceDetails);

				long ekycUserId = workflowServiceDetails.getEkycUserId();
				List<EkycWorkflowService> list = ekycWorkflowServiceRepository.findByEkycUserId(ekycUserId, merchantId);

				List<Character> listVerified = new ArrayList<Character>();
				List<Character> listRejected = new ArrayList<Character>();
				//List<String> rejectedResons = new ArrayList<String>();
				 //  JSONArray rejectedResons=new JSONArray();
				
				
				List<KycFailedRequest> rejectedResons=new ArrayList<>();
				for (EkycWorkflowService ekycWorkflowServices : list) {
					
					if(ekycWorkflowServices.getIsVerified()=='1') {
						listVerified.add(ekycWorkflowServices.getIsVerified());		
					}
					if(ekycWorkflowServices.getIsVerified()=='2') {
						listRejected.add(ekycWorkflowServices.getIsVerified());		
						
						
						
						
						String reason=ekycWorkflowServices.getRejectReason();
						String title=ekycWorkflowServices.getTitle();
						
						//rejectedResons.add(ekycWorkflowServices.getRejectReason());
						
						KycFailedRequest failedRequest= new KycFailedRequest();
						failedRequest.setServiceName(title);
						failedRequest.setFailedReason(reason);
						
						rejectedResons.add(failedRequest);
						
					}
					
				
				}
				LOGGER.info("listVerified " + listVerified.size());
				LOGGER.info("listRejected " + listRejected.size());
				LOGGER.info("rejectedResons " + rejectedResons.toString());
				
			EkycUserTable ekycUserTable = ekycUserRepository.findByEkycUserId(ekycUserId, merchantId);
				Integer serviceCount= Integer.valueOf(String.valueOf(ekycUserTable.getServiceCount()));
				LOGGER.info("serviceCount " + serviceCount);
				
				String currentDate= DateAndTime.getCurrentTimeInIST();
				 LocalDateTime dateTime = LocalDateTime.parse(currentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
			     String formattedDate = dateTime.format(formatter);
				
				String userName= Encryption.decString(ekycUserTable.getUserName());
				String email="NA";
				String userEmail=Encryption.decString(ekycUserTable.getUserEmail());
				String mobile=Encryption.decString(ekycUserTable.getUserMobile());
				
				MerchantInfo merchantInfo=merchantInfoRepository.findByMerchantId(merchantId);
				String businessName=Encryption.decString(merchantInfo.getMerchantBusinessName());
				String logo = merchantInfo.getImageUrl();

				if (!logo.equalsIgnoreCase("NA")) {
					logo = ResponseMessage.LOGO_URL + logo;
				}
				
				if(!userEmail.equals("NA")) {
					email=userEmail;
				}
				
				
				if (serviceCount==listVerified.size()) {
					ekycUserTable.setIsVerified('1');
					String res= apiImpl.sendEmailForKycVerificationSuccess(email, userName, logo, businessName, formattedDate);
					
					LOGGER.info("res " + res);
				}

				if (serviceCount==list.size() &&  listRejected.size()>=1) {
					ekycUserTable.setIsVerified('2');
					String link = ResponseMessage.LINK_WORKFLOW + ekycUserTable.getUserUniqueId();
					String res= apiImpl.sendEmailForKycVerificationFailed2(userName,email,link, logo, businessName, rejectedResons);
					 LOGGER.info("rejectedResons " + rejectedResons.toString());	 
					LOGGER.info("res failed: " + res);	
					
					
					
					// Message
					SMSAPIImpl impl = new SMSAPIImpl();
					URLGenerater urlGenerater = new URLGenerater();
					String url = urlGenerater.generateShortUrl(link);
					impl.workFlowLink(mobile, businessName, url);
				}
				   ekycUserRepository.save(ekycUserTable);
				
	//	   EkycUserTable ekycUserTable=ekycUserRepository.findByEkycUserId(workflowServiceDetails.getEkycUserId(), merchantId);
				
		         String sId=String.valueOf(workflowServiceDetails.getServiceId());
		         String sName=workflowServiceDetails.getServiceName();
		
		
				String serviceJson=ekycUserTable.getServicesJson();
				
				JSONArray jsonArray=new JSONArray(serviceJson);
				
				System.out.println("jsonArray: "+jsonArray);
				
				
				JSONArray array=new JSONArray();
				
				for(int i=0; i<jsonArray.length(); i++) {
					
					org.json.JSONObject aa = (org.json.JSONObject) jsonArray.get(i);
					
					String serviceName=aa.getString("serviceName");
					String description=aa.getString("description");
					String stepName=aa.getString("stepName");
					String flag=aa.getString("flag");
					String stepId=aa.getString("stepId");
					
					System.out.println("serviceName: "+serviceName);
					System.out.println("description: "+description);
					System.out.println("stepName: "+stepName);
					System.out.println("flag: "+flag);
					System.out.println("stepId: "+stepId);
					
					
					JSONObject jsonObject=new JSONObject();
					jsonObject.put("serviceName", serviceName);
					jsonObject.put("description", description);
					jsonObject.put("stepName", stepName);
					jsonObject.put("flag", flag);
					jsonObject.put("stepId", stepId);
					
					System.out.println("--------------------------------------------------- ");
					
					JSONArray jsonArray2=aa.getJSONArray("apis");
					
					System.out.println("jsonArray2: "+jsonArray2);
					
					JSONArray apis=new JSONArray();
					
					for(int j=0; j<jsonArray2.length(); j++) {
						
						org.json.JSONObject bb = (org.json.JSONObject) jsonArray2.get(j);
						
						String apiname=bb.getString("apiname");
						String serviceId=bb.getString("serviceId");
						String flag2=bb.getString("flag");
						String title=bb.getString("title");
						String charge=bb.getString("charge");
						
						System.out.println("apiname: "+apiname);
						System.out.println("serviceId: "+serviceId);
						System.out.println("flag2: "+flag2);
						System.out.println("title: "+title);
						System.out.println("charge: "+charge);
						
						JSONObject jsonObject2=new JSONObject();
						jsonObject2.put("apiname", apiname);
						jsonObject2.put("serviceId", serviceId);
						
                        if(sId.equals(serviceId)) {
                        	
                        	System.out.println("Inside flag update: ");
                        	System.out.println("isVerified: "+isVerified);
                        	
                        	jsonObject2.put("flag", isVerified);
                        	
                        	if(isVerified.equals("2")) {
                        		jsonObject.put("flag", "0");
                        	}
                        	
                        	if(isVerified.equals("1")) {
                        		jsonObject.put("flag", "1");
                        	}
                        	
                        	
						}
                        else {
                        	System.out.println("Inside else flag update: ");
                        	jsonObject2.put("flag", flag2);
                        }
						
						
						jsonObject2.put("title", title);
						jsonObject2.put("charge", charge);
						
						apis.put(jsonObject2);
						
						jsonObject.put("apis", apis);
						
					}
					
					array.put(jsonObject);
					System.out.println("============================================== ");
					
					}			
				ekycUserTable.setServicesJson(array.toString());
				ekycUserTable=ekycUserRepository.save(ekycUserTable);
				
				response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);


				return response;
			}
			else {
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
			return response;
	}

	@Override
	public Map<String, Object> getDocumentData(long ekycWorkflowServiceId, long merchantId) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			EkycWorkflowService ekycWorkflowService = ekycWorkflowServiceRepository.findById(ekycWorkflowServiceId)
					.get();

			if (ekycWorkflowService != null) {

				String documentData = ekycWorkflowService.getDocumentJSon();

				response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				response.put(ResponseMessage.DESCRIPTION, "Document Data");
				response.put("documentData", documentData);
			} else {
				response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				response.put(ResponseMessage.STATUS, "ekycWorkflowServiceId does not exist");
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			}

		} catch (Exception e) {
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, e.getMessage());
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		}
		return response;
	}

	@Override
	public Map<String, Object> GSTINSearch(String GSTIN) {

		LOGGER.info("Inside GSTINSearch");

		Map<String, Object> responseMap = new HashMap<String, Object>();

		String finalResponse = null;
		try {
			String loginResponse = new GSTServiceImpl().login(USERNAME, PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			String requestStr = " {\r\n" + " \"task\" : \"gstinSearch\",\r\n" + " \"essentials\": {\r\n"
					+ " \"gstin\": \"" + GSTIN + "\"\r\n" + " }\r\n" + " }\r\n" + "";

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(BASE_URL + userId + "/gstns").method("POST", body)
					.addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			finalResponse = response.body().string();

			LOGGER.info("finalResponse -" + finalResponse);

			JSONObject resp = new JSONObject(finalResponse);

			JSONObject result = resp.getJSONObject("result");

			responseMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			responseMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			responseMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
			responseMap.put(ResponseMessage.DETAILS, result.toMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> saveEkycWorkflowUserService(@Valid EkycWorkflowServiceRequest ekycWorkflowServiceRequest,
			long merchantId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			EkycWorkflowService ekycService=ekycWorkflowServiceRepository.findByEkycMerchantIdAndUserIdAndServiceId(merchantId,ekycWorkflowServiceRequest.getEkycUserId(),Long.valueOf(ekycWorkflowServiceRequest.getServiceId()));			
			if(ekycService!=null) {
				Timestamp dateTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
				
				ekycService.setCreationDate(dateTime);
				ekycService.setDocumentId("NA");
				ekycService.setDocumentJSon(ekycWorkflowServiceRequest.getApiResponse());
				ekycService.setDocumentRequest("NA");
				ekycService.setDocumentResponse(ekycWorkflowServiceRequest.getUrl());
				ekycService.setIsVerified('0');
				ekycService.setServiceName(ekycWorkflowServiceRequest.getServiceName());
				ekycService.setTitle(ekycWorkflowServiceRequest.getTitle());
				ekycService.setRejectReason("NA");
				ekycService.setReKyc('0');
				
				ekycService=ekycWorkflowServiceRepository.save(ekycService);
				
				
				EkycUserTable table=ekycUserRepository.findByEkycUserId(Long.valueOf(ekycWorkflowServiceRequest.getEkycUserId()), merchantId);
				table.setIsVerified('0');
				table=ekycUserRepository.save(table);
				
				
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "EKyc Workflow Service updated successfully");
				
				return map;
			}
			
			
			EkycUserTable userTable=ekycUserRepository.findByEkycUserId(Long.valueOf(ekycWorkflowServiceRequest.getEkycUserId()), merchantId);
			
			if(userTable!=null) {
				
				Timestamp dateTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
				
				EkycWorkflowService ekycWorkflowService=new EkycWorkflowService();
				ekycWorkflowService.setCreationDate(dateTime);
				ekycWorkflowService.setDocumentId("NA");
				ekycWorkflowService.setDocumentJSon(ekycWorkflowServiceRequest.getApiResponse());
				ekycWorkflowService.setDocumentRequest("NA");
				ekycWorkflowService.setDocumentResponse(ekycWorkflowServiceRequest.getUrl());
				ekycWorkflowService.setEkycUserId(userTable.getEkycUserId());
				ekycWorkflowService.setEkycWorkflowId(userTable.getEkycWorkflowId());
				ekycWorkflowService.setIsVerified('0');
				ekycWorkflowService.setMerchantId(merchantId);
				ekycWorkflowService.setServiceId(Long.valueOf(ekycWorkflowServiceRequest.getServiceId()));
				ekycWorkflowService.setServiceName(ekycWorkflowServiceRequest.getServiceName());
				ekycWorkflowService.setTitle(ekycWorkflowServiceRequest.getTitle());
				ekycWorkflowService.setRejectReason("NA");
				
				UUID randomUUID = UUID.randomUUID();
				String serviceUniqueId = merchantId + "SU"
						+ randomUUID.toString().replaceAll("-", "").substring(0, 15).toUpperCase();
				
				ekycWorkflowService.setServiceUniqueId(serviceUniqueId);
				
									
				ekycWorkflowService.setReKyc('0');
				
				ekycWorkflowService=ekycWorkflowServiceRepository.save(ekycWorkflowService);
				
				
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "EKyc Workflow Service saved successfully");
				}
			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "EKycUser does not exist");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

}
