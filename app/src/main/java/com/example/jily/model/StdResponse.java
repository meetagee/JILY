package com.example.jily.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class StdResponse {

    @SerializedName("returns")
    @Expose
    private Map<String, String> returns;

    @SerializedName("errors")
    @Expose
    private Map<String, String> errors;

    StdResponse(Map<String, String> returns, Map<String, String> errors) {
        this.returns = returns;
        this.errors = errors;
    }

    public String getReturnsType() { return returns.keySet().iterator().next(); }

    public String getReturnsMessage() { return returns.get(getReturnsType()); }

    public String getErrorType() { return errors.keySet().iterator().next(); }

    public String getErrorMessage() { return errors.get(getErrorType()); }
}
