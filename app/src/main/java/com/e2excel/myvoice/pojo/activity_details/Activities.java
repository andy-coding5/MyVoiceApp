package com.e2excel.myvoice.pojo.activity_details;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activities {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("AnswerCount")
    @Expose
    private Integer answerCount;
    @SerializedName("QuestionCount")
    @Expose
    private Integer questionCount;
    @SerializedName("AnswerData")
    @Expose
    private List<AnswerDatum> answerData = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public List<AnswerDatum> getAnswerData() {
        return answerData;
    }

    public void setAnswerData(List<AnswerDatum> answerData) {
        this.answerData = answerData;
    }

}