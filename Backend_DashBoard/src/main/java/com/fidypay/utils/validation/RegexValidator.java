package com.fidypay.utils.validation;

public class RegexValidator {

	private RegexValidator() {
	}

	public static boolean checkAlphanumeric(String value) {
		return value.matches("^[a-zA-Z0-9]+$");
	}

}
