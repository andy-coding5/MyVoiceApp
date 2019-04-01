package com.rohan.myvoice.pojo.activity_details;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("QuestionID")
    @Expose
    private Integer questionID;
    @SerializedName("QuestionText")
    @Expose
    private String questionText;
    @SerializedName("QuestionType")
    @Expose
    private String questionType;
    @SerializedName("Answer")
    @Expose
    private List<Answer> answer = null;

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

    public List<Answer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Answer> answer) {
        this.answer = answer;
    }

    String answer_string="";

    public String getAnswerString() {
        for (int i = 0; i < answer.size(); i++) {
            answer_string += answer.get(i).getValue();
            if (i == answer.size() - 1) {
                answer_string += "";
            } else {
                answer_string += ", ";
            }
        }
        return answer_string;
    }
}


