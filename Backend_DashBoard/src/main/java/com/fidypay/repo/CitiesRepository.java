package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.Cities;

public interface CitiesRepository extends JpaRepository<Cities, Long> {

	@Query(value = "SELECT * FROM CITIES c  WHERE c.STATE_ID=?1 ", nativeQuery = true)
	List<Cities> findByStateId(Long stateId);

}
