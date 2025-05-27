package com.fidypay.entity;

import java.sql.Timestamp;

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
@Table(name = "TICKETING_DETAIL")
@EntityListeners(AuditingEntityListener.class)
public class TicketingDetail {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TICKET_DETAIL_ID")
	private long ticketDetailId;
	
	@Column(name="TICKET_NUMBER", nullable = false, length = 100)
	private String ticketNumber;
	
	@Column(name="PNR", nullable = false, length = 100)
	private String pnr;
	
	@Column(name="SOURCE_CITY", nullable = false, length = 100)
	private String sourceCity;
	
	@Column(name="DEST_CITY", nullable = false, length = 100)
	private String destCity;
	
	@Column(name="DOJ", nullable = false)
	private Timestamp doj;
	
	@Column(name="DEPARTURE_TIME", nullable = false, length = 100)
	private String departureTime;
	
	@Column(name="SP_NAME", nullable = false, length = 200)
	private String spName;
	
	@Column(name="SERVICE_TYPE", nullable = false, length = 200)
	private String serviceType;
	
	@Column(name="SP_CONTACT", nullable = false, length = 200)
	private String spContact;
	
	@Column(name="BOARDING_POINT", nullable = false, length = 200)
	private String boardingPoint;
	
	@Column(name="BOOKING_DATE", nullable = false, length = 100)
	private Timestamp bookingDate;
	
	@Column(name="CANCEL_DATE", nullable = false)
	private Timestamp cancelDate;
	
	@Column(name="REFUND_AMT", nullable = false)
	private double refundAmt;
	
	
	@Column(name="PASSANGER_DETAIL", nullable = false, length = 500)
	private String passangerDetail;
	
	@Column(name="BOOKED_STATUS", nullable = false, length = 100)
	private String bookedStatus;
	
	@Column(name="PASSANGER_EMAIL", nullable = false, length = 100)
	private String passangerEmail;
	
	@Column(name="PASSANGER_PHONE", nullable = false, length = 100)
	private String passangerPhone;
	
	@Column(name="CANCEL_POLICY", nullable = false, length = 500)
	private String cancelPolicy;
	
	 @ManyToOne
	 @JoinColumn(name = "TRANSACTION_ID")
    private CoreTransactions coreTransactions;
	
		
	 @ManyToOne
	 @JoinColumn(name = "SERVICE_ID")
     private ServiceInfo serviceInfo;
	
	public TicketingDetail() {
	}

	public TicketingDetail(long ticketDetailId, String ticketNumber, String pnr, String sourceCity, String destCity,
			Timestamp doj, String departureTime, String spName, String serviceType, String spContact,
			String boardingPoint, Timestamp bookingDate, Timestamp cancelDate, double refundAmt, String passangerDetail,
			String bookedStatus,String passangerEmail,String passangerPhone,String cancelPolicy,
			CoreTransactions coreTransactions, ServiceInfo serviceInfo) {
		
		this.ticketDetailId = ticketDetailId;
		this.ticketNumber = ticketNumber;
		this.pnr = pnr;
		this.sourceCity = sourceCity;
		this.destCity = destCity;
		this.doj = doj;
		this.departureTime = departureTime;
		this.spName = spName;
		this.serviceType = serviceType;
		this.spContact = spContact;
		this.boardingPoint = boardingPoint;
		this.bookingDate = bookingDate;
		this.cancelDate = cancelDate;
		this.refundAmt = refundAmt;
		this.passangerDetail = passangerDetail;
		this.bookedStatus = bookedStatus;
		this.passangerEmail = passangerEmail;
		this.passangerPhone = passangerPhone;
		this.cancelPolicy = cancelPolicy;
		this.coreTransactions = coreTransactions;
		this.serviceInfo = serviceInfo;
	}

	public long getTicketDetailId() {
		return ticketDetailId;
	}

	public void setTicketDetailId(long ticketDetailId) {
		this.ticketDetailId = ticketDetailId;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getSourceCity() {
		return sourceCity;
	}

	public void setSourceCity(String sourceCity) {
		this.sourceCity = sourceCity;
	}

	public String getDestCity() {
		return destCity;
	}

	public void setDestCity(String destCity) {
		this.destCity = destCity;
	}

	public Timestamp getDoj() {
		return doj;
	}

	public void setDoj(Timestamp doj) {
		this.doj = doj;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getSpContact() {
		return spContact;
	}

	public void setSpContact(String spContact) {
		this.spContact = spContact;
	}

	public String getBoardingPoint() {
		return boardingPoint;
	}

	public void setBoardingPoint(String boardingPoint) {
		this.boardingPoint = boardingPoint;
	}

	public Timestamp getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Timestamp bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Timestamp getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Timestamp cancelDate) {
		this.cancelDate = cancelDate;
	}

	public double getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(double refundAmt) {
		this.refundAmt = refundAmt;
	}

	public String getPassangerDetail() {
		return passangerDetail;
	}

	public void setPassangerDetail(String passangerDetail) {
		this.passangerDetail = passangerDetail;
	}

	public String getBookedStatus() {
		return bookedStatus;
	}

	public void setBookedStatus(String bookedStatus) {
		this.bookedStatus = bookedStatus;
	}
	
	
	
	public String getPassangerEmail() {
		return passangerEmail;
	}

	public void setPassangerEmail(String passangerEmail) {
		this.passangerEmail = passangerEmail;
	}

	public String getPassangerPhone() {
		return passangerPhone;
	}

	public void setPassangerPhone(String passangerPhone) {
		this.passangerPhone = passangerPhone;
	}

	public CoreTransactions getCoreTransactions() {
		return coreTransactions;
	}

	public void setCoreTransactions(CoreTransactions coreTransactions) {
		this.coreTransactions = coreTransactions;
	}

	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public String getCancelPolicy() {
		return cancelPolicy;
	}

	public void setCancelPolicy(String cancelPolicy) {
		this.cancelPolicy = cancelPolicy;
	}
	
	
	
	
}
