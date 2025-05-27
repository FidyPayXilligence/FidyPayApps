package com.fidypay.request;



import java.util.UUID;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateOtpRequest {
UUID randomUUID = UUID.randomUUID();
String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

@JsonProperty(value = "merchantReferenceId")
private String reference_id = randomStr;

@JsonIgnore
@JsonProperty(value = "merchantConsent")
private final Boolean consent = true;

@JsonIgnore
@JsonProperty(value = "merchantPurpose")
private final String purpose = "Aadhaar Card Verification";

@JsonProperty(value = "merchantAadhaarNumber")
@NotEmpty(message = "merchantAadhaarNumber should not be null.")
private String aadhaar_number;

public String getReference_id() {
return reference_id;
}

public void setReference_id(String reference_id) {
this.reference_id = reference_id;
}

public String getAadhaar_number() {
return aadhaar_number;
}

public void setAadhaar_number(String aadhaar_number) {
this.aadhaar_number = aadhaar_number;
}

}
