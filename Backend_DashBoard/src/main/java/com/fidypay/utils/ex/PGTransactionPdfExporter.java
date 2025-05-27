package com.fidypay.utils.ex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.Merchants;
import com.fidypay.response.PayoutTransactionsReportPayLoad;
import com.fidypay.response.PgTransactionResponse;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PGTransactionPdfExporter {

	private static final Logger logger = LoggerFactory.getLogger(TransactionPdfExporter.class);

	private ArrayList<String> setTableColumns(String column1, String column2, String column3, String column4,
			String column5, String column6, String column7, String column8, String column9, String column10) {
		ArrayList<String> tab = new ArrayList<String>();
		tab.add(column1);
		tab.add(column2);
		tab.add(column3);
		tab.add(column4);
		tab.add(column5);
		tab.add(column6);
		tab.add(column7);
		tab.add(column8);
		tab.add(column9);
		tab.add(column10);

		return tab;
	}

	private void insertTableHeader(PdfPTable table, String text, int align, int colspan, Font font, List<String> list) {

		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		BaseColor color = new BaseColor(177, 177, 177);
		cell.setBorderColor(new BaseColor(160, 160, 160));
		cell.setBackgroundColor(color);
		float[] columnWidths = new float[] { 10f, 35f, 30f, 30f, 40f, 25f, 30f, 35f, 30f, 25f };
		try {
			table.setWidths(columnWidths);
		} catch (DocumentException e) {
			// e.printStackTrace();
		}
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(15f);
		}
		cell.setPaddingTop(0);
		table.addCell(cell);
	}

	protected void insertValueIntoRow(PdfPTable table, String text, int align, int colspan, Font font,
			List<String> list) {
		// create a new cell with the specified Text and Font
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(20f);
		}
		cell.setBorderColor(new BaseColor(160, 160, 160));
		table.addCell(cell);

	}

	public PdfWriter generateDynamicpdf(HttpServletResponse response, List<PgTransactionResponse> list2,
			String startDate, String endDate, Merchants merchants) throws ParseException {

		SimpleDateFormat dateformate = new SimpleDateFormat("yyyy-MM-dd");
		Date start2 = dateformate.parse(startDate);
		Date end2 = dateformate.parse(endDate);

		SimpleDateFormat dateformate1 = new SimpleDateFormat("dd-MM-yyyy");
		startDate = dateformate1.format(start2);
		endDate = dateformate1.format(end2);
		Font otherfont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(0, 0, 0));
		Document doc = new Document(new Rectangle(792, 612));
		PdfWriter docWriter = null;
		try {
			docWriter = PdfWriter.getInstance(doc, response.getOutputStream());

			Paragraph fromTotoDate = new Paragraph("Statement Date : " + startDate + " to " + endDate, otherfont);
			fromTotoDate.setAlignment(Element.ALIGN_LEFT);
			// fromTotoDate.setIndentationRight(5);

			Paragraph businessName = new Paragraph(
					"Business Name : " + Encryption.decString(merchants.getMerchantBusinessName()), otherfont);
			businessName.setAlignment(Element.ALIGN_LEFT);

			Paragraph address = new Paragraph("Address : " + Encryption.decString(merchants.getMerchantAddress1()),
					otherfont);
			address.setAlignment(Element.ALIGN_LEFT);

			Paragraph city = new Paragraph("City : " + Encryption.decString(merchants.getMerchantCity()), otherfont);
			city.setAlignment(Element.ALIGN_LEFT);

			docWriter.setPageEvent(new PDFBackground());
			doc.addAuthor("Jambopay Express Pvt Ltd");
			doc.addCreationDate();
			doc.addProducer();
			doc.addCreator("FidyPay");
			doc.addTitle("Transaction Statement");
			doc.setPageSize(PageSize.LETTER);
			doc.setPageSize(PageSize.A4);
			doc.open();

			if (list2.size() == 0) {
				logger.info("No Records Found");

				Paragraph p = new Paragraph("\n\n\n\n");
				Paragraph nextline = new Paragraph("\n");
				Paragraph norecords = new Paragraph("Sorry No Records Found !!",
						new Font(FontFamily.TIMES_ROMAN, 20, Font.NORMAL, new BaseColor(160, 005, 00)));
				norecords.setAlignment(Element.ALIGN_CENTER);
				doc.add(nextline);
				doc.add(p);
				doc.add(nextline);
				doc.add(businessName);
				doc.add(address);
				doc.add(city);
				doc.add(fromTotoDate);
				doc.add(nextline);
				doc.add(norecords);
				doc.resetPageCount();
				logger.info("PDF GENARATED SUCCESSFULY");
			} else {
				PdfPTable table = generateFirstPage(list2);

				Paragraph p = new Paragraph("\nTransaction Statement\n\n",
						new Font(FontFamily.TIMES_ROMAN, 15, Font.NORMAL, new BaseColor(71, 71, 73)));
				p.setAlignment(Element.ALIGN_CENTER);
				Paragraph nextline1 = new Paragraph("\n");
				doc.add(nextline1);
				doc.add(p);
				doc.add(businessName);
				doc.add(address);
				doc.add(city);
				doc.add(fromTotoDate);
				doc.add(nextline1);
				doc.add(table);
				doc.resetPageCount();
				doc.newPage();
				logger.info("PDF GENARATED SUCCESSFULY");
			}
		} catch (DocumentException dex) {
			logger.error("DocumentException : " + dex.fillInStackTrace());
		} catch (Exception ex) {
			logger.error("Exception : " + ex.fillInStackTrace());
		} finally {
			if (doc != null) {
				doc.close();
			}
			if (docWriter != null) {
				docWriter.close();
			}
		}
		return docWriter;
	}

	public PdfPTable generateFirstPage(List<PgTransactionResponse> activityList) {
		logger.info("~~~~~~~~~~Transaction Statement PDF~~~~~~~~~");
		PdfPTable table = null;
		try {
			Font headerfont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(255, 255, 255));
			Font otherfont = new Font(FontFamily.TIMES_ROMAN, 8, Font.NORMAL, new BaseColor(0, 0, 0));
			ArrayList<String> tab = new ArrayList<String>();
			tab = setTableColumns("S. NO.", "TRANSACTION DATE", "SERVICE NAME", "TRANSACTION STATUS",
					"TRANSACTION AMOUNT", "PAYMENT MODE", "PAYMENT ID", "MERCHANT TRANSACTION REF ID", "TRXN REF ID",
					"BANK REF ID");
			int len = tab.size();
			table = new PdfPTable(len);
			table.setHeaderRows(1);
			table.setWidthPercentage(90);
			for (int i = 0; i < len; i++) {
				insertTableHeader(table, "" + tab.get(i), Element.ALIGN_LEFT, 1, headerfont, tab);
			}
			for (PgTransactionResponse transactionsReportPayLoad : activityList) {

				insertValueIntoRow(table, "" + transactionsReportPayLoad.getsNo(), Element.ALIGN_LEFT, 1, otherfont,
						null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getTransactionDate(), Element.ALIGN_LEFT, 1,
						otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getServiceName(), Element.ALIGN_LEFT, 1,
						otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getTransactionStatus(), Element.ALIGN_LEFT, 1,
						otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getTransactionAmount(), Element.ALIGN_LEFT, 1,
						otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getPaymentMode(), Element.ALIGN_LEFT, 1,
						otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getPaymentId(), Element.ALIGN_LEFT, 1,
						otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getMerchantTransactionRefId(),
						Element.ALIGN_LEFT, 1, otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getTrxnRefId(), Element.ALIGN_LEFT, 1,
						otherfont, null);
				insertValueIntoRow(table, "" + transactionsReportPayLoad.getBankRefID(), Element.ALIGN_LEFT, 1,
						otherfont, null);

			}
			return table;
		} catch (Exception e) {
			logger.error("Exception : " + e.fillInStackTrace());
		}
		return table;
	}

}
