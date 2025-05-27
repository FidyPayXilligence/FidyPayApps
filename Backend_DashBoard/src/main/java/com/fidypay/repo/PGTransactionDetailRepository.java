package com.fidypay.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PGTransactionDetail;

@Repository
public interface PGTransactionDetailRepository extends JpaRepository<PGTransactionDetail, Long> {

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3))", nativeQuery = true)
	Page<PGTransactionDetail> findByStartDateAndEndDate(long merchantId, String startDate, String endDate,
			Pageable paging);

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.MERCHANT_SERVICE_ID=?4", nativeQuery = true)
	Page<PGTransactionDetail> findByStartDateAndEndDateANDService(long merchantId, String startDate, String endDate,
			Long merchantServiceId, Pageable paging);

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.TRANSACTION_STATUS_ID=?4", nativeQuery = true)
	Page<PGTransactionDetail> findByStartDateAndEndDateANDStatus(long merchantId, String startDate, String endDate,
			Long status, Pageable paging);

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.TRANSACTION_STATUS_ID=?4 and pgtd.MERCHANT_SERVICE_ID=?5", nativeQuery = true)
	Page<PGTransactionDetail> findByStartDateAndEndDateANDStatusANDService(long merchantId, String startDate,
			String endDate, Long status, Long merchantServiceId, Pageable paging);

	// total transaction and amount
	@Query(value = "Select COUNT(pgtd.TRXN_REF_ID) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Integer findTotalTransactionsByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	@Query(value = "Select SUM(pgtd.TRANSACTION_AMOUNT) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.TRANSACTION_STATUS_ID=1", nativeQuery = true)
	Double findByMerchantIdAndStartAndEndDate(long merchantId, String startDate, String endDate);

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) order by pgtd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PGTransactionDetail> findByStartDateAndEndDate(Long merchantId, String startDate, String endDate);

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.MERCHANT_SERVICE_ID=?4 order by pgtd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PGTransactionDetail> findByStartDateAndEndDateANDService(Long merchantId, String startDate, String endDate,
			Long merchantServiceId);

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.TRANSACTION_STATUS_ID=?4 order by pgtd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PGTransactionDetail> findByStartDateAndEndDateANDStatus(Long merchantId, String startDate, String endDate,
			Long statusId);

	@Query(value = "Select * from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.TRANSACTION_STATUS_ID=?4 and pgtd.MERCHANT_SERVICE_ID=?5 order by pgtd.TRANSACTION_DATE desc", nativeQuery = true)
	List<PGTransactionDetail> findByStartDateAndEndDateANDStatusANDService(Long merchantId, String startDate,
			String endDate, Long statusId, Long merchantServiceId);

	// service wise

	@Query(value = "Select pgtd.SERVICE_NAME,COUNT(pgtd.PG_TRANSACTION_ID),SUM(pgtd.TRANSACTION_AMOUNT) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) GROUP BY pgtd.SERVICE_NAME ORDER BY pgtd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantId(long merchantId, String from, String to);

	@Query(value = "Select pgtd.TRANSACTION_STATUS,COUNT(pgtd.PG_TRANSACTION_ID),SUM(pgtd.TRANSACTION_AMOUNT) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.SERVICE_NAME=?4 GROUP BY pgtd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceName(long merchantId, String from, String to, String serviceName);

	@Query(value = "Select pgtd.SERVICE_NAME,COUNT(pgtd.PG_TRANSACTION_ID),SUM(pgtd.TRANSACTION_AMOUNT) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.MERCHANT_SERVICE_ID=?4 GROUP BY pgtd.SERVICE_NAME ORDER BY pgtd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMServiceId(long merchantId, String from, String to, long merchantServiceId);

	@Query(value = "Select pgtd.SERVICE_NAME,COUNT(pgtd.PG_TRANSACTION_ID),SUM(pgtd.TRANSACTION_AMOUNT) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.TRANSACTION_STATUS_ID=?4 GROUP BY pgtd.SERVICE_NAME ORDER BY pgtd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndStatusId(long merchantId, String from, String to, long statusId);

	@Query(value = "Select pgtd.TRANSACTION_STATUS,COUNT(pgtd.PG_TRANSACTION_ID),SUM(pgtd.TRANSACTION_AMOUNT) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.SERVICE_NAME=?4 and pgtd.TRANSACTION_STATUS_ID=?5 GROUP BY pgtd.TRANSACTION_STATUS", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndServiceNameAndStatusId(long merchantId, String from, String to,
			String serviceName, long statusId);

	@Query(value = "Select pgtd.SERVICE_NAME,COUNT(pgtd.PG_TRANSACTION_ID),SUM(pgtd.TRANSACTION_AMOUNT) from PG_TRANSACTION_DETAIL pgtd where pgtd.MERCHANT_ID=?1 and ((pgtd.TRANSACTION_DATE>=?2) and (pgtd.TRANSACTION_DATE<=?3)) and pgtd.MERCHANT_SERVICE_ID=?4 and pgtd.TRANSACTION_STATUS_ID=?5 GROUP BY pgtd.SERVICE_NAME ORDER BY pgtd.SERVICE_NAME ASC", nativeQuery = true)
	List<?> findByDateANDMerchantIdAndMServiceIdStatusId(long merchantId, String from, String to, long merchantServiceId,
			long statusId);

	@Query(value = "SELECT * FROM PG_TRANSACTION_DETAIL  pg WHERE pg.MERCHANT_ID=?1 and ((pg.TRANSACTION_DATE>=?2) and (pg.TRANSACTION_DATE<=?3)) and pg.MERCHANT_USER_ID=?4  and pg.SERVICE_NAME!='MNP'  ORDER BY pg.TRANSACTION_DATE desc", nativeQuery = true)
	Page<PGTransactionDetail> findByStartDateEndDateAndMerchantUserId(long merchantId, String startDate, String endDate,
			long merchantUserId, Pageable paging);

	PGTransactionDetail findByMerchantTransactionRefId(String encString);

}
