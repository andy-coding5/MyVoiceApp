package com.e2excel.myvoice.pojo.survey_question_detail_OTT;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionOptions {

    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("MAXLength")
    @Expose
    private String mAXLength;
    @SerializedName("Validation")
    @Expose
    private String validation;
    @SerializedName("NoOFResponse")
    @Expose
    private Integer noOFResponse;

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

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public Integer getNoOFResponse() {
        return noOFResponse;
    }

    public void setNoOFResponse(Integer noOFResponse) {
        this.noOFResponse = noOFResponse;
    }

}