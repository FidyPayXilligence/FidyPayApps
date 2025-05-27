package com.fidypay.service;

import java.util.Map;
import com.fidypay.request.Pagination;

public interface SoundBoxChargesService {

	Map<String, Object> getAllSoundboxChargesById(long soundBoxSubscriptionId, Pagination pagination) throws Exception;

}