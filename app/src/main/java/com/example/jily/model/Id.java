package com.example.jily.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Id {

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @SerializedName("profile_id")
    @Expose
    private Integer profileId;

    public Id(Integer userId, Integer profileId) {
        this.userId = userId;
        this.profileId = profileId;
    }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProfileId() { return profileId; }

    public void setProfileId(Integer profileId) { this.profileId = profileId; }
}
