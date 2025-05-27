package com.fidypay.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantWalletInfo;
import com.fidypay.repo.MerchantWalletInfoRepository;
import com.fidypay.response.VirtualAccountResponse;
import com.fidypay.service.MerchantWalletInfoService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class MerchantWalletInfoServiceImpl implements MerchantWalletInfoService {

	@Autowired
	private MerchantWalletInfoRepository infoRepository;

	@Override
	public Map<String, Object> virtualAccountList(long merchantId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		List<MerchantWalletInfo> list = infoRepository.findByMerchantId(merchantId);
		List<VirtualAccountResponse> virtualAccountList = new ArrayList<VirtualAccountResponse>();
		if (!list.isEmpty()) {
			int i = 1;
			for (MerchantWalletInfo merchantWalletInfo : list) {
				VirtualAccountResponse accountResponse = new VirtualAccountResponse();

				String date = DateAndTime.dateFormatReports(merchantWalletInfo.getMerchWalletFromDate().toString());
				accountResponse.setsNo(i++);
				accountResponse.setBankName("RBL Bank");
				accountResponse.setIfsc(merchantWalletInfo.getMerchWalletPin());
				accountResponse.setVirtualAccountNo(Encryption.decString(merchantWalletInfo.getMerchWalletAccountNo()));
				accountResponse.setDate(date);
				virtualAccountList.add(accountResponse);
			}

			map.put(ResponseMessage.DATA, virtualAccountList);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		return map;
	}

}
