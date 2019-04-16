package com.e2excel.myvoice.pojo.gender_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenderList {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
