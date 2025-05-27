package com.fidypay.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fidypay.service.provider.EkycServiceProvider;

@Component
public class EkycServiceResolver {

    private final EkycProperties ekycProperties;

    private Map<String, EkycServiceProvider> providerServices;


    public EkycServiceResolver(EkycProperties ekycProperties) {
        this.ekycProperties = ekycProperties;
    }

    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        List<EkycServiceProvider> services = context.getBeansOfType(EkycServiceProvider.class)
            .values().stream().collect(Collectors.toList());
        
        // Map service name to bean
        this.providerServices = services.stream().collect(
            Collectors.toMap(
                service -> context.getBeanNamesForType(service.getClass())[0].toLowerCase(),
                Function.identity()
            )
        );
    }

    public <T> T getServiceProvider(String serviceId, Class<T> clazz) {
        String providerId = ekycProperties.getProvider(serviceId);

        String key = (providerId + "_" + serviceId).toLowerCase(); // Eg: BeFiSc_Aadhar
        EkycServiceProvider service = providerServices.get(key);
        return clazz.cast(service);
    }

}
