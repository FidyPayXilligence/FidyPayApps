package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.dto.EkycWorkflowTableResponse;
import com.fidypay.entity.EkycUserTable;
import com.fidypay.entity.EkycWorkflow;
import com.fidypay.repo.EkycUserRepository;
import com.fidypay.repo.EkycWorkflowRepository;
import com.fidypay.request.APIRequest;
import com.fidypay.request.EkycServicesRequest;
import com.fidypay.request.EkycUpdateWorkflowRequest;
import com.fidypay.request.EkycWorkflowRequest;
import com.fidypay.request.EkycworkflowFilterRequest;
import com.fidypay.service.EkycWorkflowService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class EkycWorkflowTableServiceImpl extends JdbcDaoSupport implements EkycWorkflowService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EkycWorkflowTableServiceImpl.class);

	private static final String DAYS_REGEX = "^[0-9]{1,4}$";
	
	private static final String DAYS_VALIDATION = "0|90|180|360";

	@Autowired
	private EkycWorkflowRepository ekycworkflowtablerepository;
	
	@Autowired
	private KycTypeListServiceimpl kycTypeListServiceimpl;
	
	@Autowired
	private EkycUserRepository ekycUserRepository;
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	private EKYCService ekycService;
	
	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}
	
	@Override
	public Map<String, Object> saveWorkflowDetails(EkycWorkflowRequest request, long merchantId, String imageUrl) {

		LOGGER.info("Inside saveWorkflowDetails");

		Map<String, Object> response = new HashMap<>();

		String workflowName = request.getWorkflowName().toUpperCase();
		String days = request.getDays();
		String description = request.getDescription();
		String kycType = request.getKycType();

		if (!days.matches(DAYS_REGEX)) {
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, "Invalid days, Special characters and alphabets not allowed.");
			return response;
		}

		if (ekycworkflowtablerepository.existsByWorkflowNameAndMerchantIdAndIsDeleted(workflowName, merchantId, '0')) {
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.WORKFLOW_NAME_ALREADY_REGISTERED);
			return response;
		}

		UUID randomUUID = UUID.randomUUID();
		String workflowUniqueId = merchantId + "WFL"
				+ randomUUID.toString().replace("-", "").substring(0, 15).toUpperCase();

		try {

			EkycWorkflow entity = new EkycWorkflow();

			Timestamp dateTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

			if (request.getServices().isEmpty()) {
				response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, "Services can not be empty");
				return response;
			}
			
	

			
			//List<String> apiNames = new ArrayList<>();
                   JSONArray jsonArray = new JSONArray(request.getServices());
		        for (int i = 0; i < jsonArray.length(); i++) {
		            JSONObject obj = jsonArray.getJSONObject(i);
		            JSONArray apis = obj.getJSONArray("apis");
		            for (int j = 0; j < apis.length(); j++) {
		                JSONObject api = apis.getJSONObject(j);
		                String apiName = api.getString("apiname");
		                
		                
		            	if (!ekycService.checkServiceExistOrNot(merchantId, apiName)) {

		            		response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		    				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		    				response.put(ResponseMessage.DESCRIPTION, "To subscribe for this "+apiName+" service, get in touch with the FidyPay team.");
		    				return response;

		    			}

		                
		               // apiNames.add(apiName);
		            }
		        }

		   //     System.out.println("API Names: " + apiNames);

			
			
			
		    String serviceList = objectMapper.writeValueAsString(request.getServices());
			entity.setCreationDate(dateTime);
			entity.setMerchantId(merchantId);
			entity.setServices(serviceList);
			entity.setWorkflowName(workflowName);
			entity.setWorkflowUniqueId(workflowUniqueId);
			entity.setIsDeleted('0');
			entity.setDays(days);
			entity.setDescription(description);
			entity.setImageUrl(imageUrl);
			entity.setKycType(kycType);

			ekycworkflowtablerepository.save(entity);

			response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.SUCCESSFULLY_REGISTERED);

		} catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return response;
	}

	@Override
	public Map<String, Object> getAllWorkflowDetails(Integer pageNo, Integer pageSize, long merchantId)
			throws Exception {

		LOGGER.info("Inside getAllWorkflowDetails");
		Map<String, Object> response = new HashMap<>();

		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("CREATION_DATE").descending());

		Page<EkycWorkflow> page = null;

		page = ekycworkflowtablerepository.findByMerchantId(merchantId, paging);

		List<EkycWorkflow> workflowList = new ArrayList<>();
		List<EkycWorkflowTableResponse> workflowListResponse = new ArrayList<>();

		workflowList = page.getContent();

		LOGGER.info("workflowList size {}", workflowList.size());

		if (!workflowList.isEmpty()) {

			workflowList.forEach(workflowdetails -> {

				try {
					String creationDate = DateAndTime.formatDate(workflowdetails.getCreationDate().toString());
					EkycWorkflowTableResponse workflowResponse = new EkycWorkflowTableResponse();
					workflowResponse.setEkycWorkflowId(workflowdetails.getEkycWorkflowId());
					workflowResponse.setCreationDate(creationDate);
					workflowResponse.setMerchantId(workflowdetails.getMerchantId());
					workflowResponse.setWorkflowUniqueId(workflowdetails.getWorkflowUniqueId());
					workflowResponse.setWorkflowName(workflowdetails.getWorkflowName());
					workflowResponse.setServices(workflowdetails.getServices());
					workflowResponse.setDays(workflowdetails.getDays());
					workflowResponse.setDescription(workflowdetails.getDescription());
					workflowResponse.setKycType(workflowdetails.getKycType());
					
					List<EkycUserTable> list=ekycUserRepository.findByEkycWorkflowId(workflowdetails.getEkycWorkflowId(), merchantId);
					LOGGER.info("list: "+list.size());

					
					if(list.size()!=0) {
						workflowResponse.setIsAssigned("1");
					}
					else {
						workflowResponse.setIsAssigned("0");
					}
					
					
					workflowListResponse.add(workflowResponse);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
			response.put(ResponseMessage.DATA, workflowListResponse);
			response.put("currentPage", page.getNumber());
			response.put("totalItems", page.getTotalElements());
			response.put("totalPages", page.getTotalPages());
			return response;

		}

		response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		return response;
	}

	@Override
	public Map<String, Object> getById(long ekycWorkflowId, long merchantId) throws Exception {

		LOGGER.info("Inside getByWorkflowUniqueId");

		Map<String, Object> response = new HashMap<>();

		EkycWorkflow workflowDetails = ekycworkflowtablerepository.findByIdAndMerchantId(ekycWorkflowId, merchantId);

		if (workflowDetails != null) {

			EkycWorkflowTableResponse workflowResponse = new EkycWorkflowTableResponse();

			String creationDate = DateAndTime.formatDate(workflowDetails.getCreationDate().toString());
			workflowResponse.setEkycWorkflowId(workflowDetails.getEkycWorkflowId());
			workflowResponse.setCreationDate(creationDate);
			workflowResponse.setMerchantId(workflowDetails.getMerchantId());
			workflowResponse.setWorkflowUniqueId(workflowDetails.getWorkflowUniqueId());
			workflowResponse.setWorkflowName(workflowDetails.getWorkflowName());
			workflowResponse.setServices(workflowDetails.getServices());
			workflowResponse.setDays(workflowDetails.getDays());
			workflowResponse.setDescription(workflowDetails.getDescription());
			workflowResponse.setKycType(workflowDetails.getKycType());
			
			List<EkycUserTable> list=ekycUserRepository.findByEkycWorkflowId(ekycWorkflowId, merchantId);
			LOGGER.info("list: "+list.size());

			
			if(list.size()!=0) {
				workflowResponse.setIsAssigned("1");
			}
			else {
				workflowResponse.setIsAssigned("0");
			}
			

			response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
			response.put(ResponseMessage.DATA, workflowResponse);

			return response;
		}

		response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return response;
	}

	@Override
	public Map<String, Object> deleteById(long ekycWorkflowId, long merchantId) {

		LOGGER.info("Inside deleteByWorkflowUniqueId");

		Map<String, Object> response = new HashMap<>();

		EkycWorkflow workflowDetails = ekycworkflowtablerepository.findByIdAndMerchantId(ekycWorkflowId, merchantId);

		if (workflowDetails != null) {

			workflowDetails.setIsDeleted('1');

			ekycworkflowtablerepository.save(workflowDetails);

			response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DELETE);

			return response;
		}

		response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return response;
	}
	

	@Override
	public Map<String, Object> updateById(EkycUpdateWorkflowRequest ekycUpdateWorkflowRequest, long merchantId) {
		Map<String, Object> response = new HashMap<>();
		try {
    		LOGGER.info("Inside updateByWorkflowId");
    		
    		List<EkycUserTable> userTable=ekycUserRepository.findByEkycWorkflowId(ekycUpdateWorkflowRequest.getEkycWorkflowId(), merchantId);
    		
    		if(userTable.size()!=0) {
    			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
    			response.put(ResponseMessage.DESCRIPTION, "You can not update workflow, Workflow already assigned to user");
    			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
    			return response;
    		}
    		
    		LOGGER.info("Inside updateByWorkflowId");
    		
       	    String workflowName = ekycUpdateWorkflowRequest.getWorkflowName().toUpperCase();
    		long ekycWorkflowId = ekycUpdateWorkflowRequest.getEkycWorkflowId();
    		
    		
    		LOGGER.info("workflowName: "+workflowName);
    		LOGGER.info("ekycWorkflowId:"+ekycWorkflowId);

    		EkycWorkflow workflowDetails = ekycworkflowtablerepository.findByIdAndMerchantId(ekycWorkflowId, merchantId);

    		if (workflowDetails != null) {
    			LOGGER.info("------Inside ekycWorkflow--------:");

    			if (workflowName != null && !workflowName.equals("")) {

    				LOGGER.info("Inside update workflowName.");

    				if (ekycworkflowtablerepository.existsByWorkflowNameAndMerchantIdAndIsDeleted(workflowName, merchantId,'0')) {
    					response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
    					response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
    					response.put(ResponseMessage.DESCRIPTION, ResponseMessage.WORKFLOW_NAME_ALREADY_REGISTERED);
    					return response;
    				}

    				workflowDetails.setWorkflowName(workflowName);
    			}

    			if (ekycUpdateWorkflowRequest.getDays() != null && !ekycUpdateWorkflowRequest.getDays().equals("")) {

    				LOGGER.info("Inside getDays.");

//    				if (!ekycUpdateWorkflowRequest.getDays().matches(DAYS_VALIDATION)) {
//    					response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//    					response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//    					response.put(ResponseMessage.DESCRIPTION,
//    							"Please Pass 0,90,180 and 360 on days parameter");
//    					return response;
//    				}

    				workflowDetails.setDays(ekycUpdateWorkflowRequest.getDays());
    			}

    			if (ekycUpdateWorkflowRequest.getDescription() != null
    					&& !ekycUpdateWorkflowRequest.getDescription().equals("")) {

    				LOGGER.info("Inside getDescription.");

    				if (ekycUpdateWorkflowRequest.getDescription().length() < 2
    						|| ekycUpdateWorkflowRequest.getDescription().length() > 150) {
    					response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
    					response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
    					response.put(ResponseMessage.DESCRIPTION, "description size should be 2 to 150.");
    					return response;
    				}

    				workflowDetails.setDescription(ekycUpdateWorkflowRequest.getDescription());
    			}
    			
    			
    			if(!ekycUpdateWorkflowRequest.getServices().isEmpty()) {
    				
    				LOGGER.info("Inside getServices.");
    				ObjectMapper objectMapper = new ObjectMapper();
    				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    				
    				String serviceList = objectMapper.writeValueAsString(ekycUpdateWorkflowRequest.getServices());
    				
    				workflowDetails.setServices(serviceList);
    			}
    			

    			ekycworkflowtablerepository.save(workflowDetails);

    			response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
    			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
    			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATED);

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
	public Map<String, Object> findAllWorkflowName(long merchantId) throws Exception {
          LOGGER.info("Inside findAllWorkflowName");
           Map<String, Object> map = new HashMap<>();

		List<EkycWorkflow> list = ekycworkflowtablerepository.findByMerchantId(merchantId);

		if (list.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			return map;
		}

		List<EkycWorkflowTableResponse> workflowListResponse = new ArrayList<>();
		for (EkycWorkflow workflowdetails : list) {
			EkycWorkflowTableResponse workflowResponse = new EkycWorkflowTableResponse();
			
			String date= DateAndTime.formatDate(workflowdetails.getCreationDate().toString());
			
			workflowResponse.setEkycWorkflowId(workflowdetails.getEkycWorkflowId());
			workflowResponse.setWorkflowName(workflowdetails.getWorkflowName());
		
			workflowResponse.setCreationDate(date);
			workflowResponse.setDays(workflowdetails.getDays());
			workflowResponse.setDescription(workflowdetails.getDescription());
			workflowResponse.setKycType(workflowdetails.getKycType());
			workflowResponse.setMerchantId(workflowdetails.getMerchantId());
			workflowResponse.setServices(workflowdetails.getServices());
			workflowResponse.setWorkflowUniqueId(workflowdetails.getWorkflowUniqueId());
			
			workflowListResponse.add(workflowResponse);
		}
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
		map.put(ResponseMessage.DATA, workflowListResponse);
		return map;
	}

	public Map<String, Object> getKycTypeList(Long merchantId) {
        LOGGER.info("Inside getKycTypeList");
       Map<String, Object> map = new HashMap<>();
      try {
		 String res = kycTypeListServiceimpl.kycTypeList(merchantId);
		 LOGGER.info("res: "+res);
		 ObjectMapper objectMapper = new ObjectMapper();
		 map = objectMapper.readValue(res, HashMap.class);
		 } catch (Exception e) {
			 e.printStackTrace();
		 map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
		 map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		 map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		 map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		 }
		 return map;
		 }

	@Override
	public Map<String, Object> getByWorkflowName(String ekycWorkflowName, long merchantId) throws Exception {

		LOGGER.info("Inside getByWorkflowName");

		Map<String, Object> response = new HashMap<>();

		ekycWorkflowName = ekycWorkflowName.toUpperCase();

		LOGGER.info("ekycWorkflowName--->{}", ekycWorkflowName);

		EkycWorkflow workflowDetails = ekycworkflowtablerepository.findByWorkflowNameAndMerchantId(ekycWorkflowName,
				merchantId);

		if (workflowDetails != null) {

			EkycWorkflowTableResponse workflowResponse = new EkycWorkflowTableResponse();
			

			String creationDate = DateAndTime.formatDate(workflowDetails.getCreationDate().toString());
			workflowResponse.setEkycWorkflowId(workflowDetails.getEkycWorkflowId());
			workflowResponse.setCreationDate(creationDate);
			workflowResponse.setMerchantId(workflowDetails.getMerchantId());
			workflowResponse.setWorkflowUniqueId(workflowDetails.getWorkflowUniqueId());
			workflowResponse.setWorkflowName(workflowDetails.getWorkflowName());
			workflowResponse.setServices(workflowDetails.getServices());
			workflowResponse.setDays(workflowDetails.getDays());
			workflowResponse.setDescription(workflowDetails.getDescription());
			workflowResponse.setKycType(workflowDetails.getKycType());

			response.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
			response.put(ResponseMessage.DATA, workflowResponse);

			return response;
		}

		response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return response;
	}

	@Override
	public Map<String, Object> searchEkycworkflowByFilter(@Valid EkycworkflowFilterRequest ekycworkflowFilterRequest,
			long merchantId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String query = "SELECT EKYC_WORKFLOW_ID,CREATION_DATE,IS_DELETED,MERCHANT_ID,SERVICES,WORKFLOW_NAME,WORKFLOW_UNIQUE_ID,DAYS,DESCRIPTION,IMAGE_URL,KYC_TYPE FROM EKYC_WORKFLOW";
			List<String> conditions = new ArrayList<>();

		
			if (ekycworkflowFilterRequest.getWorkflowUniqueId() != null && !ekycworkflowFilterRequest.getWorkflowUniqueId().equals("")
					&& !ekycworkflowFilterRequest.getWorkflowUniqueId().isEmpty()) {
				conditions.add("WORKFLOW_UNIQUE_ID = '" + ekycworkflowFilterRequest.getWorkflowUniqueId() + "'");

			}

			if (ekycworkflowFilterRequest.getEkycWorkflowId() != null && ekycworkflowFilterRequest.getEkycWorkflowId() != 0) {
				conditions.add("EKYC_WORKFLOW_ID  ='" + ekycworkflowFilterRequest.getEkycWorkflowId() + "'");

			}
			
			query = query + " where IS_DELETED='0' and " + conditions.stream().collect(Collectors.joining(" and "));
			LOGGER.info(query);
			List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query);
			List<EkycWorkflowTableResponse> result = new ArrayList<EkycWorkflowTableResponse>();
			
			
			if (rows.stream().count() > 0) {

				for (Map<String, Object> row : rows) {
					EkycWorkflowTableResponse workflowResponse = new EkycWorkflowTableResponse();
					
					LocalDateTime  localDateTime1=(LocalDateTime) row.get("CREATION_DATE");
					
					 Timestamp date = Timestamp.valueOf(localDateTime1);
					
					
					Long ekycWorkflowId=(Long) row.get("EKYC_WORKFLOW_ID");
					
					workflowResponse.setEkycWorkflowId(ekycWorkflowId);
					workflowResponse.setWorkflowName((String) row.get("WORKFLOW_NAME"));
				    workflowResponse.setCreationDate(DateAndTime.dateFormatForPartner3(date.toString()));
					workflowResponse.setDays((String) row.get("DAYS"));
					workflowResponse.setDescription((String) row.get("DESCRIPTION"));
					workflowResponse.setKycType((String) row.get("KYC_TYPE"));
					workflowResponse.setMerchantId((Long) row.get("MERCHANT_ID"));
					workflowResponse.setServices((String) row.get("SERVICES"));
					workflowResponse.setWorkflowUniqueId((String) row.get("WORKFLOW_UNIQUE_ID"));	
					
					

					List<EkycUserTable> list=ekycUserRepository.findByEkycWorkflowId(ekycWorkflowId, merchantId);
					LOGGER.info("list: "+list.size());

					
					if(list.size()!=0) {
						workflowResponse.setIsAssigned("1");
					}
					else {
						workflowResponse.setIsAssigned("0");
					}
					
					
					result.add(workflowResponse);
				}
				
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				response.put("code", ResponseMessage.SUCCESS);
				response.put("data", result);
			}
			
			else {
				response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return response;
	}

}
