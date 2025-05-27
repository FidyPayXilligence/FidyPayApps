package com.fidypay.service.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fidypay.dto.MerchantServiceDetailsPayload;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.MerchantSubMerchantInfo;
import com.fidypay.entity.MerchantType;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.CoreTransactionsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantSubMerchantInfoRepository;
import com.fidypay.repo.MerchantTypeRepository;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.PayinTransactionDetailRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.PassbookRequest;
import com.fidypay.request.TransactionsReportRequest;
import com.fidypay.response.MerchantServicesPayload;
import com.fidypay.response.PassbookPayload;
import com.fidypay.response.ServiceDetailPayload;
import com.fidypay.response.SubMerchantPayload;
import com.fidypay.response.TransactionsReportPayLoad;
import com.fidypay.service.ServiceInfoService;
import com.fidypay.utils.constants.ResponseMessage;

@Service
public class ServiceInfoServiceImpl implements ServiceInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInfoServiceImpl.class);

	@Autowired
	private CoreTransactionsRepository coreTransactionsRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantsRepository merchantRepository;

	@Autowired
	private MerchantSubMerchantInfoRepository merchantSubMerchantInfoRepository;

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;
	
	@Autowired
	private PayinTransactionDetailRepository payinTransactionDetailRepository;
	
	@Autowired
	private MerchantServiceRepository merchantServiceRepository;
	
	@Autowired
	private MerchantTypeRepository merchantTypeRepository;

	private Object[] appendValue(Object[] obj, Object newObj) {

		ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
		temp.add(newObj);
		return temp.toArray();

	}

	@Override
	public Map<String, Object> getSubMerchantVpaList(String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<SubMerchantPayload> vpaList = new ArrayList<SubMerchantPayload>();
		String vpa = null;
		String smRegisterInfo = null;
		try {
			Long merchantId = Long.valueOf(clientId);

			List<MerchantSubMerchantInfo> list = merchantSubMerchantInfoRepository.findVpaListByMerchantId(merchantId);
			for (MerchantSubMerchantInfo merchantSubMerchantInfo : list) {

				SubMerchantPayload subMerchantPayload = new SubMerchantPayload();

				vpa = merchantSubMerchantInfo.getSubMerchantAdditionalInfo();
				smRegisterInfo = merchantSubMerchantInfo.getSubMerchantRegisterInfo();

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(smRegisterInfo);
				JSONObject data = (JSONObject) obj;
				String merchantBussiessName = (String) data.get("merchantBussiessName");

				subMerchantPayload.setBussinessName(merchantBussiessName);

				if (vpa == null) {

					subMerchantPayload.setSubMerchantVpa("NA");

				} else {
					subMerchantPayload.setSubMerchantVpa(vpa);

				}
				vpaList.add(subMerchantPayload);
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "SubMerchant vpa list");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("vpaList", vpaList);

		} catch (Exception e) {
			LOGGER.info("error" + e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

		}
		return map;
	}

//	@Override
//	public String getServiceByMechantId(long merchantId) {
//
//		JSONObject jsonObject = new JSONObject();
//		String response = null;
//		try {
//			List list = serviceInfoRepository.getServiceByMerchantId(merchantId);
//			long serviceId = 0;
//			String serviceName = null;
//			JSONObject categoryJsonObject = new JSONObject();
//			org.json.JSONArray jsonArray = new org.json.JSONArray();
//			org.json.JSONObject serviceJson = new org.json.JSONObject();
//
//			Iterator it = list.iterator();
//			while (it.hasNext()) {
//				Object[] obj = (Object[]) it.next();
//				BigInteger Id = (BigInteger) obj[0];
//				serviceName = Encryption.decString((String) obj[1]);
//				serviceId = Id.longValue();
//				categoryJsonObject.put("serviceId", serviceId);
//				categoryJsonObject.put("serviceName", serviceName);
//				jsonArray.put(categoryJsonObject);
//				serviceJson.put("serviceDetails", jsonArray);
//				response = serviceJson.toString();
//			}
//		} catch (NullPointerException e) {
//			LOGGER.info("error" + e);
//			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
//			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
//			response = jsonObject.toString();
//		} catch (Exception e) {
//			LOGGER.info("error" + e);
//			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
//			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
//			response = jsonObject.toString();
//		}
//		return response;
//
//	}



	@Override
	public String getServiceByMechantIdOld(long merchantId) {
		JSONObject jsonObject = new JSONObject();
		String response = null;
		try {
			List list = serviceInfoRepository.getServiceByMerchantId(merchantId);
			long serviceId = 0;
			String serviceName = null;
			JSONObject categoryJsonObject = new JSONObject();
			org.json.JSONArray jsonArray = new org.json.JSONArray();
			org.json.JSONObject serviceJson = new org.json.JSONObject();

			Iterator it = list.iterator();
			while (it.hasNext()) {
				Object[] obj = (Object[]) it.next();
				BigInteger Id = (BigInteger) obj[0];
				serviceName = Encryption.decString((String) obj[1]);
				serviceId = Id.longValue();
				categoryJsonObject.put("serviceId", serviceId);
				categoryJsonObject.put("serviceName", serviceName);
				jsonArray.put(categoryJsonObject);
				serviceJson.put("serviceDetails", jsonArray);
				response = serviceJson.toString();
			}
		} catch (NullPointerException e) {
			LOGGER.info("error" + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		} catch (Exception e) {
			LOGGER.info("error" + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		}
		return response;
	}
	
	
	@Override
	public Map<String, Object> getServiceByMechantId(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantServicesPayload> servicesList = new ArrayList<MerchantServicesPayload>();

		try {
			List<MerchantService> list = merchantServiceRepository.findAllMerchantServiceByMerchantId(merchantId);
			
			for(MerchantService merchantService:list) {
				MerchantServicesPayload merchantServicesPayload=new MerchantServicesPayload();
				
				merchantServicesPayload.setServiceId(merchantService.getServiceId());
				
				ServiceInfo info=serviceInfoRepository.findById(merchantService.getServiceId()).get();
				
				merchantServicesPayload.setServiceName(Encryption.decString(info.getServiceName()));
				
				merchantServicesPayload.setCategoryName(Encryption.decString(info.getServiceCategory().getServiceCategoryName()));
				
				servicesList.add(merchantServicesPayload);
			}
            Collections.sort(servicesList);  			
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Merchant services list");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("servicesList", servicesList);
            }  catch (Exception e) {
			LOGGER.info("error" + e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

		}
		return map;

	}

	
	
	
	@Override
	public String getTotalTrxn(long merchantId) {

		JSONObject jsonObject = new JSONObject();
		String response = null;
		try {
			Date date = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			String date1 = format1.format(date);

			double walletAmount = merchantRepository.findMerchantWallet(merchantId);
			long totalTrxn = 0;
			String totalAmt = null;
			JSONObject trxnJson = new JSONObject();
			org.json.JSONArray jsonArray = new org.json.JSONArray();
			org.json.JSONObject totalTrxnJson = new org.json.JSONObject();
			List totalAmtLst = serviceInfoRepository.getTotalTrxnDetail(merchantId, date1 + " 00:00:00.0",
					date1 + " 23:59:59.9");
			Iterator it = totalAmtLst.iterator();
			while (it.hasNext()) {
				double amount = 0.0;
				Object[] tuple = (Object[]) it.next();
				List t = serviceInfoRepository.getTotalTrxnDetail2(merchantId, date1 + " 00:00:00.0",
						date1 + " 23:59:59.9");
				Iterator<?> ite = t.iterator();
				while (ite.hasNext()) {
					Object[] tuple2 = (Object[]) ite.next();
					amount = amount + Encryption.decFloat(((double) tuple2[1]));
				}
				BigInteger bigInteger = (BigInteger) tuple[0];
				totalTrxn = bigInteger.longValue();
				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String tamount = amount1.format(amount);
				tuple[1] = tamount;
				LOGGER.info("tamount" + tamount);

				String walletAmountFormatted = amount1.format(walletAmount);
				trxnJson.put("totalTrxn", totalTrxn);
				trxnJson.put("totalAmount", tamount);
				trxnJson.put("walletAmount", walletAmountFormatted);
				trxnJson.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);

				response = trxnJson.toString();
			}
		} catch (NullPointerException e) {
			LOGGER.info("error" + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		} catch (Exception e) {
			LOGGER.info("error" + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		}
		return response;

	}
	
	
	@Override
	public Map<String, Object> getMerchantWalletBalance(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Merchants merchants=merchantRepository.findById(merchantId).get();
			
			double amount = merchants.getMerchantFloatAmount();
			
			DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
			amount1.setMinimumIntegerDigits(1);
			String tamount = amount1.format(amount);			
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Merchant wallet ballance");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("merchantWallet", tamount);
			
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> getTransactionsList(TransactionsReportRequest transactionsReportRequest,
			String clientId) {
		Map<String, Object> map = new HashMap<>();

		try {

			int totalRecord = 0;
			List<?> details = null;
			List<?> totalDetails = null;

			LOGGER.info("clientId: " + clientId);
			Long merchantId = Long.parseLong(clientId);

			String startDate = transactionsReportRequest.getStartDate() + " 00.00.00.0";
			String endDate = transactionsReportRequest.getEndDate() + " 23.59.59.9";

			Pageable pageable = PageRequest.of(transactionsReportRequest.getPageNo(),
					transactionsReportRequest.getPageSize());

			if (transactionsReportRequest.getStartDate() != null && !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() == null) {

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate(merchantId, startDate,
						endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndStartDateToEndDate(merchantId, startDate,
						endDate);
				LOGGER.info("Inside1");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == null) {
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndSeriveIdAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
				LOGGER.info("Inside2");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("")
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() == null) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate(merchantId, startDate,
						endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndStartDateToEndDate(merchantId, startDate,
						endDate);
				LOGGER.info("Inside3");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("")
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == null) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndSeriveIdAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
				LOGGER.info("Inside4");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() != null) {
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId(), pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndStartDateToEndDateAndTrxnStatusId(
						merchantId, startDate, endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside5");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null) {
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId(
						merchantId, transactionsReportRequest.getServiceId(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId(), pageable);
				totalRecord = coreTransactionsRepository
						.countByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId(merchantId,
								transactionsReportRequest.getServiceId(), startDate, endDate,
								transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside6");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("")
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() != null) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId(), pageable);

				totalRecord = coreTransactionsRepository.countByMerchantIdAndStartDateToEndDateAndTrxnStatusId(
						merchantId, startDate, endDate, transactionsReportRequest.getTrxnStatusId());

				LOGGER.info("Inside7");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("")
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId(
						merchantId, transactionsReportRequest.getServiceId(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId(), pageable);

				totalRecord = coreTransactionsRepository
						.countByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId(merchantId,
								transactionsReportRequest.getServiceId(), startDate, endDate,
								transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside8");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() == null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {

				details = coreTransactionsRepository.findByMerchantIdAndVpaAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndVpaAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);
				LOGGER.info("Inside9");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() == null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndVpaAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndVpaAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);
				LOGGER.info("Inside10");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndVpaAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getServiceId(), transactionsReportRequest.getVpa(), startDate,
						endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndSeriveIdAndVpaAndStartDateToEndDate(
						merchantId, transactionsReportRequest.getServiceId(), transactionsReportRequest.getVpa(),
						startDate, endDate);
				LOGGER.info("Inside11");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndVpaAndStartDateToEndDate(merchantId,
						transactionsReportRequest.getServiceId(), transactionsReportRequest.getVpa(), startDate,
						endDate, pageable);
				totalRecord = coreTransactionsRepository.countByMerchantIdAndSeriveIdAndVpaAndStartDateToEndDate(
						merchantId, transactionsReportRequest.getServiceId(), transactionsReportRequest.getVpa(),
						startDate, endDate);
				LOGGER.info("Inside12");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {

				details = coreTransactionsRepository.findByMerchantIdAndVpaAndTrxnStatusIdAndStartDateToEndDate(
						merchantId, transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
						startDate, endDate, pageable);

				totalRecord = coreTransactionsRepository.countByMerchantIdAndVpaAndTrxnStatusIdAndStartDateToEndDate(
						merchantId, transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
						startDate, endDate);
				LOGGER.info("Inside13");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndVpaAndTrxnStatusIdAndStartDateToEndDate(
						merchantId, transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
						startDate, endDate, pageable);

				totalRecord = coreTransactionsRepository.countByMerchantIdAndVpaAndTrxnStatusIdAndStartDateToEndDate(
						merchantId, transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
						startDate, endDate);
				LOGGER.info("Inside14");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals(""))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals(""))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& transactionsReportRequest.getServiceId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {

				details = coreTransactionsRepository
						.findByMerchantIdAndVpaAndTrxnStatusIdAndServiceIdAndStartDateToEndDate(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
								transactionsReportRequest.getServiceId(), startDate, endDate, pageable);

				totalRecord = coreTransactionsRepository
						.countByMerchantIdAndVpaAndTrxnStatusIdAndServiceIdAndStartDateToEndDate(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
								transactionsReportRequest.getServiceId(), startDate, endDate);
				LOGGER.info("Inside15");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& transactionsReportRequest.getServiceId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository
						.findByMerchantIdAndVpaAndTrxnStatusIdAndServiceIdAndStartDateToEndDate(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
								transactionsReportRequest.getServiceId(), startDate, endDate, pageable);

				totalRecord = coreTransactionsRepository
						.countByMerchantIdAndVpaAndTrxnStatusIdAndServiceIdAndStartDateToEndDate(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getTrxnStatusId(),
								transactionsReportRequest.getServiceId(), startDate, endDate);
				LOGGER.info("Inside16");
				totalDetails = getServiceTotalTransactionReport(transactionsReportRequest, clientId);
			}

			else {

				LOGGER.info("Data Not Found");
			}
			List<TransactionsReportPayLoad> activityList = new ArrayList<TransactionsReportPayLoad>();

			int i = 0;
			Iterator it = details.iterator();
			while (it.hasNext()) {
				i++;
				Object[] object = (Object[]) it.next();

				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String tamount = amount1.format(Encryption.decFloat((Double) object[0]));
				Timestamp trxndate = (Timestamp) object[1];

				TransactionsReportPayLoad payLoad = new TransactionsReportPayLoad();
				payLoad.setTRXN_REF_ID(Encryption.decString((String) object[2]));
				payLoad.setTRXN_DATE(trxndate.toString());
				payLoad.setTRXN_AMOUNT(tamount);
				payLoad.setTRXN_SERVICE_IDENTIFIER(Encryption.decString((String) object[3]));
				payLoad.setSERVICE_NAME(Encryption.decString((String) object[7]));
				payLoad.setSTATUS_NAME((String) object[8]);
				payLoad.setMERCHANT_TRXN_REF_ID((String) object[4]);
				payLoad.setOPERATOR_REF_NO((String) object[6]);
				payLoad.setSP_REFERENCE_ID((String) object[5]);
				payLoad.setsNo(i);
				activityList.add(payLoad);
			}

			if (activityList.size() != 0) {
				map.put("response", "200");
				map.put("transaction_statment", activityList);
				map.put("total_detail", totalDetails);
				map.put("total_records", totalRecord);
				map.put("message", "transaction detail list");

			} else {
				map.put("response", "200");
				map.put("message", "Data Did Not Found");

			}

			return map;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error: " + e);
			map.put("Response", "401");
			map.put("error", "Exception");
		}
		return map;
	}

	@Override
	public List<TransactionsReportPayLoad> getTransactionsListReportNew(
			TransactionsReportRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<TransactionsReportPayLoad> activityList = new ArrayList<TransactionsReportPayLoad>();
		try {
			String startHours = null;
			String endHours = null;
			String sh = transactionsReportRequest.getStartHours();
			String eh = transactionsReportRequest.getEndHours();

			if (transactionsReportRequest.getStartHours().equals("0")
					|| transactionsReportRequest.getStartHours() == "0"
					|| transactionsReportRequest.getEndHours() == "0"
					|| transactionsReportRequest.getEndHours().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}

			if (transactionsReportRequest.getServiceId() == 0) {

			}

			List<?> details = null;

			Long merchantId = Long.parseLong(clientId);
			String startDate = transactionsReportRequest.getStartDate() + " " + startHours;
			String endDate = transactionsReportRequest.getEndDate() + " " + endHours;

			if (transactionsReportRequest.getStartDate() != null && !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				LOGGER.info("Inside1");
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2(merchantId, startDate,
						endDate);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				LOGGER.info("Inside2 ");
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2(merchantId, startDate,
						endDate);
				LOGGER.info("Inside3");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
				LOGGER.info("Inside4");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside5");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				details = coreTransactionsRepository
						.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2New(merchantId,
								transactionsReportRequest.getServiceId(), startDate, endDate,
								transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside6");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside7");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2(
						merchantId, transactionsReportRequest.getServiceId(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());

				LOGGER.info("Inside8");
			}

			if (transactionsReportRequest.getStartDate() != null && !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& transactionsReportRequest.getVpa() != null && !transactionsReportRequest.getVpa().equals("0")) {
				LOGGER.info("Inside9");
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& transactionsReportRequest.getVpa() != null && !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);
				LOGGER.info("Inside10");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0 && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {
				LOGGER.info("Inside11 ");
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
						endDate);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0 && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
						endDate);
				LOGGER.info("Inside12");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(
						merchantId, transactionsReportRequest.getVpa(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside13");
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(
						merchantId, transactionsReportRequest.getVpa(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside14");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				details = coreTransactionsRepository
						.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
								endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside15");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository
						.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
								endDate, transactionsReportRequest.getTrxnStatusId());

				LOGGER.info("Inside16");
			}

			else {

				LOGGER.info("Data Not Found");
			}

			int i = 0;
			Iterator it = details.iterator();
			while (it.hasNext()) {
				i++;
				Object[] object = (Object[]) it.next();

				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String tamount = amount1.format(Encryption.decFloat((Double) object[0]));
				Timestamp trxndate = (Timestamp) object[1];

				String responseMessage = (String) object[9];
				String name = null;
				String ifsc = null;
				org.json.JSONObject jsonObject = new org.json.JSONObject(responseMessage);
				if ("Domestic Payment".equalsIgnoreCase(Encryption.decString((String) object[7]))) {
					name = jsonObject.getString("BeneficiaryName");
					ifsc = jsonObject.getString("BeneficiaryIfscCode");
				} else {
					ifsc = jsonObject.getString("rspPayeeVirtualAdd");
					name = "N/A";
				}
				TransactionsReportPayLoad payLoad = new TransactionsReportPayLoad();
				payLoad.setTRXN_REF_ID(Encryption.decString((String) object[2]));
				payLoad.setTRXN_DATE(trxndate.toString());
				payLoad.setTRXN_AMOUNT(tamount);
				payLoad.setTRXN_SERVICE_IDENTIFIER(Encryption.decString((String) object[3]));
				payLoad.setSERVICE_NAME(Encryption.decString((String) object[7]));
				payLoad.setSTATUS_NAME((String) object[8]);
				payLoad.setMERCHANT_TRXN_REF_ID((String) object[4]);
				payLoad.setOPERATOR_REF_NO((String) object[6]);
				payLoad.setSP_REFERENCE_ID((String) object[5]);
				payLoad.setNAME(name);
				payLoad.setIfsc(ifsc);
				payLoad.setsNo(i);
				activityList.add(payLoad);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error: " + e);
			map.put("Response", "401");
			map.put("error", "Exception");
			return activityList;
		}
		return activityList;

	}

	@Override
	public List<?> getServiceTotalTransactionReport(TransactionsReportRequest transactionsReportRequest,
			String clientId) {

		List listServiceReports = null;
		String startDate = null;
		String endDate = null;
		String startHours = null;
		String endHours = null;
		Long merchantId = null;
		String vpa = null;
		List newList = new ArrayList();

		try {

			merchantId = Long.parseLong(clientId);
			startDate = transactionsReportRequest.getStartDate() + " 00.00.00.0";
			endDate = transactionsReportRequest.getEndDate() + " 23.59.59.9";
			startHours = transactionsReportRequest.getStartHours();
			endHours = transactionsReportRequest.getEndHours();
			vpa = transactionsReportRequest.getVpa();
			Long serviceIds = transactionsReportRequest.getServiceId();
			Long trxnStatusId = transactionsReportRequest.getTrxnStatusId();

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds == null
					&& trxnStatusId == null) {
				LOGGER.info("---Case1----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1(merchantId, startDate, endDate);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = Encryption.decFloat((double) tuple[3]);
					List q1 = serviceInfoRepository.getServiceTotalTransaction2(merchantId, (BigInteger) tuple[0],
							startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio3(merchantId, startDate, endDate,
							(BigInteger) tuple[0]);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio4(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds != null
					&& trxnStatusId == null) {
				LOGGER.info("---Case2----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1New(merchantId, startDate,
						endDate, serviceIds);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);
					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction22(merchantId, serviceIds, startDate,
							endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio33(merchantId, startDate, endDate,
							serviceIds);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio44(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && startHours != null && !startHours.equals("")
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds == null
					&& trxnStatusId == null) {
				LOGGER.info("---Case3----- ");

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1(merchantId, startDate, endDate);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction2(merchantId, (BigInteger) tuple[0],
							startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio3(merchantId, startDate, endDate,
							(BigInteger) tuple[0]);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio4(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && startHours != null && !startHours.equals("")
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds != null
					&& trxnStatusId == null) {
				LOGGER.info("---Case4----- ");
				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1New(merchantId, startDate,
						endDate, serviceIds);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);
					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction22(merchantId, serviceIds, startDate,
							endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio33(merchantId, startDate, endDate,
							serviceIds);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio44(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds == null
					&& trxnStatusId != null) {
				LOGGER.info("---Case5----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1(merchantId,
						startDate, endDate, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId2(merchantId,
							(BigInteger) tuple[0], startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId3(merchantId, startDate,
							endDate, (BigInteger) tuple[0], trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId4(merchantId, startDate,
								endDate, trxnStatusId, (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds != null
					&& trxnStatusId != null) {
				LOGGER.info("---Case6----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1New(merchantId,
						startDate, endDate, serviceIds, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId22(merchantId, serviceIds,
							startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId33(merchantId, startDate,
							endDate, serviceIds, trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId44(merchantId,
								startDate, endDate, trxnStatusId, serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds == null
					&& trxnStatusId != null) {
				LOGGER.info("---Case7----- ");
				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1(merchantId,
						startDate, endDate, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);
					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId2(merchantId,
							(BigInteger) tuple[0], startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId3(merchantId, startDate,
							endDate, (BigInteger) tuple[0], trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId4(merchantId, startDate,
								endDate, trxnStatusId, (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);
					// newList.add(tuple);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);
					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& (vpa == null || vpa.equals("")) && merchantId != null && serviceIds != null
					&& trxnStatusId != null) {
				LOGGER.info("---Case8----- ");
				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1New(merchantId,
						startDate, endDate, serviceIds, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId22(merchantId, serviceIds,
							startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId33(merchantId, startDate,
							endDate, serviceIds, trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {

							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId44(merchantId,
								startDate, endDate, trxnStatusId, serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);
					// newList.add(tuple);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& merchantId != null && serviceIds == null && trxnStatusId == null && vpa != null
					&& !vpa.equals("")) {
				LOGGER.info("---Case9----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1AndVpa(merchantId, vpa, startDate,
						endDate);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = Encryption.decFloat((double) tuple[3]);
					List q1 = serviceInfoRepository.getServiceTotalTransaction2AndVpa(merchantId, vpa,
							(BigInteger) tuple[0], startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio3AndVpa(merchantId, vpa, startDate,
							endDate, (BigInteger) tuple[0]);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio4AndVpa(merchantId, vpa, startDate,
								endDate, (BigInteger) oldtuple[3], (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& merchantId != null && serviceIds == null && trxnStatusId == null && vpa != null
					&& !vpa.equals("")) {
				LOGGER.info("---Case10----- ");

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1AndVpa(merchantId, vpa, startDate,
						endDate);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = Encryption.decFloat((double) tuple[3]);
					List q1 = serviceInfoRepository.getServiceTotalTransaction2AndVpa(merchantId, vpa,
							(BigInteger) tuple[0], startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio3AndVpa(merchantId, vpa, startDate,
							endDate, (BigInteger) tuple[0]);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio4AndVpa(merchantId, vpa, startDate,
								endDate, (BigInteger) oldtuple[3], (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& vpa != null && !vpa.equals("") && merchantId != null && serviceIds != null
					&& trxnStatusId == null) {
				LOGGER.info("---Case11----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1NewAndVpa(merchantId, vpa,
						startDate, endDate, serviceIds);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);
					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction22AndVpa(merchantId, vpa, serviceIds,
							startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio33AndVpa(merchantId, vpa, startDate,
							endDate, serviceIds);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio44AndVpa(merchantId, vpa, startDate,
								endDate, (BigInteger) oldtuple[3], serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& vpa != null && !vpa.equals("") && merchantId != null && serviceIds != null
					&& trxnStatusId == null) {
				LOGGER.info("---Case12----- ");

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1NewAndVpa(merchantId, vpa,
						startDate, endDate, serviceIds);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);
					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction22AndVpa(merchantId, vpa, serviceIds,
							startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio33AndVpa(merchantId, vpa, startDate,
							endDate, serviceIds);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio44AndVpa(merchantId, vpa, startDate,
								endDate, (BigInteger) oldtuple[3], serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& vpa != null && !vpa.equals("") && merchantId != null && serviceIds == null
					&& trxnStatusId != null) {
				LOGGER.info("---Case13----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1AndVpa(merchantId,
						vpa, startDate, endDate, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId2AndVpa(merchantId, vpa,
							(BigInteger) tuple[0], startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId3AndVpa(merchantId, vpa,
							startDate, endDate, (BigInteger) tuple[0], trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId4AndVpa(merchantId, vpa,
								startDate, endDate, trxnStatusId, (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& vpa != null && !vpa.equals("") && merchantId != null && serviceIds == null
					&& trxnStatusId != null) {
				LOGGER.info("---Case14----- ");

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1AndVpa(merchantId,
						vpa, startDate, endDate, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId2AndVpa(merchantId, vpa,
							(BigInteger) tuple[0], startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId3AndVpa(merchantId, vpa,
							startDate, endDate, (BigInteger) tuple[0], trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId4AndVpa(merchantId, vpa,
								startDate, endDate, trxnStatusId, (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("")) && (endHours == null || endHours.equals(""))
					&& vpa != null && !vpa.equals("") && merchantId != null && serviceIds != null
					&& trxnStatusId != null) {
				LOGGER.info("---Case15----- ");
				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1NewAndVpa(
						merchantId, vpa, startDate, endDate, serviceIds, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId22AndVpa(merchantId, vpa,
							serviceIds, startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId33AndVpa(merchantId, vpa,
							startDate, endDate, serviceIds, trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId44AndVpa(merchantId,
								vpa, startDate, endDate, trxnStatusId, serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& vpa != null && !vpa.equals("") && merchantId != null && serviceIds != null
					&& trxnStatusId != null) {
				LOGGER.info("---Case16----- ");

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1NewAndVpa(
						merchantId, vpa, startDate, endDate, serviceIds, trxnStatusId);

				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId22AndVpa(merchantId, vpa,
							serviceIds, startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId33AndVpa(merchantId, vpa,
							startDate, endDate, serviceIds, trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId44AndVpa(merchantId,
								vpa, startDate, endDate, trxnStatusId, serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return newList;
	}

	public static double convertToDouble(String temp) {
		String a = temp;
		// replace all commas if present with no comma
		String s = a.replaceAll(",", "").trim();
		// if there are any empty spaces also take it out.
		String f = s.replaceAll(" ", "");
		// now convert the string to double
		double result = Double.parseDouble(f);
		return result; // return the result
	}

	@Override
	public Map<String, Object> getPassbook(PassbookRequest passbookRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
     	try {
         	int totalRecord = 0;
			List<?> details = null;
			List<?> totalDetails = null;

			LOGGER.info("clientId: " + clientId);
			Long merchantId = Long.parseLong(clientId);
			String startDate = passbookRequest.getStartDate() + " 00.00.00.0";
//			String endDate = passbookRequest.getEndDate() + " 23.59.59.9";
			String endDate = "2022-12-09 13.59.59.9";
			Pageable pageable = PageRequest.of(passbookRequest.getPageNo(), passbookRequest.getPageSize());

			if (passbookRequest.getStartDate() != null && !passbookRequest.getStartDate().equals("")
					&& passbookRequest.getEndDate() != null && !passbookRequest.getEndDate().equals("")
					&& merchantId != null) {
				details = merchantWalletTransactionsRepository.findByMerchantIdAndStartDateToEndDate(merchantId,
						startDate, endDate, pageable);
				totalRecord = merchantWalletTransactionsRepository.countByMerchantIdAndStartDateToEndDate(merchantId,
						startDate, endDate);
				LOGGER.info("Inside1");
			}

			else {

				LOGGER.info("Data Not Found");
			}
			List<PassbookPayload> activityList = new ArrayList<PassbookPayload>();

			int i = 0;

			Iterator it = details.iterator();
			while (it.hasNext()) {
				Object[] object = (Object[]) it.next();
				i++;

				PassbookPayload payLoad = new PassbookPayload();

				Timestamp walletTrxndate = (Timestamp) object[0];

				payLoad.setsNo(i);
				payLoad.setTransactionDate(walletTrxndate.toString());
				payLoad.setTrxnRefId((String) object[1]);
				String walletType = (String) object[2];
				double walletNewBalance = (double) object[3];
				double walletPreviousBalance = (double) object[4];
				String merchantTrxnRefId = (String) object[5];
				String serviceName = (String) object[6];
				if (walletType != null && walletType.equals("Debit")) {

					double a = Encryption.decFloat(walletNewBalance);
					double b = Encryption.decFloat(walletPreviousBalance);
					double c = b - a;

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					amount1.setMinimumIntegerDigits(1);
					String s = amount1.format(c);
					payLoad.setDebitAmount(s);
					payLoad.setCreditAmount("-");

				}

				if (walletType != null && walletType.equals("Credit")) {

					double a = Encryption.decFloat(walletNewBalance);
					double b = Encryption.decFloat(walletPreviousBalance);
					double c = a - b;

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					amount1.setMinimumIntegerDigits(1);
					String s = amount1.format(c);
					payLoad.setCreditAmount(s);
					payLoad.setDebitAmount("-");

				}

				payLoad.setMerchantTrxnRefId(merchantTrxnRefId);

				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String mAmount = amount1.format(Encryption.decFloat(walletNewBalance));

				payLoad.setAmount(mAmount);
				payLoad.setServiceName(Encryption.decString(serviceName));
				activityList.add(payLoad);
			}

			map.put("response", "200");
			map.put("passbook_statement", activityList);
			map.put("total_records", totalRecord);
			map.put("message", "passbook details");
			return map;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Error: " + e);
			map.put("Response", "401");
			map.put("error", "Exception");
		}
		return map;

	}

	@Override
	public List<PassbookPayload> getPassbookExcel(PassbookRequest passbookRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<PassbookPayload> activityList = new ArrayList<PassbookPayload>();
		try {

			List<?> details = null;
			List<?> totalDetails = null;

			LOGGER.info("clientId: " + clientId);
			Long merchantId = Long.parseLong(clientId);

			String startDate = passbookRequest.getStartDate() + " 00.00.00.0";
			String endDate = passbookRequest.getEndDate() + " 23.59.59.9";

			if (passbookRequest.getStartDate() != null && !passbookRequest.getStartDate().equals("")
					&& passbookRequest.getEndDate() != null && !passbookRequest.getEndDate().equals("")
					&& merchantId != null) {
				details = merchantWalletTransactionsRepository.findByMerchantIdAndStartDateToEndDate(merchantId,
						startDate, endDate);
				LOGGER.info("Inside1");
			}

			else {

				LOGGER.info("Data Not Found");
			}

			int i = 0;

			Iterator it = details.iterator();
			while (it.hasNext()) {
				Object[] object = (Object[]) it.next();
				i++;
				PassbookPayload payLoad = new PassbookPayload();

				Timestamp walletTrxndate = (Timestamp) object[0];

				payLoad.setsNo(i);
				payLoad.setTransactionDate(walletTrxndate.toString());
				payLoad.setTrxnRefId((String) object[1]);
				String walletType = (String) object[2];
				double walletNewBalance = (double) object[3];
				double walletPreviousBalance = (double) object[4];
				String merchantTrxnRefId = (String) object[5];
				String serviceName = (String) object[6];
				if (walletType != null && walletType.equals("Debit")) {

					double a = Encryption.decFloat(walletNewBalance);
					double b = Encryption.decFloat(walletPreviousBalance);
					double c = b - a;

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					amount1.setMinimumIntegerDigits(1);
					String s = amount1.format(c);
					payLoad.setDebitAmount(s);
					payLoad.setCreditAmount("-");

				}

				if (walletType != null && walletType.equals("Credit")) {

					double a = Encryption.decFloat(walletNewBalance);
					double b = Encryption.decFloat(walletPreviousBalance);
					double c = a - b;

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					amount1.setMinimumIntegerDigits(1);
					String s = amount1.format(c);
					payLoad.setCreditAmount(s);
					payLoad.setDebitAmount("-");

				}

				payLoad.setMerchantTrxnRefId(merchantTrxnRefId);

				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String mAmount = amount1.format(Encryption.decFloat(walletNewBalance));

				payLoad.setAmount(mAmount);
				payLoad.setServiceName(Encryption.decString(serviceName));
				activityList.add(payLoad);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error: " + e);
			map.put("Response", "401");
			map.put("error", "Exception");
		}
		return activityList;
	}

	@Override
	public List<?> getServiceTotalTransactionReportNew(TransactionsReportRequest transactionsReportRequest,
			String clientId) {

		List listServiceReports = null;
		String startDate = null;
		String endDate = null;
		String startHours = null;
		String endHours = null;
		Long merchantId = null;
		List newList = new ArrayList();

		try {

			merchantId = Long.parseLong(clientId);
			startDate = transactionsReportRequest.getStartDate() + " 00.00.00.0";
			endDate = transactionsReportRequest.getEndDate() + " 23.59.59.9";
			startHours = transactionsReportRequest.getStartHours();
			endHours = transactionsReportRequest.getEndHours();
			Long serviceIds = transactionsReportRequest.getServiceId();
			Long trxnStatusId = transactionsReportRequest.getTrxnStatusId();

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("0")) && (endHours == null || endHours.equals("0"))
					&& merchantId != null && (serviceIds == null || serviceIds == 0)
					&& (trxnStatusId == null || trxnStatusId == 0)) {

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1(merchantId, startDate, endDate);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = Encryption.decFloat((double) tuple[3]);
					List q1 = serviceInfoRepository.getServiceTotalTransaction2(merchantId, (BigInteger) tuple[0],
							startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio3(merchantId, startDate, endDate,
							(BigInteger) tuple[0]);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio4(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}
					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("0")) && (endHours == null || endHours.equals("0"))
					&& merchantId != null && serviceIds != null && serviceIds != 0
					&& (trxnStatusId == null || trxnStatusId == 0)) {

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1New(merchantId, startDate,
						endDate, serviceIds);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction22(merchantId, serviceIds, startDate,
							endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio33(merchantId, startDate, endDate,
							serviceIds);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio44(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && startHours != null && !startHours.equals("")
					&& merchantId != null && (serviceIds == null || serviceIds == 0)
					&& (trxnStatusId == null || trxnStatusId == 0)) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1(merchantId, startDate, endDate);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction2(merchantId, (BigInteger) tuple[0],
							startDate, endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio3(merchantId, startDate, endDate,
							(BigInteger) tuple[0]);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio4(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);
					// newList.add(tuple);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);
					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			else if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && startHours != null && !startHours.equals("")
					&& merchantId != null && serviceIds != null && serviceIds != 0
					&& (trxnStatusId == null || trxnStatusId == 0)) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransaction1New(merchantId, startDate,
						endDate, serviceIds);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransaction22(merchantId, serviceIds, startDate,
							endDate);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactio33(merchantId, startDate, endDate,
							serviceIds);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactio44(merchantId, startDate, endDate,
								(BigInteger) oldtuple[3], serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("0")) && (endHours == null || endHours.equals("0"))
					&& merchantId != null && (serviceIds == null || serviceIds == 0) && trxnStatusId != null
					&& trxnStatusId != 0) {

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1(merchantId,
						startDate, endDate, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);
					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId2(merchantId,
							(BigInteger) tuple[0], startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId3(merchantId, startDate,
							endDate, (BigInteger) tuple[0], trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId4(merchantId, startDate,
								endDate, trxnStatusId, (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);
					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& (startHours == null || startHours.equals("0")) && (endHours == null || endHours.equals("0"))
					&& merchantId != null && serviceIds != null && serviceIds != 0 && trxnStatusId != null
					&& trxnStatusId != 0) {

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1New(merchantId,
						startDate, endDate, serviceIds, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId22(merchantId, serviceIds,
							startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));
					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId33(merchantId, startDate,
							endDate, serviceIds, trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId44(merchantId,
								startDate, endDate, trxnStatusId, serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& merchantId != null && (serviceIds == null || serviceIds == 0) && trxnStatusId != null
					&& trxnStatusId != 0) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1(merchantId,
						startDate, endDate, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);
					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId2(merchantId,
							(BigInteger) tuple[0], startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId3(merchantId, startDate,
							endDate, (BigInteger) tuple[0], trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId4(merchantId, startDate,
								endDate, trxnStatusId, (BigInteger) tuple[0]);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);
					// newList.add(tuple);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

			if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")
					&& startHours != null && !startHours.equals("") && endHours != null && !endHours.equals("")
					&& merchantId != null && serviceIds != null && serviceIds != 0 && trxnStatusId != 0
					&& trxnStatusId != null) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				listServiceReports = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId1New(merchantId,
						startDate, endDate, serviceIds, trxnStatusId);
				Iterator It1 = listServiceReports.iterator();
				Object[] tuple = null;
				int sNo = 0;

				while (It1.hasNext()) {
					sNo++;
					String serviceName = null;
					double amount = 0;
					double totalamount = 0;

					String samount = "0";
					String pamount = "0";
					String famount = "0";
					double totalAmount = 0;
					int totaltrnsaction = 0;
					int strnsaction = 0;
					int ptrnsaction = 0;
					int ftrnsaction = 0;
					String ramount = "0";
					int rtrnsaction = 0;

					tuple = (Object[]) It1.next();
					tuple[1] = Encryption.decString((String) tuple[1]);

					serviceName = Encryption.decString((String) tuple[1]);
					totaltrnsaction = ((BigInteger) tuple[2]).intValue();

					totalAmount = (double) tuple[3];
					totalAmount = Encryption.decFloat(totalAmount);

					List q1 = serviceInfoRepository.getServiceTotalTransactionAndTrxnStatusId22(merchantId, serviceIds,
							startDate, endDate, trxnStatusId);

					Iterator itts = q1.iterator();
					while (itts.hasNext()) {
						Object[] tuple2s = (Object[]) itts.next();
						totalamount = totalamount + Encryption.decFloat(((double) tuple2s[1]));

					}
					DecimalFormat amount1s = new DecimalFormat("#,##,##,##,###.00");
					String tamounts = amount1s.format(totalamount);
					tuple[3] = tamounts;
					List q2 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId33(merchantId, startDate,
							endDate, serviceIds, trxnStatusId);

					Iterator itt2 = q2.iterator();

					for (int i = 0; itt2.hasNext(); i++) {

						amount = 0;
						Object[] oldtuple = (Object[]) itt2.next();

						if (((String) oldtuple[0]).equals("Success")) {
							strnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Pending")) {
							ptrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Fail")) {
							ftrnsaction = ((Number) oldtuple[2]).intValue();
						} else if (((String) oldtuple[0]).equals("Reversed")) {
							rtrnsaction = ((Number) oldtuple[2]).intValue();
						}

						List q3 = serviceInfoRepository.getServiceTotalTransactioAndTrxnStatusId44(merchantId,
								startDate, endDate, trxnStatusId, serviceIds);

						Iterator itt3 = q3.iterator();
						while (itt3.hasNext()) {
							Object[] tuple3 = (Object[]) itt3.next();
							amount = amount + Encryption.decFloat(((double) tuple3[1]));
						}

						DecimalFormat amount1 = new DecimalFormat("#,###.00");
						String tamount = amount1.format(amount);

						int k = ((Number) oldtuple[3]).intValue();

						if (k == 1) {
							samount = tamount;
						} else {
							if (k == 2) {
								famount = tamount;
							} else {
								if (k == 3)
									pamount = tamount;
								else {
									if (k == 5)
										ramount = tamount;
								}
							}

						}

					}

					tuple = appendValue(tuple, strnsaction);
					tuple = appendValue(tuple, samount);
					tuple = appendValue(tuple, ptrnsaction);
					tuple = appendValue(tuple, pamount);
					tuple = appendValue(tuple, ftrnsaction);
					tuple = appendValue(tuple, famount);
					tuple = appendValue(tuple, rtrnsaction);
					tuple = appendValue(tuple, ramount);
					// newList.add(tuple);

					ServiceDetailPayload payload = new ServiceDetailPayload();
					payload.setsNo(sNo);

					DecimalFormat amountf = new DecimalFormat("#,##,##,##,###.00");
					amountf.setMinimumIntegerDigits(1);
					String finalAmount = amountf.format(totalamount);

					payload.setServicename(serviceName);
					payload.setTotaltrnsaction(totaltrnsaction);
					payload.setTotalamount(finalAmount);
					payload.setStrnsaction(strnsaction);
					payload.setSamount(samount);
					payload.setPtrnsaction(ptrnsaction);
					payload.setPamount(pamount);
					payload.setFtrnsaction(ftrnsaction);
					payload.setFamount(famount);
					payload.setRtrnsaction(rtrnsaction);
					payload.setRamount(ramount);

					newList.add(payload);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return newList;
	}

	@Override
	public List<TransactionsReportPayLoad> getTransactionsListReport(
			TransactionsReportRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<TransactionsReportPayLoad> activityList = new ArrayList<TransactionsReportPayLoad>();
		try {
			String startHours = null;
			String endHours = null;
			String sh = transactionsReportRequest.getStartHours();
			String eh = transactionsReportRequest.getEndHours();

			if (transactionsReportRequest.getStartHours().equals("0")
					|| transactionsReportRequest.getStartHours() == "0"
					|| transactionsReportRequest.getEndHours() == "0"
					|| transactionsReportRequest.getEndHours().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}

			if (transactionsReportRequest.getServiceId() == 0) {

			}

			List<?> details = null;

			Long merchantId = Long.parseLong(clientId);
			String startDate = transactionsReportRequest.getStartDate() + " " + startHours;
			String endDate = transactionsReportRequest.getEndDate() + " " + endHours;

			if (transactionsReportRequest.getStartDate() != null && !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				LOGGER.info("Inside1");
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2(merchantId, startDate,
						endDate);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				LOGGER.info("Inside2 ");
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2(merchantId, startDate,
						endDate);
				LOGGER.info("Inside3");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
				LOGGER.info("Inside4");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside5");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2(
						merchantId, transactionsReportRequest.getServiceId(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside6");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside7");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2(
						merchantId, transactionsReportRequest.getServiceId(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());

				LOGGER.info("Inside8");
			}

			if (transactionsReportRequest.getStartDate() != null && !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& transactionsReportRequest.getVpa() != null && !transactionsReportRequest.getVpa().equals("0")) {
				LOGGER.info("Inside9");
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& transactionsReportRequest.getVpa() != null && !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);
				LOGGER.info("Inside10");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0 && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {
				LOGGER.info("Inside11 ");
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
						endDate);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0 && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
						endDate);
				LOGGER.info("Inside12");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(
						merchantId, transactionsReportRequest.getVpa(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside13");
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(
						merchantId, transactionsReportRequest.getVpa(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside14");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				details = coreTransactionsRepository
						.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
								endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside15");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository
						.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
								endDate, transactionsReportRequest.getTrxnStatusId());

				LOGGER.info("Inside16");
			}

			else {

				LOGGER.info("Data Not Found");
			}

			int i = 0;
			Iterator it = details.iterator();
			while (it.hasNext()) {
				i++;
				Object[] object = (Object[]) it.next();

				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String tamount = amount1.format(Encryption.decFloat((Double) object[0]));
				Timestamp trxndate = (Timestamp) object[1];

				TransactionsReportPayLoad payLoad = new TransactionsReportPayLoad();
				payLoad.setTRXN_REF_ID(Encryption.decString((String) object[2]));
				payLoad.setTRXN_DATE(trxndate.toString());
				payLoad.setTRXN_AMOUNT(tamount);
				payLoad.setTRXN_SERVICE_IDENTIFIER(Encryption.decString((String) object[3]));
				payLoad.setSERVICE_NAME(Encryption.decString((String) object[7]));
				payLoad.setSTATUS_NAME((String) object[8]);
				payLoad.setMERCHANT_TRXN_REF_ID((String) object[4]);
				payLoad.setOPERATOR_REF_NO((String) object[6]);
				payLoad.setSP_REFERENCE_ID((String) object[5]);
				payLoad.setsNo(i);
				activityList.add(payLoad);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error: " + e);
			map.put("Response", "401");
			map.put("error", "Exception");
			return activityList;
		}
		return activityList;

	}

	@Override
	public List<TransactionsReportPayLoad> getTransactionsListReportCoop(
			TransactionsReportRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<TransactionsReportPayLoad> activityList = new ArrayList<TransactionsReportPayLoad>();
		try {
			String startHours = null;
			String endHours = null;
			String sh = transactionsReportRequest.getStartHours();
			String eh = transactionsReportRequest.getEndHours();

			if (transactionsReportRequest.getStartHours().equals("0")
					|| transactionsReportRequest.getStartHours() == "0"
					|| transactionsReportRequest.getEndHours() == "0"
					|| transactionsReportRequest.getEndHours().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}

			List<?> details = null;

			Long merchantId = Long.parseLong(clientId);
			String startDate = transactionsReportRequest.getStartDate() + " " + startHours;
			String endDate = transactionsReportRequest.getEndDate() + " " + endHours;

			if (transactionsReportRequest.getStartDate() != null && !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				LOGGER.info("Inside1");
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2(merchantId, startDate,
						endDate);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				LOGGER.info("Inside2 ");
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2(merchantId, startDate,
						endDate);
				LOGGER.info("Inside3");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2(merchantId,
						transactionsReportRequest.getServiceId(), startDate, endDate);
				LOGGER.info("Inside4");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside5");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2(
						merchantId, transactionsReportRequest.getServiceId(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside6");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2(merchantId,
						startDate, endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside7");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null
					&& (transactionsReportRequest.getVpa() == null || transactionsReportRequest.getVpa().equals("0"))) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2(
						merchantId, transactionsReportRequest.getServiceId(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());

				LOGGER.info("Inside8");
			}

			if (transactionsReportRequest.getStartDate() != null && !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& transactionsReportRequest.getVpa() != null && !transactionsReportRequest.getVpa().equals("0")) {
				LOGGER.info("Inside9");
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);

			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0 && transactionsReportRequest.getTrxnStatusId() == 0
					&& transactionsReportRequest.getVpa() != null && !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), startDate, endDate);
				LOGGER.info("Inside10");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0 && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {
				LOGGER.info("Inside11 ");
				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
						endDate);
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() == 0 && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndSeriveIdAndStartDateToEndDate2AndVpa(merchantId,
						transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
						endDate);
				LOGGER.info("Inside12");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {
				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(
						merchantId, transactionsReportRequest.getVpa(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside13");
			} else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() == 0
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository.findByMerchantIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(
						merchantId, transactionsReportRequest.getVpa(), startDate, endDate,
						transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside14");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& (transactionsReportRequest.getStartHours() == null
							|| transactionsReportRequest.getStartHours().equals("0"))
					&& (transactionsReportRequest.getEndHours() == null
							|| transactionsReportRequest.getEndHours().equals("0"))
					&& merchantId != null && transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				details = coreTransactionsRepository
						.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
								endDate, transactionsReportRequest.getTrxnStatusId());
				LOGGER.info("Inside15");
			}

			else if (transactionsReportRequest.getStartDate() != null
					&& !transactionsReportRequest.getStartDate().equals("")
					&& transactionsReportRequest.getEndDate() != null
					&& !transactionsReportRequest.getEndDate().equals("")
					&& transactionsReportRequest.getStartHours() != null
					&& !transactionsReportRequest.getStartHours().equals("")
					&& transactionsReportRequest.getEndHours() != null
					&& !transactionsReportRequest.getEndHours().equals("") && merchantId != null
					&& transactionsReportRequest.getServiceId() != null
					&& transactionsReportRequest.getTrxnStatusId() != null && transactionsReportRequest.getVpa() != null
					&& !transactionsReportRequest.getVpa().equals("0")) {

				startDate = transactionsReportRequest.getStartDate() + " " + transactionsReportRequest.getStartHours()
						+ ".00.0";
				endDate = transactionsReportRequest.getEndDate() + " " + transactionsReportRequest.getEndHours()
						+ ".00.0";

				details = coreTransactionsRepository
						.findByMerchantIdAndSeriveIdAndStartDateToEndDateAndTrxnStatusId2AndVpa(merchantId,
								transactionsReportRequest.getVpa(), transactionsReportRequest.getServiceId(), startDate,
								endDate, transactionsReportRequest.getTrxnStatusId());

				LOGGER.info("Inside16");
			}

			else {

				LOGGER.info("Data Not Found");
			}

			LOGGER.info("LIST: " + details.size());

			String payerAccName = "NA";
			String payerVPA = "NA";
			String payeeVPA = "NA";
			String merchnatBussiessName = "NA";
			String description = null;
			int i = 0;
			Iterator it = details.iterator();
			while (it.hasNext()) {
				i++;
				Object[] object = (Object[]) it.next();

				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
				amount1.setMinimumIntegerDigits(1);
				String tamount = amount1.format(Encryption.decFloat((Double) object[0]));
				Timestamp trxndate = (Timestamp) object[1];

				String responseMessage = (String) object[9];

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(responseMessage);
				JSONObject data = (JSONObject) obj;

				String seriveName = Encryption.decString((String) object[7]);

				if (seriveName.equals("CollectUPI")) {

					description = (String) data.get("description");

					if (description == null || description.equals(null)) {

						payerVPA = "NA";
						payeeVPA = "NA";
						merchnatBussiessName = "NA";

					} else {

						payerAccName = (String) data.get("payerAccName");
						payerVPA = (String) data.get("payerVPA");
						payeeVPA = (String) data.get("payeeVPA");

						MerchantSubMerchantInfo subMerchantInfo = merchantSubMerchantInfoRepository
								.findBySubmerchantVpa(payeeVPA);

						String regInfo = subMerchantInfo.getSubMerchantRegisterInfo();

						org.json.JSONObject jObject = new org.json.JSONObject(regInfo);

						merchnatBussiessName = jObject.getString("merchantBussiessName");

					}

				}

				if (seriveName.equals("Domestic Payment")) {
					payerAccName = (String) data.get("BeneficiaryName");
					payerVPA = (String) data.get("BeneficiaryAccNo");
				}

				
				String trf=(String) object[2];
				
			
				
				
				String settelementStatus = null;
//				char settelementStatusChar = (char) object[10];
				
				
				BigInteger bg= (BigInteger) object[11];
				
				Long trxnId=bg.longValue();
				
				LOGGER.info("trxnId: " + trxnId);
				
				char settelementStatusChar =payinTransactionDetailRepository.findByRequestId(trxnId);

			//	char settelementStatusChar=detail.getIsSettled();
				
				LOGGER.info("settelementStatusChar: " + settelementStatusChar);
				
				if (settelementStatusChar == '1') {
					settelementStatus = "Settled";
				} else {
					settelementStatus = " Not Settled";
				}

				TransactionsReportPayLoad payLoad = new TransactionsReportPayLoad();
				payLoad.setTRXN_REF_ID(Encryption.decString((String) object[2]));
				payLoad.setTRXN_DATE(trxndate.toString());
				payLoad.setTRXN_AMOUNT(tamount);
				payLoad.setTRXN_SERVICE_IDENTIFIER(Encryption.decString((String) object[3]));
				payLoad.setSERVICE_NAME(Encryption.decString((String) object[7]));
				payLoad.setSTATUS_NAME((String) object[8]);
				payLoad.setMERCHANT_TRXN_REF_ID((String) object[4]);
				payLoad.setOPERATOR_REF_NO((String) object[6]);
				payLoad.setSP_REFERENCE_ID((String) object[5]);
				payLoad.setsNo(i);
				payLoad.setMerchnatBussiessName(merchnatBussiessName);
				payLoad.setCustomerName(payerAccName);
				payLoad.setCustomerAccountNo(payerVPA);
				payLoad.setSettlementStatus(settelementStatus);
				activityList.add(payLoad);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error: " + e);
			map.put("Response", "401");
			map.put("error", "Exception");
			return activityList;
		}
		return activityList;

	}

	@Override
	public Map<String, Object> getMerchantServiceDetails(String serviceName) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantServiceDetailsPayload> detailsList=new ArrayList<MerchantServiceDetailsPayload>();
		try {
			int totalRecords=0;
			
		        ServiceInfo info=serviceInfoRepository.findByServiceName(Encryption.encString(serviceName));
				
				List<MerchantService> list=merchantServiceRepository.findByServiceId(info.getServiceId());
		
			if(list.size()!=0) {
				totalRecords=merchantServiceRepository.totalMerchantsByServiceId(info.getServiceId());
				
				for(MerchantService merchantService:list) {
					
					Merchants merchants=merchantRepository.findById(merchantService.getMerchantId()).get();
					
					MerchantType merchantType=merchantTypeRepository.findById(merchants.getMerchantType().getMerchantTypeId()).get();
					
					MerchantServiceDetailsPayload merchantServiceDetailsPayload= new MerchantServiceDetailsPayload();	
					
					merchantServiceDetailsPayload.setMerchantBussinessName(Encryption.decString(merchants.getMerchantBusinessName()));
					merchantServiceDetailsPayload.setMerchantEmail(Encryption.decString(merchants.getMerchantEmail()));
					merchantServiceDetailsPayload.setMerchantMobileNo(Encryption.decString(merchants.getMerchantPhone()));
					merchantServiceDetailsPayload.setMerchantName(Encryption.decString(merchants.getMerchantFirstname())+" "+Encryption.decString(merchants.getMerchantLastname()));
					merchantServiceDetailsPayload.setMerchantType(merchantType.getMerchantTypeName());
					
					
					detailsList.add(merchantServiceDetailsPayload);
				}
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Merchant service details list");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("merchantServiceDetails", detailsList);
				map.put("totalRecords", totalRecords);
				
				
			}
			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Data not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
			
			
			
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		
		return map;
	}


	

}
