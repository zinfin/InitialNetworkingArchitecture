package com.bignerdranch.android.networkingarchitecture.web;

import com.bignerdranch.android.networkingarchitecture.BuildConfig;
import com.bignerdranch.android.networkingarchitecture.exception.UnauthorizedException;
import com.bignerdranch.android.networkingarchitecture.listener.VenueCheckInListener;
import com.bignerdranch.android.networkingarchitecture.listener.VenueSearchListener;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.model.Venue;
import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import retrofit.Callback;
import retrofit.RestAdapter;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by sand8529 on 8/15/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class DataManagerTest {
  @Captor
  private ArgumentCaptor<Callback<VenueSearchResponse>> mSearchCaptor;
  private DataManager mDataManager;
  private static RestAdapter mBasicRestAdapter = mock(RestAdapter.class);
  private static RestAdapter mAuthenticatedRestAdapter = mock(RestAdapter.class);
  private static VenueInterface mVenueInterface = mock(VenueInterface.class);
  private static VenueSearchListener mVenuseSearchListener = mock(VenueSearchListener.class);
  private static VenueCheckInListener mVenueCheckInListener = mock(VenueCheckInListener.class);

  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);
    mDataManager = DataManager.get(RuntimeEnvironment.application, mBasicRestAdapter, mAuthenticatedRestAdapter);

    when(mBasicRestAdapter.create(VenueInterface.class)).thenReturn(mVenueInterface);
    when(mAuthenticatedRestAdapter.create(VenueInterface.class)).thenReturn(mVenueInterface);

    mDataManager.addVenueSearchListener(mVenuseSearchListener);
    mDataManager.addVenueCheckInListener(mVenueCheckInListener);
  }
  @After
  public void tearDown(){
    // clear data manager state in between tests
    reset(mBasicRestAdapter, mAuthenticatedRestAdapter, mVenueInterface, mVenuseSearchListener, mVenueCheckInListener);
    mDataManager.removeVenueSearchListener(mVenuseSearchListener);
    mDataManager.removeVenueCheckInListener(mVenueCheckInListener);
  }

  @Test
  public void searchListenerTriggeredOnSuccessfulSearch(){
    mDataManager.fetchVenueSearch();

    verify(mVenueInterface).venueSearch(anyString(), mSearchCaptor.capture());

    VenueSearchResponse response = mock(VenueSearchResponse.class);

    mSearchCaptor.getValue().success(response,null);
    verify(mVenuseSearchListener).onVenueSearchFinished();
  }

  @Test
  public void venueSearchListSavedOnSuccessfulSearch() {
    mDataManager.fetchVenueSearch();

    verify(mVenueInterface).venueSearch(anyString(), mSearchCaptor.capture());

    String firstVenueName = "Cool first venue";
    Venue firstVenue = mock(Venue.class);
    when (firstVenue.getName()).thenReturn(firstVenueName);

    String secondVenueName = "awesome second venue";
    Venue secondVenue = mock(Venue.class);
    when (secondVenue.getName()).thenReturn(secondVenueName);

    List<Venue> venueList = new ArrayList<>();
    venueList.add(firstVenue);
    venueList.add(secondVenue);

    VenueSearchResponse response = mock(VenueSearchResponse.class);
    when (response.getVenueList()).thenReturn(venueList);

    mSearchCaptor.getValue().success(response,null);

    List<Venue> dataManagerVenueList = mDataManager.getVenueList();
    assertThat(dataManagerVenueList, is(equalTo(venueList)));
  }
  @Test
  public void checkInListenerTriggeredOnSuccessfulCheckIn(){
    Observable<Object> successObservable = Observable.just(new Object());
    when(mVenueInterface.venueCheckIn(anyString())).thenReturn(successObservable);

    String fakeVenueId = "fake Venue Id";
    mDataManager.checkInToVenue(fakeVenueId);

    verify(mVenueCheckInListener).onVenueCheckInIsFinished();
  }

  @Test
  public void checkInListenerNotifiesTokenExpiredOnUnauthorizedException(){
    Observable<Object> unauthorizedObservable =
        Observable.error(new UnauthorizedException(null));
    when(mVenueInterface.venueCheckIn(anyString()))
        .thenReturn(unauthorizedObservable);

    String fakeVenueId = "fake venue id";
    mDataManager.checkInToVenue(fakeVenueId);

    verify(mVenueCheckInListener).onTokenExpired();
  }

  @Test
  public void checkInListenerDoesNotNotifyTokenExpiredOnPlainException(){
    Observable<Object> runtimeObservable = Observable.error(new RuntimeException());
    when(mVenueInterface.venueCheckIn(anyString()))
        .thenReturn(runtimeObservable);

    String fakeVenueId = "fake venue id";
    mDataManager.checkInToVenue(fakeVenueId);

    verify(mVenueCheckInListener, never()).onTokenExpired();

  }

  @Test
  public void tokenClearedFromTokenStoreOnUnauthorizedException(){
    String testToken = "abcdew";
    TokenStore tokenStore = TokenStore.get(RuntimeEnvironment.application);
    tokenStore.setAccessToken(testToken);
    assertThat(tokenStore.getAccessToken(), is(equalTo(testToken)));

    Observable<Object> unauthorizedObservable = Observable.error(new UnauthorizedException(null));
    when(mVenueInterface.venueCheckIn(anyString()))
        .thenReturn(unauthorizedObservable);

    String fakeVenueId = "fake venue id";
    mDataManager.checkInToVenue(fakeVenueId);

    assertThat(tokenStore.getAccessToken(), is(equalTo(null)));
  }
}
