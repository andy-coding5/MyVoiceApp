package com.rohan.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("QuestionID")
    @Expose
    private Integer questionID;
    @SerializedName("ParentID")
    @Expose
    private Integer parentID;
    @SerializedName("AttributeID")
    @Expose
    private Integer attributeID;
    @SerializedName("QuestionText")
    @Expose
    private String questionText;
    @SerializedName("QuestionDesc")
    @Expose
    private Object questionDesc;
    @SerializedName("QuestionIsMedia")
    @Expose
    private Boolean questionIsMedia;
    @SerializedName("QuestionMedia")
    @Expose
    private String questionMedia;
    @SerializedName("QuestionVideoMedia")
    @Expose
    private String questionVideoMedia;
    @SerializedName("QuestionAudioMedia")
    @Expose
    private String questionAudioMedia;
    @SerializedName("QuestionType")
    @Expose
    private String questionType;
    @SerializedName("QuestionOptions")
    @Expose
    private QuestionOptions questionOptionsSCQMCQRNK;
    @SerializedName("DisplayFlag")
    @Expose
    private Integer displayFlag;
    @SerializedName("ConditionType")
    @Expose
    private String conditionType;
    @SerializedName("ConditionValue")
    @Expose
    private String conditionValue;
    @SerializedName("AppDisplay")
    @Expose
    private Integer appDisplay;
    @SerializedName("NoOfSubQus")
    @Expose
    private Integer noOfSubQus;
    @SerializedName("NoOfOpt")
    @Expose
    private Integer noOfOpt;
    @SerializedName("AttrTags")
    @Expose
    private List<AttrTag> attrTags = null;
    @SerializedName("IsAnswered")
    @Expose
    private Boolean isAnswered;

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public Integer getAttributeID() {
        return attributeID;
    }

    public void setAttributeID(Integer attributeID) {
        this.attributeID = attributeID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Object getQuestionDesc() {
        return questionDesc;
    }

    public void setQuestionDesc(Object questionDesc) {
        this.questionDesc = questionDesc;
    }

    public Boolean getQuestionIsMedia() {
        return questionIsMedia;
    }

    public void setQuestionIsMedia(Boolean questionIsMedia) {
        this.questionIsMedia = questionIsMedia;
    }

    public String getQuestionMedia() {
        return questionMedia;
    }

    public void setQuestionMedia(String questionMedia) {
        this.questionMedia = questionMedia;
    }

    public String getQuestionVideoMedia() {
        return questionVideoMedia;
    }

    public void setQuestionVideoMedia(String questionVideoMedia) {
        this.questionVideoMedia = questionVideoMedia;
    }

    public String getQuestionAudioMedia() {
        return questionAudioMedia;
    }

    public void setQuestionAudioMedia(String questionAudioMedia) {
        this.questionAudioMedia = questionAudioMedia;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public QuestionOptions getQuestionOptionsSCQMCQRNK() {
        return questionOptionsSCQMCQRNK;
    }

    public void setQuestionOptionsSCQMCQRNK(QuestionOptions questionOptionsSCQMCQRNK) {
        this.questionOptionsSCQMCQRNK = questionOptionsSCQMCQRNK;
    }

    public Integer getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(Integer displayFlag) {
        this.displayFlag = displayFlag;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public Integer getAppDisplay() {
        return appDisplay;
    }

    public void setAppDisplay(Integer appDisplay) {
        this.appDisplay = appDisplay;
    }

    public Integer getNoOfSubQus() {
        return noOfSubQus;
    }

    public void setNoOfSubQus(Integer noOfSubQus) {
        this.noOfSubQus = noOfSubQus;
    }

    public Integer getNoOfOpt() {
        return noOfOpt;
    }

    public void setNoOfOpt(Integer noOfOpt) {
        this.noOfOpt = noOfOpt;
    }

    public List<AttrTag> getAttrTags() {
        return attrTags;
    }

    public void setAttrTags(List<AttrTag> attrTags) {
        this.attrTags = attrTags;
    }

    public Boolean getIsAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(Boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

}