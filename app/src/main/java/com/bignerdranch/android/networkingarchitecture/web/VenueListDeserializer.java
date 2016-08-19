package com.bignerdranch.android.networkingarchitecture.web;

import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by sand8529 on 8/15/16.
 */
public class VenueListDeserializer implements JsonDeserializer<VenueSearchResponse> {
  @Override public VenueSearchResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonElement responseElement = json.getAsJsonObject().get("response");

    return new Gson().fromJson(responseElement, VenueSearchResponse.class);
  }
}
