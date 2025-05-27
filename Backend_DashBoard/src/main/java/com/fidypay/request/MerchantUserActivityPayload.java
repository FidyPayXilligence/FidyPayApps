package com.fidypay.request;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;

/**
 * @author prave
 * @Date 09-10-2023
 */
public class MerchantUserActivityPayload {

    @NotBlank(message = "fromDate can not be blank")
    private String fromDate;
    @NotBlank(message = "toDate can not be blank")
    private String toDate;
    @Value("10")
    private Integer pageSize;
    @Value("0")
    private Integer pageNo;
    private Long merchantUserId;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Long getMerchantUserId() {
        return merchantUserId;
    }

    public void setMerchantUserId(Long merchantUserId) {
        this.merchantUserId = merchantUserId;
    }
}
