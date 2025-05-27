package com.fidypay.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.SoundBoxCharges;

@Repository
public interface SoundBoxChargesRepository extends JpaRepository<SoundBoxCharges, Long> {

	boolean existsBySoundBoxSubscriptionId(long soundBoxSubscriptionId);

	@Query(value = "select * from SOUND_BOX_CHARGES where SOUND_BOX_SUBSCRIPTION_ID=?1", nativeQuery = true)
	Page<SoundBoxCharges> findBySoundBoxSubscriptionId(long soundBoxSubscriptionId, Pageable paging);

	@Query(value = "select * from SOUND_BOX_CHARGES sbc where sbc.SOUND_BOX_SUBSCRIPTION_ID=?1 and sbc.START_DATE>=?2 and sbc.END_DATE>=?3 and sbc.DATE<=?4  ORDER BY DATE DESC", nativeQuery = true)
	List<SoundBoxCharges> findBySoundBoxSubscriptionIdAndEndDate(long soundBoxSubscriptionId, Timestamp startDate,
			Timestamp toDate, Timestamp date);

}