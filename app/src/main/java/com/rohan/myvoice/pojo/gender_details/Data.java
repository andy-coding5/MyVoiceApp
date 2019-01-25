package com.rohan.myvoice.pojo.gender_details;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("gender_list")
    @Expose
    private List<GenderList> genderList = null;

    public List<GenderList> getGenderList() {
        return genderList;
    }

    public void setGenderList(List<GenderList> genderList) {
        this.genderList = genderList;
    }
}
