package com.fidypay.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class DigilockerInitiateSession {
	UUID randomUUID = UUID.randomUUID();
	String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

	@ApiModelProperty(hidden = true)
	@JsonProperty(value = "merchantReferenceId")
	private String reference_id = randomStr;

	@ApiModelProperty(hidden = true)
	@JsonProperty(value = "merchantConsent")
	private boolean consent = true;

	@ApiModelProperty(hidden = true)
	@JsonProperty(value = "merchantPurpose")
	private String consent_purpose = "For Banking Purpose Only";

	public String getReference_id() {
	return reference_id;
	}

	public void setReference_id(String reference_id) {
	this.reference_id = reference_id;
	}
}
