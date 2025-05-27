package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.PartnerServices;

public interface PartnerServiceRepository extends JpaRepository<PartnerServices, Long> {

	
	@Query(value = "SELECT * from PARTNER_SERVICE ps  Where ps.PARTNER_ID=?1 AND ps.SERVICE_ID=?2 AND ps.SERVICE_PROVIDER_ID=?3", nativeQuery = true)
	PartnerServices findByPartnerIdAndServiceIdAndServiceProviderId(long partnerId, long serviceId,
			long serviceProviderId);
	
	@Query(value = "SELECT * from PARTNER_SERVICE ps  Where ps.PARTNER_ID=?1 AND ps.SERVICE_ID=?2", nativeQuery = true)
	PartnerServices findByPartnerIdAndServiceId(long partnerId, long serviceId);

	@Query(value = "SELECT * from PARTNER_SERVICE ps  ORDER BY ps.PARTNER_SERVICE_ID DESC", nativeQuery = true)
	List<PartnerServices> findAllPartnersService(Pageable pageable);

	@Query(value = "SELECT count(*) from PARTNER_SERVICE", nativeQuery = true)
	int totalPartnersService();

	
	@Query(value = "SELECT * from PARTNER_SERVICE ps  Where ps.PARTNER_ID=?1", nativeQuery = true)
	List<PartnerServices> findAllPartnerServiceByPartnerId(Long partnerId);

}
