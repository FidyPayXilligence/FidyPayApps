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
import com.fidypay.response.FdWealthTxnResponse;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class FdTransactionsPdfExporter {

    private static final Logger log = LoggerFactory.getLogger(FdTransactionsPdfExporter.class);
    public PdfWriter generateDynamicPdf(HttpServletResponse response, List<FdWealthTxnResponse> list2,
                                        String startDate, String endDate, Merchants merchants) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start2 = dateFormat.parse(startDate);
        Date end2 = dateFormat.parse(endDate);

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
        startDate = dateFormat1.format(start2);
        endDate = dateFormat1.format(end2);
        Font otherfont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(0, 0, 0));
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
            doc.addTitle("FD Transactions PDF");
            doc.setPageSize(PageSize.LETTER);
            doc.setPageSize(PageSize.A4);
            doc.open();

            if (list2.size() == 0) {
                log.info("No Records Found");

                Paragraph p = new Paragraph("\n\n\n\n");
                Paragraph nextLine = new Paragraph("\n");
                Paragraph noRecords = new Paragraph("Sorry No Records Found !!",
                        new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL, new BaseColor(160, 005, 00)));
                noRecords.setAlignment(Element.ALIGN_CENTER);
                doc.add(nextLine);
                doc.add(p);
                doc.add(nextLine);
                doc.add(businessName);
                doc.add(address);
                doc.add(city);
                doc.add(fromTotoDate);
                doc.add(nextLine);
                doc.add(noRecords);
                doc.resetPageCount();
                log.info("PDF GENERATED SUCCESSFULLY");
            } else {
                PdfPTable table = generateFirstPage(list2);

                Paragraph p = new Paragraph("\nFD Transactions PDF\n\n",
                        new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.NORMAL, new BaseColor(71, 71, 73)));
                p.setAlignment(Element.ALIGN_CENTER);
                Paragraph nextLine1 = new Paragraph("\n");
                doc.add(nextLine1);
                doc.add(p);
                doc.add(businessName);
                doc.add(address);
                doc.add(city);
                doc.add(fromTotoDate);
                doc.add(nextLine1);
                doc.add(table);
                doc.resetPageCount();
                doc.newPage();
                log.info("PDF GENERATED SUCCESSFULLY");
            }
        } catch (DocumentException dex) {
            log.error("DocumentException : " + dex.fillInStackTrace());
        } catch (Exception ex) {
            log.error("Exception : " + ex.fillInStackTrace());
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

    //
    public PdfPTable generateFirstPage(List<FdWealthTxnResponse> activityList) {
        log.info("================= FD Transactions PDF =================");
        PdfPTable table = null;

        try {
            Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(255, 255, 255));
            Font otherFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL, new BaseColor(0, 0, 0));
            ArrayList<String> tab = new ArrayList<String>();

            tab = setTableColumns("S NO.", "DATE", "CUSTOMER_NAME", "CUSTOMER_MOBILE", "CUSTOMER_EMAIL", "PAYMENT_MODE", "ACCOUNT_NUMBER", "IFSC", "UID", "DOB",
                    "GENDER", "INVESTMENT_AMOUNT", "INVESTMENT_PERIOD", "INTEREST_RATE","PAN NUMBER","PAYMENT TXID");

            int len = tab.size();
            table = new PdfPTable(len);
            table.setHeaderRows(1);
            table.setWidthPercentage(90);

            for (int i = 0; i < len; i++) {
                insertTableHeader(table, "" + tab.get(i), Element.ALIGN_LEFT, 1, headerFont, tab);
            }

            for (FdWealthTxnResponse fdWealthTxnResponse : activityList) {

                insertValueIntoRow(table, "" + fdWealthTxnResponse.getsNo(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getDate(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getCustomerName(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getCustomerMobile(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getCustomerEmail(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getPaymentMode(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getAccountNumber(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getIfsc(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getuId(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getDob(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getGender(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getInvestmentAmount(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getInvestmentPeriod(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getInterestRate(), Element.ALIGN_LEFT, 1, otherFont, null);
                insertValueIntoRow(table, "" + fdWealthTxnResponse.getPanNumber(), Element.ALIGN_LEFT, 1, otherFont, null);
				insertValueIntoRow(table, "" + fdWealthTxnResponse.getPaymentTxId(), Element.ALIGN_LEFT, 1, otherFont,
						null);

            }
            return table;
        } catch (Exception ex) {
            log.error("Exception : " + ex.fillInStackTrace());
        }
        return table;
    }

    //
    private ArrayList<String> setTableColumns(String column1, String column2, String column3, String column4, String column5, String column6,
                                              String column7, String column8, String column9, String column10,
                                              String column11, String column12, String column13, String column14, String column15, String column16) {
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
        tab.add(column11);
        tab.add(column12);
        tab.add(column13);
        tab.add(column14);
        tab.add(column15);
        tab.add(column16);
        return tab;
    }

    //
    private void insertTableHeader(PdfPTable table, String text, int align, int colsPan, Font font, List<String> list) {

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

    //
    protected void insertValueIntoRow(PdfPTable table, String text, int align, int colsPan, Font font, List<String> list) {
        // create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(20f);
        }
        cell.setBorderColor(new BaseColor(160, 160, 160));
        table.addCell(cell);
    }
}
