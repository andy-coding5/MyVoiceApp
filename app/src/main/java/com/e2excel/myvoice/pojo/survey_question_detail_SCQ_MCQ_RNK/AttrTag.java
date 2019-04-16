package com.e2excel.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK;

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