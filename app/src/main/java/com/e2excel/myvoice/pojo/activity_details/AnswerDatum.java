package com.e2excel.myvoice.pojo.activity_details;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnswerDatum {

    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Company")
    @Expose
    private String company;
    @SerializedName("Questions")
    @Expose
    private List<Question> questions = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}