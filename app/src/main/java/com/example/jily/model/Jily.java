package com.example.jily.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Jily<T> implements Serializable {

    @SerializedName("model")
    @Expose
    public String model;

    @SerializedName("pk")
    @Expose
    public String pk;

    @SerializedName("fields")
    @Expose
    public T fields;

    public Jily(String model, String pk, T fields) {
        this.model = model;
        this.pk = pk;
        this.fields = fields;
    }

    public T getFields() { return fields; }

    public void setFields(T fields) { this.fields = fields; }
}
