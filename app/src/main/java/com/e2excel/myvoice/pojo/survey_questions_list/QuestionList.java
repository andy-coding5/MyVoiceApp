package com.e2excel.myvoice.pojo.survey_questions_list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionList {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("ProjectTitle")
    @Expose
    private String projectTitle;
    @SerializedName("QuestionCount")
    @Expose
    private Integer questionCount;
    @SerializedName("QuestionData")
    @Expose
    private List<QuestionDatum> questionData = null;

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public List<QuestionDatum> getQuestionData() {
        return questionData;
    }

    public void setQuestionData(List<QuestionDatum> questionData) {
        this.questionData = questionData;
    }

}
