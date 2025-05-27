package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.ServiceInfo;

@Repository
public interface ServiceInfoRepository extends JpaRepository<ServiceInfo, Long> {

	@Query(value = "SELECT * FROM SERVICE_INFO si WHERE si.SERVICE_NAME=?1", nativeQuery = true)
	ServiceInfo findServiceByName(String serviceName);

	boolean existsByServiceName(String serviceName);

	ServiceInfo findByServiceName(String serviceName);

}
