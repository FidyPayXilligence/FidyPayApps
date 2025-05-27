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
import com.fidypay.request.EKYCTransactionRequest;
import com.fidypay.response.EKYCTransactionResponse;
import com.fidypay.service.EkycTransactionDetailsService;
import com.fidypay.service.MerchantsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.EKYCTransactionPdfExporter;
import com.fidypay.utils.ex.ExcelExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/v2")
public class EKYCTransactionReportController {

	@Autowired
	private EkycTransactionDetailsService ekycTransactionDetailsService;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantsService merchantsService;

	@PostMapping("/ekycTransactionList")
	public Map<String, Object> ekycTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody EKYCTransactionRequest ekycTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return ekycTransactionDetailsService.ekycTransactionList(ekycTransactionRequest,
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

	@PostMapping("/ekycServiceTransactionList")
	public Map<String, Object> ekycServiceTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody EKYCTransactionRequest ekycTransactionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return ekycTransactionDetailsService.ekycServiceTransactionList(ekycTransactionRequest,
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

	@ApiOperation(value = "Get Ekyc Transactions Statement Report Excel")
	@GetMapping("/getEkycTransactionsStatementReportExcel")
	public void getEkycTransactionsStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("merchantServiceId") Long merchantServiceId,
			@RequestParam("statusId") Long statusId, HttpServletResponse response) throws IOException {

		String endHours = "0";
		String startHours = "0";

		Map<String, Object> map = new HashMap<>();
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
					EKYCTransactionRequest transactionsReportRequest = new EKYCTransactionRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartTime(startHours);
					transactionsReportRequest.setEndTime(endHours);
					transactionsReportRequest.setMerchantServiceId(merchantServiceId);
					transactionsReportRequest.setStatusId(statusId);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					String cId = String.valueOf(merchantInfo.getMerchantId());
					List<EKYCTransactionResponse> list = ekycTransactionDetailsService
							.getEkycTransactionsStatementReportExcel(transactionsReportRequest, cId);

					ExcelExporter excelExporter = new ExcelExporter();
					ByteArrayInputStream byteArrayInputStream = excelExporter.exportEKYCTransactions(list);
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=EKYC_Transaction_" + currentDateTime + ".xlsx");
					IOUtils.copy(byteArrayInputStream, response.getOutputStream());

				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
	}

	@ApiOperation(value = "Get Ekyc Transactions Statement Report Pdf")
	@GetMapping("/getEkycTransactionsStatementReportPdf")
	public void getEkycTransactionsStatementReportPdf(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("merchantServiceId") Long merchantServiceId,
			@RequestParam("statusId") Long statusId, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
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

					EKYCTransactionRequest transactionsReportRequest = new EKYCTransactionRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartTime(startHours);
					transactionsReportRequest.setEndTime(endHours);
					transactionsReportRequest.setMerchantServiceId(merchantServiceId);
					transactionsReportRequest.setStatusId(statusId);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					String headerKey = "Content-Disposition";
					String headerValue = "attachment; filename=EKYC_Transaction_" + currentDateTime + ".pdf";
					response.setContentType("application/pdf");
					response.setHeader(headerKey, headerValue);

					String cId = String.valueOf(merchantInfo.getMerchantId());
					List<EKYCTransactionResponse> list = ekycTransactionDetailsService
							.getEkycTransactionsStatementReportExcel(transactionsReportRequest, cId);

					Merchants merchant = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
					EKYCTransactionPdfExporter pdfExporter2 = new EKYCTransactionPdfExporter();
					pdfExporter2.generateDynamicpdf(response, list, startDate, endDate, merchant);

				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
	}

	@PostMapping("/eKycTotalTransactionAndTotalAmount")
	public Map<String, Object> eKycTotalTransactionAndTotalAmount(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return ekycTransactionDetailsService.eKycTotalTransactionAndTotalAmount(merchantInfo.getMerchantId());
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

	@GetMapping("/eKycServicesList")
	public Map<String, Object> eKycServicesList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return ekycTransactionDetailsService.eKycServicesList(merchantInfo.getMerchantId());
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

	@PostMapping("/findEKYCChargeRate")
	public Map<String, Object> findKYcChargesRate(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				return merchantsService.findKYcChargesRate(merchantId);
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
