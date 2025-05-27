package com.fidypay.utils.ex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fidypay.dto.BulkEkycUserPayload;

public class ExcelExporter {

	public File exportUnVerifiedMandateDataForEkycUsers(List<BulkEkycUserPayload> unVerifiedDataList) {
		try (Workbook workbook = new XSSFWorkbook()) {
			DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
			String currentDateTime = dateFormatter.format(new Date());
			
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
			cell.setCellValue("WORKFLOW UNIQUE ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("USER NAME");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("USER EMAIL");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(4);
			cell.setCellValue("USER MOBILE");
			cell.setCellStyle(headerCellStyle);


			cell = row.createCell(5);
			cell.setCellValue("REASON");
			cell.setCellStyle(headerCellStyle);

			
			
			

			// Creating data rows for each contact
			for (int i = 0; i < unVerifiedDataList.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(unVerifiedDataList.get(i).getSno());
				dataRow.createCell(1).setCellValue(unVerifiedDataList.get(i).getWorkflowUniqueId());
				dataRow.createCell(2).setCellValue(unVerifiedDataList.get(i).getUserName());
				dataRow.createCell(3).setCellValue(unVerifiedDataList.get(i).getUserEmail());
				dataRow.createCell(4).setCellValue(unVerifiedDataList.get(i).getUserMobile());
				
				
				System.out.println("Reason: "+unVerifiedDataList.get(i).getReason());
				
//				if(!unVerifiedDataList.get(i).getReason().startsWith("}")) {
				dataRow.createCell(5).setCellValue(unVerifiedDataList.get(i).getReason());
//				}
//				else {
//					dataRow.createCell(5).setCellValue("");
//				}
				
			
				
		}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);

			


			File file = new File("UnVerified_EKyc_User_Data_Report_" + currentDateTime + ".xlsx");
			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
			return file;
		} catch (IOException ex) {
			// LOGGER.error("Error during export Excel file", ex);
			return null;
		}

	}
	
}
