package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantServices;

@Repository
public interface MerchantServicesRepository extends JpaRepository<MerchantServices, Long> {

	@Query(value = "SELECT * FROM MERCHANT_SERVICES u where u.SERVICE_ID=?1", nativeQuery = true)
	MerchantServices findByServiceId(long serviceId);

	@Query(value = "select mt.MERCHANT_ID,mot.MERCHANT_OUTLET_ID from merchant_tills mt inner join MERCHANT_OUTLET_TILLS mot on mt.MERCHANT_TILL_ID=mot.MERCHANT_TILL_ID where mt.TILL_CODE=?1", nativeQuery = true)
	MerchantServices findByTillCode(String tillCode);

	@Query(value = "select mt.MERCHANT_ID from merchant_tills mt inner join MERCHANT_OUTLET_TILLS mot on mt.MERCHANT_TILL_ID=mot.MERCHANT_TILL_ID where mt.TILL_CODE=?1", nativeQuery = true)
	long findMerchantIdByTillCode(String tillCode);

	@Query(value = "select mot.MERCHANT_OUTLET_ID from merchant_tills mt inner join MERCHANT_OUTLET_TILLS mot on mt.MERCHANT_TILL_ID=mot.MERCHANT_TILL_ID where mt.TILL_CODE=?1", nativeQuery = true)
	long findMerchantOutletIdByTillCode(String tillCode);

	@Query(value = "SELECT * FROM MERCHANT_SERVICES u where u.SERVICE_ID=?1 and u.MERCHANT_ID=?2 and u.MERCHANT_OUTLET_ID=?3", nativeQuery = true)
	MerchantServices findByServiceIdAndMerchtIdAndMerchOutletId(long serviceId, long merchtId, long merchOutletId);

	@Query(value = "Update MERCHANT_SERVICES u set  u.COMMISION=?1 where u.SERVICE_ID=?2 and u.MERCHANT_ID=?3 and u.MERCHANT_OUTLET_ID=?4", nativeQuery = true)
	MerchantServices updateServiceCommisionByServiceIdAndMerchtIdAndMerchOutletId(double totalAmount, long serviceId,
			long merchtId, long merchOutletId);

	@Query(value = "SELECT * FROM MERCHANT_SERVICES u where u.MERCHANT_ID=?1", nativeQuery = true)
	List<MerchantServices> getServiceByMerchantId(long merchantId);

	//BBPS
	@Query(value = "SELECT ms.MERCHANT_SERVICE_ID,msc.MERCHANT_SERVICE_COMMISSION_START,msc.MERCHANT_SERVICE_COMMISSION_END,msc.MERCHANT_SERVICE_COMMISSION_RATE,ms.MERCHANT_ID ,ms.SERVICE_ID from MERCHANT_SERVICE ms\n"
			+ "inner join MERCHANT_SERVICE_COMMISSION msc\n" + "on ms.MERCHANT_SERVICE_ID=msc.MERCHANT_SERVICE_ID\n"
			+ "inner join SERVICE_INFO si\n" + "on ms.SERVICE_ID=si.SERVICE_ID\n" + "inner join SERVICE_CATEGORY sc\n"
			+ "on si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID\n"
			+ "WHERE ms.MERCHANT_ID=?1 And sc.SERVICE_CATEGORY_NAME='gBjo9hRVm3+Ek8tvHVXER4NDalVIIuLZLxzjFFhPQuY='", nativeQuery = true)
	List<Object[]> findByMerchantIdToServiceCommission(long merchantId);

	//EKYC
	@Query(value = "SELECT ms.MERCHANT_SERVICE_ID,msc.MERCHANT_SERVICE_CHARGE_START,msc.MERCHANT_SERVICE_CHARGE_END,\n"
			+ "msc.MERCHANT_SERVICE_CHARGE_RATE,ms.MERCHANT_ID ,ms.SERVICE_ID from MERCHANT_SERVICE ms \n"
			+ "inner join MERCHANT_SERVICE_CHARGES msc on ms.MERCHANT_SERVICE_ID=msc.MERCHANT_SERVICE_ID \n"
			+ "inner join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID inner join SERVICE_CATEGORY sc on si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID\n"
			+ " WHERE ms.MERCHANT_ID=?1 And sc.SERVICE_CATEGORY_NAME='1/jjuHS2SKD6jySjfr0fjA=='", nativeQuery = true)
	List<Object[]> findByMerchantIdToEkycServiceCharges(long merchantId);

