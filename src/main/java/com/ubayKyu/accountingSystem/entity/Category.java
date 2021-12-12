package com.ubayKyu.accountingSystem.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "category_id", insertable = false, updatable = false, nullable = false, columnDefinition = "uniqueidentifier")
    public UUID categoryID;

	@Column(name = "category_name", nullable=false, unique=false, columnDefinition = "nvarchar(MAX)")
    public String categoryName;

	@Column(name = "create_date", nullable=false, unique=false, insertable = false, updatable = false, columnDefinition = "datetime default getdate()")
    public LocalDateTime createDate;

	@Column(name = "remark", nullable=false, unique=false, columnDefinition = "nvarchar(MAX)")
    public String remark;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = false, updatable = false)
    public UserInfo userInfo;

}
