package com.fidypay.request;

import javax.validation.constraints.NotBlank;

public class EncryptedRequest {

    @NotBlank(message = "Invalid request")
    private String request;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "{" +
                "request='" + request + '\'' +
                '}';
    }
}
