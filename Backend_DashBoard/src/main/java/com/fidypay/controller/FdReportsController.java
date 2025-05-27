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
import com.fidypay.request.WealthReportRequest;
import com.fidypay.response.FdWealthTxnResponse;
import com.fidypay.service.FdReportService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.ExcelExporter;
import com.fidypay.utils.ex.FdTransactionsPdfExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class FdReportsController {

	private static final Logger log = LoggerFactory.getLogger(FdReportsController.class);
	private final MerchantInfoRepository merchantInfoRepository;
	private final FdReportService fdReportService;
	private final MerchantsRepository merchantsRepository;

	public FdReportsController(MerchantInfoRepository merchantInfoRepository, FdReportService fdReportService,
			MerchantsRepository merchantsRepository) {
		this.merchantInfoRepository = merchantInfoRepository;
		this.fdReportService = fdReportService;
		this.merchantsRepository = merchantsRepository;
	}

	// 1. List Reports
	@ApiOperation(value = "Get Wealth Transaction Details Report")
	@PostMapping("/fetch-fd-reports")
	public Map<String, Object> fetchFdTxnReports(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody WealthReportRequest wealthReportRequest) {
		Map<String, Object> map = new HashMap<>();
		log.info("REST Request to fetchFdTxnReports: {}", clientId);

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				return fdReportService.fetchWealthTxnReport(wealthReportRequest);
			} else {
				log.info("FALSE CONDITION CASE: {}");
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("EXCEPTION CONDITION CASE: {}");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	// 4.
	@GetMapping("/find-fdTxn-details-with-txnId")
	public Map<String, Object> findFdTxnDetailsByTxnId(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam String merchantTrxnRefId)
			throws Exception {
		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return fdReportService.findByTrxnId(merchantTrxnRefId, merchantInfo.getMerchantId());
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

	@ApiOperation(value = "FD Transactions Details Report Excel")
	@GetMapping("/fd-txn-reports-excel")
	public void generateFdTxnReportExcel(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, HttpServletResponse response) throws IOException {
		log.info("REST Request to generateFdTxnReportExcel: {}, {}", clientId, clientSecret);
		String endHours = "0";
		String startHours = "0";

		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				WealthReportRequest wealthReportRequest = new WealthReportRequest();

				wealthReportRequest.setStartDate(startDate);
				wealthReportRequest.setEndDate(endDate);
				wealthReportRequest.setStartTime(startHours);
				wealthReportRequest.setEndTime(endHours);

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String cId = String.valueOf(merchantInfo.getMerchantId());
				List<FdWealthTxnResponse> list = fdReportService.generateFdTxnReportExcel(wealthReportRequest, cId);

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.exportFDTxnExcel(list);
				response.setHeader("Content-Disposition",
						"attachment; filename=Fd_Report_" + currentDateTime + ".xlsx");
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

	// 3. PDF Report
	@ApiOperation(value = "FD Transactions Details Report PDF")
	@GetMapping("/fd-txn-reports-pdf")
	public void generateFdTxnReportPDF(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, HttpServletResponse response) throws IOException {
		String endHours = "0";
		String startHours = "0";

		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				WealthReportRequest wealthReportRequest = new WealthReportRequest();

				wealthReportRequest.setStartDate(startDate);
				wealthReportRequest.setEndDate(endDate);
				wealthReportRequest.setStartTime(startHours);
				wealthReportRequest.setEndTime(endHours);

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=Fd_Report_" + currentDateTime + ".pdf";
				response.setContentType("application/pdf");
				response.setHeader(headerKey, headerValue);

				String cId = String.valueOf(merchantInfo.getMerchantId());

				List<FdWealthTxnResponse> list = fdReportService.generateFdTxnReportExcel(wealthReportRequest, cId);

				Merchants merchant = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
				FdTransactionsPdfExporter fDPdfExporter = new FdTransactionsPdfExporter();

				fDPdfExporter.generateDynamicPdf(response, list, startDate, endDate, merchant);

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
