package com.fidypay.entity;

import java.sql.Timestamp;
import java.util.Set;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
//@Table(name = "AUTHORIZED_USERS")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "AUTHORIZED_USERS", indexes = {
		@Index(name = "index_AUTH_USER_ID", columnList = "AUTH_USER_ID"),
		@Index(name = "index_AUTH_USER_TYPE_ID", columnList = "AUTH_USER_TYPE_ID"),
		@Index(name = "index_AUTH_USER_EMAIL", columnList = "AUTH_USER_EMAIL"),
		@Index(name = "index_AUTH_USER_PASSWORD", columnList = "AUTH_USER_PASSWORD"),
		@Index(name = "index_AUTH_USER_FIRST_NAME", columnList = "AUTH_USER_FIRST_NAME"),
		@Index(name = "index_AUTH_USER_PHONE", columnList = "AUTH_USER_PHONE"),
		@Index(name = "index_AUTH_USER_ADDRESS", columnList = "AUTH_USER_ADDRESS"),
		@Index(name = "index_AUTH_USER_COUNTRY", columnList = "AUTH_USER_COUNTRY"),
		@Index(name = "index_AUTH_USER_IS_ACTIVE", columnList = "AUTH_USER_IS_ACTIVE"),
		@Index(name = "index_AUTH_USER_LAST_NAME", columnList = "AUTH_USER_LAST_NAME"),
		@Index(name = "index_AUTH_USER_STATE", columnList = "AUTH_USER_STATE"),
		@Index(name = "index_AUTH_USER_CITY", columnList = "AUTH_USER_CITY"),
		@Index(name = "index_AUTH_USER_LOGIN_COUNT", columnList = "AUTH_USER_LOGIN_COUNT"),
		@Index(name = "index_AUTH_USER_FROMDATE", columnList = "AUTH_USER_FROMDATE"),	
		@Index(name = "index_AUTH_USER_LASTLOGIN", columnList = "AUTH_USER_LASTLOGIN")
         })
