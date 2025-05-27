
package com.fidypay.request;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class Validate {

	UUID randomUUID = UUID.randomUUID();
	String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

	@ApiModelProperty(hidden = true)
	@JsonProperty(value = "merchantReferenceId")
	private String reference_id = randomStr;

	@JsonProperty(value = "merchantDocumentType")
	@NotEmpty(message = "merchantDocumentType should not be null.")
	private String document_type;

	@JsonProperty(value = "documentIdNumber")
	@NotEmpty(message = "documentIdNumber should not be null.")
	private String id_number;

	@JsonIgnore
	@JsonProperty(value = "merchantConsent")
	private String consent = "Y";

//	@Pattern(regexp = "()|([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Invalid Date Of Birth(dob) format. The format should be yyyy-mm-dd.")
	@JsonIgnore
	@JsonProperty(value = "merchantDob")
	private String dob = "1900-01-25";

	@JsonIgnore
	@JsonProperty(value = "merchantPurpose")
	private String consent_purpose = "For bank account purpose only";

	public String getReference_id() {
		return reference_id;
	}

	public void setReference_id(String reference_id) {
		this.reference_id = reference_id;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

}