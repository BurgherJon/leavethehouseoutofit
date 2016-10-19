package com.lthoi;

/**
 * Contains the client IDs and scopes for allowed clients consuming your API.
 */
public class Constants {
  //The first web client id is for prod, the second is for test.
  public static final String WEB_CLIENT_ID = "470243017734-h8rqcqsmvbhh4qrc95dra34anvo6h4ci.apps.googleusercontent.com";
  //public static final String WEB_CLIENT_ID = "424262593373-ktjrme8l5aeqlgo61dei3lesj30m8fop.apps.googleusercontent.com";
  public static final String ANDROID_CLIENT_ID = "replace this with your Android client ID";
  public static final String IOS_CLIENT_ID = "replace this with your iOS client ID";
  public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;

  public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
}
