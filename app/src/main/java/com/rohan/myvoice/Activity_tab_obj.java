package com.rohan.myvoice;

public class Activity_tab_obj {
    String survey_title;
    String question;
    String ans;

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

    public Activity_tab_obj(String survey_title, String question, String ans) {
        this.survey_title = survey_title;
        this.question = question;
        this.ans = ans;
    }
}
