package com.fidypay.entity;

import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "Authorized_Users_Permission")
@EntityListeners(AuditingEntityListener.class)
public class AuthorizedUsersPermission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUTHORIZED_USERS_PERMISSION_ID")
	private long authorizedUsersPermissionId;

	@ManyToOne
	@JoinColumn(name = "AUTHORIZED_USERS")
	private AuthorizedUsers authorizedUsers;

	@Column(name = "AUTH_USERS_MANAGE_PERMISSION", length = 100)
	private String authusersManagePermission;

	@Column(name = "CUSOMERS_MANAGE_PERMISSION", length = 100)
	private String customersManagePermission;

	@Column(name = "UP_MERC_MANAGE_PERMISSION", length = 100)
	private String upMercManagePermission;

	@Column(name = "TP_MERC_MANAGE_PERMISSION", length = 100)
	private String tpMercManagePermission;

	@Column(name = "SERVICE_MANAGE_PERMISSION", length = 100)
	private String serviceManagePermission;

	@Column(name = "PROVIDER_MANAGE_PERMISSION", length = 100)
	private String providerManagePermission;

	@Column(name = "PAYCHANNEL_MANAGE_PERMISSION", length = 100)
	private String paychannelManagePermission;

	@Column(name = "API_MANAGE_PERMISSION", length = 100)
	private String apiManagePermission;

	@Column(name = "POS_MANAGE_PERMISSION", length = 100)
	private String posManagePermission;

	@Column(name = "TILL_FLOAT_MANAGE_PERMISSION", length = 100)
	private String tillFloatManagePermission;

	@Column(name = "MERSERV_MANAGE_PERMISSION", length = 100)
	private String merservManagePermission;

	@Column(name = "DSR_MANAGE_PERMISSION", length = 100)
	private String dsrManagePermission;

	@Column(name = "SETTLEMENT_MANAGE_PERMISSION", length = 100)
	private String settlementManagePermission;

	@Column(name = "RECON_MANAGE_PERMISSION", length = 100)
	private String reconManagePermission;

	public AuthorizedUsersPermission() {
	}

	public AuthorizedUsersPermission(long authorizedUsersPermissionId, AuthorizedUsers authorizedUsers) {
		this.authorizedUsersPermissionId = authorizedUsersPermissionId;
		this.authorizedUsers = authorizedUsers;
	}

	public AuthorizedUsersPermission(long authorizedUsersPermissionId, AuthorizedUsers authorizedUsers,
			String authusersManagePermission, String customersManagePermission, String upMercManagePermission,
			String tpMercManagePermission, String serviceManagePermission, String providerManagePermission,
			String paychannelManagePermission, String apiManagePermission, String posManagePermission,
			String tillFloatManagePermission, String merservManagePermission, String dsrManagePermission,
			String settlementManagePermission, String reconManagePermission) {
		this.authorizedUsersPermissionId = authorizedUsersPermissionId;
		this.authorizedUsers = authorizedUsers;
		this.authusersManagePermission = authusersManagePermission;
		this.customersManagePermission = customersManagePermission;
		this.upMercManagePermission = upMercManagePermission;
		this.tpMercManagePermission = tpMercManagePermission;
		this.serviceManagePermission = serviceManagePermission;
		this.providerManagePermission = providerManagePermission;
		this.paychannelManagePermission = paychannelManagePermission;
		this.apiManagePermission = apiManagePermission;
		this.posManagePermission = posManagePermission;
		this.tillFloatManagePermission = tillFloatManagePermission;
		this.merservManagePermission = merservManagePermission;
		this.dsrManagePermission = dsrManagePermission;
		this.settlementManagePermission = settlementManagePermission;
		this.reconManagePermission = reconManagePermission;
	}

	public long getAuthorizedUsersPermissionId() {
		return this.authorizedUsersPermissionId;
	}

	public void setAuthorizedUsersPermissionId(long authorizedUsersPermissionId) {
		this.authorizedUsersPermissionId = authorizedUsersPermissionId;
	}

	public AuthorizedUsers getAuthorizedUsers() {
		return this.authorizedUsers;
	}

	public void setAuthorizedUsers(AuthorizedUsers authorizedUsers) {
		this.authorizedUsers = authorizedUsers;
	}

	public String getAuthusersManagePermission() {
		return this.authusersManagePermission;
	}

	public void setAuthusersManagePermission(String authusersManagePermission) {
		this.authusersManagePermission = authusersManagePermission;
	}

	public String getCustomersManagePermission() {
		return this.customersManagePermission;
	}

	public void setCustomersManagePermission(String customersManagePermission) {
		this.customersManagePermission = customersManagePermission;
	}

	public String getUpMercManagePermission() {
		return this.upMercManagePermission;
	}

	public void setUpMercManagePermission(String upMercManagePermission) {
		this.upMercManagePermission = upMercManagePermission;
	}

	public String getTpMercManagePermission() {
		return this.tpMercManagePermission;
	}

	public void setTpMercManagePermission(String tpMercManagePermission) {
		this.tpMercManagePermission = tpMercManagePermission;
	}

	public String getServiceManagePermission() {
		return this.serviceManagePermission;
	}

	public void setServiceManagePermission(String serviceManagePermission) {
		this.serviceManagePermission = serviceManagePermission;
	}

	public String getProviderManagePermission() {
		return this.providerManagePermission;
	}

	public void setProviderManagePermission(String providerManagePermission) {
		this.providerManagePermission = providerManagePermission;
	}

	public String getPaychannelManagePermission() {
		return this.paychannelManagePermission;
	}

	public void setPaychannelManagePermission(String paychannelManagePermission) {
		this.paychannelManagePermission = paychannelManagePermission;
	}

	public String getApiManagePermission() {
		return this.apiManagePermission;
	}

	public void setApiManagePermission(String apiManagePermission) {
		this.apiManagePermission = apiManagePermission;
	}

	public String getPosManagePermission() {
		return this.posManagePermission;
	}

	public void setPosManagePermission(String posManagePermission) {
		this.posManagePermission = posManagePermission;
	}

	public String getTillFloatManagePermission() {
		return this.tillFloatManagePermission;
	}

	public void setTillFloatManagePermission(String tillFloatManagePermission) {
		this.tillFloatManagePermission = tillFloatManagePermission;
	}

	public String getMerservManagePermission() {
		return this.merservManagePermission;
	}

	public void setMerservManagePermission(String merservManagePermission) {
		this.merservManagePermission = merservManagePermission;
	}

	public String getDsrManagePermission() {
		return this.dsrManagePermission;
	}

	public void setDsrManagePermission(String dsrManagePermission) {
		this.dsrManagePermission = dsrManagePermission;
	}

	public String getSettlementManagePermission() {
		return this.settlementManagePermission;
	}

	public void setSettlementManagePermission(String settlementManagePermission) {
		this.settlementManagePermission = settlementManagePermission;
	}

	public String getReconManagePermission() {
		return this.reconManagePermission;
	}

	public void setReconManagePermission(String reconManagePermission) {
		this.reconManagePermission = reconManagePermission;
	}

}