	//ENACH
	@Query(value = "SELECT ms.MERCHANT_SERVICE_ID,msc.MERCHANT_SERVICE_CHARGE_START,msc.MERCHANT_SERVICE_CHARGE_END,\n"
			+ "msc.MERCHANT_SERVICE_CHARGE_RATE,ms.MERCHANT_ID ,ms.SERVICE_ID from MERCHANT_SERVICE ms \n"
			+ "inner join MERCHANT_SERVICE_CHARGES msc on ms.MERCHANT_SERVICE_ID=msc.MERCHANT_SERVICE_ID \n"
			+ "inner join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID inner join SERVICE_CATEGORY sc on si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID\n"
			+ " WHERE ms.MERCHANT_ID=?1 And sc.SERVICE_CATEGORY_NAME='swOQA3j+ax44opJF5xHHjA=='", nativeQuery = true)
	List<Object[]> findByMerchantIdToENachServiceCharges(long merchantId);

	// PAYIN
	@Query(value = "SELECT ms.MERCHANT_SERVICE_ID,msc.MERCHANT_SERVICE_CHARGE_START,msc.MERCHANT_SERVICE_CHARGE_END,\n"
			+ "msc.MERCHANT_SERVICE_CHARGE_RATE,ms.MERCHANT_ID ,ms.SERVICE_ID from MERCHANT_SERVICE ms \n"
			+ "inner join MERCHANT_SERVICE_CHARGES msc on ms.MERCHANT_SERVICE_ID=msc.MERCHANT_SERVICE_ID \n"
			+ "inner join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID inner join SERVICE_CATEGORY sc on si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID\n"
			+ " WHERE ms.MERCHANT_ID=?1 And sc.SERVICE_CATEGORY_NAME='Zg8QL/aIB2kMCZCqWJM7TA=='", nativeQuery = true)
	List<Object[]> findByMerchantIdToPayinServiceCharges(long merchantId);

	// PAYOUT
	@Query(value = "SELECT ms.MERCHANT_SERVICE_ID,msc.MERCHANT_SERVICE_CHARGE_START,msc.MERCHANT_SERVICE_CHARGE_END,\n"
			+ "msc.MERCHANT_SERVICE_CHARGE_RATE,ms.MERCHANT_ID ,ms.SERVICE_ID from MERCHANT_SERVICE ms \n"
			+ "inner join MERCHANT_SERVICE_CHARGES msc on ms.MERCHANT_SERVICE_ID=msc.MERCHANT_SERVICE_ID \n"
			+ "inner join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID inner join SERVICE_CATEGORY sc on si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID\n"
			+ " WHERE ms.MERCHANT_ID=?1 And sc.SERVICE_CATEGORY_NAME='QCXXR3dtDbgCEZpu6oiGlw=='", nativeQuery = true)
	List<Object[]> findByMerchantIdToServiceCharges(long merchantId);

	// PG
	@Query(value = "SELECT ms.MERCHANT_SERVICE_ID,msc.MERCHANT_SERVICE_CHARGE_START,msc.MERCHANT_SERVICE_CHARGE_END,\n"
			+ "msc.MERCHANT_SERVICE_CHARGE_RATE,ms.MERCHANT_ID ,ms.SERVICE_ID from MERCHANT_SERVICE ms \n"
			+ "inner join MERCHANT_SERVICE_CHARGES msc on ms.MERCHANT_SERVICE_ID=msc.MERCHANT_SERVICE_ID \n"
			+ "inner join SERVICE_INFO si on ms.SERVICE_ID=si.SERVICE_ID inner join SERVICE_CATEGORY sc on si.SERVICE_CATEGORY_ID=sc.SERVICE_CATEGORY_ID\n"
			+ " WHERE ms.MERCHANT_ID=?1 And sc.SERVICE_CATEGORY_NAME='oQTHSEbT1ZU9+3q4DsJniQ=='", nativeQuery = true)
	List<Object[]> findByMerchantIdToPgServiceCharges(long merchantId);
}
