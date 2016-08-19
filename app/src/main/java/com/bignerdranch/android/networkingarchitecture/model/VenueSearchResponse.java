package com.bignerdranch.android.networkingarchitecture.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sand8529 on 8/15/16.
 */
public class VenueSearchResponse {
  @SerializedName("venues") List<Venue> mVenueList;

  public List<Venue> getVenueList(){ return  mVenueList; }
}
