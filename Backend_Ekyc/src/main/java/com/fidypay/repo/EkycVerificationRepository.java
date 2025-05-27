package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycVerification;

@Repository
public interface EkycVerificationRepository extends JpaRepository<EkycVerification, Long> {

	boolean existsByMobile(String mobile);

	boolean existsByEmail(String email);

	@Query(value = "Select * from EKYC_VER_V2 ev where ev.EMAIL=?1 AND ev.MOBILE=?2", nativeQuery = true)
	EkycVerification findByEmailAndPhone(String email, String phone);
}
