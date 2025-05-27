package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.BBPSApiRequest;

@Repository
public interface BBPSApiRequestRepository extends JpaRepository<BBPSApiRequest, Long> {

	@Query(value = "SELECT * FROM BBPS_API_REQUEST where CATEGORY IN ('MOBILE POSTPAID', 'MOBILE PREPAID')   And MERCHANT_TRXN_REF_ID=?1", nativeQuery = true)
	BBPSApiRequest findByMerchantTrxnRefId(String merchantTransactionRefId);

	//@Query(value = "SELECT * FROM BBPS_API_REQUEST where CATEGORY IN ('MOBILE POSTPAID', 'MOBILE PREPAID')", nativeQuery = true)
	//List<BBPSApiRequest> findAllpostpaidandprepaidservice();

	
	@Query(value = " SELECT * FROM BBPS_API_REQUEST WHERE CATEGORY IN ('MOBILE POSTPAID', 'MOBILE PREPAID') AND CREATION_DATE >= DATE_SUB(CURDATE(), INTERVAL 15 DAY)", nativeQuery = true)
	List<BBPSApiRequest> findAllpostpaidandprepaidservice();
	
	@Query(value = " SELECT * FROM BBPS_API_REQUEST WHERE CATEGORY IN ('MOBILE POSTPAID', 'MOBILE PREPAID') AND ((CREATION_DATE >=?1) AND (CREATION_DATE <=?2))", nativeQuery = true)
	List<BBPSApiRequest> findPostpaidandprepaidserviceDataByDate(String start,String end);
}
