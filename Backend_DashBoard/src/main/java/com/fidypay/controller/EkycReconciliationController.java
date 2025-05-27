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
import javax.validation.Valid;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.ENachReconciliationRequestlist;
import com.fidypay.request.EkycReconciliationRequest;
import com.fidypay.response.EKYCReconciliationReportPayLoad;
import com.fidypay.service.EkycReconciliationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.ExcelExporter;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class EkycReconciliationController {

	@Autowired
	private EkycReconciliationService ekycReconciliationService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@PostMapping("findEkycReconciliationDetails")
	public Map<String, Object> getMerchantDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody @Valid ENachReconciliationRequestlist eNachReconciliationrequest) {

		Map<String, Object> map = new HashMap<>();
		try {

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				return ekycReconciliationService.findEkycReconciliationDetails(eNachReconciliationrequest,
						merchantInfo.getMerchantId());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Get EKYC Reconciliation Report Excel")
	@GetMapping("/getEKYCReconciliationReportExcel")
	public void getEKYCReconciliationReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				System.out.println("Start");

				EkycReconciliationRequest ekycreconciliationrequest = new EkycReconciliationRequest();
				ekycreconciliationrequest.setFromDate(fromDate);
				ekycreconciliationrequest.setToDate(toDate);
				ekycreconciliationrequest.setStartTime(startTime);
				ekycreconciliationrequest.setEndTime(endTime);

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String cId = String.valueOf(merchantInfo.getMerchantId());

				List<EKYCReconciliationReportPayLoad> list = ekycReconciliationService
						.getEKYCReconciliationReportExcel(ekycreconciliationrequest, cId);

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.exportEKYCReconciliation(list);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment; filename=EKYC_Reconciliation" + currentDateTime + ".xlsx");
				IOUtils.copy(byteArrayInputStream, response.getOutputStream());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
	}

}
