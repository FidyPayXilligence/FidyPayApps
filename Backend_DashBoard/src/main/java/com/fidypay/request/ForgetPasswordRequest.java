package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ForgetPasswordRequest {


	@NotBlank(message = "token cannot be empty")
	private String token;

	@NotBlank(message = "password cannot be empty")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!*-_@#$%^&+=])(?=\\S+$).*$", message = "There must be at least eight characters in the password, including at least one of each: a digit, a lowercase letter, an uppercase letter, and a special character. Whitespace is not permitted anywhere in the password.")
    @Size(min = 8, max = 20, message = "password must be between 8 to 20")
	private String password;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
