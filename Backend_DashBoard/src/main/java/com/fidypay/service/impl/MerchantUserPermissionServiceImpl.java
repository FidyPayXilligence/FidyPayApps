package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.entity.MerchantUserPermission;
import com.fidypay.repo.MerchantUserPermissionRepository;
import com.fidypay.repo.MerchantUserRepository;
import com.fidypay.request.MerchantUserPermissionPayload;
import com.fidypay.request.UserPermissionAdministrator;
import com.fidypay.request.UserPermissionBBPS;
import com.fidypay.request.UserPermissionEkyc;
import com.fidypay.request.UserPermissionEnach;
import com.fidypay.request.UserPermissionPG;
import com.fidypay.request.UserPermissionPayin;
import com.fidypay.request.UserPermissionPayout;
import com.fidypay.request.UserPermissionType;
import com.fidypay.response.MerchantPermissionPayload;
import com.fidypay.response.MerchantUserPermissionResponse;
import com.fidypay.service.MerchantUserPermissionService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.google.gson.Gson;

@Service
public class MerchantUserPermissionServiceImpl implements MerchantUserPermissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantUserPermissionServiceImpl.class);

	@Autowired
	private MerchantUserPermissionRepository merchantUserPermissionRepository;

	@Autowired
	private MerchantUserRepository merchantUserRepository;

	public Map<String, Object> saveMerchantUserPermission(long merchantId,
			MerchantUserPermissionPayload merchantUserPermissionPayload) throws ParseException {
		Map<String, Object> map = new HashedMap<>();

		long merchantUserId = merchantUserPermissionPayload.getMerchantUserId();
		String productName = merchantUserPermissionPayload.getProductName();
		String payoutData = "{\"Payout\":\"NA\"}" + "";
		String payinData = "{\"Payin\":\"NA\"}" + "";
		String ekycData = "{\"EKyc\":\"NA\"}" + "";
		String eNachData = "{\"ENach\":\"NA\"}" + "";
		String pgData = "{\"PG\":\"NA\"}" + "";
		String bbpsData = "{\"Bbps\":\"NA\"}" + "";
		String administratorData = "{\"Administrator\":\"NA\"}" + "";
		LOGGER.info(" payout : " + payoutData);

		if (merchantUserRepository.existsByIsActiveAndMerchantUserId('0', merchantUserId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_USER_DEACTIVATED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (!merchantUserRepository.existsByMerchantUserId(merchantUserId)) {
			LOGGER.info("already exists...merchantUserRepository.");

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (merchantUserPermissionRepository.existsByMerchantUserId(merchantUserId)) {
			LOGGER.info("already exists...merchantUserPermissionRepository.");

			MerchantUserPermission merchantUserPermission = merchantUserPermissionRepository
					.findByMerchantUserId(merchantUserId);

			switch (productName) {
			case "Payout":
				UserPermissionPayout payout = merchantUserPermissionPayload.getPayout();
				payoutData = new Gson().toJson(payout);
				merchantUserPermission.setPayout(payoutData);
				break;
			case "EKyc":
				UserPermissionEkyc ekyc = merchantUserPermissionPayload.geteKyc();
				ekycData = new Gson().toJson(ekyc);
				merchantUserPermission.seteKyc(ekycData);
				break;
			case "ENach":
				UserPermissionEnach eNach = merchantUserPermissionPayload.geteNach();
				eNachData = new Gson().toJson(eNach);
				merchantUserPermission.seteNach(eNachData);
				break;
			case "Payin":
				UserPermissionPayin payin = merchantUserPermissionPayload.getPayin();
				payinData = new Gson().toJson(payin);
				merchantUserPermission.setPayin(payinData);
				break;
			case "PG":
				UserPermissionPG pg = merchantUserPermissionPayload.getPg();
				pgData = new Gson().toJson(pg);
				merchantUserPermission.setPg(pgData);
				break;
			case "Bbps":
				UserPermissionBBPS bbps = merchantUserPermissionPayload.getBbps();
				bbpsData = new Gson().toJson(bbps);
				LOGGER.info("bbpsdata " + bbpsData);
				merchantUserPermission.setBbps(bbpsData);
				break;
			case "Administrator":
				UserPermissionAdministrator administrator = merchantUserPermissionPayload.getAdministrator();
				administratorData = new Gson().toJson(administrator);
				merchantUserPermission.setAdministrator(administratorData);
				break;

			default:
				break;
			}

			merchantUserPermissionRepository.save(merchantUserPermission);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, productName + " updated successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			return map;

		} else {

			LOGGER.info("Else...merchantUserPermissionRepository.");

			MerchantUserPermission merchantUserPermission = new MerchantUserPermission();

			switch (productName) {
			case "Payout":
				UserPermissionPayout payout = merchantUserPermissionPayload.getPayout();
				payoutData = new Gson().toJson(payout);
				break;
			case "EKyc":
				UserPermissionEkyc ekyc = merchantUserPermissionPayload.geteKyc();
				ekycData = new Gson().toJson(ekyc);
				break;
			case "ENach":
				UserPermissionEnach eNach = merchantUserPermissionPayload.geteNach();
				eNachData = new Gson().toJson(eNach);
				break;
			case "Payin":
				UserPermissionPayin payin = merchantUserPermissionPayload.getPayin();
				payinData = new Gson().toJson(payin);
				break;
			case "PG":
				UserPermissionPG pg = merchantUserPermissionPayload.getPg();
				pgData = new Gson().toJson(pg);
				break;
			case "Bbps":
				UserPermissionBBPS bbps = merchantUserPermissionPayload.getBbps();
				bbpsData = new Gson().toJson(bbps);
				break;
			case "Administrator":
				UserPermissionAdministrator administrator = merchantUserPermissionPayload.getAdministrator();
				administratorData = new Gson().toJson(administrator);
				break;

			default:
				break;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			merchantUserPermission.setDate(trxnDate);
			merchantUserPermission.setMerchantId(merchantId);
			merchantUserPermission.setPayout(payoutData);
			merchantUserPermission.setBbps(bbpsData);
			merchantUserPermission.seteKyc(ekycData);
			merchantUserPermission.seteNach(eNachData);
			merchantUserPermission.setPayin(payinData);
			merchantUserPermission.setPg(pgData);
			merchantUserPermission.setMerchantUserId(merchantUserId);
			merchantUserPermission.setIsActive('1');
			merchantUserPermission.setAdministrator(administratorData);
			merchantUserPermissionRepository.save(merchantUserPermission);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, productName + " save successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			return map;

		}

	}

	@Override
	public Map<String, Object> findAllData(long merchantId, Integer pageNo, Integer pageSize) {

		LOGGER.info(" merchantId : " + merchantId);
		Map<String, Object> map = new HashedMap<>();

		if (pageSize == 0) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Page size not be less than one");
			return map;
		}

		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("DATE").descending());

		Page<MerchantUserPermission> page = null;

		List<MerchantUserPermissionResponse> responseList = new ArrayList<MerchantUserPermissionResponse>();

		List<MerchantUserPermission> merchantUserPermissionList = new ArrayList<MerchantUserPermission>();

		page = merchantUserPermissionRepository.findByMerchantIdAndPaging(merchantId, paging);

		merchantUserPermissionList = page.getContent();

		LOGGER.info(" merchantUserPermissionList : " + merchantUserPermissionList.size());

		if (!merchantUserPermissionList.isEmpty()) {

			merchantUserPermissionList.forEach(merchantUserPermissionDetails -> {

				MerchantUserPermissionResponse response = new MerchantUserPermissionResponse();

				response.setMerchantUserPermissionId(merchantUserPermissionDetails.getMerchantUserPermissionId());
				response.setMerchnatUserId(merchantUserPermissionDetails.getMerchantUserId());
				response.setMerchantId(merchantUserPermissionDetails.getMerchantId());
				response.setPayout(merchantUserPermissionDetails.getPayout());
				response.setPayin(merchantUserPermissionDetails.getPayin());
				response.seteKyc(merchantUserPermissionDetails.geteKyc());
				response.seteNach(merchantUserPermissionDetails.geteNach());
				response.setBbps(merchantUserPermissionDetails.getBbps());
				response.setPg(merchantUserPermissionDetails.getPg());
				response.setAdministrator(merchantUserPermissionDetails.getAdministrator());
				response.setIsActive(String.valueOf(merchantUserPermissionDetails.getIsActive()));

				responseList.add(response);
			});

			map.put(ResponseMessage.DATA, responseList);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("currentPage", page.getNumber());
			map.put("totalItems", page.getTotalElements());
			map.put("totalPages", page.getTotalPages());
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> findByMerchantUserId(long merchantUserId, long merchantId) {

		LOGGER.info(" merchantId : " + merchantId);
		Map<String, Object> map = new HashedMap<>();

		MerchantUserPermission merchantUserPermissionDetails = merchantUserPermissionRepository
				.findByMerchantUserId(merchantUserId);

		if (merchantUserPermissionDetails == null) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			return map;
		} else {

			MerchantUserPermissionResponse response = new MerchantUserPermissionResponse();
			response.setMerchantUserPermissionId(merchantUserPermissionDetails.getMerchantUserPermissionId());
			response.setMerchnatUserId(merchantUserPermissionDetails.getMerchantUserId());
			response.setMerchantId(merchantUserPermissionDetails.getMerchantId());
			response.setPayout(merchantUserPermissionDetails.getPayout());
			response.setPayin(merchantUserPermissionDetails.getPayin());
			response.seteKyc(merchantUserPermissionDetails.geteKyc());
			response.seteNach(merchantUserPermissionDetails.geteNach());
			response.setBbps(merchantUserPermissionDetails.getBbps());
			response.setPg(merchantUserPermissionDetails.getPg());
			response.setAdministrator(merchantUserPermissionDetails.getAdministrator());
			response.setIsActive(String.valueOf(merchantUserPermissionDetails.getIsActive()));

			map.put(ResponseMessage.DATA, response);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		}

		return map;
	}

	public Map<String, Object> findByProductName(String productName) throws ParseException {
		Map<String, Object> map = new HashedMap<>();

		if (!productName.matches("Payout|EKyc|ENach|Payin|Bbps|PG|Administrator")) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION,
					"productName -> please pass Payout,EKyc,ENach,Payin,PG,Bbps or Administrator on productName parameter");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;

		}

		String productData = "NA";
		MerchantPermissionPayload merchantPermissionPayload = new MerchantPermissionPayload();
		UserPermissionType permissionType = new UserPermissionType();
		permissionType.setEdit("0");
		permissionType.setView("0");
		permissionType.setNone("0");

		switch (productName) {
		case "Payout":

			UserPermissionPayout payout = new UserPermissionPayout();
			payout.setBalance(permissionType);
			payout.setBankAccount(permissionType);
			payout.setBeneficiary(permissionType);
			payout.setPayment(permissionType);
			payout.setStatement(permissionType);
			payout.setTransactionReport(permissionType);
			payout.setTransactionStatus(permissionType);

			merchantPermissionPayload.setPayout(payout);
			productData = new Gson().toJson(merchantPermissionPayload);
			break;

		case "EKyc":
			UserPermissionEkyc ekyc = new UserPermissionEkyc();
			ekyc.setAssignUser(permissionType);
			ekyc.setCreateWorkflow(permissionType);
			ekyc.setTansactionReport(permissionType);
			ekyc.setWalletBalance(permissionType);
			merchantPermissionPayload.seteKyc(ekyc);
			productData = new Gson().toJson(merchantPermissionPayload);
			break;

		case "ENach":
			UserPermissionEnach eNach = new UserPermissionEnach();
			eNach.setBulkDebit(permissionType);
			eNach.setBulkDebitReport(permissionType);
			eNach.setBulkMandate(permissionType);
			eNach.setDebitPause(permissionType);
			eNach.setDebitCancel(permissionType);
			eNach.setDebitResume(permissionType);
			eNach.setMandateStatus(permissionType);
			eNach.setSettlementReport(permissionType);
			eNach.setSingleDebit(permissionType);
			eNach.setSingleMandate(permissionType);
			eNach.setTransactionReport(permissionType);
			merchantPermissionPayload.seteNach(eNach);
			productData = new Gson().toJson(merchantPermissionPayload);

			break;

		case "Payin":
			UserPermissionPayin payin = new UserPermissionPayin();
			payin.setAddSubmerchant(permissionType);
			payin.setSettlementReport(permissionType);
			payin.setSubmerchantList(permissionType);
			payin.setTransactionReport(permissionType);
			payin.setTransactionStatus(permissionType);
			merchantPermissionPayload.setPayin(payin);
			productData = new Gson().toJson(merchantPermissionPayload);

			break;

		case "PG":
			UserPermissionPG pg = new UserPermissionPG();
			pg.setSettlementReport(permissionType);
			pg.setTransactionReport(permissionType);
			pg.setTransactionStatus(permissionType);
			merchantPermissionPayload.setPg(pg);
			productData = new Gson().toJson(merchantPermissionPayload);

			break;

		case "Bbps":
			UserPermissionBBPS bbps = new UserPermissionBBPS();
			bbps.setSettlementReport(permissionType);
			bbps.setTransactionReport(permissionType);
			bbps.setTransactionStatus(permissionType);
			merchantPermissionPayload.setBbps(bbps);
			productData = new Gson().toJson(merchantPermissionPayload);

			break;

		case "Administrator":
			UserPermissionAdministrator administrator = new UserPermissionAdministrator();
			administrator.setVirtualAccount(permissionType);
			administrator.setAuthentication(permissionType);
			administrator.setServices(permissionType);
			merchantPermissionPayload.setAdministrator(administrator);
			productData = new Gson().toJson(merchantPermissionPayload);

			break;

		default:
			break;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(productName, productData);

		return map;
	}

}
