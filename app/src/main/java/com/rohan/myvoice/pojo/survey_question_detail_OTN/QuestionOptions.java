package com.rohan.myvoice.pojo.survey_question_detail_OTN;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionOptions {

    @SerializedName("Max")
    @Expose
    private String max;
    @SerializedName("Min")
    @Expose
    private String min;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("MAXLength")
    @Expose
    private String mAXLength;
    @SerializedName("NoOFResponse")
    @Expose
    private Integer noOFResponse;

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMAXLength() {
        return mAXLength;
    }

    public void setMAXLength(String mAXLength) {
        this.mAXLength = mAXLength;
    }

    public Integer getNoOFResponse() {
        return noOFResponse;
    }

    public void setNoOFResponse(Integer noOFResponse) {
        this.noOFResponse = noOFResponse;
    }

}