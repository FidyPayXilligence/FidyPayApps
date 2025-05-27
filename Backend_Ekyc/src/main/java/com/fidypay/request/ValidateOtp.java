package com.fidypay.request;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;

@ToString
public class ValidateOtp {

    UUID randomUUID = UUID.randomUUID();

    String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

    @ApiModelProperty(hidden = true)
    @JsonProperty(value = "merchantReferenceId")
    private String reference_id = randomStr;

    @JsonProperty(value = "aadhaarNumber")
    private String aadhaarNumber;

    @JsonIgnore
    @JsonProperty(value = "merchantConsent")
    private final Boolean consent = true;


    @JsonIgnore
    @JsonProperty(value = "merchantPurpose")
    private final String purpose = "Aadhaar Card Verification";

    @JsonProperty(value = "merchantTxnRefId")
    // @NotEmpty(message = "merchantTxnRefId should not be null.")
    private String initiation_transaction_id;

    @JsonProperty(value = "otp")
    @NotEmpty(message = "OTP should not be null.")
    private String otp;


    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public String getInitiation_transaction_id() {
        return initiation_transaction_id;
    }

    public void setInitiation_transaction_id(String initiation_transaction_id) {
        this.initiation_transaction_id = initiation_transaction_id;
    }


    public String getOtp() {
        return otp;
    }

}