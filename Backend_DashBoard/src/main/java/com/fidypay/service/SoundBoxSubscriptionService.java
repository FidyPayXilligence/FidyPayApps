package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.SoundBoxSubscriptionRequest;
import com.fidypay.request.UpadteSoundBoxDetailsRequest;
import com.fidypay.request.UpdateSoundBoxSubscriptionRequest;

public interface SoundBoxSubscriptionService {

	Map<String, Object> saveSoundBoxSubscriptionDetails(@Valid SoundBoxSubscriptionRequest soundBoxSubscriptionRequest) throws Exception;

	Map<String, Object> updateSoundBoxSubscriptionDetails(
			@Valid UpdateSoundBoxSubscriptionRequest updateSoundBoxSubscriptionRequest, long merchantId);

	Map<String, Object> getSoundBoxDetails(long subMerchantInfoIdV2, long merchantId) throws Exception;

	Map<String, Object> soundBoxSubscriptionAcitveDeActive(long soundBoxSubscriptionId, String isActive);

	Map<String, Object> soundBoxSubscriptionIsDeleted(long soundBoxSubscriptionId, String isDeleted);

	Map<String, Object> UpdateSoundBoxSubscription(@Valid UpadteSoundBoxDetailsRequest soundBoxSubscriptionRequest,
			long merchantId);

}
