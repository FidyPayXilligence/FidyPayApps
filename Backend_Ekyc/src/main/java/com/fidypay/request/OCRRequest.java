package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class OCRRequest {

	@NotBlank(message = "filePath cannot be empty")
	private String filePath;
	
	@NotBlank(message = "docType cannot be empty")
	@Pattern(regexp = "NA|dl", message = "Please Pass dl for  driving licence or NA on docType parameter")
	private String docType;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}
	

	
	

	
}
