package com.fidypay.request;

import com.opencsv.bean.CsvBindByName;

public class BulkEkycUsersRequest {
	
	@CsvBindByName(column = "S NO")
	private String Sno;

	@CsvBindByName(column = "USER NAME")
	private String userName;

	@CsvBindByName(column = "USER EMAIL")
	private String userEmail;

	@CsvBindByName(column = "USER MOBILE")
	private String userMobile;
	
	

	public String getSno() {
		return Sno;
	}

	public void setSno(String sno) {
		Sno = sno;
	}

	
	


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

		
	

}
