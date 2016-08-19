package com.bignerdranch.android.networkingarchitecture.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sand8529 on 8/15/16.
 */
public class Stats {
  @SerializedName("checkinsCount") private int checkInsCount;
  @SerializedName("userCount") private int userCount;
  @SerializedName("tipCount") private int tipCount;

  public int getCheckInsCount() {
    return checkInsCount;
  }

  public void setCheckInsCount(int checkInsCount) {
    this.checkInsCount = checkInsCount;
  }

  public int getUserCount() {
    return userCount;
  }

  public void setUserCount(int userCount) {
    this.userCount = userCount;
  }

  public int getTipCount() {
    return tipCount;
  }

  public void setTipCount(int tipCount) {
    this.tipCount = tipCount;
  }
}
