package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.Partners;

public interface PartnersRepository extends JpaRepository<Partners, Long> {
	
	
	@Query(value = "Select * from PARTNERS p where  p.PARTNER_MOBILE=?1", nativeQuery = true)
	Partners findByByMobileNo(String partnerMobile);

	@Query(value = "Select * from PARTNERS p where  p.PARTNER_EMAIL=?1", nativeQuery = true)
	Partners findByByEmail(String partnerEmail);

	@Query(value = "Select * from PARTNERS p where  p.IS_PARTNER_ACTIVE='Y' ORDER BY p.PARTNER_ID DESC", nativeQuery = true)
	List<Partners> findAllPartners(Pageable pageable);

	@Query(value = "SELECT  count(*) FROM PARTNERS", nativeQuery = true)
	int totalPartners();

	@Query(value = "Select * from PARTNERS p where  p.PARTNER_BUSINESS_NAME=?1", nativeQuery = true)
	Partners finByPartnerBussinessName(String businessName);

}
