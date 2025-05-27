package com.fidypay.service;

import java.util.Map;

public interface MerchantWalletInfoService {

	Map<String, Object> virtualAccountList(long merchantId) throws Exception;

}
