package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.ServiceProviders;

public interface ServiceProvidersRepository extends JpaRepository<ServiceProviders, Long> {

	@Query(value = "SELECT * FROM SERVICE_PROVIDERS sp WHERE sp.SP_NAME=?1", nativeQuery = true)
	ServiceProviders findBySpName(String serviceName);

}
