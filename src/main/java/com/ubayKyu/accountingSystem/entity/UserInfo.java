package com.ubayKyu.accountingSystem.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="UserInfo")
public class UserInfo {
	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id", updatable = false, nullable = false, columnDefinition = "uniqueidentifier")
    private UUID userID;
	
	@Column(name = "account", nullable=false, columnDefinition = "varchar(50)")
	private String account;

	@Column(name = "name", nullable=false, columnDefinition = "nvarchar(100)")
	private String name;

	@Column(name = "pwd", nullable=false, columnDefinition = "varchar(50)")
	private String pwd;

	@Column(name = "email", nullable=false, columnDefinition = "nvarchar(MAX)")
	private String email;

	@Column(name = "user_level", nullable=false)
	private Integer userLevel;

	@Column(name = "create_date", nullable=false, columnDefinition = "datetime")
	private LocalDateTime createDate;

	@Column(name = "modify_date", nullable=false, columnDefinition = "datetime")
	private LocalDateTime modifyDate;

	public UUID getUserID() {
		return userID;
	}

	public void setUserID(UUID userID) {
		this.userID = userID;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(LocalDateTime modifyDate) {
		this.modifyDate = modifyDate;
	}



}
