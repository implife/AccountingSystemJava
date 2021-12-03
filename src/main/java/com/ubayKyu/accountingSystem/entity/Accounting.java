package com.ubayKyu.accountingSystem.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Accounting")
public class Accounting {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

	@Column(name = "caption", nullable=false, unique=false, columnDefinition = "nvarchar(MAX)")
    public String caption;

	@Column(name = "amount", nullable=false, unique=false)
    public Integer amount;

	@Column(name = "act_type", nullable=false, unique=false)
    public Integer actType;

	@Column(name = "create_date", nullable=false, unique=false, columnDefinition = "datetime")
    public LocalDateTime createDate;

	@Column(name = "remark", nullable=false, unique=false, columnDefinition = "nvarchar(MAX)")
    public String remark;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = false, updatable = false)
    public UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, unique = false, updatable = false)
    public Category category;

}
