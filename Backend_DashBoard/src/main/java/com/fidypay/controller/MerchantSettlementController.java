package com.fidypay.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.MerchantSettlementRequest;
import com.fidypay.request.SettlementRequest;
import com.fidypay.response.SettlemenetReportResponse;
import com.fidypay.service.MerchantSettlementsService;
import com.fidypay.utils.constants.ResponseMessage;

import com.fidypay.utils.ex.TransactionExcelExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/merchantSettlement")
public class MerchantSettlementController {

	@Autowired
	private MerchantSettlementsService merchantSettlementsService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSettlementController.class);

	@ApiOperation(value = "Get Settlement List")
	@PostMapping(value = "/getSettlmentList")
	public Map<String, Object> getSettlmentList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody SettlementRequest settlementRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				String cId = String.valueOf(merchantInfo.getMerchantId());
				return merchantSettlementsService.getSettlmentList(settlementRequest, cId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;

	}

	@ApiOperation(value = "Get Merchant Settlement List")
	@PostMapping(value = "/getMerchantSettlmentList")
	public Map<String, Object> getMerchantSettlmentList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody MerchantSettlementRequest merchantSettlementRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				String cId = String.valueOf(merchantInfo.getMerchantId());

				return merchantSettlementsService.getMerchantSettlmentList(merchantSettlementRequest, cId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;

	}

	@ApiOperation(value = "Get Transactions Statement Report Excel")
	 @GetMapping("/getMerchantStatementReportExcel")
	 public void getMerchantStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
	 @RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
	 @RequestParam("endDate") String endDate, @RequestParam("startHours") String startHours,
	 @RequestParam("endHours") String endHours,@RequestParam("status") String status,@RequestParam("vpa") String vpa, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		try {
			if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			} else {
				MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
						Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
				if (merchantInfo != null) {

					String cId = String.valueOf(merchantInfo.getMerchantId());

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					MerchantSettlementRequest merchantSettlementRequest = new MerchantSettlementRequest();
					merchantSettlementRequest.setFromDate(startDate);
					merchantSettlementRequest.setToDate(endDate);
					merchantSettlementRequest.setStartTime(startHours);
					merchantSettlementRequest.setEndTime(endHours);
					merchantSettlementRequest.setStatus(status);
					merchantSettlementRequest.setVpa(vpa);

					List<SettlemenetReportResponse> list = merchantSettlementsService
							.getMerchantSettlmentExcelReport(merchantSettlementRequest, cId);
					LOGGER.info("List Size " + list.size());

					TransactionExcelExporter excelExporter = new TransactionExcelExporter();
					ByteArrayInputStream byteArrayInputStream = excelExporter.exportSettlement(list);
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=SettlementReport_" + currentDateTime + ".xlsx");
					IOUtils.copy(byteArrayInputStream, response.getOutputStream());

				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
					finalResponse = map.toString();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			finalResponse = map.toString();
		}
	}

}
