package com.fidypay.utils.ex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.fidypay.response.BBPSCommissionResponse;
import com.fidypay.response.BBPSTransactionsReportPayLoad;
import com.fidypay.response.EKYCReconciliationReportPayLoad;
import com.fidypay.response.EKYCTransactionResponse;
import com.fidypay.response.ENachReconciliationReportPayLoad;
import com.fidypay.response.ENachSattelmentReportPayLoad;
import com.fidypay.response.ENachTransactionResponse;
import com.fidypay.response.FdWealthTxnResponse;
import com.fidypay.response.MerchantCommissionDeatilsResponse;
import com.fidypay.response.PassbookPayload;
import com.fidypay.response.PayinTransactionsReportPayLoad;
import com.fidypay.response.PayoutTransactionsReportPayLoad;
import com.fidypay.response.PgTransactionResponse;
import com.fidypay.response.SubMerchantDeatilsResponse;
import com.fidypay.response.SubMerchantDetailsResponse;

public class ExcelExporter {

	public ByteArrayInputStream exportBBPSTransactions(List<BBPSTransactionsReportPayLoad> transactionsReportPayLoad) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells

			String[] headers = { "S NO.", "TRANSACTION DATE", "SERVICE NAME", "MERCHANT TRANSACTION REF ID",
					"TRANSACTION STATUS", "TRANSACTION AMOUNT", "TRXN ID", "PAYMENT MODE", "COMMISSION",
					"TRXN IDENTIFIER" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getTransactionDate());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getServiceName());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getMerchantTransactionRefId());
				dataRow.createCell(4).setCellValue(transactionsReportPayLoad.get(i).getTransactionStatus());
				String transactionAmountStr = transactionsReportPayLoad.get(i).getTransactionAmount().replace(",", "");
				double transactionAmount = Double.parseDouble(transactionAmountStr);
				dataRow.createCell(5).setCellValue(transactionAmount);
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getTrxnId());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getPaymentMode());
				dataRow.createCell(8)
						.setCellValue(String.valueOf(transactionsReportPayLoad.get(i).getMerchantServiceCommision()));
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getMobile());
			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);

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
			// LOGGER.error("Error during export Excel file", ex);
			return null;
		}

	}

	public ByteArrayInputStream exportEKYCTransactions(List<EKYCTransactionResponse> list) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells

			String[] headers = { "S NO.", "TRANSACTION DATE", "SERVICE NAME", "MERCHANT TRANSACTION REF ID",
					"TRANSACTION STATUS", "TRXN_REF_ID", "EKYC ID", "CHARGES" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(list.get(i).getsNo());
				dataRow.createCell(1).setCellValue(list.get(i).getTransactionDate());
				dataRow.createCell(2).setCellValue(list.get(i).getServiceName());
				dataRow.createCell(3).setCellValue(list.get(i).getMerchantTransactionRefId());
				dataRow.createCell(4).setCellValue(list.get(i).getStatus());
				dataRow.createCell(5).setCellValue(list.get(i).getTrxnRefId());
				dataRow.createCell(6).setCellValue(list.get(i).getEkycId());
				dataRow.createCell(7).setCellValue(list.get(i).getCharges());

			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);

			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			// LOGGER.error("Error during export Excel file", ex);
			return null;
		}

	}

	public ByteArrayInputStream exportPayinTransactions(
			List<PayinTransactionsReportPayLoad> transactionsReportPayLoad) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells

			String[] headers = { "S NO.", "TRANSACTION DATE", "SERVICE NAME", "TRANSACTION STATUS",
					"TRANSACTION AMOUNT", "PAYEE VPA", "PAYEE BUSINESS NAME", "PAYER VPA", "TRXN REF ID",
					"MERCHANT TRANSACTION REF ID", "UTR", "BANK REFERENCE ID", "NPCI REFERENCE ID", "REFUND ID",
					"CHARGES", "RECONCILIATION", "SETTLEMENT", "REMARK" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getTransactionDate());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getServiceName());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getTransactionStatus());

				String transactionAmountStr = transactionsReportPayLoad.get(i).getTransactionAmount().replace(",", "");
				double transactionAmount = Double.parseDouble(transactionAmountStr);
				dataRow.createCell(4).setCellValue(transactionAmount);
				dataRow.createCell(5).setCellValue(transactionsReportPayLoad.get(i).getPayeeVpa());
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getSubMerchantBussinessName());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getPayerVpa());
				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getTrxnRefId());
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getMerchantTransactionRefId());
				dataRow.createCell(10).setCellValue(transactionsReportPayLoad.get(i).getUtr());
				dataRow.createCell(11).setCellValue(transactionsReportPayLoad.get(i).getBankReferenceId());
				dataRow.createCell(12).setCellValue(transactionsReportPayLoad.get(i).getNpciReferenceId());
				dataRow.createCell(13).setCellValue(transactionsReportPayLoad.get(i).getRefundId());
				dataRow.createCell(14).setCellValue(transactionsReportPayLoad.get(i).getCharge());
				dataRow.createCell(17).setCellValue(transactionsReportPayLoad.get(i).getRemark());

				if (transactionsReportPayLoad.get(i).getIsReconcile().equals("0")) {
					dataRow.createCell(15).setCellValue("Not Verified");
				} else {
					dataRow.createCell(15).setCellValue("Verified");
				}
				if (transactionsReportPayLoad.get(i).getIsSettled().equals("0")) {
					dataRow.createCell(16).setCellValue("Not Settled");
				} else {
					dataRow.createCell(16).setCellValue("Settled");
				}

			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);
			sheet.trackColumnForAutoSizing(12);
			sheet.trackColumnForAutoSizing(13);
			sheet.trackColumnForAutoSizing(14);
			sheet.trackColumnForAutoSizing(15);
			sheet.trackColumnForAutoSizing(16);
			sheet.trackColumnForAutoSizing(17);
			sheet.trackColumnForAutoSizing(18);

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
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			sheet.autoSizeColumn(17);
			sheet.autoSizeColumn(18);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			// LOGGER.error("Error during export Excel file", ex);
			return null;
		}

	}

	public ByteArrayInputStream exportPayoutTransactions(
			List<PayoutTransactionsReportPayLoad> transactionsReportPayLoad) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "TRANSACTION DATE", "SERVICE NAME", "TRANSACTION STATUS",
					"TRANSACTION AMOUNT", "DEBITOR ACCOUNT NUMBER", "CREDITOR ACCOUNT NUMBER", "CREDITOR IFSC",
					"CREDITOR NAME", "MERCHANT TRANSACTION REF ID", "TRXN REF ID", "UTR", "TRANSFER TYPE", "CHARGES" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getTransactionDate());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getServiceName());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getTransactionStatus());
				String transactionAmountStr = transactionsReportPayLoad.get(i).getTransactionAmount().replace(",", "");
				double transactionAmount = Double.parseDouble(transactionAmountStr);
				dataRow.createCell(4).setCellValue(transactionAmount);
				String debitorAccountNo = transactionsReportPayLoad.get(i).getDebitorAcountNumber();
				debitorAccountNo = "XXXXXXXXXX" + debitorAccountNo.substring(debitorAccountNo.length() - 4);
				dataRow.createCell(5).setCellValue(debitorAccountNo);
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getCreditorAccountNumber());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getCreditorIfsc());
				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getCreditorName());
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getMerchantTransactionRefId());
				dataRow.createCell(10).setCellValue(transactionsReportPayLoad.get(i).getTrxnRefId());
				dataRow.createCell(11).setCellValue(transactionsReportPayLoad.get(i).getUtr());
				dataRow.createCell(12).setCellValue(transactionsReportPayLoad.get(i).getTransactionType());
				dataRow.createCell(13).setCellValue(transactionsReportPayLoad.get(i).getCharges());

			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);
			sheet.trackColumnForAutoSizing(12);
			sheet.trackColumnForAutoSizing(13);
			sheet.trackColumnForAutoSizing(14);

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
			sheet.autoSizeColumn(14);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			// LOGGER.error("Error during export Excel file", ex);
			return null;
		}
	}

	public ByteArrayInputStream exportENachTransactions(List<ENachTransactionResponse> transactionsReportPayLoad) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "TRANSACTION DATE", "SERVICE NAME", "TRANSACTION STATUS",
					"TRANSACTION AMOUNT", "CUSTOMER ID", "CUSTOMER NAME", "MERCHANT TRANSACTION REF ID", "TRXN REF ID",
					"UMRN", "MANDATE ID", "CHARGES", "REASON", "DEBIT DATE", "BANK ACCOUNT NUMBEER", "BANK IFSC" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < transactionsReportPayLoad.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				String transactionAmountStr = transactionsReportPayLoad.get(i).getTransactionAmount().replace(",", "");
				double transactionAmount = Double.parseDouble(transactionAmountStr);

				dataRow.createCell(0).setCellValue(transactionsReportPayLoad.get(i).getsNo());
				dataRow.createCell(1).setCellValue(transactionsReportPayLoad.get(i).getTransactionDate());
				dataRow.createCell(2).setCellValue(transactionsReportPayLoad.get(i).getServiceName());
				dataRow.createCell(3).setCellValue(transactionsReportPayLoad.get(i).getTransactionStatus());
				dataRow.createCell(4).setCellValue(transactionAmount);
				dataRow.createCell(5).setCellValue(transactionsReportPayLoad.get(i).getCustomerId());
				dataRow.createCell(6).setCellValue(transactionsReportPayLoad.get(i).getCustomerName());
				dataRow.createCell(7).setCellValue(transactionsReportPayLoad.get(i).getMerchantTransactionRefId());
				dataRow.createCell(8).setCellValue(transactionsReportPayLoad.get(i).getTrxnRefId());
				dataRow.createCell(9).setCellValue(transactionsReportPayLoad.get(i).getUmrnNo());
				dataRow.createCell(10).setCellValue(transactionsReportPayLoad.get(i).getMandateId());
				dataRow.createCell(11).setCellValue(transactionsReportPayLoad.get(i).getCharges());
				dataRow.createCell(12).setCellValue(transactionsReportPayLoad.get(i).getRemark());
				dataRow.createCell(13).setCellValue(transactionsReportPayLoad.get(i).getDebitDate());
				dataRow.createCell(14).setCellValue(transactionsReportPayLoad.get(i).getCustomerBankAccountNo());
				dataRow.createCell(15).setCellValue(transactionsReportPayLoad.get(i).getCustomerBankIFSC());
			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);
			sheet.trackColumnForAutoSizing(12);
			sheet.trackColumnForAutoSizing(13);
			sheet.trackColumnForAutoSizing(14);
			sheet.trackColumnForAutoSizing(15);
			;

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
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			// LOGGER.error("Error during export Excel file", ex);
			return null;
		}
	}

	public ByteArrayInputStream exportPassbook(List<PassbookPayload> passbookPayload) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "TRANSACTION DATE", "MERCHANT TRXN REF ID", "DEBIT", "CREDIT", "AMOUNT",
					"SERVICE NAME", "TRXN REF ID", "WALLET TRXN REF ID" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < passbookPayload.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(passbookPayload.get(i).getsNo());
				dataRow.createCell(1).setCellValue(passbookPayload.get(i).getTransactionDate());
				dataRow.createCell(2).setCellValue(passbookPayload.get(i).getMerchantTrxnRefId());
				dataRow.createCell(3).setCellValue(passbookPayload.get(i).getDebitAmount());
				dataRow.createCell(4).setCellValue(passbookPayload.get(i).getCreditAmount());
				dataRow.createCell(5).setCellValue(passbookPayload.get(i).getAmount());
				dataRow.createCell(6).setCellValue(passbookPayload.get(i).getServiceName());
				dataRow.createCell(7).setCellValue(passbookPayload.get(i).getTrxnRefId());
				dataRow.createCell(8).setCellValue(passbookPayload.get(i).getWalletTrxnRefId());
			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);

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
			return null;
		}
	}

	public ByteArrayInputStream exportPGransactions(List<PgTransactionResponse> list) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "TRANSACTION DATE", "SERVICE NAME", "TRANSACTION STATUS",
					"TRANSACTION AMOUNT", "PAYMENT MODE", "PAYMENT ID", "MERCHANT TRANSACTION REF ID", "TRXN REF ID",
					"BANK REF ID", "CHARGES" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				String transactionAmountStr = list.get(i).getTransactionAmount().replace(",", "");
				double transactionAmount = Double.parseDouble(transactionAmountStr);
				dataRow.createCell(0).setCellValue(list.get(i).getsNo());
				dataRow.createCell(1).setCellValue(list.get(i).getTransactionDate());
				dataRow.createCell(2).setCellValue(list.get(i).getServiceName());
				dataRow.createCell(3).setCellValue(list.get(i).getTransactionStatus());
				dataRow.createCell(4).setCellValue(transactionAmount);
				dataRow.createCell(5).setCellValue(list.get(i).getPaymentMode());
				dataRow.createCell(6).setCellValue(list.get(i).getPaymentId());
				dataRow.createCell(7).setCellValue(list.get(i).getMerchantTransactionRefId());
				dataRow.createCell(8).setCellValue(list.get(i).getTrxnRefId());
				dataRow.createCell(9).setCellValue(list.get(i).getBankRefID());
				dataRow.createCell(10).setCellValue(list.get(i).getCharges());

			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);

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
			return null;
		}
	}

	public ByteArrayInputStream exportBBPSCommissions(List<BBPSCommissionResponse> list) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "RECONCILIATION DATE", "SERVICE NAME", "FROM DATE", "TO DATE",
					"TOTAL TRANSACTION", "TOTAL COMMISSION AMOUNT", "GST", "TDS", "TOTAL SETTLEMENT AMOUNT",
					"SETTLEMENT TYPE" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(list.get(i).getsNo());
				dataRow.createCell(1).setCellValue(list.get(i).getReconciliationDate());
				dataRow.createCell(2).setCellValue(list.get(i).getServiceName());
				dataRow.createCell(3).setCellValue(list.get(i).getFromDate());
				dataRow.createCell(4).setCellValue(list.get(i).getToDate());
				dataRow.createCell(5).setCellValue(list.get(i).getReconTotalTransactionCount());
				dataRow.createCell(6).setCellValue(list.get(i).getReconTotalAmount());
				String recgstStr = list.get(i).getRecoAmountGst().replace(",", "");
				double recoGstAmount = Double.parseDouble(recgstStr);
				String rectdsStr = list.get(i).getRecoAmountTds().replace(",", "");
				double rectdsStrAmount = Double.parseDouble(rectdsStr);
				dataRow.createCell(7).setCellValue(recoGstAmount);
				dataRow.createCell(8).setCellValue(rectdsStrAmount);
				dataRow.createCell(9).setCellValue(list.get(i).getReconSettlementAmount());

				String s = null;
				if (list.get(i).getIsVerified() == 0) {
					s = "Not Settled";
					dataRow.createCell(10).setCellValue(s);
				} else {
					s = "Settled";
					dataRow.createCell(10).setCellValue(s);
				}

			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);

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
			return null;
		}
	}

	public ByteArrayInputStream exportEKYCReconciliation(List<EKYCReconciliationReportPayLoad> list) {

		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "RECONCILIATION DATE", "FROM DATE", "TO DATE", "SERVICE NAME",
					"RECONCILIATION TOTAL TRXN", "RECONCILIATION TOTAL AMOUNT" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(list.get(i).getsNo());
				dataRow.createCell(1).setCellValue(list.get(i).getReconciliationDate());
				dataRow.createCell(2).setCellValue(list.get(i).getFromDate());
				dataRow.createCell(3).setCellValue(list.get(i).getToDate());
				dataRow.createCell(4).setCellValue(list.get(i).getServiceName());
				dataRow.createCell(5).setCellValue(list.get(i).getReconciliationTotalTrxn());
				dataRow.createCell(6).setCellValue(list.get(i).getReconciliationTotalAmount());
			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);

			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (Exception e) {
			return null;
		}

	}

	public ByteArrayInputStream exportENachReconciliation(List<ENachReconciliationReportPayLoad> list) {

		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "FROM DATE", "TO DATE", "RECONCILIATION DATE", "MERCHANT TRANSACTION REF ID",
					"MERCHANT ID", "COLLECTION AMOUNT", "PRINCIPAL AMOUNT", "RECONCILIATION AMOUNT", "RECONCILIATION" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(list.get(i).getsNo());
				dataRow.createCell(1).setCellValue(list.get(i).getFromDate());
				dataRow.createCell(2).setCellValue(list.get(i).getToDate());
				dataRow.createCell(3).setCellValue(list.get(i).getReconciliationDate());
				dataRow.createCell(4).setCellValue(list.get(i).getMerchantTransactionRefId());
				dataRow.createCell(5).setCellValue(list.get(i).getMerchantId());
				String collectionAmountStr = list.get(i).getCollectionAmount().replace(",", "");
				double collectionAmount = Double.parseDouble(collectionAmountStr);
				dataRow.createCell(6).setCellValue(collectionAmount);

				String principalAmountStr = list.get(i).getPrincipalAmount().replace(",", "");
				double principalAmount = Double.parseDouble(principalAmountStr);
				dataRow.createCell(7).setCellValue(principalAmount);

				String reconciliationAmountStr = list.get(i).getReconciliationAmount().replace(",", "");
				double reconciliationAmount = Double.parseDouble(reconciliationAmountStr);
				dataRow.createCell(8).setCellValue(reconciliationAmount);

				if (list.get(i).getIsVerified().equals("0")) {
					dataRow.createCell(9).setCellValue("Not Verified");
				} else {
					dataRow.createCell(9).setCellValue("Verified");
				}

			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);

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

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (Exception e) {
			return null;
		}

	}

	public ByteArrayInputStream exportENachSattelment(List<ENachSattelmentReportPayLoad> list) {

		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "SETTLEMENT DATE", "FROM DATE", "TO DATE", "SERVICE NAME",
					"TOTAL TRANSACTION", "TOTAL AMOUNT", "SETTLEMENT AMOUNT", "SETTLEMENT", "UTR" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Creating data rows for each contact
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(list.get(i).getsNo());
				dataRow.createCell(1).setCellValue(list.get(i).getSettlementDate());
				dataRow.createCell(2).setCellValue(list.get(i).getFromDate());
				dataRow.createCell(3).setCellValue(list.get(i).getToDate());
				dataRow.createCell(4).setCellValue(list.get(i).getServiceName());
				dataRow.createCell(5).setCellValue(list.get(i).getTotalTransaction());
				String amountStr = list.get(i).getAmount().replace(",", "");
				double amount = Double.parseDouble(amountStr);
				dataRow.createCell(6).setCellValue(amount);

				String settlementAmountStr = list.get(i).getSettlementAmount().replace(",", "");
				double settlementAmount = Double.parseDouble(settlementAmountStr);
				dataRow.createCell(7).setCellValue(settlementAmount);

				if (list.get(i).getIsVerfied().equals("0")) {
					dataRow.createCell(8).setCellValue("Not Settled");
				} else {
					dataRow.createCell(8).setCellValue("Settled");
				}
				dataRow.createCell(9).setCellValue(list.get(i).getUtr());
			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);

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
		} catch (Exception e) {
			return null;
		}

	}

	public ByteArrayInputStream exportFDTxnExcel(List<FdWealthTxnResponse> fdWealthTxnResponse) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			String[] headers = { "S NO.", "WEALTH_TRXN_ID", "DATE", "MERCHANT_TRXN_REF_ID", "CUSTOMER_NAME",
					"CUSTOMER_MOBILE", "CUSTOMER_EMAIL", "PAYMENT_MODE", "ACCOUNT_NUMBER", "IFSC", "UID", "DOB",
					"GENDER", "INVESTMENT_AMOUNT", "INVESTMENT_PERIOD", "INTEREST_RATE", "PAN NUMBER", "PAYMENT TXID" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			for (int i = 0; i < fdWealthTxnResponse.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);

				dataRow.createCell(0).setCellValue(fdWealthTxnResponse.get(i).getsNo());
				dataRow.createCell(1).setCellValue(fdWealthTxnResponse.get(i).getWealthTxnId());
				dataRow.createCell(2).setCellValue(fdWealthTxnResponse.get(i).getDate());
				dataRow.createCell(3).setCellValue(fdWealthTxnResponse.get(i).getMerchantTrxnRefId());
				dataRow.createCell(4).setCellValue(fdWealthTxnResponse.get(i).getCustomerName());
				dataRow.createCell(5).setCellValue(fdWealthTxnResponse.get(i).getCustomerMobile());
				dataRow.createCell(6).setCellValue(fdWealthTxnResponse.get(i).getCustomerEmail());
				dataRow.createCell(7).setCellValue(fdWealthTxnResponse.get(i).getPaymentMode());
				dataRow.createCell(8).setCellValue(fdWealthTxnResponse.get(i).getAccountNumber());
				dataRow.createCell(9).setCellValue(fdWealthTxnResponse.get(i).getIfsc());
				dataRow.createCell(10).setCellValue(fdWealthTxnResponse.get(i).getuId());
				dataRow.createCell(11).setCellValue(fdWealthTxnResponse.get(i).getDob());
				dataRow.createCell(12).setCellValue(fdWealthTxnResponse.get(i).getGender());
				dataRow.createCell(13).setCellValue(fdWealthTxnResponse.get(i).getInvestmentAmount());
				dataRow.createCell(14).setCellValue(fdWealthTxnResponse.get(i).getInvestmentPeriod());
				dataRow.createCell(15).setCellValue(fdWealthTxnResponse.get(i).getInterestRate());
				dataRow.createCell(16).setCellValue(fdWealthTxnResponse.get(i).getPanNumber());
				dataRow.createCell(17).setCellValue(fdWealthTxnResponse.get(i).getPaymentTxId());
			}

			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);
			sheet.trackColumnForAutoSizing(12);
			sheet.trackColumnForAutoSizing(13);
			sheet.trackColumnForAutoSizing(14);
			sheet.trackColumnForAutoSizing(15);
			sheet.trackColumnForAutoSizing(16);
			sheet.trackColumnForAutoSizing(17);

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
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			sheet.autoSizeColumn(17);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());

		} catch (IOException ex) {
			return null;
		}
	}

	public ByteArrayInputStream exportSubMerchantDetails(List<SubMerchantDetailsResponse> activityList) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			String[] headers = { "S NO.", "SUB MERCHANT NAME", "SUB MERCHANT BUSSINESS NAME", "SUB MERCHANT EMAIL",
					"SUB MERCHANT MOBILE", "DATE OF BIRTH", "DATE OF BUSSINESS", "ADDRESS", "SUB MERCHANT PAN",
					"OWNERSHIP", "SUB MERCHANT ACCOUNT NO", "PINCODE", "QR ISSUE DATE", "BUSSINESS TYPE" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			for (int i = 0; i < activityList.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);

				dataRow.createCell(0).setCellValue(activityList.get(i).getsNo());
				dataRow.createCell(1).setCellValue(activityList.get(i).getSubMerchantName());
				dataRow.createCell(2).setCellValue(activityList.get(i).getSubMerchantBussinessName());
				dataRow.createCell(3).setCellValue(activityList.get(i).getSubMerchantEmail());
				dataRow.createCell(4).setCellValue(activityList.get(i).getSubMerchantMobile());
				dataRow.createCell(5).setCellValue(activityList.get(i).getDob());
				dataRow.createCell(6).setCellValue(activityList.get(i).getDoBussiness());
				dataRow.createCell(7).setCellValue(activityList.get(i).getAddress());
				dataRow.createCell(8).setCellValue(activityList.get(i).getSubMerchantPan());
				dataRow.createCell(9).setCellValue(activityList.get(i).getOwnership());
				dataRow.createCell(10).setCellValue(activityList.get(i).getAccountNumber());
				dataRow.createCell(11).setCellValue(activityList.get(i).getPincode());
				dataRow.createCell(12).setCellValue(activityList.get(i).getQrIssueDate());
				dataRow.createCell(13).setCellValue(activityList.get(i).getBussinessType());
			}

			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);
			sheet.trackColumnForAutoSizing(12);
			sheet.trackColumnForAutoSizing(13);

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
			return null;
		}
	}

	public ByteArrayInputStream subMerchantDetailsExcel(List<SubMerchantDeatilsResponse> activityList) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			String[] headers = { "S NO.", "SUB MERCHANT NAME", "SUB MERCHANT BUSSINESS NAME", "SUB MERCHANT EMAIL",
					"SUB MERCHANT MOBILE", "SUB MERCHANT ACCOUNT NO", "SUB MERCHANT IFSC", "REGISTRATION DATE", "VPA",
					"SOUND BOX", "SOUND BOX ID" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			for (int i = 0; i < activityList.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);

				dataRow.createCell(0).setCellValue(activityList.get(i).getsNo());
				dataRow.createCell(1).setCellValue(activityList.get(i).getSubMerchantName());
				dataRow.createCell(2).setCellValue(activityList.get(i).getSubMerchantBussinessName());
				dataRow.createCell(3).setCellValue(activityList.get(i).getSubMerchantEmail());
				dataRow.createCell(4).setCellValue(activityList.get(i).getSubMerchantMobile());
				dataRow.createCell(5).setCellValue(activityList.get(i).getAccountNumber());
				dataRow.createCell(6).setCellValue(activityList.get(i).getIfsc());
				dataRow.createCell(7).setCellValue(activityList.get(i).getRegistrationDate());
				dataRow.createCell(8).setCellValue(activityList.get(i).getVpa());
				dataRow.createCell(9).setCellValue(activityList.get(i).getSoundBox());
				dataRow.createCell(10).setCellValue(activityList.get(i).getSoundBoxId());

			}

			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);

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

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			return null;
		}
	}

	public ByteArrayInputStream exportSubMerchantDetailsSheet(List<SubMerchantDetailsResponse> activityList) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			String[] headers = { "S NO.", "NAME", "EMAIL", "MOBILE No", "BUSSINESS NAME", "PAN No(full details)",
					"DATE OF BIRTH", "DATE OF BUSSINESS", "ADDRESS", "BUSSINESS TYPE", "OWNERSHIP", "ACCOUNT NO",
					"PINCODE", "QR ISSUE DATE" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			for (int i = 0; i < activityList.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);

				dataRow.createCell(0).setCellValue(activityList.get(i).getsNo());
				dataRow.createCell(1).setCellValue(activityList.get(i).getSubMerchantName());
				dataRow.createCell(2).setCellValue(activityList.get(i).getSubMerchantEmail());
				dataRow.createCell(3).setCellValue(activityList.get(i).getSubMerchantMobile());
				dataRow.createCell(4).setCellValue(activityList.get(i).getSubMerchantBussinessName());
				dataRow.createCell(5).setCellValue(activityList.get(i).getSubMerchantPan());
				dataRow.createCell(6).setCellValue(activityList.get(i).getDob());
				dataRow.createCell(7).setCellValue(activityList.get(i).getDoBussiness());
				dataRow.createCell(8).setCellValue(activityList.get(i).getAddress());
				dataRow.createCell(9).setCellValue(activityList.get(i).getBussinessType());
				dataRow.createCell(10).setCellValue(activityList.get(i).getOwnership());
				dataRow.createCell(11).setCellValue(activityList.get(i).getAccountNumber());
				dataRow.createCell(12).setCellValue(activityList.get(i).getPincode());
				dataRow.createCell(13).setCellValue(activityList.get(i).getQrIssueDate());

			}

			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);
			sheet.trackColumnForAutoSizing(10);
			sheet.trackColumnForAutoSizing(11);
			sheet.trackColumnForAutoSizing(12);
			sheet.trackColumnForAutoSizing(13);

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
			return null;
		}
	}

	public ByteArrayInputStream subMerchantDetailsExcelWithName(List<SubMerchantDeatilsResponse> activityList) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			String[] headers = { "S NO.", "SUB MERCHANT NAME", "SUB MERCHANT BUSSINESS NAME", "SUB MERCHANT EMAIL",
					"SUB MERCHANT MOBILE", "SUB MERCHANT ACCOUNT NO", "SUB MERCHANT IFSC", "REGISTRATION DATE", "VPA",
					"MERCHANT BUSINESS NAME" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			for (int i = 0; i < activityList.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);

				dataRow.createCell(0).setCellValue(activityList.get(i).getsNo());
				dataRow.createCell(1).setCellValue(activityList.get(i).getSubMerchantName());
				dataRow.createCell(2).setCellValue(activityList.get(i).getSubMerchantBussinessName());
				dataRow.createCell(3).setCellValue(activityList.get(i).getSubMerchantEmail());
				dataRow.createCell(4).setCellValue(activityList.get(i).getSubMerchantMobile());
				dataRow.createCell(5).setCellValue(activityList.get(i).getAccountNumber());
				dataRow.createCell(6).setCellValue(activityList.get(i).getIfsc());
				dataRow.createCell(7).setCellValue(activityList.get(i).getRegistrationDate());
				dataRow.createCell(8).setCellValue(activityList.get(i).getVpa());
				dataRow.createCell(9).setCellValue(activityList.get(i).getMerchantBussinessName());
			}

			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);
			sheet.trackColumnForAutoSizing(6);
			sheet.trackColumnForAutoSizing(7);
			sheet.trackColumnForAutoSizing(8);
			sheet.trackColumnForAutoSizing(9);

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
			return null;
		}
	}

	public ByteArrayInputStream exportBBPSCommissionsListExcel(List<MerchantCommissionDeatilsResponse> list) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Transaction Statement");
			Row row = sheet.createRow(0);

			// Define header cell style
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Creating header cells
			String[] headers = { "S NO.", "SERVICE NAME", "MERCHANT SERVICE COMMISSION START",
					"MERCHANT SERVICE COMMISSION END", "MERCHANT SERVICE COMMISSION RATE", "TYPE" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerCellStyle);
			}

			AtomicInteger atomicInteger = new AtomicInteger(1);

			// Creating data rows for each contact
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(atomicInteger.getAndIncrement());
				dataRow.createCell(1).setCellValue(list.get(i).getServiceName());
				dataRow.createCell(2).setCellValue(list.get(i).getMerchantServiceCommissionStart());
				dataRow.createCell(3).setCellValue(list.get(i).getMerchantServiceCommissionEnd());
				dataRow.createCell(4).setCellValue(list.get(i).getMerchantServiceCommissionRate());
				dataRow.createCell(5).setCellValue(list.get(i).getType());
			}

			// Making size of column auto resize to fit with data
			sheet.trackColumnForAutoSizing(0);
			sheet.trackColumnForAutoSizing(1);
			sheet.trackColumnForAutoSizing(2);
			sheet.trackColumnForAutoSizing(3);
			sheet.trackColumnForAutoSizing(4);
			sheet.trackColumnForAutoSizing(5);

			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			return null;
		}
	}

}
