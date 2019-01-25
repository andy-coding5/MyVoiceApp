package com.rohan.myvoice.pojo.education_details;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("edu_list")
    @Expose
    private List<EduList> eduList = null;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<EduList> getEduList() {
        return eduList;
    }

    public void setEduList(List<EduList> eduList) {
        this.eduList = eduList;
    }

}
