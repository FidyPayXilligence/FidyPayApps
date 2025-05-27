package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.ServiceCategory;


@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

}
