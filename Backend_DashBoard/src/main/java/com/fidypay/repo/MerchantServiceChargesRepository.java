package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.MerchantServiceCharges;

public interface MerchantServiceChargesRepository extends JpaRepository<MerchantServiceCharges, Long> {

	@Query(value = "SELECT * from MERCHANT_SERVICE_CHARGES msc  ORDER BY msc.MERCHANT_SERVICE_ID DESC", nativeQuery = true)
	List<MerchantServiceCharges> findAllMerchantServiceCharges(Pageable pageable);

	@Query(value = "SELECT  count(*) FROM MERCHANT_SERVICE_CHARGES", nativeQuery = true)
	int countAllMerchantServiceCharges();

//	@Query(value = "SELECT * from MERCHANT_SERVICE_CHARGES msc where msc.MERCHANT_SERVICE_ID=?1  ORDER BY msc.MERCHANT_SERVICE_ID DESC", nativeQuery = true)
//	MerchantServiceCharges findByMerchantServiceId(Long merchantServiceId);
	
	@Query(value = "SELECT * from MERCHANT_SERVICE_CHARGES msc where MERCHANT_SERVICE_ID=?1 ORDER BY msc.MERCHANT_SERVICE_ID DESC", nativeQuery = true)
	List<MerchantServiceCharges> findByMerchantServiceId(Long merchantServiceId);

	
	@Query(value = "SELECT * from MERCHANT_SERVICE_CHARGES msc where msc.MERCHANT_SERVICE_ID=?1  ORDER BY msc.MERCHANT_SERVICE_ID DESC", nativeQuery = true)
	List<MerchantServiceCharges> findAllByMerchantServiceId(Long merchantServiceId);

	@Query(value = "SELECT * from MERCHANT_SERVICE_CHARGES msc where msc.MERCHANT_SERVICE_ID=?1 And msc.MERCHANT_SERVICE_CHARGE_START=?2", nativeQuery = true)
	MerchantServiceCharges existByMerchantServiceIdAndMerchantServiceChargeStart(Long merchantServiceId,Long merchantServiceChargeStart);
	
	@Query(value = "SELECT * from MERCHANT_SERVICE_CHARGES msc where msc.MERCHANT_SERVICE_ID=?1 And msc.MERCHANT_SERVICE_CHARGE_END=?2", nativeQuery = true)
	MerchantServiceCharges existByMerchantServiceIdAndMerchantServiceChargeEnd(Long merchantServiceId,Long merchantServiceChargeEnd);

//	@Query(value = "SELECT * from MERCHANT_SERVICE_CHARGES msc where msc.MERCHANT_SERVICE_ID=?1 And (msc.MERCHANT_SERVICE_CHARGE_START>=?2 And msc.MERCHANT_SERVICE_CHARGE_END<=?3)", nativeQuery = true)
//	MerchantServiceCharges existByMerchantServiceIdAndMerchantServiceChargeStartAndMerchantServiceChargeEnd(Long merchantServiceId,Long merchantServiceChargeStart,Long merchantServiceChargeEnd);

	
//	@Query(value = "SELECT * FROM MERCHANT_SERVICE_CHARGES msc\r\n"
//			+ "WHERE (msc.MERCHANT_SERVICE_ID=?1 and ?2 BETWEEN msc.MERCHANT_SERVICE_CHARGE_START AND msc.MERCHANT_SERVICE_CHARGE_END\r\n"
//			+ "And ?3 BETWEEN msc.MERCHANT_SERVICE_CHARGE_START AND msc.MERCHANT_SERVICE_CHARGE_END) OR (msc.MERCHANT_SERVICE_ID=?1 And (msc.MERCHANT_SERVICE_CHARGE_START>=?2 And msc.MERCHANT_SERVICE_CHARGE_END<=?3));", nativeQuery = true)
//	List<MerchantServiceCharges> existByMerchantServiceIdAndMerchantServiceChargeStartAndMerchantServiceChargeEnd(Long merchantServiceId,Long merchantServiceChargeStart,Long merchantServiceChargeEnd);
	
	@Query(value = "SELECT * FROM MERCHANT_SERVICE_CHARGES msc\r\n"
			+ "WHERE msc.MERCHANT_SERVICE_ID=?1 and ?2 BETWEEN msc.MERCHANT_SERVICE_CHARGE_START AND msc.MERCHANT_SERVICE_CHARGE_END\r\n"
			+ "And ?3 BETWEEN msc.MERCHANT_SERVICE_CHARGE_START AND msc.MERCHANT_SERVICE_CHARGE_END", nativeQuery = true)
	List<MerchantServiceCharges> existByMerchantServiceIdAndMerchantServiceChargeStartAndMerchantServiceChargeEnd(Long merchantServiceId,Long merchantServiceChargeStart,Long merchantServiceChargeEnd);
	
}
