package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateUserPassword {

	private long merchantUserId;

	@NotBlank(message = "Current Password can not be empty")
	private String oldPassword;

	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!*-_@#$%^&+=])(?=\\S+$).*$", message = "There must be at least eight characters in the password, including at least one of each: a digit, a lowercase letter, an uppercase letter, and a special character. Whitespace is not permitted anywhere in the password.")
	@Size(min = 8, max = 20, message = "password must be between 8 to 20")
	@NotBlank(message = "newPassword can not be empty")
	private String newPassword;

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
