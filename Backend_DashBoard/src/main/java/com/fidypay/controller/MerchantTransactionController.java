package com.fidypay.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.json.simple.JSONObject;
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

import com.fidypay.ServiceProvider.YesBank.YesBankPaymentImpl;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.TransactionStatus;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.TransactionStatusRepository;
import com.fidypay.request.PassbookRequest;
import com.fidypay.request.TransactionsReportRequest;
import com.fidypay.response.PassbookPayload;
import com.fidypay.response.TransactionStatusPayload;
import com.fidypay.response.TransactionsReportPayLoad;
import com.fidypay.service.ServiceInfoService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.ExcelExporter;
import com.fidypay.utils.ex.PassbookPdfExporter;
import com.fidypay.utils.ex.TransactionExcelExporter;
import com.fidypay.utils.ex.TransactionPdfExporter;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/serviceReport")
public class MerchantTransactionController {

	@Autowired
	private ServiceInfoService serviceInfoService;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private TransactionStatusRepository transactionStatusRepository;
	
	@Autowired
	private MerchantInfoRepository merchantInfoRepository; 

	@Autowired
	private YesBankPaymentImpl yesBankPaymentImpl;

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantTransactionController.class);

	@ApiOperation(value = "Get SubMerchant Vpa List")
	@PostMapping(value = "/getSubMerchantVpaList")
	public Map<String, Object> getSubMerchantVpaList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) {
		Map<String, Object> map = new HashMap<>();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

		} else {
			  MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
			   if(merchantInfo!=null) {
                 String cId=String.valueOf(merchantInfo.getMerchantId());
				return serviceInfoService.getSubMerchantVpaList(cId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		}
		return map;

	}

	@ApiOperation(value = "Get Transactions Statement")
	@PostMapping(value = "/getTransactionsStatement")
	public Map<String, Object> getTransactionsStatement(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody TransactionsReportRequest transactionsReportRequest) {
		Map<String, Object> map = new HashMap<>();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

		} else {
			  MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
			   if(merchantInfo!=null) {
                  String cId=String.valueOf(merchantInfo.getMerchantId());
				return serviceInfoService.getTransactionsList(transactionsReportRequest, cId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		}
		return map;

	}

	// Created by Arpan 29-4-2022
	@ApiOperation(value = "Get Transactions Statement Report Excel")
	@GetMapping("/getTransactionsStatementReportExcelNew")
	public void getTransactionsStatementReportExcelNew(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("serviceId") Long serviceId,
			HttpServletResponse response) throws IOException {

		String vpa = "0";
		long trxnStatusId = 1;
		Integer pageSize = 0;
		Integer pageNo = 0;
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
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
			      if(merchantInfo!=null) {
		         String cId=String.valueOf(merchantInfo.getMerchantId());
					TransactionsReportRequest transactionsReportRequest = new TransactionsReportRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartHours(startHours);
					transactionsReportRequest.setEndHours(endHours);
					transactionsReportRequest.setPageNo(pageNo);
					transactionsReportRequest.setPageSize(pageSize);
					transactionsReportRequest.setServiceId(serviceId);
					transactionsReportRequest.setTrxnStatusId(trxnStatusId);
					transactionsReportRequest.setVpa(vpa);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					List<TransactionsReportPayLoad> list = serviceInfoService
							.getTransactionsListReportNew(transactionsReportRequest, cId);
					LOGGER.info("List Size " + list.size());
					// TransactionExcelExporter excelExporter = new TransactionExcelExporter(list);
					// excelExporter.export(response);

					TransactionExcelExporter excelExporter = new TransactionExcelExporter();
					ByteArrayInputStream byteArrayInputStream = excelExporter.exportNew(list);
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=Transaction_" + currentDateTime + ".xlsx");
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

	@ApiOperation(value = "Get Passbook Statement")
	@PostMapping(value = "/getPassbookStatement")
	public Map<String, Object> getPassbookStatement(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody PassbookRequest passbookRequest) {
		Map<String, Object> map = new HashMap<>();

		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

		} else {
			 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
		  
			 if(merchantInfo!=null) {
				   String cId=String.valueOf(merchantInfo.getMerchantId());
				return serviceInfoService.getPassbook(passbookRequest, cId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		}
		return map;

	}

	@ApiOperation(value = "Get Passbook Statement Report Excel")
	@GetMapping(value = "/getpassbookStatementReportExcel")
	public void getpassbookStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		try {
			if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			} else {
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				 
					 if(merchantInfo!=null) {

					PassbookRequest passbookRequest = new PassbookRequest();
					passbookRequest.setStartDate(startDate);
					passbookRequest.setEndDate(endDate);
					passbookRequest.setPageNo(pageNo);
					passbookRequest.setPageSize(pageSize);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());
					   String cId=String.valueOf(merchantInfo.getMerchantId());
					List<PassbookPayload> list = serviceInfoService.getPassbookExcel(passbookRequest, cId);

					LOGGER.info("List Size " + list.size());

					ExcelExporter excelExporter = new ExcelExporter();
					ByteArrayInputStream byteArrayInputStream = excelExporter.exportPassbook(list);
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=Passbook_" + currentDateTime + ".xlsx");
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

	@ApiOperation(value = "Get Passbook Statement Report Pdf")
	@GetMapping(value = "/getpassbookStatementReportPdf")
	public void getpassbookStatementReportPdf(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize, HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		try {
			if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			} else {
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				    
					 if(merchantInfo!=null) {
					// String clientId="1";
					PassbookRequest passbookRequest = new PassbookRequest();
					passbookRequest.setStartDate(startDate);
					passbookRequest.setEndDate(endDate);
					passbookRequest.setPageNo(pageNo);
					passbookRequest.setPageSize(pageSize);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					String headerKey = "Content-Disposition";
					String headerValue = "attachment; filename=Passbook_" + currentDateTime + ".pdf";
					response.setContentType("application/pdf");
					response.setHeader(headerKey, headerValue);

					String cId=String.valueOf(merchantInfo.getMerchantId());
					List<PassbookPayload> list = serviceInfoService.getPassbookExcel(passbookRequest, cId);
					LOGGER.info("List Size " + list.size());
					// PassbookPdfExporter pdfExporter = new PassbookPdfExporter(list);
					// pdfExporter.export(response);

					Merchants merchant=merchantsRepository.findById(merchantInfo.getMerchantId()).get();
					PassbookPdfExporter pdfExporter2 = new PassbookPdfExporter();
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

	@ApiOperation(value = "Check Trxn Status")
	@GetMapping("/checkTrxnStatus")
	public String checkTrxnStatus(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("serviceName") String serviceName,
			@RequestParam("trxnId") String trxnId) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			if (clientId == "" || clientSecret == "") {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				response = jsonObject.toString();
			} else {
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				 
					 if(merchantInfo!=null) {
					switch (serviceName) {

					case "Domestic Payment":
						response = yesBankPaymentImpl.domesticPaymentStatusRequest(trxnId, merchantInfo.getMerchantId());
						LOGGER.info("response : " + response);
						break;
					case "UPI":
						response = yesBankPaymentImpl.trxnStatusRequest(trxnId, merchantInfo.getMerchantId());
						LOGGER.info("response : " + response);
						break;

					}

				} else {
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
					jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
					response = jsonObject.toString();
				}

			}
			LOGGER.info(" Check Status API Response : " + response);

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		}

		return response;
	}

	@ApiOperation(value = "Get Status For Payment Link")
	@GetMapping("/getStatus")
	public String getStatus(@RequestParam("trxnId") String trxnId, @RequestParam("vpa") String vpa,
			@RequestParam("mechantId") String merchantId) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();
		try {

			response = yesBankPaymentImpl.trxnStatus(trxnId, Long.parseLong(merchantId), vpa);

			LOGGER.info(" Get Status  API Response : " + response);

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		}

		return response;
	}

	@ApiOperation(value = "get Transaction Status List")
	@GetMapping(value = "/getTransactionStatusList")
	public Map<Object, Object> getTransactionStatusList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) {
		Map<Object, Object> map = new HashMap<>();
		List<TransactionStatusPayload> activityList = new ArrayList<TransactionStatusPayload>();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

		} else {
			 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
		
				 if(merchantInfo!=null) {

				List<TransactionStatus> list = transactionStatusRepository.findAll();

				for (TransactionStatus transactionStatus : list) {

					TransactionStatusPayload payload = new TransactionStatusPayload();
					payload.setTransactionStatusId(transactionStatus.getTransactionStatusId());
					payload.setStatusName(transactionStatus.getStatusName());

					activityList.add(payload);

					map.put("Data", activityList);
				}
				return map;

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				return map;
			}

		}
		return map;
	}

	@ApiOperation(value = "Get Transactions Statement Report Excel")
	@GetMapping("/getTransactionsStatementReportExcel")
	public void getTransactionsStatementReportExcel(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("startHours") String startHours,
			@RequestParam("endHours") String endHours, @RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize, @RequestParam("serviceId") Long serviceId,
			@RequestParam("trxnStatusId") Long trxnStatusId, @RequestParam("vpa") String vpa,
			HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		try {
			if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			} else {
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				   
					 if(merchantInfo!=null) {
					TransactionsReportRequest transactionsReportRequest = new TransactionsReportRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartHours(startHours);
					transactionsReportRequest.setEndHours(endHours);
					transactionsReportRequest.setPageNo(pageNo);
					transactionsReportRequest.setPageSize(pageSize);
					transactionsReportRequest.setServiceId(serviceId);
					transactionsReportRequest.setTrxnStatusId(trxnStatusId);
					transactionsReportRequest.setVpa(vpa);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());
					 String cId=String.valueOf(merchantInfo.getMerchantId());
					List<TransactionsReportPayLoad> list = serviceInfoService
							.getTransactionsListReport(transactionsReportRequest, cId);
					LOGGER.info("List Size " + list.size());

					TransactionExcelExporter excelExporter = new TransactionExcelExporter();
					ByteArrayInputStream byteArrayInputStream = excelExporter.export(list);
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=Transaction_" + currentDateTime + ".xlsx");
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

	@ApiOperation(value = "Get Transactions Statement Report Excel for cooprative bank")
	@GetMapping("/getTransactionsStatementReportExcel/coop")
	public void getTransactionsStatementReportExcelCoop(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("startHours") String startHours,
			@RequestParam("endHours") String endHours, @RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize, @RequestParam("serviceId") Long serviceId,
			@RequestParam("trxnStatusId") Long trxnStatusId, @RequestParam("vpa") String vpa,
			HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		try {
			if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			} else {
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				  
					 if(merchantInfo!=null) {
					TransactionsReportRequest transactionsReportRequest = new TransactionsReportRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartHours(startHours);
					transactionsReportRequest.setEndHours(endHours);
					transactionsReportRequest.setPageNo(pageNo);
					transactionsReportRequest.setPageSize(pageSize);
					transactionsReportRequest.setServiceId(serviceId);
					transactionsReportRequest.setTrxnStatusId(trxnStatusId);
					transactionsReportRequest.setVpa(vpa);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());
					  String cId=String.valueOf(merchantInfo.getMerchantId());
					List<TransactionsReportPayLoad> list = serviceInfoService
							.getTransactionsListReportCoop(transactionsReportRequest, cId);
					LOGGER.info("List Size " + list.size());
					// TransactionExcelExporter excelExporter = new TransactionExcelExporter(list);
					// excelExporter.export(response);

					TransactionExcelExporter excelExporter = new TransactionExcelExporter();
					ByteArrayInputStream byteArrayInputStream = excelExporter.exportCoop(list);
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=Transaction_" + currentDateTime + ".xlsx");
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

	@ApiOperation(value = "Get Transactions Statement Report Pdf for cooprative bank")
	@GetMapping("/getTransactionsStatementReportPdf/coop")
	public void getTransactionsStatementReportPdfCoop(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("startHours") String startHours,
			@RequestParam("endHours") String endHours, @RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize, @RequestParam("serviceId") Long serviceId,
			@RequestParam("trxnStatusId") Long trxnStatusId, @RequestParam("vpa") String vpa,
			HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		try {
			if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			} else {
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				    
					 if(merchantInfo!=null) {

					// String clientId="1";
					TransactionsReportRequest transactionsReportRequest = new TransactionsReportRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartHours(startHours);
					transactionsReportRequest.setEndHours(endHours);
					transactionsReportRequest.setPageNo(pageNo);
					transactionsReportRequest.setPageSize(pageSize);
					transactionsReportRequest.setServiceId(serviceId);
					transactionsReportRequest.setTrxnStatusId(trxnStatusId);
					transactionsReportRequest.setVpa(vpa);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					String headerKey = "Content-Disposition";
					String headerValue = "attachment; filename=Transaction_" + currentDateTime + ".pdf";
					response.setContentType("application/pdf");
					response.setHeader(headerKey, headerValue);
					String cId=String.valueOf(merchantInfo.getMerchantId());
					List<TransactionsReportPayLoad> list = serviceInfoService
							.getTransactionsListReportCoop(transactionsReportRequest, cId);
					LOGGER.info("List Size " + list.size());
					// TransactionPdfExporter pdfExporter = new TransactionPdfExporter(list);
					// pdfExporter.export(response);

					Merchants merchant=merchantsRepository.findById(merchantInfo.getMerchantId()).get();
					TransactionPdfExporter pdfExporter2 = new TransactionPdfExporter();
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

	@ApiOperation(value = "Get Transactions Statement Report Pdf")
	@GetMapping("/getTransactionsStatementReportPdf")
	public void getTransactionsStatementReportPdf(@RequestParam(value = "Client-Id") String clientId,
			@RequestParam("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("startHours") String startHours,
			@RequestParam("endHours") String endHours, @RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize, @RequestParam("serviceId") Long serviceId,
			@RequestParam("trxnStatusId") Long trxnStatusId, @RequestParam("vpa") String vpa,
			HttpServletResponse response) throws IOException {

		Map<String, Object> map = new HashMap<>();
		String finalResponse = null;
		try {
			if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			} else {
				 MerchantInfo merchantInfo=merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(Encryption.encString(clientId),Encryption.encString(clientSecret),'1'); 
				    
					 if(merchantInfo!=null) {
					// String clientId="1";
					TransactionsReportRequest transactionsReportRequest = new TransactionsReportRequest();
					transactionsReportRequest.setStartDate(startDate);
					transactionsReportRequest.setEndDate(endDate);
					transactionsReportRequest.setStartHours(startHours);
					transactionsReportRequest.setEndHours(endHours);
					transactionsReportRequest.setPageNo(pageNo);
					transactionsReportRequest.setPageSize(pageSize);
					transactionsReportRequest.setServiceId(serviceId);
					transactionsReportRequest.setTrxnStatusId(trxnStatusId);
					transactionsReportRequest.setVpa(vpa);

					DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
					String currentDateTime = dateFormatter.format(new Date());

					String headerKey = "Content-Disposition";
					String headerValue = "attachment; filename=Transaction_" + currentDateTime + ".pdf";
					response.setContentType("application/pdf");
					response.setHeader(headerKey, headerValue);
					String cId=String.valueOf(merchantInfo.getMerchantId());
					List<TransactionsReportPayLoad> list = serviceInfoService
							.getTransactionsListReport(transactionsReportRequest, cId);
					LOGGER.info("List Size " + list.size());
					// TransactionPdfExporter pdfExporter = new TransactionPdfExporter(list);
					// pdfExporter.export(response);

					Merchants merchant=merchantsRepository.findById(merchantInfo.getMerchantId()).get();
					TransactionPdfExporter pdfExporter2 = new TransactionPdfExporter();
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

}
