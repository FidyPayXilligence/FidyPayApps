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
import com.fidypay.request.ENachTxnInfoRequest;
import com.fidypay.request.PayoutTransactionRequest;
import com.fidypay.response.ENachTransactionResponse;
import com.fidypay.service.ENachTransactionDetailsService;
import com.fidypay.service.MerchantsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.ENachTransactionPdfExporter;
import com.fidypay.utils.ex.ExcelExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/v2")
public class ENachTransactionReportController {

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private ENachTransactionDetailsService eNachTransactionDetailsService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantsService merchantsService;

	@PostMapping("/eNachTotalTransactionAndTotalAmount")
	public Map<String, Object> eNachTotalTransactionAndTotalAmount(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {

		Map<String, Object> map = new HashMap<>();
		try {

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return eNachTransactionDetailsService.eNachTotalTransactionAndTotalAmount(merchantInfo.getMerchantId());
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

	@GetMapping("/eNachServicesList")
	public Map<String, Object> eNachServicesList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return eNachTransactionDetailsService.eNachServicesList(merchantInfo.getMerchantId());
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

	@PostMapping("/eNachTransactionList")
	public Map<String, Object> eNachTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody PayoutTransactionRequest payoutTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return eNachTransactionDetailsService.eNachTransactionList(payoutTransactionRequest,
						merchantInfo.getMerchantId());
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

	@ApiOperation(value = "Get ENach Transactions Statement Report Excel")
	@GetMapping("/getENachTransactionsStatementReportExcel")
	public void getENachTransactionsStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("merchantServiceId") Long merchantServiceId,
			@RequestParam("statusId") Long statusId, HttpServletResponse response) throws IOException {

		String endHours = "0";
		String startHours = "0";

		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				PayoutTransactionRequest transactionsReportRequest = new PayoutTransactionRequest();
				transactionsReportRequest.setStartDate(startDate);
				transactionsReportRequest.setEndDate(endDate);
				transactionsReportRequest.setStartTime(startHours);
				transactionsReportRequest.setEndTime(endHours);
				transactionsReportRequest.setMerchantServiceId(merchantServiceId);
				transactionsReportRequest.setStatusId(statusId);

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String cId = String.valueOf(merchantInfo.getMerchantId());
				List<ENachTransactionResponse> list = eNachTransactionDetailsService
						.getENachTransactionsStatementReportExcel(transactionsReportRequest, cId);

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.exportENachTransactions(list);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment; filename=ENach_Transaction_" + currentDateTime + ".xlsx");
				IOUtils.copy(byteArrayInputStream, response.getOutputStream());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (

		Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
	}

	@ApiOperation(value = "Get ENach Transactions Statement Report Pdf")
	@GetMapping("/getENachTransactionsStatementReportPdf")
	public void getENachTransactionsStatementReportPdf(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("merchantServiceId") Long merchantServiceId,
			@RequestParam("statusId") Long statusId, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String endHours = "0";
		String startHours = "0";
		try {

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				// String clientId="1";
				PayoutTransactionRequest transactionsReportRequest = new PayoutTransactionRequest();
				transactionsReportRequest.setStartDate(startDate);
				transactionsReportRequest.setEndDate(endDate);
				transactionsReportRequest.setStartTime(startHours);
				transactionsReportRequest.setEndTime(endHours);
				transactionsReportRequest.setMerchantServiceId(merchantServiceId);
				transactionsReportRequest.setStatusId(statusId);

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=ENach_Transaction_" + currentDateTime + ".pdf";
				response.setContentType("application/pdf");
				response.setHeader(headerKey, headerValue);

				String cId = String.valueOf(merchantInfo.getMerchantId());
				List<ENachTransactionResponse> list = eNachTransactionDetailsService
						.getENachTransactionsStatementReportExcel(transactionsReportRequest, cId);

				Merchants merchant = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
				ENachTransactionPdfExporter pdfExporter2 = new ENachTransactionPdfExporter();
				pdfExporter2.generateDynamicpdf(response, list, startDate, endDate, merchant);

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

	@PostMapping("/eNachServiceTransactionList")
	public Map<String, Object> eNachServiceTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody PayoutTransactionRequest payoutTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return eNachTransactionDetailsService.eNachServiceTransactionList(payoutTransactionRequest,
						merchantInfo.getMerchantId());
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

	// bharti5july
	@GetMapping("/findENachDetailsByENachTransactionId")
	public Map<String, Object> findENachDetailsByENachTransactionId(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam long eNachTransactionId)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return eNachTransactionDetailsService.findByENachTransactionId(eNachTransactionId,
						merchantInfo.getMerchantId());
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

	@PostMapping("/findENachTransactionByFilter")
	public Map<String, Object> findENachTransactionByFilter(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody ENachTxnInfoRequest eNachTxnInfoRequest)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return eNachTransactionDetailsService.findENachTransactionByFilter(eNachTxnInfoRequest);
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

	@PostMapping("/findENachChargesRate")
	public Map<String, Object> findENachChargesRate(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				return merchantsService.findENachChargesRate(merchantId);
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
