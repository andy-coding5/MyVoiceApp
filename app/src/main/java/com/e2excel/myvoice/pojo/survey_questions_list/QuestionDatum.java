package com.e2excel.myvoice.pojo.survey_questions_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionDatum {

    @SerializedName("QuestionID")
    @Expose
    private Integer questionID;
    @SerializedName("QuestionText")
    @Expose
    private String questionText;
    @SerializedName("QuestionType")
    @Expose
    private String questionType;

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

}