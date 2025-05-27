package com.fidypay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidypay.entity.MerchantFloatManage;

@Repository
public interface MerchantFloatManageRepository extends JpaRepository<MerchantFloatManage, Long> {

}
