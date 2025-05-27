package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserPermissionType {

	@NotBlank
	@Pattern(regexp = "0|1", message = "please use 0 or 1 value")
	private String view;
	@NotBlank
	@Pattern(regexp = "0|1", message = "please use 0 or 1 value")
	private String edit;
	@NotBlank
	@Pattern(regexp = "0|1", message = "please use 0 or 1 value")
	private String none;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getNone() {
		return none;
	}

	public void setNone(String none) {
		this.none = none;
	}

}
