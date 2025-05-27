package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycWorkflow;

@Repository
public interface EkycWorkflowRepository extends JpaRepository<EkycWorkflow, Long> {

	boolean existsByWorkflowNameAndMerchantId(String workflowName, long merchantId);
	
	boolean existsByWorkflowNameAndMerchantIdAndIsDeleted(String workflowName, long merchantId,char isDeleted);
	
	
	boolean existsByWorkflowUniqueIdAndIsDeleted(String workflowName,char isDeleted);
	

	@Query(value = "SELECT * FROM EKYC_WORKFLOW WHERE MERCHANT_ID=?1 and IS_DELETED='0'", nativeQuery = true)
	Page<EkycWorkflow> findByMerchantId(long merchantId, Pageable paging);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW WHERE EKYC_WORKFLOW_ID=?1 and MERCHANT_ID=?2 and IS_DELETED='0'", nativeQuery = true)
	EkycWorkflow findByIdAndMerchantId(long ekycWorkflowId, long merchantId);
	
	@Query(value = "SELECT * FROM EKYC_WORKFLOW WHERE WORKFLOW_NAME=?1 and MERCHANT_ID=?2 and IS_DELETED='0'", nativeQuery = true)
	EkycWorkflow findByWorkflowNameAndMerchantId(String ekycWorkflowName, long merchantId);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW WHERE EKYC_WORKFLOW_ID=?1", nativeQuery = true)
	EkycWorkflow findByWorkflowId(long ekycWorkflowId);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW WHERE WORKFLOW_UNIQUE_ID=?1", nativeQuery = true)
	EkycWorkflow findByWorkflowUniqueId(String workFlowUniqueId);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW WHERE MERCHANT_ID=?1 and IS_DELETED='0'", nativeQuery = true)
	List<EkycWorkflow> findByMerchantId(long merchantId);

	
}
