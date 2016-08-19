package com.bignerdranch.android.networkingarchitecture;

import java.util.concurrent.Executor;

/**
 * Created by sand8529 on 8/15/16.
 */
public class SynchronousExecutor implements Executor {
  @Override public void execute(Runnable command) {
    command.run();
  }
}
