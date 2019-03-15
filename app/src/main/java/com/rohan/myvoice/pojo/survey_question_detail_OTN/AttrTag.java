package com.rohan.myvoice.pojo.survey_question_detail_OTN;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttrTag {

    @SerializedName("AttrName")
    @Expose
    private String attrName;

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

}