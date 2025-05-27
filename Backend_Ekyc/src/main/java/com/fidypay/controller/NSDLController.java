package com.fidypay.controller;

import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.ServiceProvider.NSDL.Encrypt;
import com.fidypay.ServiceProvider.NSDL.NSDLEncrypt;
import com.fidypay.ServiceProvider.NSDL.NSDLServiceImpl;
import com.fidypay.request.NSDLRequest;

@RestController
public class NSDLController {

	@Autowired
	private NSDLServiceImpl nSDLServiceImpl;

	private static final Logger logger = LoggerFactory.getLogger(NSDLController.class);

	@PostMapping("/hash")
	public String hash(@RequestParam String accountNo, @RequestParam String ifsc, @RequestParam String refId) {
		logger.info("refId: {}", refId);
		logger.info("ifsc: {}", ifsc);
		logger.info("accountNo: {}", accountNo);
		return NSDLEncrypt.generateHaskKey(refId + accountNo + ifsc);

	}

	@PostMapping("/decrypt")
	public String decrypt(@RequestBody String request, @RequestParam String message) {
		logger.info("decryptedrequest : {}", request);
		if (message.equals("Drop")) {
			return Encrypt.decryptString(request,
					"swBGKwEc2Y3PESTjAyXvocv0VLJbR3VtcKb6Vrx5wR9wr9cz7VBRSkwMPLf8KCT3DORi1d12ZazJwwn8d3VFPonUFnaGN7YpcZ8TnIoZvfTcsdab1fjwRDS3ppgosmmTtaQ8NouUnLZrdURfcbcTb8hpZz5QqtSBc5nArUk5Yq7NsdXBf67HGl5fFkWAcdQuFo47Y1V1YTFZ1M8O6IV8kX71v3ob7dFBJJahJzGD3HGbyp9uSZRbDqGNJUlzkdeT");
		} else {
			return Encrypt.decryptValidationRequest(request,
					"32e90259334cf6d36880df8d2ef9c6188e0fe598fac306be8dcf4642bb57417b");
		}

	}

	@PostMapping("/encrypt")
	public String encrypt(@RequestBody String request, @RequestParam String message) {
		logger.info("encryptedrequest : {}", request);

		if (message.equals("Drop")) {
			return Encrypt.encryptstring(request,
					"swBGKwEc2Y3PESTjAyXvocv0VLJbR3VtcKb6Vrx5wR9wr9cz7VBRSkwMPLf8KCT3DORi1d12ZazJwwn8d3VFPonUFnaGN7YpcZ8TnIoZvfTcsdab1fjwRDS3ppgosmmTtaQ8NouUnLZrdURfcbcTb8hpZz5QqtSBc5nArUk5Yq7NsdXBf67HGl5fFkWAcdQuFo47Y1V1YTFZ1M8O6IV8kX71v3ob7dFBJJahJzGD3HGbyp9uSZRbDqGNJUlzkdeT");
		} else {
			return Encrypt.encryptValidationRequest(request,
					"32e90259334cf6d36880df8d2ef9c6188e0fe598fac306be8dcf4642bb57417b");
		}

	}

	@PostMapping("/verifyAccountPennyDrop")
	public Map<String, Object> verifyAccountPennyDrop(@Valid @RequestBody NSDLRequest nsdlRequest) {
		return nSDLServiceImpl.verifyAccountPennyDrop(nsdlRequest);
	}

	@PostMapping("/accounVerifyPennyLess")
	public Map<String, Object> accounVerifyPennyLess(@Valid @RequestBody NSDLRequest nsdlRequest) {
		String beneficiaryAccount = nsdlRequest.getBankAccountNumber();
		String beneficiaryIFSC = nsdlRequest.getBankIFSCCode();
		String merchantTrxnRefId = nsdlRequest.getMerchantTrxnRefId();
		return nSDLServiceImpl.accounVerifyPennyLess(beneficiaryAccount, beneficiaryIFSC, merchantTrxnRefId);
	}

}
