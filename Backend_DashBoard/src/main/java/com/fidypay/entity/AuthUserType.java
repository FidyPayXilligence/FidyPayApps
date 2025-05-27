package com.fidypay.entity;

import java.util.Set;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
//@Table(name = "AUTH_USER_TYPE")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "AUTH_USER_TYPE", indexes = {
		@Index(name = "index_AUTH_USER_TYPE_ID", columnList = "AUTH_USER_TYPE_ID"),
		@Index(name = "index_AUTH_USER_TYPE_NAME", columnList = "AUTH_USER_TYPE_NAME"),
		@Index(name = "index_AUTH_USER_TYPE_DETAILS", columnList = "AUTH_USER_TYPE_DETAILS"),
		@Index(name = "index_IS_AUTH_USER_TYPE_DELETED", columnList = "IS_AUTH_USER_TYPE_DELETED")
          })
public class AuthUserType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUTH_USER_TYPE_ID")
    private long authUserTypeId;
	
	@Column(name = "AUTH_USER_TYPE_NAME", length = 100)
	private String authUserTypeName;
	
	@Column(name = "AUTH_USER_TYPE_DETAILS", length = 100)
	private String authUserTypeDetails;
	
	@Column(name = "IS_AUTH_USER_TYPE_DELETED", length = 1)
	private Character isAuthUserTypeDeleted;
	
	
	@OneToMany(mappedBy = "authUserType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AuthorizedUsers> authorizedUserses;

	public AuthUserType() {
	}

	public AuthUserType(long authUserTypeId) {
		this.authUserTypeId = authUserTypeId;
	}

	public AuthUserType(long authUserTypeId, String authUserTypeName, String authUserTypeDetails,
			Character isAuthUserTypeDeleted, Set<AuthorizedUsers> authorizedUserses) {
		this.authUserTypeId = authUserTypeId;
		this.authUserTypeName = authUserTypeName;
		this.authUserTypeDetails = authUserTypeDetails;
		this.isAuthUserTypeDeleted = isAuthUserTypeDeleted;
		this.authorizedUserses = authorizedUserses;
	}

	public long getAuthUserTypeId() {
		return this.authUserTypeId;
	}

	public void setAuthUserTypeId(long authUserTypeId) {
		this.authUserTypeId = authUserTypeId;
	}

	public String getAuthUserTypeName() {
		return this.authUserTypeName;
	}

	public void setAuthUserTypeName(String authUserTypeName) {
		this.authUserTypeName = authUserTypeName;
	}

	public String getAuthUserTypeDetails() {
		return this.authUserTypeDetails;
	}

	public void setAuthUserTypeDetails(String authUserTypeDetails) {
		this.authUserTypeDetails = authUserTypeDetails;
	}

	public Character getIsAuthUserTypeDeleted() {
		return this.isAuthUserTypeDeleted;
	}

	public void setIsAuthUserTypeDeleted(Character isAuthUserTypeDeleted) {
		this.isAuthUserTypeDeleted = isAuthUserTypeDeleted;
	}

	public Set<AuthorizedUsers> getAuthorizedUserses() {
		return this.authorizedUserses;
	}

	public void setAuthorizedUserses(Set<AuthorizedUsers> authorizedUserses) {
		this.authorizedUserses = authorizedUserses;
	}

}
