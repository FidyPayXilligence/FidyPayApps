package com.fidypay.request;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fidypay.utils.validation.Uppercase;

/**
 * @author prave
 * @Date 09-10-2023
 */
public class MerchantUserActivityRequest {

    private Long merchantUserId;
    
    @NotBlank(message = "apiUrl can not be blank")
    private String apiUrl;

    @NotBlank(message = "productName can not be blank")
    @Pattern(regexp = "BANKING|EKYC|ENACH|UPI|BBPS|PG|ADMINISTRATOR", message = "productName -> please pass BANKING, EKYC, ENACH, UPI, BBPS, PG, or ADMINISTRATOR on productName parameter")
    private String productName;

    @NotBlank(message = "apiName can not be blank")
    @Uppercase
    private String apiName;
    @NotBlank(message = "apiRequest can not be blank")
    private String apiRequest;

    public Long getMerchantUserId() {
        return merchantUserId;
    }

    public void setMerchantUserId(Long merchantUserId) {
        this.merchantUserId = merchantUserId;
    }


    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiRequest() {
        return apiRequest;
    }

    public void setApiRequest(String apiRequest) {
        this.apiRequest = apiRequest;
    }

    @Override
    public String toString() {
        return "MerchantUserActivityRequest{" +
                "merchantUserId=" + merchantUserId +
                ", apiUrl='" + apiUrl + '\'' +
                ", productName='" + productName + '\'' +
                ", apiName='" + apiName + '\'' +
                ", apiRequest='" + apiRequest + '\'' +
                '}';
    }
}
