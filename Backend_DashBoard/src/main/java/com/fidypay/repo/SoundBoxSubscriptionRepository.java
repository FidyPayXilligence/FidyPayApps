package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.SoundBoxSubscription;

@Repository
public interface SoundBoxSubscriptionRepository extends JpaRepository<SoundBoxSubscription, Long> {

	@Query(value = "select sbs.SOUND_BOX_TID,sbs.SOUND_BOX_PROVIDER,sbs.SOUND_BOX_LANGUAGE from SOUND_BOX_SUBSCRIPTION sbs where sbs.MERCHANT_ID =?1 and sbs.SUB_MERCHANT_ID =?2", nativeQuery = true)
	List<?> findByMerchantIdAndSubMerchantId(String merchantId, String submerchantId);

	boolean existsBySoundTId(String soundTId);

	@Query(value = "SELECT * FROM SOUND_BOX_SUBSCRIPTION sbs where sbs.SOUND_BOX_TID=?1  ORDER BY DATE DESC", nativeQuery = true)
	List<SoundBoxSubscription> findBySoundBoxTId(String soundTId);

	boolean existsBySoundBoxSubscriptionId(long soundBoxSubscriptionId);

	@Query(value = "SELECT * FROM SOUND_BOX_SUBSCRIPTION sbs where sbs.SOUND_BOX_SUBSCRIPTION_ID=?1 ", nativeQuery = true)
	SoundBoxSubscription findBySoundBoxSubscriptionId(long soundBoxSubscriptionId);

	@Query(value = "SELECT * FROM SOUND_BOX_SUBSCRIPTION sbs where sbs.SUB_MERCHANT_INFO_ID=?1 and sbs.IS_DELETED='0' ORDER BY DATE DESC", nativeQuery = true)
	List<SoundBoxSubscription> findBySubMerchantInfoIdV2Id(long subMerchantInfoIdV2);

	List<SoundBoxSubscription> findBySoundTIdAndIsDeleted(String soundTId, char c);

	@Query(value = "SELECT COUNT(SOUND_BOX_TID) FROM SOUND_BOX_SUBSCRIPTION WHERE SUB_MERCHANT_INFO_ID=?1 AND IS_DELETED=?2", nativeQuery = true)
	int findSoundBoxCount(Long subMerchantInfoId, char c);

	@Query(value = "SELECT SOUND_BOX_TID FROM SOUND_BOX_SUBSCRIPTION sbs where sbs.SUB_MERCHANT_INFO_ID=?1 and sbs.IS_DELETED='0'", nativeQuery = true)
	List<String> findBySoundBoxTId(long subMerchantInfoIdV2);

}
