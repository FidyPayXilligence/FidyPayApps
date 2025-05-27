package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PartnerServiceCommission;

@Repository
public interface PartnerServiceCommissionRepository extends JpaRepository<PartnerServiceCommission, Long> {

	@Query(value = "SELECT * from PARTNER_SERVICE_COMMISSION psc where psc.PARTNER_SERVICE_ID=?1  And psc.PARTNER_SERVICE_COMMISSION_START=?2", nativeQuery = true)
	PartnerServiceCommission existByPartnerServiceIdAndPartnerServiceCommissionStart(Long partnerServiceId,
			Long partnerServiceCommissionStart);

	@Query(value = "SELECT * from PARTNER_SERVICE_COMMISSION psc where psc.PARTNER_SERVICE_ID=?1  And psc.PARTNER_SERVICE_COMMISSION_END=?2", nativeQuery = true)
	PartnerServiceCommission existByPartnerServiceIdAndPartnerServiceCommissionEnd(Long partnerServiceId,
			Long partnerServiceCommissionEnd);

	@Query(value = "SELECT * FROM PARTNER_SERVICE_COMMISSION psc\r\n"
			+ "WHERE psc.PARTNER_SERVICE_ID=?1 and ?2 BETWEEN psc.PARTNER_SERVICE_COMMISSION_START AND psc.PARTNER_SERVICE_COMMISSION_END\r\n"
			+ "And ?3 BETWEEN psc.PARTNER_SERVICE_COMMISSION_START AND psc.PARTNER_SERVICE_COMMISSION_END", nativeQuery = true)
	List<PartnerServiceCommission> existByPartnerServiceIdAndPartnerServiceCommissionStartAndPartnerServiceCommissionEnd(
			Long partnerServiceId, Long partnerServiceCommissionStart, Long partnerServiceCommissionEnd);

	@Query(value = "SELECT * from PARTNER_SERVICE_COMMISSION psc where psc.PARTNER_SERVICE_ID=?1", nativeQuery = true)
	List<PartnerServiceCommission> findByPartnerServiceId(Long partnerServiceId);

}
