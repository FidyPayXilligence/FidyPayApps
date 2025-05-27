package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
//@Table(name = "BANK_HOLIDAYS_INFO")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "BANK_HOLIDAYS_INFO", indexes = {
		@Index(name = "index_BANK_HOLIDAYS_ID", columnList = "BANK_HOLIDAYS_ID"),
		@Index(name = "index_BANK_HOLIDAY_NAME", columnList = "BANK_HOLIDAY_NAME"),
		@Index(name = "index_IS_BANK_HOLIDAY", columnList = "IS_BANK_HOLIDAY")
          })
public class BankHoildaysInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BANK_HOLIDAYS_ID")
	private long bankHolidaysId;

	@Column(name = "BANK_HOLIDAY_NAME", length = 200)
	private String bankHoildayName;

	@Column(name = "BANK_HOLIDAY_DATE")
	private Timestamp bankHolidayDate;

	@Column(name = "IS_BANK_HOLIDAY", length = 1)
	private Character isBankHoliday;

	public BankHoildaysInfo() {
		super();
	}

	public BankHoildaysInfo(long bankHolidaysId, String bankHoildayName, Timestamp bankHolidayDate,
			Character isBankHoliday) {
		super();
		this.bankHolidaysId = bankHolidaysId;
		this.bankHoildayName = bankHoildayName;
		this.bankHolidayDate = bankHolidayDate;
		this.isBankHoliday = isBankHoliday;
	}

	public long getBankHolidaysId() {
		return bankHolidaysId;
	}

	public void setBankHolidaysId(long bankHolidaysId) {
		this.bankHolidaysId = bankHolidaysId;
	}

	public String getBankHoildayName() {
		return bankHoildayName;
	}

	public void setBankHoildayName(String bankHoildayName) {
		this.bankHoildayName = bankHoildayName;
	}

	public Timestamp getBankHolidayDate() {
		return bankHolidayDate;
	}

	public void setBankHolidayDate(Timestamp bankHolidayDate) {
		this.bankHolidayDate = bankHolidayDate;
	}

	public Character getIsBankHoliday() {
		return isBankHoliday;
	}

	public void setIsBankHoliday(Character isBankHoliday) {
		this.isBankHoliday = isBankHoliday;
	}

}
