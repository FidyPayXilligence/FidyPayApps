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
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.PayinTransactionRequest;
import com.fidypay.response.PayinTransactionsReportPayLoad;
import com.fidypay.service.MerchantsService;
import com.fidypay.service.PayinTransactionDetailsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.ExcelExporter;
import com.fidypay.utils.ex.PayinTransactionPdfExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/v2")
public class PayinTransactionReportController {

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private PayinTransactionDetailsService payinTransactionDetailsService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantsService merchantsService;

	@PostMapping("/payinTransactionList")
	public Map<String, Object> payinTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody PayinTransactionRequest payinTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		} else {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return payinTransactionDetailsService.payinTransactionList(payinTransactionRequest,
						merchantInfo.getMerchantId());
			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		}
		return map;
	}

	@PostMapping("/payinServiceTransactionList")
	public Map<String, Object> payinServiceTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody PayinTransactionRequest payinTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		} else {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return payinTransactionDetailsService.payinServiceTransactionList(payinTransactionRequest,
						merchantInfo.getMerchantId());
			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		}
		return map;
	}

	@ApiOperation(value = "Get Payin Transactions Statement Report Excel")
	@GetMapping("/getPayinTransactionsStatementReportExcel")
	public void getPayinTransactionsStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("merchantServiceId") Long merchantServiceId,
			@RequestParam("statusId") Long statusId, @RequestParam("vpa") String vpa, HttpServletResponse response)
			throws IOException {

		String endHours = "0";
		String startHours = "0";

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
					PayinTransactionRequest transactionsReportRequest = new PayinTransactionRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartTime(startHours);
					transactionsReportRequest.setEndTime(endHours);
					transactionsReportRequest.setMerchantServiceId(merchantServiceId);
					transactionsReportRequest.setStatusId(statusId);
					transactionsReportRequest.setVpa(vpa);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					String cId = String.valueOf(merchantInfo.getMerchantId());
					List<PayinTransactionsReportPayLoad> list = payinTransactionDetailsService
							.getPayinTransactionsStatementReportExcel(transactionsReportRequest, cId);

					ExcelExporter excelExporter = new ExcelExporter();
					ByteArrayInputStream byteArrayInputStream = excelExporter.exportPayinTransactions(list);
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=UPI_Transaction_" + currentDateTime + ".xlsx");
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

	@ApiOperation(value = "Get Payin Transactions Statement Report Pdf")
	@GetMapping("/getPayinTransactionsStatementReportPdf")
	public void getPayinTransactionsStatementReportPdf(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("merchantServiceId") Long merchantServiceId,
			@RequestParam("statusId") Long statusId, @RequestParam("vpa") String vpa, HttpServletResponse response)
			throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		String endHours = "0";
		String startHours = "0";
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

					PayinTransactionRequest transactionsReportRequest = new PayinTransactionRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartTime(startHours);
					transactionsReportRequest.setEndTime(endHours);
					transactionsReportRequest.setMerchantServiceId(merchantServiceId);
					transactionsReportRequest.setStatusId(statusId);
					transactionsReportRequest.setVpa(vpa);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					String headerKey = "Content-Disposition";
					String headerValue = "attachment; filename=UPI_Transaction_" + currentDateTime + ".pdf";
					response.setContentType("application/pdf");
					response.setHeader(headerKey, headerValue);

					String cId = String.valueOf(merchantInfo.getMerchantId());
					List<PayinTransactionsReportPayLoad> list = payinTransactionDetailsService
							.getPayinTransactionsStatementReportExcel(transactionsReportRequest, cId);

					Merchants merchant = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
					PayinTransactionPdfExporter pdfExporter2 = new PayinTransactionPdfExporter();
					pdfExporter2.generateDynamicpdf(response, list, startDate, endDate, merchant);

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

	@PostMapping("/payinTotalTransactionAndTotalAmount")
	public Map<String, Object> payinTotalTransactionAndTotalAmount(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return payinTransactionDetailsService.payinTotalTransactionAndTotalAmount(merchantInfo.getMerchantId());
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

	@ApiOperation(value = "Get SubMerchant Vpa List")
	@PostMapping(value = "/getSubMerchantVpaList")
	public Map<String, Object> getSubMerchantVpaList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				String cId = String.valueOf(merchantInfo.getMerchantId());
				return payinTransactionDetailsService.getSubMerchantVpaList(cId);

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

	@GetMapping("/payinServicesList")
	public Map<String, Object> payinServicesList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return payinTransactionDetailsService.payinServicesList(merchantInfo.getMerchantId());
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

	@PostMapping("/findPayinChargeRate")
	public Map<String, Object> findPayinChargesRate(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				return merchantsService.findPayinChargesRate(merchantId);
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

}
