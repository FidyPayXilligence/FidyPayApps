package com.fidypay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fidypay.entity.States;

public interface StatesRepository extends JpaRepository<States, Long> {
	
	@Query(value = "SELECT * FROM STATES s  WHERE s.COUNTRY_ID=?1 ", nativeQuery = true)
    List<States> findByCountryId(Long countryId);

	@Query(value = "SELECT * FROM STATES s  WHERE s.NAME=?1 ", nativeQuery = true)
	States findByStateName(String stateName);

}
