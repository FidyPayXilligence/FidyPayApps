package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycUserTempDetails;


@Repository
public interface EkycUserTempDetailsRepository extends JpaRepository<EkycUserTempDetails, Long> {

	
	
	
	@Query(value = "SELECT * FROM EKYC_USER_TEMP_DETAILS Where MERCHANT_ID=?1 And ((CREATION_DATE>=?2) And (CREATION_DATE<=?3)) And IS_VERIFIED='0' Order by EKYC_USER_TEMP_ID desc", nativeQuery = true)
	List<EkycUserTempDetails> findByStartDateAndEndDate(long merchantId, String startDate, String endDate,
			Pageable pageable);

	
	@Query(value = "SELECT Count(*) FROM EKYC_USER_TEMP_DETAILS Where MERCHANT_ID=?1 And ((CREATION_DATE>=?2) And (CREATION_DATE<=?3)) And IS_VERIFIED='0'", nativeQuery = true)
	int countByStartDateAndEndDate(long merchantId, String startDate, String endDate);
	
	
	
	@Query(value = "SELECT * FROM EKYC_USER_TEMP_DETAILS Where MERCHANT_ID=?1 And ((CREATION_DATE>=?2) And (CREATION_DATE<=?3)) And WORKFLOW_UNIQUE_ID=?4 And IS_VERIFIED='0' Order by EKYC_USER_TEMP_ID desc", nativeQuery = true)
	List<EkycUserTempDetails> findByStartDateAndEndDateAndEkycWorkflowId(long merchantId, String startDate, String endDate,String ekycWorkflowId,
			Pageable pageable);

	
	@Query(value = "SELECT Count(*) FROM EKYC_USER_TEMP_DETAILS Where MERCHANT_ID=?1 And ((CREATION_DATE>=?2) And (CREATION_DATE<=?3)) And WORKFLOW_UNIQUE_ID=?4 And IS_VERIFIED='0'", nativeQuery = true)
	int countByStartDateAndEndDateAndEkycWorkflowId(long merchantId, String startDate, String endDate,String kycWorkflowId);
}
