package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class DrivingLicenceKarzaRequest {

	@Size(min = 10, max = 20, message = "dlNo must be between 10 to 20.")
	@NotBlank(message = "dlNo can not be Empty")
	private String dlNo;

	@Pattern(regexp = "[0-9]{2}-[0-9]{2}-[0-9]{4}", message = "Invalid DOB, Please pass in dd-mm-yyyy format.")
	@NotBlank(message = "dob can not be Empty")
	private String dob;

	public String getDlNo() {
		return dlNo;
	}

	public String getDob() {
		return dob;
	}

	public void setDlNo(String dlNo) {
		this.dlNo = dlNo;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

}
