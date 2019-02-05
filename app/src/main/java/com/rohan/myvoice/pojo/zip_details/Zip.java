package com.rohan.myvoice.pojo.zip_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Zip {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("respo")
    @Expose
    private String respo;

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

    public String getRespo() {
        return respo;
    }

    public void setRespo(String respo) {
        this.respo = respo;
    }
}
