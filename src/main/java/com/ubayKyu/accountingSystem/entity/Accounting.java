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
    private String caption;

	@Column(name = "amount", nullable=false, unique=false)
    private Integer amount;

	@Column(name = "act_type", nullable=false, unique=false)
    private Integer actType;

	@Column(name = "create_date", nullable=false, unique=false, columnDefinition = "datetime")
    private LocalDateTime createDate;

	@Column(name = "remark", nullable=false, unique=false, columnDefinition = "nvarchar(MAX)")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = false, updatable = false)
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, unique = false, updatable = false)
    private Category category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getActType() {
        return actType;
    }

    public void setActType(Integer actType) {
        this.actType = actType;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }



}
