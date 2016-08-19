package com.bignerdranch.android.networkingarchitecture;

import android.app.Application;
import android.content.Context;
import com.bignerdranch.android.networkingarchitecture.inject.DaggerDataManagerComponent;
import com.bignerdranch.android.networkingarchitecture.inject.DataManagerComponent;
import com.bignerdranch.android.networkingarchitecture.inject.VenueApplicationModule;
import timber.log.Timber;

/**
 * Created by sand8529 on 8/16/16.
 */
public class VenueApplication extends Application {
  private DataManagerComponent mComponent;

  @Override
  public void onCreate(){
    super.onCreate();
    Timber.plant(new Timber.DebugTree());
    buildComponentAndInject();
  }

  private void buildComponentAndInject() {
    mComponent = DaggerComponentInitializer.init(this);
  }

  public static DataManagerComponent component(Context context){
    return ((VenueApplication)context.getApplicationContext()).getComponent();
  }

  public  DataManagerComponent getComponent(){
    return mComponent;
  }

  private final static class DaggerComponentInitializer{
    public static DataManagerComponent init(VenueApplication app){
      return DaggerDataManagerComponent.builder()
          .venueApplicationModule(new VenueApplicationModule(app))
          .build();
    }
  }
}
