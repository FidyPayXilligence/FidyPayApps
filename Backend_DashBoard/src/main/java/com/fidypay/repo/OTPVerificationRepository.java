package com.fidypay.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.OTPVerification;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {

	@Query(value = "Select * from OTP_VERFICATION ov where ov.MERCHANT_ID=?1 AND ov.OTP=?2 AND ov.OTP_REF_ID=?3", nativeQuery = true)
	Optional<OTPVerification> findOtpANDOtpRefId(long merchantId, String otp, String otpRefId);

	@Query(value = "Select * from OTP_VERFICATION ov where ov.OTP_REF_ID=?1", nativeQuery = true)
	OTPVerification findByToken(String token);

	@Query(value = "SELECT * FROM OTP_VERFICATION ORDER BY OTP_VERIFICATION_ID DESC LIMIT 5", nativeQuery = true)
	List<OTPVerification> findLastFiveOTP();

	@Query(value = "Select * from OTP_VERFICATION ov where ov.OTP=?1 AND ov.OTP_REF_ID=?2", nativeQuery = true)
	Optional<OTPVerification> findOtpANDOtpRefId(String otp, String otpRefId);

	@Query(value = "Select * from OTP_VERFICATION ov where ov.BANK_ID=?1 and ov.MERCHANT_BANK_ACCOUNT_NUMBER=?2 and ov.MERCHANT_ID=?3 ORDER BY OTP_VERIFICATION_ID DESC LIMIT 1", nativeQuery = true)
	Optional<OTPVerification> findByBankId(String email, String bankAccountNumber, long merchantId);
}
