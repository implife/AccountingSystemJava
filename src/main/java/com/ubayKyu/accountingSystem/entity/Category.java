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
    private UUID categoryID;

	@Column(name = "category_name", nullable=false, columnDefinition = "nvarchar(MAX)")
    private String categoryName;

	@Column(name = "create_date", nullable=false, insertable = false, updatable = false, columnDefinition = "datetime default getdate()")
    private LocalDateTime createDate;

	@Column(name = "remark", nullable=false, columnDefinition = "nvarchar(MAX)")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserInfo userInfo;

    public UUID getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(UUID categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }



}
