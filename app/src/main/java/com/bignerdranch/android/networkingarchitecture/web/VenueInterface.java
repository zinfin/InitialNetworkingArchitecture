package com.bignerdranch.android.networkingarchitecture.web;

import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;
import retrofit.Callback;
import retrofit.http.*;
import rx.Observable;

/**
 * Created by sand8529 on 8/15/16.
 */
public interface VenueInterface {
  @GET("/venues/search")
  void venueSearch(@Query("ll") String latLngString, Callback<VenueSearchResponse> callback);

  @FormUrlEncoded
  @POST("/checkins/add") Observable<Object> venueCheckIn(@Field("venueId") String venueId);
}
