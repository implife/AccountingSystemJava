package com.ubayKyu.accountingSystem.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="UserInfo")
public class UserInfo {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Integer id;
	@Column(name="PWD", length=50, nullable=false, unique=false)
	public String pwd;
	@Column(name="NAME", length=50, nullable=false, unique=false)
	public String name;
	@Column(name="EMAIL", length=50, nullable=false, unique=false)
	public String email;
	@Column(name="USER_LEVEL", length=50, nullable=false, unique=false)
	public Integer userLevel;
	@Column(name="CREATE_DATE", length=50, nullable=false, unique=false)
	public LocalDateTime createDate;
}
