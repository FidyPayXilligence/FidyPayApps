package com.fidypay.utils.constants;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

public class AuthenticationVerify {

	public static String authentication(HttpServletRequest request) {

		String upd = request.getHeader("authorization");
		String pair = new String(Base64.decodeBase64(upd.substring(6)));
		String password = pair.split(":")[1];
		return password;

	}

	public static String authenticationPassword(String request) {

		String pair = new String(Base64.decodeBase64(request.substring(6)));
		String password = pair.split(":")[1];
		return password;

	}

	public static String authenticationUsername(String request) {

		String pair = new String(Base64.decodeBase64(request.substring(6)));
		String username = pair.split(":")[0];
		return username;

	}

}
