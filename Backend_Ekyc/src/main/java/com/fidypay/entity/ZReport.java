package com.fidypay.entity;


import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "Z_REPORT")
@EntityListeners(AuditingEntityListener.class)
public class ZReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Z_REPORT_ID")
	private long ZReportId;

	@ManyToOne
	@JoinColumn(name = "MERCHANT_TILL_ID")
	private MerchantTills merchantTills;

	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;

	@Column(name = "TOTAL_AMOUNT")
	private double totalAmount;

	@Column(name = "TOTAL_TRXN", nullable = false, length = 100)
	private Long totalTrxn;

	@Column(name = "SUPERVISOR_DEPOSIT", nullable = false)
	private double supervisorDeposit;

	@Column(name = "SUPERVISOR_ID", nullable = false)
	private long supervisorId;

	public ZReport() {
	}

	public ZReport(long zReportId, Timestamp createdDate, double totalAmount, Long totalTrxn,
			double supervisorDeposit) {
		super();
		ZReportId = zReportId;
		this.createdDate = createdDate;
		this.totalAmount = totalAmount;
		this.totalTrxn = totalTrxn;
		this.supervisorDeposit = supervisorDeposit;
	}

	public ZReport(long zReportId, MerchantTills merchantTills, Timestamp createdDate, double totalAmount,
			Long totalTrxn, double supervisorDeposit, long supervisorId) {
		super();
		ZReportId = zReportId;
		this.merchantTills = merchantTills;
		this.createdDate = createdDate;
		this.totalAmount = totalAmount;
		this.totalTrxn = totalTrxn;
		this.supervisorDeposit = supervisorDeposit;
		this.supervisorId = supervisorId;
	}

	public long getZReportId() {
		return ZReportId;
	}

	public void setZReportId(long zReportId) {
		ZReportId = zReportId;
	}

	public MerchantTills getMerchantTills() {
		return merchantTills;
	}

	public void setMerchantTills(MerchantTills merchantTills) {
		this.merchantTills = merchantTills;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getTotalTrxn() {
		return totalTrxn;
	}

	public void setTotalTrxn(Long totalTrxn) {
		this.totalTrxn = totalTrxn;
	}

	public double getSupervisorDeposit() {
		return supervisorDeposit;
	}

	public void setSupervisorDeposit(double supervisorDeposit) {
		this.supervisorDeposit = supervisorDeposit;
	}

	public long getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(long supervisorId) {
		this.supervisorId = supervisorId;
	}

}
