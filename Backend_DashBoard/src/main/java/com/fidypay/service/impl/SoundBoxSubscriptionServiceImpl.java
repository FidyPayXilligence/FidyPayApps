package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.SoundBoxCharges;
import com.fidypay.entity.SoundBoxSubscription;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.repo.SoundBoxChargesRepository;
import com.fidypay.repo.SoundBoxSubscriptionRepository;
import com.fidypay.request.SoundBoxSubscriptionRequest;
import com.fidypay.request.UpadteSoundBoxDetailsRequest;
import com.fidypay.request.UpdateSoundBoxSubscriptionRequest;
import com.fidypay.response.SoundBoxSubscriptionResponse;
import com.fidypay.service.SoundBoxSubscriptionService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class SoundBoxSubscriptionServiceImpl implements SoundBoxSubscriptionService {

	@Autowired
	private MerchantSubMerchantInfoV2Repository merchantSubMerchantInfoV2Repository;

	@Autowired
	private SoundBoxSubscriptionRepository soundBoxSubscriptionRepository;

	@Autowired
	private SoundBoxChargesRepository soundBoxChargesRepository;

	@Override
	public Map<String, Object> saveSoundBoxSubscriptionDetails(
			@Valid SoundBoxSubscriptionRequest soundBoxSubscriptionRequest) throws Exception {
		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
		Map<String, Object> map = new HashMap<>();
		try {
			if (!merchantSubMerchantInfoV2Repository
					.existsBySubMerchantInfoIdV2(soundBoxSubscriptionRequest.getSubMerchantInfoIdV2())) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SubMerchantInfoId not exists");
				return map;
			}
			List<SoundBoxSubscription> soundBoxSubList = soundBoxSubscriptionRepository
					.findBySoundBoxTId(soundBoxSubscriptionRequest.getSoundTId());

			if (!soundBoxSubList.isEmpty()) {

				for (SoundBoxSubscription soundBoxSubscriptionObj : soundBoxSubList) {
					if (soundBoxSubscriptionObj.getIsDeleted() == '1') {
						SoundBoxSubscription soundBoxSubscription = new SoundBoxSubscription();
						MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 = merchantSubMerchantInfoV2Repository
								.findBySubMerchantInfoIdV2(soundBoxSubscriptionRequest.getSubMerchantInfoIdV2());

						soundBoxSubscription.setDeactivationDate(null);
						soundBoxSubscription.setDate(trxnDate);
						soundBoxSubscription.setEndDate(null);
						soundBoxSubscription.setStartDate(null);
						soundBoxSubscription.setFrequency("NA");
						soundBoxSubscription.setIsActive('0');
						soundBoxSubscription.setIsDeleted('0');
						soundBoxSubscription.setOtc(0.0);
						soundBoxSubscription.setPaymentMode("NA");
						soundBoxSubscription.setRemark("NA");
						soundBoxSubscription.setRentalAmount(0.0);
						soundBoxSubscription.setUtr("NA");
						soundBoxSubscription.setSoundTId(soundBoxSubscriptionRequest.getSoundTId());
						soundBoxSubscription.setSoundBoxLanguage(soundBoxSubscriptionRequest.getSoundBoxLanguage());
						soundBoxSubscription.setSoundBoxProvider(soundBoxSubscriptionRequest.getSoundBoxProvider());
						soundBoxSubscription.setMerchantId(merchantSubMerchantInfoV2.getMerchantId());
						soundBoxSubscription.setSubMerchantInfoId(soundBoxSubscriptionRequest.getSubMerchantInfoIdV2());
						soundBoxSubscriptionRepository.save(soundBoxSubscription);

						map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
						map.put(ResponseMessage.DESCRIPTION, "SoundBox Added Successfully");
						return map;
					} else {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put(ResponseMessage.DESCRIPTION, "SoundBox Already Added ");
						return map;
					}
				}
			} else {
				SoundBoxSubscription soundBoxSubscription = new SoundBoxSubscription();
				MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 = merchantSubMerchantInfoV2Repository
						.findBySubMerchantInfoIdV2(soundBoxSubscriptionRequest.getSubMerchantInfoIdV2());

				soundBoxSubscription.setDeactivationDate(null);
				soundBoxSubscription.setDate(trxnDate);
				soundBoxSubscription.setEndDate(null);
				soundBoxSubscription.setStartDate(null);
				soundBoxSubscription.setFrequency("NA");
				soundBoxSubscription.setIsActive('0');
				soundBoxSubscription.setIsDeleted('0');
				soundBoxSubscription.setOtc(0.0);
				soundBoxSubscription.setPaymentMode("NA");
				soundBoxSubscription.setRemark("NA");
				soundBoxSubscription.setRentalAmount(0.0);
				soundBoxSubscription.setUtr("NA");
				soundBoxSubscription.setSoundTId(soundBoxSubscriptionRequest.getSoundTId());
				soundBoxSubscription.setSoundBoxLanguage(soundBoxSubscriptionRequest.getSoundBoxLanguage());
				soundBoxSubscription.setSoundBoxProvider(soundBoxSubscriptionRequest.getSoundBoxProvider());
				soundBoxSubscription.setMerchantId(merchantSubMerchantInfoV2.getMerchantId());
				soundBoxSubscription.setSubMerchantInfoId(soundBoxSubscriptionRequest.getSubMerchantInfoIdV2());
				soundBoxSubscriptionRepository.save(soundBoxSubscription);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "SoundBox Added Successfully");
				return map;
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;

	}

	@Override
	public Map<String, Object> updateSoundBoxSubscriptionDetails(
			@Valid UpdateSoundBoxSubscriptionRequest updateSoundBoxSubscriptionRequest, long merchantId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			long soundBoxSubscriptionId = updateSoundBoxSubscriptionRequest.getSoundBoxSubscriptionId();
			if (!soundBoxSubscriptionRepository.existsBySoundBoxSubscriptionId(soundBoxSubscriptionId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "soundBoxSubscriptionId not exists");
				return map;
			}
			String amountRegex = "^[0-9]\\d*(\\.\\d+)?$";

			String retAmount = updateSoundBoxSubscriptionRequest.getRentalAmount().toString();
			if (!retAmount.matches(amountRegex)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "please pass valid rentalAmount");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			SoundBoxSubscription soundBoxSubscription = soundBoxSubscriptionRepository
					.findBySoundBoxSubscriptionId(soundBoxSubscriptionId);

			if (soundBoxSubscription.getIsDeleted().equals('1')) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SoundBox Remove can not be Update");
				return map;
			}
			soundBoxSubscription.setStartDate(trxnDate);
			soundBoxSubscription.setDate(trxnDate);
			String frequency = updateSoundBoxSubscriptionRequest.getFrequency();

			if ("monthly".equalsIgnoreCase(frequency) || "quarterly".equalsIgnoreCase(frequency)
					|| "half yearly".equalsIgnoreCase(frequency) || "yearly".equalsIgnoreCase(frequency)) {

				int daysToAdd = 0;
				if ("monthly".equalsIgnoreCase(frequency)) {
					daysToAdd = 1;
					System.out.println(daysToAdd);
				} else if ("quarterly".equalsIgnoreCase(frequency)) {
					daysToAdd = 3;
				} else if ("half yearly".equalsIgnoreCase(frequency)) {
					daysToAdd = 6;
				} else if ("yearly".equalsIgnoreCase(frequency)) {
					daysToAdd = 12;
				}
				LocalDateTime endDateTime = LocalDate.now().plusMonths(daysToAdd).atTime(LocalTime.MAX);
				LocalDateTime endOfTheDay = endDateTime.withHour(23).withMinute(59).withSecond(59).withNano(9);
				Timestamp endDate = Timestamp.valueOf(endOfTheDay);

				soundBoxSubscription.setEndDate(endDate);

				soundBoxSubscription.setDeactivationDate(endDate);

			}
			soundBoxSubscription.setFrequency(frequency);
			soundBoxSubscription.setOtc(Double.parseDouble(updateSoundBoxSubscriptionRequest.getOtc()));
			soundBoxSubscription
					.setRentalAmount(Double.parseDouble(updateSoundBoxSubscriptionRequest.getRentalAmount()));
			soundBoxSubscription.setPaymentMode(updateSoundBoxSubscriptionRequest.getPaymentMode());
			soundBoxSubscription.setIsActive('1');
			soundBoxSubscription.setRemark(updateSoundBoxSubscriptionRequest.getRemark());
			soundBoxSubscriptionRepository.save(soundBoxSubscription);

			SoundBoxCharges soundBoxCharges = new SoundBoxCharges();
			soundBoxCharges.setSoundBoxSubscriptionId(soundBoxSubscriptionId);
			soundBoxCharges.setDate(trxnDate);
			soundBoxSubscription.setRemark(updateSoundBoxSubscriptionRequest.getRemark());
			soundBoxCharges.setOtc(Double.parseDouble(updateSoundBoxSubscriptionRequest.getOtc()));
			soundBoxCharges.setPaymentMode(updateSoundBoxSubscriptionRequest.getPaymentMode());

			soundBoxCharges.setRentalAmount(Double.parseDouble(updateSoundBoxSubscriptionRequest.getRentalAmount()));
			soundBoxCharges.setUtr(updateSoundBoxSubscriptionRequest.getUtr());
			soundBoxCharges.setIsPaymentReceived("NA");
			soundBoxCharges.setIsActive('1');
			soundBoxCharges.setFrequency(frequency);
			soundBoxSubscriptionRepository.save(soundBoxSubscription);
			soundBoxChargesRepository.save(soundBoxCharges);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Update SoundBoxSubscriptionDetails successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			return map;
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> getSoundBoxDetails(long subMerchantInfoIdV2, long merchantId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!merchantSubMerchantInfoV2Repository.existsBySubMerchantInfoIdV2(subMerchantInfoIdV2)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SubMerchantInfoId not exists");
				return map;
			}
			List<SoundBoxSubscriptionResponse> soundBoxSubscriptionList = new ArrayList<SoundBoxSubscriptionResponse>();
			List<SoundBoxSubscription> list = new ArrayList<SoundBoxSubscription>();

			list = soundBoxSubscriptionRepository.findBySubMerchantInfoIdV2Id(subMerchantInfoIdV2);

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (!list.isEmpty()) {
				for (SoundBoxSubscription info : list) {
					SoundBoxSubscriptionResponse response = new SoundBoxSubscriptionResponse();
					DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
					if (info.getStartDate() == null || info.getEndDate() == null
							|| info.getDeactivationDate() == null) {
						response.setEndDate("NA");
						response.setDeactivationDate("NA");
						response.setStartDate("NA");
					} else {
						response.setEndDate(DateAndTime.dateFormatReports(info.getEndDate().toString()));
						response.setStartDate(DateAndTime.dateFormatReports(info.getStartDate().toString()));
						response.setDeactivationDate(
								DateAndTime.dateFormatReports(info.getDeactivationDate().toString()));

					}
					response.setsNo(atomicInteger.getAndIncrement());
					response.setDate(DateAndTime.dateFormatReports(info.getDate().toString()));
					response.setSoundBoxSubscriptionId(info.getSoundBoxSubscriptionId());
					response.setIsActive(info.getIsActive());
					response.setFrequency(info.getFrequency());
					response.setMerchantId(info.getMerchantId());
					response.setOtc(amountFormate.format(info.getOtc()));
					response.setPaymentMode(info.getPaymentMode());
					if (info.getIsDeleted() == null) {
						response.setIsDeleted('0');
					} else {
						response.setIsDeleted(info.getIsDeleted());
					}
					response.setRemark(info.getRemark());
					response.setRentalAmount(amountFormate.format(info.getRentalAmount()));
					response.setSoundBoxLanguage(info.getSoundBoxLanguage());
					response.setSoundBoxProvider(info.getSoundBoxProvider());
					response.setSoundTId(info.getSoundTId());
					response.setSubMerchantInfoId(info.getSubMerchantInfoId());
					soundBoxSubscriptionList.add(response);
				}

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "SoundBoxSubscriptionList");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("soundBoxSubscriptionList", soundBoxSubscriptionList);
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

	@Override
	public Map<String, Object> soundBoxSubscriptionAcitveDeActive(long soundBoxSubscriptionId, String isActive) {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!soundBoxSubscriptionRepository.existsBySoundBoxSubscriptionId(soundBoxSubscriptionId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SoundBoxSubscriptionId not exists");
				return map;
			}
			if (!isActive.equals("1") && !isActive.equals("0")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid isActive value. Please enter 0 or 1");
				return map;
			}

			SoundBoxSubscription soundBoxSubscription = soundBoxSubscriptionRepository
					.findBySoundBoxSubscriptionId(soundBoxSubscriptionId);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			if (soundBoxSubscription.getIsDeleted() == '1') {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SoundBox is removed can not be Activ Deactiv");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
			if (isActive.equals("0")) {
				if (soundBoxSubscription.getIsActive() == '1') {
					soundBoxSubscription.setIsActive('0');
					soundBoxSubscription.setDeactivationDate(trxnDate);
					soundBoxSubscription = soundBoxSubscriptionRepository.save(soundBoxSubscription);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "SoundBox Deactivated Successfully");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "SoundBox already Deactivated");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}
			} else if (isActive.equals("1")) {
				if (soundBoxSubscription.getIsActive() == '0') {
					soundBoxSubscription.setIsActive('1');
					soundBoxSubscription = soundBoxSubscriptionRepository.save(soundBoxSubscription);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "SoundBox Activated Successfully");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "SoundBox already Activated");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> soundBoxSubscriptionIsDeleted(long soundBoxSubscriptionId, String isDeleted) {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!soundBoxSubscriptionRepository.existsBySoundBoxSubscriptionId(soundBoxSubscriptionId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SoundBoxSubscriptionId not exists");
				return map;
			}
			if (!isDeleted.equals("1")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid isDeleted value.Please enter 1");
				return map;
			}

			SoundBoxSubscription soundBoxSubscription = soundBoxSubscriptionRepository
					.findBySoundBoxSubscriptionId(soundBoxSubscriptionId);
			if (isDeleted.equals("1")) {
				if (soundBoxSubscription.getIsDeleted() == '0') {
					soundBoxSubscription.setIsDeleted('1');
					soundBoxSubscription = soundBoxSubscriptionRepository.save(soundBoxSubscription);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "SoundBox Remove Successfully");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "SoundBox Already Remove");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> UpdateSoundBoxSubscription(
			@Valid UpadteSoundBoxDetailsRequest soundBoxSubscriptionRequest, long merchantId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String soundTId = soundBoxSubscriptionRequest.getSoundTId();
			String soundBoxProvider = soundBoxSubscriptionRequest.getSoundBoxProvider();
			String soundBoxLanguage = soundBoxSubscriptionRequest.getSoundBoxLanguage();
			long soundBoxSubscriptionId = soundBoxSubscriptionRequest.getSoundBoxSubscriptionId();
			if (!soundBoxSubscriptionRepository.existsBySoundBoxSubscriptionId(soundBoxSubscriptionId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "soundBoxSubscriptionId not exists");
				return map;
			}
			SoundBoxSubscription soundBoxSubscription = soundBoxSubscriptionRepository
					.findBySoundBoxSubscriptionId(soundBoxSubscriptionId);
			if (soundBoxSubscription.getIsDeleted().equals('1')) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SoundBox Remove can not be Update");
				return map;
			}

			if (soundTId != null && !soundTId.equals("")) {
				if (soundBoxSubscription.getSoundTId().equals(soundTId)) {
					if (soundBoxProvider != null && !soundBoxProvider.equals("")) {
						soundBoxSubscription.setSoundBoxProvider(soundBoxProvider);
					}
					if (soundBoxLanguage != null && !soundBoxLanguage.equals("")) {
						soundBoxSubscription.setSoundBoxLanguage(soundBoxLanguage);
					}

					soundBoxSubscriptionRepository.save(soundBoxSubscription);

					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
					return map;
				} else {
					List<SoundBoxSubscription> list = soundBoxSubscriptionRepository
							.findBySoundTIdAndIsDeleted(soundTId, '0');
					if (list.size() > 0) {
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, "soundTId already exists");
						return map;

					} else {
						soundBoxSubscription.setSoundTId(soundTId);
						if (soundBoxProvider != null && !soundBoxProvider.equals("")) {
							soundBoxSubscription.setSoundBoxProvider(soundBoxProvider);
						}
						if (soundBoxLanguage != null && !soundBoxLanguage.equals("")) {
							soundBoxSubscription.setSoundBoxLanguage(soundBoxLanguage);
						}

						soundBoxSubscriptionRepository.save(soundBoxSubscription);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
						map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
						return map;

					}
				}
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

}
