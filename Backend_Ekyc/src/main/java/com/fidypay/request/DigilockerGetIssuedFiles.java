package com.fidypay.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class DigilockerGetIssuedFiles {
	UUID randomUUID = UUID.randomUUID();
	String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);


	@JsonProperty("merchantTransactionId")
	private String initial_decentro_transaction_id;

	@JsonIgnore
	@JsonProperty("merchantConsent")
	@ApiModelProperty(hidden = true)
	private Boolean consent = true;

	@JsonIgnore
	@JsonProperty("merchantPurpose")
	@ApiModelProperty(hidden = true)
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