public class AuthorizedUsers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUTH_USER_ID")
	private long authUserId;

	@ManyToOne
	@JoinColumn(name = "AUTH_USER_TYPE_ID", nullable = false)
	private AuthUserType authUserType;

	@Column(name = "AUTH_USER_EMAIL", length = 200)
	private String authUserEmail;

	@Column(name = "AUTH_USER_PASSWORD", length = 200)
	private String authUserPassword;

	@Column(name = "AUTH_USER_FIRST_NAME", length = 100)
	private String authUserFirstName;

	@Column(name = "AUTH_USER_PHONE", length = 100)
	private String authUserPhone;

	@Column(name = "AUTH_USER_ADDRESS", length = 200)
	private String authUserAddress;

	@Column(name = "AUTH_USER_COUNTRY", length = 100)
	private String authUserCountry;

	@Column(name = "AUTH_USER_IS_ACTIVE", length = 1)
	private Character authUserIsActive;

	@Column(name = "AUTH_USER_LAST_NAME", length = 100)
	private String authUserLastName;

	@Column(name = "AUTH_USER_STATE", length = 100)
	private String authUserState;

	@Column(name = "AUTH_USER_CITY", length = 100)
	private String authUserCity;

	@Column(name = "AUTH_USER_LOGIN_COUNT", length = 18)
	private Long authUserLoginCount;

	@Column(name = "AUTH_USER_FROMDATE")
	private Timestamp authUserFromdate;

	@Column(name = "AUTH_USER_LASTLOGIN")
	private Timestamp authUserLastlogin;

	@OneToMany(mappedBy = "authorizedUsersByAuthUserId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<RequestFloat> requestFloatsForAuthUserId;

	@OneToMany(mappedBy = "authorizedUsersByApprovedbyAuth", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<RequestFloat> requestFloatsForApprovedbyAuth;

	@OneToMany(mappedBy = "authorizedUsers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AuthorizedUsersPermission> authorizedUsersPermissions;

	public AuthorizedUsers() {
	}

	public AuthorizedUsers(long authUserId) {
		this.authUserId = authUserId;
	}

	public AuthorizedUsers(long authUserId, AuthUserType authUserType, String authUserEmail, String authUserPassword,
			String authUserFirstName, String authUserPhone, String authUserAddress, String authUserCountry,
			Character authUserIsActive, String authUserLastName, String authUserState, String authUserCity,
			Long authUserLoginCount, Timestamp authUserFromdate, Timestamp authUserLastlogin,
			Set<RequestFloat> requestFloatsForAuthUserId, Set<RequestFloat> requestFloatsForApprovedbyAuth,
			Set<AuthorizedUsersPermission> authorizedUsersPermissions) {
		this.authUserId = authUserId;
		this.authUserType = authUserType;
		this.authUserEmail = authUserEmail;
		this.authUserPassword = authUserPassword;
		this.authUserFirstName = authUserFirstName;
		this.authUserPhone = authUserPhone;
		this.authUserAddress = authUserAddress;
		this.authUserCountry = authUserCountry;
		this.authUserIsActive = authUserIsActive;
		this.authUserLastName = authUserLastName;
		this.authUserState = authUserState;
		this.authUserCity = authUserCity;
		this.authUserLoginCount = authUserLoginCount;
		this.authUserFromdate = authUserFromdate;
		this.authUserLastlogin = authUserLastlogin;
		this.requestFloatsForAuthUserId = requestFloatsForAuthUserId;
		this.requestFloatsForApprovedbyAuth = requestFloatsForApprovedbyAuth;
		this.authorizedUsersPermissions = authorizedUsersPermissions;
	}

	public long getAuthUserId() {
		return authUserId;
	}

	public void setAuthUserId(long authUserId) {
		this.authUserId = authUserId;
	}

	public AuthUserType getAuthUserType() {
		return authUserType;
	}

	public void setAuthUserType(AuthUserType authUserType) {
		this.authUserType = authUserType;
	}

	public String getAuthUserEmail() {
		return authUserEmail;
	}

	public void setAuthUserEmail(String authUserEmail) {
		this.authUserEmail = authUserEmail;
	}

	public String getAuthUserPassword() {
		return authUserPassword;
	}

	public void setAuthUserPassword(String authUserPassword) {
		this.authUserPassword = authUserPassword;
	}

	public String getAuthUserFirstName() {
		return authUserFirstName;
	}

	public void setAuthUserFirstName(String authUserFirstName) {
		this.authUserFirstName = authUserFirstName;
	}

	public String getAuthUserPhone() {
		return authUserPhone;
	}

	public void setAuthUserPhone(String authUserPhone) {
		this.authUserPhone = authUserPhone;
	}

	public String getAuthUserAddress() {
		return authUserAddress;
	}

	public void setAuthUserAddress(String authUserAddress) {
		this.authUserAddress = authUserAddress;
	}

	public String getAuthUserCountry() {
		return authUserCountry;
	}

	public void setAuthUserCountry(String authUserCountry) {
		this.authUserCountry = authUserCountry;
	}

	public Character getAuthUserIsActive() {
		return authUserIsActive;
	}

	public void setAuthUserIsActive(Character authUserIsActive) {
		this.authUserIsActive = authUserIsActive;
	}

	public String getAuthUserLastName() {
		return authUserLastName;
	}

	public void setAuthUserLastName(String authUserLastName) {
		this.authUserLastName = authUserLastName;
	}

	public String getAuthUserState() {
		return authUserState;
	}

	public void setAuthUserState(String authUserState) {
		this.authUserState = authUserState;
	}

	public String getAuthUserCity() {
		return authUserCity;
	}

	public void setAuthUserCity(String authUserCity) {
		this.authUserCity = authUserCity;
	}

	public Long getAuthUserLoginCount() {
		return authUserLoginCount;
	}

	public void setAuthUserLoginCount(Long authUserLoginCount) {
		this.authUserLoginCount = authUserLoginCount;
	}

	public Timestamp getAuthUserFromdate() {
		return authUserFromdate;
	}

	public void setAuthUserFromdate(Timestamp authUserFromdate) {
		this.authUserFromdate = authUserFromdate;
	}

	public Timestamp getAuthUserLastlogin() {
		return authUserLastlogin;
	}

	public void setAuthUserLastlogin(Timestamp authUserLastlogin) {
		this.authUserLastlogin = authUserLastlogin;
	}

	public Set<RequestFloat> getRequestFloatsForAuthUserId() {
		return requestFloatsForAuthUserId;
	}

	public void setRequestFloatsForAuthUserId(Set<RequestFloat> requestFloatsForAuthUserId) {
		this.requestFloatsForAuthUserId = requestFloatsForAuthUserId;
	}

	public Set<RequestFloat> getRequestFloatsForApprovedbyAuth() {
		return requestFloatsForApprovedbyAuth;
	}

	public void setRequestFloatsForApprovedbyAuth(Set<RequestFloat> requestFloatsForApprovedbyAuth) {
		this.requestFloatsForApprovedbyAuth = requestFloatsForApprovedbyAuth;
	}

	public Set<AuthorizedUsersPermission> getAuthorizedUsersPermissions() {
		return authorizedUsersPermissions;
	}

	public void setAuthorizedUsersPermissions(Set<AuthorizedUsersPermission> authorizedUsersPermissions) {
		this.authorizedUsersPermissions = authorizedUsersPermissions;
	}

}
