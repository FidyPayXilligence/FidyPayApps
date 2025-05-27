package com.fidypay.utils.ex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantType;
import com.fidypay.entity.PartnerServices;
import com.fidypay.entity.Partners;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantTypeRepository;
import com.fidypay.repo.PartnerServiceRepository;
import com.fidypay.repo.PartnersRepository;
import com.fidypay.repo.ServiceInfoRepository;

@Service
public class PartnerServiceValidate {
	
	
	@Autowired
	private PartnersRepository partnersRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;
	
	@Autowired
	private MerchantTypeRepository merchantTypeRepository;
	
	@Autowired
	private PartnerServiceRepository partnerServiceRepository;
	
	
	public boolean checkServiceExistOrNot(Long mId,String serviceName) {
		try {
			MerchantType merchantType=merchantTypeRepository.findById(mId).get();
			
			System.out.println("merchantType: "+merchantType.getMerchantTypeName());
			
			Partners partners=partnersRepository.finByPartnerBussinessName(Encryption.encString(merchantType.getMerchantTypeName()));
			
			ServiceInfo serviceInfo=serviceInfoRepository.findByServiceName(Encryption.encString(serviceName));
			
			
			System.out.println("partnerId: "+partners.getPartnerId());
			System.out.println("serviceId: "+serviceInfo.getServiceId());
			
			PartnerServices partnerServices=partnerServiceRepository.findByPartnerIdAndServiceId(partners.getPartnerId(), serviceInfo.getServiceId());
			
			if(partnerServices!=null) {
				return true;
			}
			else {
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	
		
	}
	
	
}
