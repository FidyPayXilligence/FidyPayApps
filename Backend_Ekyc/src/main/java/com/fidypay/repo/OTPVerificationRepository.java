package com.fidypay.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.OTPVerification;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {

	@Query(value = "Select * from OTP_VERFICATION ov where ov.MERCHANT_ID=?1 AND ov.OTP=?2 AND ov.OTP_REF_ID=?3", nativeQuery = true)
	Optional<OTPVerification> findOtpANDOtpRefId(long merchantId, String otp, String otpRefId);

}
