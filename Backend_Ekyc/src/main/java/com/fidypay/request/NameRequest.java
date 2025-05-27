package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NameRequest {

	@NotBlank(message = "name1 cannot be empty")
	@Pattern(regexp = "^[a-zA-Z0-9 ]+$"
 , message = "name1 should be alphanumeric only." )
	@Size(min=1 , max= 50 , message = "The size of name1 should be between 1 and 50.")
	private String name1;
	
	@NotBlank(message = "name2 cannot be empty")
	@Pattern(regexp = "^[a-zA-Z0-9 ]+$" , message = "name2 should be alphanumeric only." )
	@Size(min=1 , max= 50 , message = "The size of name2 should be between 1 and 50.")
	private String name2;

	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	

	
	
}
