package com.e2excel.myvoice.pojo.survey_question_detail_SCL;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionOptions {

    @SerializedName("Max")
    @Expose
    private String max;
    @SerializedName("Min")
    @Expose
    private String min;
    @SerializedName("Step")
    @Expose
    private String step;
    @SerializedName("Type")
    @Expose
    private String type;
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

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNoOFResponse() {
        return noOFResponse;
    }

    public void setNoOFResponse(Integer noOFResponse) {
        this.noOFResponse = noOFResponse;
    }

}