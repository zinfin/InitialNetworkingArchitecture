package com.bignerdranch.android.networkingarchitecture.inject;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;
import com.bignerdranch.android.networkingarchitecture.web.DataManager;
import com.bignerdranch.android.networkingarchitecture.web.RetrofitErrorHandler;
import com.bignerdranch.android.networkingarchitecture.web.VenueInterface;
import com.bignerdranch.android.networkingarchitecture.web.VenueListDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by sand8529 on 8/16/16.
 */
@Module
public class VenueApplicationModule {
  private Context mApplicationContext;
  private static TokenStore sTokenStore;

  public VenueApplicationModule (Context applicationContext){
    sTokenStore = TokenStore.get(applicationContext);
    mApplicationContext = applicationContext;
  }

  @Provides
  @Singleton
  DataManager providesVenueServiceInterface(Context context, @Named("basic") RestAdapter basicAdapter, @Named("auth") RestAdapter authAdapter){
    return  new DataManager(context, basicAdapter, authAdapter);
  }

  @Provides
  @Singleton
  @Named("basic")
  RestAdapter providesBasicAdapter(Gson gson){

    return new RestAdapter.Builder()
        .setEndpoint(DataManager.FOURSQUARE_ENDPOINT)
        .setConverter(new GsonConverter(gson))
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setRequestInterceptor(sRequestInterceptor)
        .build();
  }

  @Provides
  @Singleton
  @Named("auth")
  RestAdapter providesAuthenticatedAdapter(Gson gson){
    return new RestAdapter.Builder()
        .setEndpoint(DataManager.FOURSQUARE_ENDPOINT)
        .setConverter(new GsonConverter(gson))
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setRequestInterceptor(sAuthenticatedRequestInterceptor)
        .setErrorHandler(new RetrofitErrorHandler())
        .build();
  }

  @Provides
  @Singleton
  Gson provideGson() {
    return new GsonBuilder()
        .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
        .create();
  }
  @Provides
  Context providesContext(){
    return mApplicationContext;
  }


  private static RequestInterceptor sRequestInterceptor = new RequestInterceptor() {
    @Override public void intercept(RequestFacade request) {
      request.addQueryParam("client_id", DataManager.CLIENT_ID);
      request.addQueryParam("client_secret", DataManager.CLIENT_SECRET);
      request.addQueryParam("v", DataManager.FOURSQUARE_VERSON);
      request.addQueryParam("m", DataManager.FOURSQUARE_MODE);
    }
  };

  private static RequestInterceptor sAuthenticatedRequestInterceptor = new RequestInterceptor() {
    @Override public void intercept(RequestFacade request) {
      request.addQueryParam("oauth_token", sTokenStore.getAccessToken());
      request.addQueryParam("v", DataManager.FOURSQUARE_VERSON);
      request.addQueryParam("m", DataManager.FOURSQUARE_MODE);
    }
  };
}

