package com.fidypay.controller;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.ServiceProvidersRepository;

//Important Note---------------This function is used to fetch the volume of the transaction of perticular provider like:Signzy/Decentro/Karza

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class TestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@PostMapping("/listOfTotalTransactionAccordingToServiceName")
	public Map<String, Object> listOfTotalTransactionAccordingToServiceName(
			@RequestHeader("serviceProvider") String serviceProvider, @RequestHeader("startDate") String startDate,
			@RequestHeader("endDate") String endDate) {

		Map<String, Object> map = new HashMap<>();

		ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
		System.out.println(spInfo.getSpBankName().toString());

		Long serviceProviderId = spInfo.getServiceProviderId();

		LOGGER.info(" serviceProviderId: {}", serviceProviderId);

		List<String> list = ekycTransactionDetailsRepository.findServiceNameList(startDate, endDate, serviceProviderId);

		LOGGER.info(" list: {}", list.size());
		for (String serviceName : list) {

			LOGGER.info(" serviceName: {}", serviceName);

			List<Object[]> count = ekycTransactionDetailsRepository.findCountOfServiceName(startDate, endDate,
					serviceProviderId, serviceName);

			Object[] result = count.get(0);
			String creationDate = result[0].toString();
			Long totalCount = ((BigInteger) result[1]).longValue();

			LOGGER.info("Creation Date: {}", creationDate);
			LOGGER.info("Transaction Count: {}", totalCount);

			map.put(serviceName, "Total Tansaction--> " + totalCount + " And Last Transaction-----> " + creationDate);

		}

		return map;
	}

}
