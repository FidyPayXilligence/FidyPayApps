package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.BBPSApiResponse;

@Repository
public interface BBPSApiResponseRepository extends JpaRepository<BBPSApiResponse, Long> {

}
