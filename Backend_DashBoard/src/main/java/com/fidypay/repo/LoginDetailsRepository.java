package com.fidypay.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.LoginDetails;

@Repository
public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {

	LoginDetails findBylogInid(String logInid);

    @Query(value = "SELECT * FROM LOGIN_DETAILS ld WHERE ld.MERCHANT_ID=?1 and ld.DESCRIPTION=?2 and ((ld.DATE>=?3) and (ld.DATE<=?4))  ORDER BY ld.DATE DESC", nativeQuery = true)
	Page<LoginDetails> findByStartDateAndEndDateAndMerchantIdAndDescription(long merchantId, String description,
			String startDate, String endDate, Pageable paging);

}
