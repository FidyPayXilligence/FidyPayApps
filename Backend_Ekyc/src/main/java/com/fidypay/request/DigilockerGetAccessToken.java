package com.fidypay.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class DigilockerGetAccessToken {
	
	UUID randomUUID = UUID.randomUUID();
	String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

	@JsonProperty("merchantTransactionId")
	private String initial_decentro_transaction_id;

	@JsonProperty("digilockerCode")
	private String digilocker_code;

	@ApiModelProperty(hidden = true)
	@JsonProperty("merchantConsent")
	private Boolean consent = true;

	@ApiModelProperty(hidden = true)
	@JsonProperty("merchantPurpose")
	private String consent_purpose = "for banking purpose only";

	@ApiModelProperty(hidden = true)
	@JsonProperty("merchantReferenceId")
	private String reference_id = randomStr;

	public String getReference_id() {
	return reference_id;
	}

	public void setReference_id(String reference_id) {
	this.reference_id = reference_id;
	}
	
	
}
