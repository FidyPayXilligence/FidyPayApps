package com.fidypay.utils.ex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fidypay.response.SettlemenetReportResponse;
import com.fidypay.response.TransactionsReportPayLoad;

public class TransactionExcelExporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionExcelExporter.class);

	public ByteArrayInputStream exportCoop(List<TransactionsReportPayLoad> transactionsReportPayLoad) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Transaction Statement");

			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			Cell cell = row.createCell(0);
			cell.setCellValue("S NO.");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("DATE");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("SERVICE NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("UTR");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(4);
			cell.setCellValue("TRANSACTION REF ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(5);
			cell.setCellValue("STATUS");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(6);
			cell.setCellValue("AMOUNT");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(7);
			cell.setCellValue("SERVICE IDENTIFIER");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(8);
			cell.setCellValue("MERCHANT BUSINESS NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(9);
			cell.setCellValue("MERCHANT TRANSACTION REF ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(10);
			cell.setCellValue("MERCHANT ORDER ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(11);
			cell.setCellValue("CUSTOMER NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(12);
			cell.setCellValue("CUSTOMER ACCOUNT NUMBER / VPA");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(13);
			cell.setCellValue("Settlement Status");
			cell.setCellStyle(headerCellStyle);

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getTRXN_DATE());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getSERVICE_NAME());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getOPERATOR_REF_NO());
				dataRow.createCell(4).setCellValue(transactionsReportPayLoad.get(i).getTRXN_REF_ID());
				dataRow.createCell(5).setCellValue(transactionsReportPayLoad.get(i).getSTATUS_NAME());
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getTRXN_AMOUNT());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getTRXN_SERVICE_IDENTIFIER());
				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getMerchnatBussiessName());
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getMERCHANT_TRXN_REF_ID());
				dataRow.createCell(10).setCellValue(transactionsReportPayLoad.get(i).getSP_REFERENCE_ID());
				dataRow.createCell(11).setCellValue(transactionsReportPayLoad.get(i).getCustomerName());
				dataRow.createCell(12).setCellValue(transactionsReportPayLoad.get(i).getCustomerAccountNo());
				dataRow.createCell(13).setCellValue(transactionsReportPayLoad.get(i).getSettlementStatus());
			}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.autoSizeColumn(11);
			sheet.autoSizeColumn(12);
			sheet.autoSizeColumn(13);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			LOGGER.error("Error during export Excel file", ex);
			return null;
		}
	}

