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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.fidypay.request.BBPSCommissionRequest;
import com.fidypay.request.BBPSTransactionHistoryRequest;
import com.fidypay.request.BBPSTransactionRequest;
import com.fidypay.response.BBPSCommissionResponse;
import com.fidypay.response.BBPSTransactionsReportPayLoad;
import com.fidypay.response.MerchantCommissionDeatilsResponse;
import com.fidypay.service.BBPSTransactionDetailsService;
import com.fidypay.service.MerchantsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.BBPSTransactionPdfExporter;
import com.fidypay.utils.ex.ExcelExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/v2")
public class BBPSTransactionReportController {

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private BBPSTransactionDetailsService bbpsTransactionDetailsService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantsService merchantsService;

	@PostMapping("/bbpsTransactionList")
	public Map<String, Object> bbpsTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody BBPSTransactionRequest bbpsTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return bbpsTransactionDetailsService.bbpsTransactionList(bbpsTransactionRequest,
						merchantInfo.getMerchantId());
			} else {
				System.out.println("Inside Condition false");
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Inside Condition exception");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@PostMapping("/bbpsServiceTransactionList")
	public Map<String, Object> bbpsServiceTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody BBPSTransactionRequest bbpsTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return bbpsTransactionDetailsService.bbpsServiceTransactionList(bbpsTransactionRequest,
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

	@ApiOperation(value = "Get BBPS Transactions Statement Report Excel")
	@GetMapping("/getBBPSTransactionsStatementReportExcel")
	public void getBBPSTransactionsStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
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
				BBPSTransactionRequest transactionsReportRequest = new BBPSTransactionRequest();
				transactionsReportRequest.setStartDate(startDate);
				transactionsReportRequest.setEndDate(endDate);
				transactionsReportRequest.setStartTime(startHours);
				transactionsReportRequest.setEndTime(endHours);
				transactionsReportRequest.setMerchantServiceId(merchantServiceId);
				transactionsReportRequest.setStatusId(statusId);

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String cId = String.valueOf(merchantInfo.getMerchantId());
				List<BBPSTransactionsReportPayLoad> list = bbpsTransactionDetailsService
						.getBBPSTransactionsStatementReportExcel(transactionsReportRequest, cId);

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.exportBBPSTransactions(list);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment; filename=BBPS_Transaction_" + currentDateTime + ".xlsx");
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

	@ApiOperation(value = "Get BBPS Transactions Statement Report Pdf")
	@GetMapping("/getBBPSTransactionsStatementReportPdf")
	public void getBBPSTransactionsStatementReportPdf(@RequestParam(value = "Client-Id") String clientId,
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
				BBPSTransactionRequest transactionsReportRequest = new BBPSTransactionRequest();
				transactionsReportRequest.setStartDate(startDate);
				transactionsReportRequest.setEndDate(endDate);
				transactionsReportRequest.setStartTime(startHours);
				transactionsReportRequest.setEndTime(endHours);
				transactionsReportRequest.setMerchantServiceId(merchantServiceId);
				transactionsReportRequest.setStatusId(statusId);

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=BBPS_Transaction_" + currentDateTime + ".pdf";
				response.setContentType("application/pdf");
				response.setHeader(headerKey, headerValue);

				String cId = String.valueOf(merchantInfo.getMerchantId());
				List<BBPSTransactionsReportPayLoad> list = bbpsTransactionDetailsService
						.getBBPSTransactionsStatementReportExcel(transactionsReportRequest, cId);

				BBPSTransactionPdfExporter pdfExporter2 = new BBPSTransactionPdfExporter();

				Merchants merchant = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

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

	@PostMapping("/bbpsTotalTransactionAndTotalAmount")
	public Map<String, Object> bbpsTotalTransactionAndTotalAmount(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return bbpsTransactionDetailsService.bbpsTotalTransactionAndTotalAmount(merchantInfo.getMerchantId());
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

	@GetMapping("/bbpsServicesList")
	public Map<String, Object> bbpsServicesList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return bbpsTransactionDetailsService.bbpsServicesList(merchantInfo.getMerchantId());
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

	@PostMapping("/bbpsTransactionHistory")
	public Map<String, Object> bbpsTransactionHistory(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody BBPSTransactionHistoryRequest bbpsTransactionHistoryRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				System.out.println("Inside Condition true");
				return bbpsTransactionDetailsService.bbpsTransactionHistory(bbpsTransactionHistoryRequest,
						merchantInfo.getMerchantId());
			} else {
				System.out.println("Inside Condition false");
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Inside Condition exception");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	// bharti13july
	@PostMapping("/findBBPSCommissionRate")
	public Map<String, Object> findBBPSCommissionRate(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				return merchantsService.findBBPSCommissionRate(merchantId);
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

	@ApiOperation(value = "BBPS Commissions List Report Excel")
	@GetMapping("/bbpsCommissionsListReportExcel")
	public void bbpsCommissionsListReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				List<BBPSCommissionResponse> list = bbpsTransactionDetailsService
						.bbpsCommissionsistReportExcel(startDate, endDate, merchantInfo.getMerchantId());

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.exportBBPSCommissions(list);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment; filename=BBPS_Commission_" + currentDateTime + ".xlsx");
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

	@PostMapping("/bbpsCommissionsList")
	public Map<String, Object> bbpsCommissionsList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody BBPSCommissionRequest bbpsCommissionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return bbpsTransactionDetailsService.bbpsCommissionslist(bbpsCommissionRequest,
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

	@PostMapping("/findBBPSTransactionDetails/{key}/{value}")
	public Map<String, Object> findBBPSTransactionDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable String key, @PathVariable String value)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return bbpsTransactionDetailsService.checkBBPSTransactionDetails(key, value,
						merchantInfo.getMerchantId());
			} else {
				System.out.println("Inside Condition false");
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Inside Condition exception");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@GetMapping("/updateTrxnIdentifierOnBBPSTrxnDeyails")
	public Map<String, Object> updateTrxnIdentifierOnBBPSTrxnDeyails(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate) throws Exception {
		return bbpsTransactionDetailsService.updateTrxnIdentifierOnBBPSTrxnDeyails(fromDate, toDate);

	}

	@GetMapping("/findBBPSCommissionRateExcel")
	public void findBBPSCommissionRateExcel(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, HttpServletResponse response) throws Exception {

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				List<MerchantCommissionDeatilsResponse> list = merchantsService
						.findBBPSCommissionRateExcel(merchantInfo.getMerchantId());

				System.out.println("list size :  {}" + list.size());

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.exportBBPSCommissionsListExcel(list);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment; filename=BBPS_Commission_Rate" + currentDateTime + ".xlsx");
				IOUtils.copy(byteArrayInputStream, response.getOutputStream());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ApiOperation(value = "fetch amount detail by merchantTrxnRefID")
	@PostMapping("/fetch-amount-details")
	public Map<String, Object> fetchAmountDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("merchantTrxnRefId") String merchantTrxnRefId) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return bbpsTransactionDetailsService.fetchAmountDetails(merchantTrxnRefId);
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
