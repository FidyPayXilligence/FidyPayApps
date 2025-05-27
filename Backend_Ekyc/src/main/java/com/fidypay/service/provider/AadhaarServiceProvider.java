package com.fidypay.service.provider;

import java.util.Map;

import com.fidypay.exception.EkycProviderException;
import com.fidypay.request.ValidateOtp;

public interface AadhaarServiceProvider extends EkycServiceProvider {

    public static final String SERVICE_ID = "Aadhaar";


    public Map<String, Object> generateOtp(String aadhaarNumber, long merchantId)
        throws EkycProviderException;

    public Map<String, Object> validateOtp(ValidateOtp validateotp) throws EkycProviderException;

    
    @Override
    public default Class<?> getProviderType() {
        return AadhaarServiceProvider.class;
    }

    @Override
    public default String getServiceId() {
        return SERVICE_ID;
    }
}
