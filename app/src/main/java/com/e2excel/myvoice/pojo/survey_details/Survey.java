package com.e2excel.myvoice.pojo.survey_details;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Survey {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("ProjectData")
    @Expose
    private List<ProjectDatum> projectData = null;
    @SerializedName("requestcount")
    @Expose
    private String requestcount;

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

    public List<ProjectDatum> getProjectData() {
        return projectData;
    }

    public void setProjectData(List<ProjectDatum> projectData) {
        this.projectData = projectData;
    }

    public String getRequestcount() {
        return requestcount;
    }

    public void setRequestcount(String requestcount) {
        this.requestcount = requestcount;
    }
}
