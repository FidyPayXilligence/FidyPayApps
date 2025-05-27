package com.fidypay.utils.ex;


import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFBackground extends PdfPageEventHelper {

	
	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			float width = PageSize.A4.getWidth() - 40;
			float height = PageSize.A4.getHeight() - 40;
			if (document.getPageNumber() == 0) {
				/* For Every New Service Vice Page */
				document.setMargins(35, 35, 35, 40);// (left,right,top,bottom)
			} else {
				/* For Every Extended Page */
				document.setMargins(35, 35, 115, 40);// (left,right,top,bottom)
			}
			Image background = Image.getInstance("https://fidypaypdfimg.s3.ap-south-1.amazonaws.com/pdfImg.png");
			writer.getDirectContentUnder().addImage(background, width, 0, 0, height, 20, 20);
		} catch (Exception e) {
	      e.printStackTrace();
		}
	}
}
