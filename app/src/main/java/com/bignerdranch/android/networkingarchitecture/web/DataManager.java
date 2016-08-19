package com.bignerdranch.android.networkingarchitecture.web;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bignerdranch.android.networkingarchitecture.exception.UnauthorizedException;
import com.bignerdranch.android.networkingarchitecture.listener.VenueCheckInListener;
import com.bignerdranch.android.networkingarchitecture.listener.VenueSearchListener;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.model.Venue;
import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sand8529 on 8/15/16.
 */
public class DataManager {
  private static final String TAG = "DataManager";
  public static final String FOURSQUARE_ENDPOINT = "https://api.foursquare.com/v2";
  public static final String CLIENT_ID = "C0LQS25NXVDWA0AMHROEDUD54RHKNME0QMIWWWUOH2OV31TV";
  public static final String CLIENT_SECRET = "X5SIMMYX3ADATULNZ150UM1W2ZVABILUPHN4401WRZLUFEB2";
  public static final String FOURSQUARE_VERSON = "20160815";
  public static final String FOURSQUARE_MODE = "foursquare";
  private static final String SWARM_MODE = "swarm";
  private static final String TEST_LAT_LNG = "33.759,-84.332";

  private static DataManager sDataManager;
  private static TokenStore sTokenStore;
  private Context mContext;
  private RestAdapter mBasicRestAdapter, mAuthenticatedRestAdapter;
  private List<Venue> mVenueList;
  private List<VenueSearchListener> mSearchListenerList;
  private List<VenueCheckInListener> mCheckInListenerList;

  private static  final String  OAUTH_ENDPOINT = "https://foursquare.com/oauth2/authenticate";
  public static final String OAUTH_REDIRECT_URI = "http://www.bignerdranch.com";
  @VisibleForTesting
  public static DataManager get(Context context, RestAdapter basicRestAdapter,
      RestAdapter authenticatedRestAdapter) {
    sDataManager = new DataManager(context, basicRestAdapter,
        authenticatedRestAdapter);
    return sDataManager;
  }

//  public static DataManager get(Context context){
//    if (sDataManager == null){
//      Gson gson = new GsonBuilder()
//          .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
//          .create();
//
//      RestAdapter basicRestAdapter = new RestAdapter.Builder()
//          .setEndpoint(FOURSQUARE_ENDPOINT)
//          .setConverter(new GsonConverter(gson))
//          .setLogLevel(RestAdapter.LogLevel.FULL)
//          .setRequestInterceptor(sRequestInterceptor)
//          .build();
//
//      RestAdapter authenticatedRestAdapter = new RestAdapter.Builder()
//          .setEndpoint(FOURSQUARE_ENDPOINT)
//          .setConverter(new GsonConverter(gson))
//          .setLogLevel(RestAdapter.LogLevel.FULL)
//          .setRequestInterceptor(sAuthenticatedRequestInterceptor)
//          .setErrorHandler(new RetrofitErrorHandler())
//          .build();
//
//      sDataManager = new DataManager(context, basicRestAdapter, authenticatedRestAdapter);
//
//    }
//    return sDataManager;
//  }
  public DataManager(Context context, RestAdapter basicRestAdapter, RestAdapter authenticatedRestAdapter){
    mContext = context;
    sTokenStore = TokenStore.get(mContext);
    mBasicRestAdapter = basicRestAdapter;
    mSearchListenerList = new ArrayList<>();
    mCheckInListenerList = new ArrayList<>();
    mAuthenticatedRestAdapter = authenticatedRestAdapter;
  }

  public  void fetchVenueSearch(){
    VenueInterface venueInterface = mBasicRestAdapter.create(VenueInterface.class);
    venueInterface.venueSearch(TEST_LAT_LNG, new Callback<VenueSearchResponse>() {
      @Override public void success(VenueSearchResponse venueSearchResponse, Response response) {
        mVenueList = venueSearchResponse.getVenueList();
        notifySearchListeners();
      }

      @Override public void failure(RetrofitError error) {
        Log.e(TAG, "Failed to fetch venue search", error);
      }
    });
  }

  public List<Venue> getVenueList(){ return mVenueList; }

  public void addVenueSearchListener(VenueSearchListener listener){
    mSearchListenerList.add(listener);
  }

  public void removeVenueSearchListener(VenueSearchListener listener){
    mSearchListenerList.remove(listener);
  }

  public void addVenueCheckInListener(VenueCheckInListener listener){
    mCheckInListenerList.add(listener);
  }

  public void removeVenueCheckInListener(VenueCheckInListener listener){
    mCheckInListenerList.remove(listener);
  }

  private  void notifySearchListeners(){
    for (VenueSearchListener listener : mSearchListenerList){
      listener.onVenueSearchFinished();
    }
  }

  public String getAuthenticationUrl(){
    return Uri.parse(OAUTH_ENDPOINT).buildUpon()
        .appendQueryParameter("client_id", CLIENT_ID)
        .appendQueryParameter("response_type", "token")
        .appendQueryParameter("redirect_uri", OAUTH_REDIRECT_URI)
        .build()
        .toString();
  }

  public Venue getVenue(String venueId){
    for (Venue venue : mVenueList){
      if (venue.getId().equals(venueId)){
        return venue;
      }
    }
    return null;
  }

  public void checkInToVenue(String venueId){
    VenueInterface venueInterface =
        mAuthenticatedRestAdapter.create(VenueInterface.class);
//    venueInterface.venueCheckIn(venueId, new Callback<Object>() {
//      @Override
//      public void success(Object object, Response response){
//        notifyCheckInListeners();
//      }
//
//      @Override
//      public void failure (RetrofitError error){
//        Log.e(TAG, "Failed to check in to venue", error);
//        if (error.getCause() instanceof UnauthorizedException){
//          sTokenStore.setAccessToken(null);
//          notifyCheckInListenersTokenExpired();
//        }
//      }
//    });
      venueInterface.venueCheckIn(venueId)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              result-> notifyCheckInListeners(),
              error -> handleCheckInException(error)
          );
//          .subscribe(new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//              notifyCheckInListeners();
//            }
//          }, new Action1<Throwable>() {
//            @Override public void call(Throwable throwable) {
//              Log.d("venueCheckIn", "Have error: " + throwable);
//              if (throwable instanceof UnauthorizedException){
//                sTokenStore.setAccessToken(null);
//                notifyCheckInListenersTokenExpired();
//              }
//            }
//          });
  }

  private void handleCheckInException(Throwable error) {
    if (error instanceof  UnauthorizedException){
      sTokenStore.setAccessToken(null);
      notifyCheckInListenersTokenExpired();
    }
  }

  private void notifyCheckInListenersTokenExpired() {
    for (VenueCheckInListener listener : mCheckInListenerList){
      listener.onTokenExpired();
    }
  }

  private void notifyCheckInListeners() {
    for (VenueCheckInListener checkInListener : mCheckInListenerList){
      checkInListener.onVenueCheckInIsFinished();
    }
  }
}
