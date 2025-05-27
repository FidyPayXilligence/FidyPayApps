package com.fidypay.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.entity.SoundBoxCharges;
import com.fidypay.repo.SoundBoxChargesRepository;
import com.fidypay.request.Pagination;
import com.fidypay.response.SoundBoxChargesResponse;
import com.fidypay.service.SoundBoxChargesService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class SoundBoxChargesServiceImpl implements SoundBoxChargesService {

	@Autowired
	private SoundBoxChargesRepository soundBoxChargesRepository;

	@Override
	public Map<String, Object> getAllSoundboxChargesById(long soundBoxSubscriptionId, Pagination pagination)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!soundBoxChargesRepository.existsBySoundBoxSubscriptionId(soundBoxSubscriptionId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "soundBoxSubscriptionId not exists");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Pageable paging = PageRequest.of(pagination.getPageNo(), pagination.getPageSize(),
					Sort.by("DATE").descending());

			List<SoundBoxChargesResponse> soundBoxChargesResponseList = new ArrayList<>();
			List<SoundBoxCharges> soundBoxChargeslist = new ArrayList<>();
			Page<SoundBoxCharges> pageList = null;

			pageList = soundBoxChargesRepository.findBySoundBoxSubscriptionId(soundBoxSubscriptionId, paging);
			soundBoxChargeslist = pageList.getContent();
			AtomicInteger atomicInteger = new AtomicInteger(1);

			if (!soundBoxChargeslist.isEmpty()) {
				for (SoundBoxCharges info : soundBoxChargeslist) {
					DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
					SoundBoxChargesResponse response = new SoundBoxChargesResponse();
					response.setsNo(atomicInteger.getAndIncrement());
					response.setSoundBoxChargesId(info.getSoundBoxChargesId());
					response.setSoundBoxSubscriptionId(info.getSoundBoxSubscriptionId());
					response.setFrequency(info.getFrequency());
					response.setIsActive(info.getIsActive());
					response.setIsPaymentReceived(info.getIsPaymentReceived());
					response.setOtc(amountFormate.format(info.getOtc()));
					response.setPaymentMode(info.getPaymentMode());
					response.setRentalAmount(amountFormate.format(info.getRentalAmount()));
					response.setUtr(info.getUtr());
					response.setStartDate(DateAndTime.dateFormatReports(info.getStartDate().toString()));
					response.setEndDate(DateAndTime.dateFormatReports(info.getEndDate().toString()));
					soundBoxChargesResponseList.add(response);

				}

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("soundBoxChargesResponseList", soundBoxChargesResponseList);
				map.put("currentPage", pageList.getNumber());
				map.put("totalItems", pageList.getTotalElements());
				map.put("totalPages", pageList.getTotalPages());

				return map;
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}
	}

}