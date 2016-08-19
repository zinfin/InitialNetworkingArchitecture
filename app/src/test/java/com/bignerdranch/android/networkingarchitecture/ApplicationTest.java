package com.bignerdranch.android.networkingarchitecture;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by sand8529 on 8/15/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class ApplicationTest {
  @Test
  public void testRobolectricSetupWorks(){
    assertThat(1, equalTo(1));
  }
}
