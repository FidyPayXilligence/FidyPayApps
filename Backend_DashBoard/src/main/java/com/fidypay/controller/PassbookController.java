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
import com.fidypay.request.PassbookRequest;
import com.fidypay.response.PassbookPayload;
import com.fidypay.service.PassbookService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.ExcelExporter;
import com.fidypay.utils.ex.PassbookPdfExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/passBook")
public class PassbookController {

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private PassbookService passbookService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;
	
	@PostMapping("/getpassbookStatementReport")
	public Map<String, Object> payoutTransactionList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody PassbookRequest passbookRequest)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
			  if(merchantInfo!=null) {
				return passbookService.passBookTransactionList(passbookRequest, merchantInfo.getMerchantId());
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

	@ApiOperation(value = "Get Passbook Statement Report Excel")
	@GetMapping(value = "/getpassbookStatementReportExcel")
	public void getpassbookStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		try {

			  MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				if(merchantInfo!=null) { 

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				List<PassbookPayload> list = passbookService.passBookTransactionListExcel(merchantInfo.getMerchantId(), startDate,
						endDate);

				ExcelExporter excelExporter = new ExcelExporter();
				ByteArrayInputStream byteArrayInputStream = excelExporter.exportPassbook(list);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment; filename=Passbook_" + currentDateTime + ".xlsx");
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

	@ApiOperation(value = "Get Passbook Statement Report Pdf")
	@GetMapping(value = "/getpassbookStatementReportPdf")
	public void getpassbookStatementReportPdf(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		try {

			  MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				if(merchantInfo!=null) { 

				DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
				String currentDateTime = dateFormatter.format(new Date());

				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=Passbook_" + currentDateTime + ".pdf";
				response.setContentType("application/pdf");
				response.setHeader(headerKey, headerValue);

				List<PassbookPayload> list = passbookService.passBookTransactionListExcel(merchantInfo.getMerchantId(), startDate,
						endDate);

				Merchants merchant=merchantsRepository.findById(merchantInfo.getMerchantId()).get();
				PassbookPdfExporter pdfExporter2 = new PassbookPdfExporter();
				pdfExporter2.generateDynamicpdf(response, list, startDate, endDate, merchant);

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
	}

}
