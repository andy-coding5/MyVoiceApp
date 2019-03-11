package com.rohan.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionOptions {


    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Options")
    @Expose
    private List<Option> options = null;
    @SerializedName("NoOFResponse")
    @Expose
    private String noOFResponse;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getNoOFResponse() {
        return noOFResponse;
    }

    public void setNoOFResponse(String noOFResponse) {
        this.noOFResponse = noOFResponse;
    }

}