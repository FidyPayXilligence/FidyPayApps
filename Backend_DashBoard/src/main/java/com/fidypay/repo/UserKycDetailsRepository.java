package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.UserKycDetails;

@Repository
public interface UserKycDetailsRepository extends JpaRepository<UserKycDetails, Long>  {

	
	@Query(value = "SELECT * FROM USER_KYC_DETAILS ukd where ukd.USER_MOBILE=?1", nativeQuery = true)
    List<UserKycDetails> findByUserMobileNo(String encString);

	@Query(value = "SELECT * FROM USER_KYC_DETAILS ukd where ukd.USER_UNIQUE_ID=?1", nativeQuery = true)
	List<UserKycDetails> findByUserUniqueId(String encString);

	boolean existsByUserMobile(String userMobile);

	boolean existsByUserUniqueId(String userUniqueId);

	@Query(value = "SELECT * FROM USER_KYC_DETAILS ukd where ukd.USER_MOBILE=?1", nativeQuery = true)
	UserKycDetails findByUserMobile(String encString);

	boolean existsByUserEmail(String userEmail);
	
	
}
