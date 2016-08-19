package com.bignerdranch.android.networkingarchitecture.web;

import com.bignerdranch.android.networkingarchitecture.exception.UnauthorizedException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.net.HttpURLConnection;

/**
 * Created by sand8529 on 8/15/16.
 */
public class RetrofitErrorHandler implements ErrorHandler {
  @Override public Throwable handleError(RetrofitError cause) {
    Response response = cause.getResponse();
    if (response != null && response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED){
      return new UnauthorizedException(cause);
    }
    return cause;
  }
}
