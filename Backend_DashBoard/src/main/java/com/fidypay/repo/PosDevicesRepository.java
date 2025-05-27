package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.PosDevices;

@Repository
public interface PosDevicesRepository extends JpaRepository<PosDevices, Long> {

}
