package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycUserTable;

@Repository
public interface EkycUserRepository extends JpaRepository<EkycUserTable, Long> {

	boolean existsByEkycWorkflowIdAndUserMobile(long ekycWorkflowId, String userMobile);

	boolean existsByUserUniqueIdAndUserMobile(String userUniqueId, String userMobile);

	boolean existsByEkycWorkflowIdAndUserMobileAndIsDeleted(long ekycWorkflowId, String userMobile, char isDeleted);

	@Query(value = "SELECT * FROM EKYC_USER eu where eu.EKYC_USER_ID=?1 and eu.MERCHANT_ID=?2 and eu.IS_DELETED='0'", nativeQuery = true)
	EkycUserTable findByEkycUserId(Long ekycUserId, long merchantId);

	@Query(value = "SELECT * FROM EKYC_USER eu Inner Join EKYC_WORKFLOW ew ON eu.EKYC_WORKFLOW_ID=ew.EKYC_WORKFLOW_ID where  eu.MERCHANT_ID=?1 and eu.IS_DELETED='0' and ew.IS_DELETED='0' order by eu.CREATION_DATE desc", nativeQuery = true)
	Page<EkycUserTable> findAllByPage(long merchantId, Pageable pageble);

	@Query(value = "SELECT * FROM EKYC_USER eu Inner Join EKYC_WORKFLOW ew On eu.EKYC_WORKFLOW_ID=ew.EKYC_WORKFLOW_ID Where eu.USER_NAME=?1 and eu.MERCHANT_ID=?2 and eu.IS_DELETED='0' and ew.IS_DELETED='0' order by eu.CREATION_DATE desc", nativeQuery = true)
	Page<EkycUserTable> findByName(String name, long merchantId,Pageable pageble);

	@Query(value = "SELECT * FROM EKYC_USER eu Inner Join EKYC_WORKFLOW ew On eu.EKYC_WORKFLOW_ID=ew.EKYC_WORKFLOW_ID Where eu.USER_MOBILE=?1 and eu.MERCHANT_ID=?2 and eu.IS_DELETED='0' and ew.IS_DELETED='0' order by eu.CREATION_DATE desc", nativeQuery = true)
	Page<EkycUserTable> findByUserMobile(String mobile, long merchantId,Pageable pageble);

	@Query(value = "SELECT * FROM EKYC_USER eu Inner Join EKYC_WORKFLOW ew On eu.EKYC_WORKFLOW_ID=ew.EKYC_WORKFLOW_ID Where eu.USER_EMAIL=?1 and eu.MERCHANT_ID=?2 and eu.IS_DELETED='0' and ew.IS_DELETED='0' order by eu.CREATION_DATE desc", nativeQuery = true)
	Page<EkycUserTable> findByEmail(String email, long merchantId,Pageable pageble);

	@Query(value = "SELECT * FROM EKYC_USER eu Inner Join EKYC_WORKFLOW ew On eu.EKYC_WORKFLOW_ID=ew.EKYC_WORKFLOW_ID Where eu. WORKFLOW_NAME=?1 and eu.MERCHANT_ID=?2 and eu.IS_DELETED='0' and ew.IS_DELETED='0' order by eu.CREATION_DATE desc", nativeQuery = true)
	Page<EkycUserTable> findByWorkflowName(String workflowName, long merchantId,Pageable pageble);

	@Query(value = "SELECT * FROM EKYC_USER eu Where eu.EKYC_WORKFLOW_ID=?1 and eu.USER_MOBILE=?2", nativeQuery = true)
	EkycUserTable findByEkycWorkflowIdAndUserMobile(long ekycWorkflowId, String userMobile);

	@Query(value = "SELECT * FROM EKYC_USER WHERE EKYC_WORKFLOW_ID=?1 and MERCHANT_ID=?2 and IS_DELETED='0' order by CREATION_DATE desc", nativeQuery = true)
	List<EkycUserTable> findByEkycWorkflowId(long ekycWorkflowId, long merchantId);

	@Query(value = "SELECT * FROM EKYC_USER WHERE USER_UNIQUE_ID=?1 and IS_DELETED='0'", nativeQuery = true)
	EkycUserTable findByUserUniqueId(String userUniqueId);

	@Query(value = "SELECT * FROM EKYC_USER eu Where eu.USER_UNIQUE_ID=?1 and eu.USER_MOBILE=?2", nativeQuery = true)
	EkycUserTable findByUserUniqueIdAndUserMobile(String userUniqueId, String userMobile);

	
	@Query(value = "SELECT * FROM EKYC_USER eu Where eu.USER_UNIQUE_ID=?1", nativeQuery = true)
	EkycUserTable findByEkycUserUniqueId(String userUniqueId);

	@Query(value = "SELECT * FROM EKYC_USER eu Where eu.IS_VERIFIED='2'", nativeQuery = true)
	List<EkycUserTable> findAllVerifiedUsers();

}
