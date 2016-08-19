package com.bignerdranch.android.networkingarchitecture.com.bignerdranch.android.inject.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;
import com.bignerdranch.android.networkingarchitecture.BuildConfig;
import com.bignerdranch.android.networkingarchitecture.R;
import com.bignerdranch.android.networkingarchitecture.SynchronousExecutor;
import com.bignerdranch.android.networkingarchitecture.controller.VenueDetailActivity;
import com.bignerdranch.android.networkingarchitecture.controller.VenueDetailFragment;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.model.VenueSearchResponse;
import com.bignerdranch.android.networkingarchitecture.web.DataManager;
import com.bignerdranch.android.networkingarchitecture.web.RetrofitErrorHandler;
import com.bignerdranch.android.networkingarchitecture.web.VenueListDeserializer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

import static org.hamcrest.CoreMatchers.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

/**
 * Created by sand8529 on 8/15/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class VenueDetailFragmentTest {
  @Rule
  public WireMockRule mWireMockRule = new WireMockRule(1111);
  private String mEndpoint = "http://localhost:1111";
  private DataManager mDataManager;
  private VenueDetailActivity mVenueDetailtActivity;
  private VenueDetailFragment mVenueDetailFragment;

  @Before
  public void setUp() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer()).create();
    SynchronousExecutor executor = new SynchronousExecutor();
    RestAdapter basicRestAdapter = new RestAdapter.Builder()
        .setEndpoint(mEndpoint)
        .setConverter(new GsonConverter(gson))
        .setExecutors(executor, executor)
        .build();
    RestAdapter authenticateRestAdapter = new RestAdapter.Builder()
        .setEndpoint(mEndpoint)
        .setConverter(new GsonConverter(gson))
        .setErrorHandler(new RetrofitErrorHandler())
        .setExecutors(executor, executor)
        .build();
    mDataManager = DataManager.get(RuntimeEnvironment.application, basicRestAdapter, authenticateRestAdapter);

    stubFor(get(urlMatching("/venues/search.*"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBodyFile("search.json")));

    mDataManager.fetchVenueSearch();

    TokenStore tokenStore = TokenStore.get(RuntimeEnvironment.application);
    tokenStore.setAccessToken("bogus token for testing");
  }
  @Test
  public void toastShownOnSuccessfulCheckIn(){
    stubFor(post(urlMatching("/checkins/add.*"))
    .willReturn(aResponse()
      .withStatus(200)));
    String venueId = "527c1d4f11d20f41ba39fc01";
    Intent detailIntent = VenueDetailActivity.newIntent(RuntimeEnvironment.application, venueId);
    mVenueDetailtActivity = Robolectric.buildActivity(VenueDetailActivity.class)
        .withIntent(detailIntent)
        .create().start().resume().get();
    mVenueDetailFragment = (VenueDetailFragment) mVenueDetailtActivity
        .getSupportFragmentManager()
        .findFragmentById(R.id.fragment_container);
    Button checkInBtn = (Button) mVenueDetailFragment.getView().findViewById(R.id.fragment_venue_detail_check_in_button);
    checkInBtn.performClick();

    String expectedToast = mVenueDetailtActivity.getString(R.string.successful_check_in_message);
    assertThat(ShadowToast.getTextOfLatestToast(), is(equalTo(expectedToast)));
  }

  @Test
  public void errorDialogShownOnUnauthorizedException(){
    stubFor(post(urlMatching("/checkins/add.*"))
        .willReturn(aResponse()
            .withStatus(401)));
    String venueId = "527c1d4f11d20f41ba39fc01";
    Intent detailIntent = VenueDetailActivity.newIntent(RuntimeEnvironment.application, venueId);
    mVenueDetailtActivity = Robolectric.buildActivity(VenueDetailActivity.class)
        .withIntent(detailIntent)
        .create().start().resume().get();
    mVenueDetailFragment = (VenueDetailFragment) mVenueDetailtActivity
        .getSupportFragmentManager()
        .findFragmentById(R.id.fragment_container);
    Button checkInBtn = (Button) mVenueDetailFragment.getView().findViewById(R.id.fragment_venue_detail_check_in_button);
    checkInBtn.performClick();

    ShadowLooper.idleMainLooper();

    AlertDialog errorDialog = ShadowAlertDialog.getLatestAlertDialog();
    assertThat(errorDialog, is(notNullValue()));

    ShadowAlertDialog alertDialog = shadowOf(errorDialog);
    String expectedDialogTitlte = mVenueDetailtActivity.getString(R.string.expired_token_dialog_title);
    String expectedDialogMessage = mVenueDetailtActivity.getString(R.string.expired_token_dialog_message);
    assertThat(alertDialog.getTitle(), is(equalTo(expectedDialogTitlte)));
    assertThat(alertDialog.getMessage(), is(equalTo(expectedDialogMessage)));
  }
}
