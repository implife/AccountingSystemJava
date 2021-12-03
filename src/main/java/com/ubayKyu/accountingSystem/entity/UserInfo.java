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
    public UUID userID;
	
	@Column(name = "account", nullable=false, unique=false, columnDefinition = "varchar(50)")
	public String account;

	@Column(name = "name", nullable=false, unique=false, columnDefinition = "nvarchar(100)")
	public String name;

	@Column(name = "pwd", nullable=false, unique=false, columnDefinition = "varchar(50)")
	public String pwd;

	@Column(name = "email", nullable=false, unique=false, columnDefinition = "nvarchar(MAX)")
	public String email;

	@Column(name = "user_level", nullable=false, unique=false)
	public Integer userLevel;

	@Column(name = "create_date", nullable=false, unique=false, columnDefinition = "datetime")
	public LocalDateTime createDate;

	@Column(name = "modify_date", nullable=false, unique=false, columnDefinition = "datetime")
	public LocalDateTime modifyDate;



}
