/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 * Generated by the CloudDB ObjectType compiler.  DO NOT EDIT!
 */
package com.fithealthteam.fithealth.huawei.CloudDB;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.Text;
import com.huawei.agconnect.cloud.database.annotations.DefaultValue;
import com.huawei.agconnect.cloud.database.annotations.EntireEncrypted;
import com.huawei.agconnect.cloud.database.annotations.NotNull;
import com.huawei.agconnect.cloud.database.annotations.Indexes;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

import java.util.Date;

/**
 * Definition of ObjectType user.
 *
 * @since 2021-08-14
 */
@PrimaryKeys({"id"})
@Indexes({"userId:id"})
public final class user extends CloudDBZoneObject {
    private String id;

    private String firstName;

    private String lastName;

    private String gender;

    private Double weight;

    private Double height;

    @DefaultValue(booleanValue = false)
    private Boolean drinkWater;

    @DefaultValue(booleanValue = false)
    private Boolean excessiveCalories;

    @DefaultValue(booleanValue = false)
    private Boolean subscribeTips;

    public user() {
        super(user.class);
        this.drinkWater = false;
        this.excessiveCalories = false;
        this.subscribeTips = false;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getHeight() {
        return height;
    }

    public void setDrinkWater(Boolean drinkWater) {
        this.drinkWater = drinkWater;
    }

    public Boolean getDrinkWater() {
        return drinkWater;
    }

    public void setExcessiveCalories(Boolean excessiveCalories) {
        this.excessiveCalories = excessiveCalories;
    }

    public Boolean getExcessiveCalories() {
        return excessiveCalories;
    }

    public void setSubscribeTips(Boolean subscribeTips) {
        this.subscribeTips = subscribeTips;
    }

    public Boolean getSubscribeTips() {
        return subscribeTips;
    }

}