package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fidypay.entity.Countries;

public interface CountriesRepository extends JpaRepository<Countries, Long> {

}