//	public ByteArrayInputStream exportSettlement(List<SettlementReportPayload> transactionsReportPayLoad) {
//		try (Workbook workbook = new XSSFWorkbook()) {
//			Sheet sheet = workbook.createSheet("Transaction Statement");
//
//			Row row = sheet.createRow(0);
//
//			// Define header cell style
//			CellStyle headerCellStyle = workbook.createCellStyle();
//			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
//			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//			// Creating header cells
//			Cell cell = row.createCell(0);
//			cell.setCellValue("S NO.");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(1);
//			cell.setCellValue("SETTLEMENT DATE");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(2);
//			cell.setCellValue("MERCHANT BUSINESSNAME");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(3);
//			cell.setCellValue("VPA");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(4);
//			cell.setCellValue("AMOUNT");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(5);
//			cell.setCellValue("STATUS");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(6);
//			cell.setCellValue("UTR");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(7);
//			cell.setCellValue("TOTAL TRANSACTION");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(8);
//			cell.setCellValue("SETTLEMENT FROM DATE");
//			cell.setCellStyle(headerCellStyle);
//
//			cell = row.createCell(9);
//			cell.setCellValue("SETTLEMENT TO DATE");
//			cell.setCellStyle(headerCellStyle);
//
//			// Creating data rows for each contact
//			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
//				Row dataRow = sheet.createRow(i + 1);
//				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
//				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getSettlementDate());
//				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getSubmerchantBusinessName());
//				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getSubmerchantVpa());
//				dataRow.createCell(4).setCellValue(transactionsReportPayLoad.get(i).getAmount());
//				dataRow.createCell(5).setCellValue(transactionsReportPayLoad.get(i).getStatus());
//				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getUtr());
//				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getTotalTransaction());
//				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getSettlementFromDate());
//				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getSettlementToDate());
//
//			}
//
//			// Making size of column auto resize to fit with data
//			sheet.autoSizeColumn(0);
//			sheet.autoSizeColumn(1);
//			sheet.autoSizeColumn(2);
//			sheet.autoSizeColumn(3);
//			sheet.autoSizeColumn(4);
//			sheet.autoSizeColumn(5);
//			sheet.autoSizeColumn(6);
//			sheet.autoSizeColumn(7);
//			sheet.autoSizeColumn(8);
//			sheet.autoSizeColumn(9);
//
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			workbook.write(outputStream);
//			return new ByteArrayInputStream(outputStream.toByteArray());
//		} catch (IOException ex) {
//			LOGGER.error("Error during export Excel file", ex);
//			return null;
//		}
//	}

	public ByteArrayInputStream exportSettlement(List<SettlemenetReportResponse> transactionsReportPayLoad) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Transaction Statement");

			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			Cell cell = row.createCell(0);
			cell.setCellValue("S NO.");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("SETTLEMENT DATE");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("BUSINESS NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("VPA");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(4);
			cell.setCellValue("AMOUNT");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(5);
			cell.setCellValue("STATUS");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(6);
			cell.setCellValue("UTR");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(7);
			cell.setCellValue("TOTAL TRANSACTION");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(8);
			cell.setCellValue("SETTLEMENT FROM DATE");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(9);
			cell.setCellValue("SETTLEMENT TO DATE");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(10);
			cell.setCellValue("SETTLEMENT TYPE");
			cell.setCellStyle(headerCellStyle);

			 cell = row.createCell(11);
			 cell.setCellValue("ACCOUNT NUMBER");
			 cell.setCellStyle(headerCellStyle);
			
			 cell = row.createCell(12);
			 cell.setCellValue("IFSC CODE");
			 cell.setCellStyle(headerCellStyle);
			
			
			 cell = row.createCell(13);
			 cell.setCellValue("BANK NAME");
			 cell.setCellStyle(headerCellStyle);

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getSettlementDate());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getSubMerchantBusinessName());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getVpa());
				 String transactionAmountStr = transactionsReportPayLoad.get(i).getAmount().replace(",", "");
				 double transactionAmount = Double.parseDouble(transactionAmountStr);
				dataRow.createCell(4).setCellValue(transactionAmount);
				dataRow.createCell(5).setCellValue(transactionsReportPayLoad.get(i).getStatus());
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getTrxnId());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getTotalTransactions());
				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getFromDate());
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getToDate());
				dataRow.createCell(10).setCellValue(transactionsReportPayLoad.get(i).getSettlementType());
				 dataRow.createCell(11).setCellValue(transactionsReportPayLoad.get(i).getSubMerchantBankAccount());
				 dataRow.createCell(12).setCellValue(transactionsReportPayLoad.get(i).getSubMerchantIfscCode());
				 dataRow.createCell(13).setCellValue(transactionsReportPayLoad.get(i).getSubMerchantBankName());
			}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			 sheet.autoSizeColumn(11);
			 sheet.autoSizeColumn(12);
			 sheet.autoSizeColumn(13);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			LOGGER.error("Error during export Excel file", ex);
			return null;
		}
	}

	public ByteArrayInputStream exportNew(List<TransactionsReportPayLoad> transactionsReportPayLoad) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Transaction Statement");
			sheet.autoSizeColumn(1000000);
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			Cell cell = row.createCell(0);
			cell.setCellValue("S NO.");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("DATE");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("SERVICE NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("UTR");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(4);
			cell.setCellValue("TRANSACTION REF ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(5);
			cell.setCellValue("STATUS");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(6);
			cell.setCellValue("AMOUNT");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(7);
			cell.setCellValue("ACCOUNT NO");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(8);
			cell.setCellValue("NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(9);
			cell.setCellValue("IFSC CODE");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(10);
			cell.setCellValue("MERCHANT TRANSACTION REF ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(11);
			cell.setCellValue("MERCHANT ORDER ID");
			cell.setCellStyle(headerCellStyle);

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getTRXN_DATE());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getSERVICE_NAME());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getOPERATOR_REF_NO());
				dataRow.createCell(4).setCellValue(transactionsReportPayLoad.get(i).getTRXN_REF_ID());
				dataRow.createCell(5).setCellValue(transactionsReportPayLoad.get(i).getSTATUS_NAME());
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getTRXN_AMOUNT());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getTRXN_SERVICE_IDENTIFIER());
				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getNAME());
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getIfsc());
				dataRow.createCell(10).setCellValue(transactionsReportPayLoad.get(i).getMERCHANT_TRXN_REF_ID());
				dataRow.createCell(11).setCellValue(transactionsReportPayLoad.get(i).getSP_REFERENCE_ID());

			}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.autoSizeColumn(11);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			LOGGER.error("Error during export Excel file", ex);
			return null;
		}
	}

	public ByteArrayInputStream export(List<TransactionsReportPayLoad> transactionsReportPayLoad) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Transaction Statement");

			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			Cell cell = row.createCell(0);
			cell.setCellValue("S NO.");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("DATE");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("SERVICE NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("UTR");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(4);
			cell.setCellValue("TRANSACTION REF ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(5);
			cell.setCellValue("STATUS");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(6);
			cell.setCellValue("AMOUNT");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(7);
			cell.setCellValue("SERVICE IDENTIFIER");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(8);
			cell.setCellValue("MERCHANT TRANSACTION REF ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(9);
			cell.setCellValue("MERCHANT ORDER ID");
			cell.setCellStyle(headerCellStyle);

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getTRXN_DATE());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getSERVICE_NAME());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getOPERATOR_REF_NO());
				dataRow.createCell(4).setCellValue(transactionsReportPayLoad.get(i).getTRXN_REF_ID());
				dataRow.createCell(5).setCellValue(transactionsReportPayLoad.get(i).getSTATUS_NAME());
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getTRXN_AMOUNT());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getTRXN_SERVICE_IDENTIFIER());
				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getMERCHANT_TRXN_REF_ID());
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getSP_REFERENCE_ID());
			}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			LOGGER.error("Error during export Excel file", ex);
			return null;
		}
	}
}
