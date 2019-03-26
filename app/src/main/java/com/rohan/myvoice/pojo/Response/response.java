package com.rohan.myvoice.pojo.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class response {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("QuestionID")
    @Expose
    private Integer questionID;
    @SerializedName("QuestionType")
    @Expose
    private String questionType;
    @SerializedName("IsNext")
    @Expose
    private String isNext;

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

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getIsNext() {
        return isNext;
    }

    public void setIsNext(String isNext) {
        this.isNext = isNext;
    }

}
