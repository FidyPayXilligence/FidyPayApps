package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.CredDetails;

@Repository
public interface CredDetailsRepository extends JpaRepository<CredDetails, Long> {

	CredDetails findByName(String string);

	
}
