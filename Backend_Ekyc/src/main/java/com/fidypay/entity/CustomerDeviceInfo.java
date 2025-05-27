package com.fidypay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "CUSTOMER_DEVICE_INFO")
@EntityListeners(AuditingEntityListener.class)
public class CustomerDeviceInfo  {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUSTOMER_DEVICE_INFO_ID")
	private long custDeviceInfoID;
	
	@Column(name = "CUSTOMER_ID")
	private long cust_ID;
	
	 @Column(name="CUSTOMER_FIREBASE_ID", length = 1000)
	private String FirebaseID;
	 
	 @Column(name="CUSTOMER_DEVICE_STATUS", length = 1)
	private Character custDeviceStatus;

	public CustomerDeviceInfo() {
	}

	public CustomerDeviceInfo(long custDeviceInfoID, long cust_ID,
			String FirebaseID, Character custDeviceStatus) {
		this.custDeviceInfoID = custDeviceInfoID;
		this.cust_ID = cust_ID;
		this.FirebaseID = FirebaseID;
		this.custDeviceStatus = custDeviceStatus;
	}

	public long getcustDeviceInfoID() {
		return this.custDeviceInfoID;
	}

	public void setcustDeviceInfoID(long custDeviceInfoID) {
		this.custDeviceInfoID = custDeviceInfoID;
	}

	public String getFirebaseID() {
		return this.FirebaseID;
	}

	public void setFirebaseID(String FirebaseID) {
		this.FirebaseID = FirebaseID;
	}
	public Character getcustDeviceStatus() {
		return custDeviceStatus;
	}

	public void setcustDeviceStatus(Character custDeviceStatus) {
		this.custDeviceStatus = custDeviceStatus;
	}

	public long getCust_ID() {
		return cust_ID;
	}

	public void setCust_ID(long cust_ID) {
		this.cust_ID = cust_ID;
	}

	
}
