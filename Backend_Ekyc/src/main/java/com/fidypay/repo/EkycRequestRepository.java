package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.EkycRequest;

@Repository
public interface EkycRequestRepository extends JpaRepository<EkycRequest, Long> {

	boolean existsByUserRequest(String userRequest);
}
