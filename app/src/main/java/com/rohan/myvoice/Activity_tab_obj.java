package com.rohan.myvoice;

public class Activity_tab_obj {
    String survey_title;
    String question;
    String ans;
    String company;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    String logical_survey_title;        //for ex, is two survey has name google google then from here titles would be 1google 2google

    public String getLogical_survey_title() {
        return logical_survey_title;
    }

    public void setLogical_survey_title(String logical_survey_title) {
        this.logical_survey_title = logical_survey_title;
    }

    public Activity_tab_obj() {
    }


    public String getSurvey_title() {
        return survey_title;
    }

    public void setSurvey_title(String survey_title) {
        this.survey_title = survey_title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

}
