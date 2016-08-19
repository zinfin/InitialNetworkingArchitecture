package com.bignerdranch.android.networkingarchitecture.inject;

import com.bignerdranch.android.networkingarchitecture.controller.AuthenticationActivity;
import com.bignerdranch.android.networkingarchitecture.controller.VenueDetailActivity;
import com.bignerdranch.android.networkingarchitecture.controller.VenueDetailFragment;
import com.bignerdranch.android.networkingarchitecture.controller.VenueListFragment;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by sand8529 on 8/16/16.
 */
@Singleton
@Component(modules = {VenueApplicationModule.class})
public interface DataManagerComponent {
  void inject(VenueDetailFragment fragment);
  void inject(VenueListFragment fragment);
  void inject(AuthenticationActivity authenticationActivity);
}
