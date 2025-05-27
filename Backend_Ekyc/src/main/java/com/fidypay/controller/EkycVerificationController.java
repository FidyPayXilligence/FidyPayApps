package com.fidypay.controller;

import java.io.IOException;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.dto.EKycVerificationDTO;
import com.fidypay.entity.EkycVerification;
import com.fidypay.request.EkycMerchantRequest;
import com.fidypay.service.EkycVerificationService;
import com.fidypay.service.impl.AadhaarServiceImpl;
import com.fidypay.utils.constants.ResponseMessage;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/ekyc")
public class EkycVerificationController {

	@Autowired
	private EkycVerificationService ekycVerificationService;

	@Autowired
	private AadhaarServiceImpl aadhaarServiceImpl;

	@PostMapping("/saveEmailAndPhone")
	public ResponseEntity<?> saveEmailAndPhone(@Valid @RequestBody EKycVerificationDTO eKycVerificationDTO) {
		Map<String, Object> map = new HashedMap<String, Object>();

		if (ekycVerificationService.existsByEmail(eKycVerificationDTO.getEmail())
				&& ekycVerificationService.existsByMobile(eKycVerificationDTO.getPhone())) {

			EkycVerification ekycVerification = ekycVerificationService
					.findByEmailAndPhone(eKycVerificationDTO.getEmail(), eKycVerificationDTO.getPhone());

			map.put("code", ResponseMessage.FAILED);
			map.put("details", ekycVerification);
			map.put("description", ResponseMessage.EKYC_EMAIL);

			return ResponseEntity.ok(map);
		}

		map = ekycVerificationService.saveEkycVerification(eKycVerificationDTO.getPhone(),
				eKycVerificationDTO.getEmail());
		return ResponseEntity.ok(map);
	}

	@GetMapping("/checkMobile/{mobile}")
	public ResponseEntity<?> checkMobile(@PathVariable String mobile) throws IOException {

		Map<String, Object> map = ekycVerificationService.checkMobileNo(mobile);
		return ResponseEntity.ok(map);
	}

	@GetMapping("/checkEmail/{email}")
	public ResponseEntity<?> checkEmail(@PathVariable String email) throws IOException {

		Map<String, Object> map = ekycVerificationService.checkEmail(email);
		return ResponseEntity.ok(map);
	}

	@GetMapping("/findById/{ekyc_id}")
	public ResponseEntity<?> findById(@PathVariable String ekyc_id) {
        EkycVerification ekycVerification = ekycVerificationService.findByEKycId(Long.parseLong(ekyc_id));
		return ResponseEntity.ok(ekycVerification);
	}

	@GetMapping("{email}/findByEmailAndPhone/{phone}")
	public ResponseEntity<?> findByEmailAndPhone(@PathVariable String email, @PathVariable String phone) {

		EkycVerification ekycVerification = ekycVerificationService.findByEmailAndPhone(email, phone);
		return ResponseEntity.ok(ekycVerification);
	}

	@PostMapping("/updateEkyById/{id}")
	public Map<String, Object> updateEkyById(@PathVariable Long id,
			@RequestBody EkycMerchantRequest ekycMerchantRequest) {
		return ekycVerificationService.updateEkyc(id, ekycMerchantRequest);
	}

	@GetMapping("/createUrlForDigilocker/{rId}")
	public String createUrlForDigilockerNew(@PathVariable String rId) throws Exception {
		return aadhaarServiceImpl.createUrlForDigilockerNew(rId);
	}
}
