package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PartnerServiceCharges;

@Repository
public interface PartnerServiceChargesRepository extends JpaRepository<PartnerServiceCharges, Long> {

	@Query(value = "SELECT * from PARTNER_SERVICE_CHARGES psc where psc.PARTNER_SERVICE_ID=?1 And psc.PARTNER_SERVICE_CHARGE_START=?2", nativeQuery = true)
	PartnerServiceCharges existByPartnerServiceIdAndPartnerServiceChargeStart(Long partnerServiceId,
			Long partnerServiceChargeStart);

	@Query(value = "SELECT * from PARTNER_SERVICE_CHARGES psc where psc.PARTNER_SERVICE_ID=?1 And psc.PARTNER_SERVICE_CHARGE_END=?2", nativeQuery = true)
	PartnerServiceCharges existByPartnerServiceIdAndPartnerServiceChargeEnd(Long partnerServiceId,
			Long partnerServiceChargeEnd);

	@Query(value = "SELECT * FROM PARTNER_SERVICE_CHARGES psc\r\n"
			+ "WHERE psc.PARTNER_SERVICE_ID=?1 and ?2 BETWEEN psc.PARTNER_SERVICE_CHARGE_START AND psc.PARTNER_SERVICE_CHARGE_END\r\n"
			+ "And ?3 BETWEEN psc.PARTNER_SERVICE_CHARGE_START AND psc.PARTNER_SERVICE_CHARGE_END", nativeQuery = true)
	List<PartnerServiceCharges> existByPartnerServiceIdAndPartnerServiceChargeStartAndPartnerServiceChargeEnd(
			Long partnerServiceId, Long partnerServiceChargeStart, Long partnerServiceChargeEnd);

	@Query(value = "SELECT * from PARTNER_SERVICE_CHARGES psc ORDER BY psc.PARTNER_SERVICE_CHARGE_DATE DESC", nativeQuery = true)
	List<PartnerServiceCharges> findAllPartnerServiceCharges(Pageable pageable);

	@Query(value = "SELECT Count(*) from PARTNER_SERVICE_CHARGES", nativeQuery = true)
	int countAllMerchantServiceCharges();

	@Query(value = "SELECT * from PARTNER_SERVICE_CHARGES psc Where psc.PARTNER_SERVICE_ID ORDER BY psc.PARTNER_SERVICE_CHARGE_DATE DESC", nativeQuery = true)
	List<PartnerServiceCharges> findAllByPartnerServiceId(Long partnerServiceId);

	@Query(value = "SELECT * from PARTNER_SERVICE_CHARGES psc where psc.PARTNER_SERVICE_ID=?1", nativeQuery = true)
	List<PartnerServiceCharges> findByPartnerServiceId(Long partnerServiceId);

}
