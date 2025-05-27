package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycWorkflowService;
import com.fidypay.service.EkycUserService;

@Repository
public interface EkycWorkflowServiceRepository extends JpaRepository<EkycWorkflowService, Long> {

	boolean existsByEkycWorkflowIdAndEkycUserIdAndMerchantIdAndDocumentId(Long ekycWorkflowId, Long ekycUserId,
			Long merchantId, String fileName);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW_SERVICE WHERE EKYC_USER_ID=?1 and MERCHANT_ID=?2", nativeQuery = true)
	List<EkycWorkflowService> findByEkycUserId(long ekycUserId, long merchantId);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW_SERVICE WHERE EKYC_WORKFLOW_SERVICE_ID=?1 and MERCHANT_ID=?2", nativeQuery = true)
	EkycWorkflowService findByEkycWorkflowServiceIdAndMerchantId(long ekycWorkflowServiceId, long merchantId);
	
	@Query(value = "SELECT * FROM EKYC_WORKFLOW_SERVICE WHERE SERVICE_ID=?1 and MERCHANT_ID=?2", nativeQuery = true)
	List<EkycWorkflowService> findByServiceIdAndMerchantIdForRekyc(long serviceId, long merchantId);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW_SERVICE WHERE EKYC_USER_ID=?1", nativeQuery = true)
	List<EkycWorkflowService> findByUserId(long ekycUserId);

	@Query(value = "SELECT SERVICE_NAME FROM EKYC_WORKFLOW_SERVICE WHERE EKYC_USER_ID=?1", nativeQuery = true)
	List<String> findByUserIdService(long userId);
	
	@Query(value = "SELECT * FROM EKYC_WORKFLOW_SERVICE WHERE EKYC_USER_ID=?1 and MERCHANT_ID=?2 And RE_KYC='0'", nativeQuery = true)
	List<EkycWorkflowService> findByEkycUserIdForVerified(long ekycUserId, long merchantId);
	
	@Query(value = "SELECT * FROM EKYC_WORKFLOW_SERVICE WHERE EKYC_USER_ID=?1 and MERCHANT_ID=?2 And RE_KYC='1'", nativeQuery = true)
	List<EkycWorkflowService> findByEkycUserIdForReKyc(long ekycUserId, long merchantId);

	@Query(value = "SELECT * FROM EKYC_WORKFLOW_SERVICE WHERE MERCHANT_ID=?1 and EKYC_USER_ID=?2 and SERVICE_ID=?3", nativeQuery = true)
	EkycWorkflowService findByEkycMerchantIdAndUserIdAndServiceId(long merchantId, String ekycUserId, long serviceId);

}
