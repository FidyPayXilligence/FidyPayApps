package com.fidypay.response;

/**
 * @author prave
 * @Date 09-10-2023
 */
public class MerchantUserActivityResponse {

    private String merchantUserName;
    private Long merchantUserId;
    private Long merchantId;
    private String date;
    private String apiUrl;
    private String productName;
    private String apiName;
    private String apiRequest;

    public String getMerchantUserName() {
        return merchantUserName;
    }

    public void setMerchantUserName(String merchantUserName) {
        this.merchantUserName = merchantUserName;
    }

    public Long getMerchantUserId() {
        return merchantUserId;
    }

    public void setMerchantUserId(Long merchantUserId) {
        this.merchantUserId = merchantUserId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
