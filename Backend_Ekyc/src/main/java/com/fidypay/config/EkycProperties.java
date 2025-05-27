package com.fidypay.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fidypay.service.provider.AadhaarServiceProvider;

@Component
@ConfigurationProperties(prefix = "fidypay.ekyc")
public class EkycProperties {

    public static final String SERVICE_ID_GST = "GST";

    private Map<String, String> serviceProviderMappings = new HashMap<>();


    public Map<String, String> getServiceProviderMappings() {
        return serviceProviderMappings;
    }

    public void setServiceProviderMapping(Map<String, String> serviceProviderMappings) {
        this.serviceProviderMappings = serviceProviderMappings;
    }

    public String getProvider(String serviceId) {
        String providerId = serviceProviderMappings.get(serviceId);
        if (providerId == null) {
            return getDefaultProviderId(serviceId);
        }

        return providerId;
    }

    private String getDefaultProviderId(String serviceId) {

        switch (serviceId) {
            case AadhaarServiceProvider.SERVICE_ID:
                return "Signzy";

            case SERVICE_ID_GST:
                return "Signzy";

            default:
                throw new IllegalArgumentException("Invalid service ID: " + serviceId);
        }
    }

}
