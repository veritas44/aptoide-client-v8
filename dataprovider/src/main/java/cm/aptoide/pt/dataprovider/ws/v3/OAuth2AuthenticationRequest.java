/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.OAuth;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 25-04-2016.
 */
public class OAuth2AuthenticationRequest extends V3<OAuth> {

  public OAuth2AuthenticationRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static OAuth2AuthenticationRequest of(String username, String password, String mode,
      @Nullable String nameForGoogle, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, String extraId,
      String authMode) {

    final BaseBody body = new BaseBody();

    body.put("grant_type", "password");
    body.put("client_id", "Aptoide");
    body.put("mode", "json");

    if (mode != null) {
      switch (mode) {
        case "APTOIDE":
          body.put("username", username);
          body.put("password", password);
          break;
        case "GOOGLE":
          body.put("authMode", authMode);
          body.put("oauthUserName", nameForGoogle);
          body.put("oauthToken", password);
          break;
        case "FACEBOOK":
          body.put("authMode", authMode);
          body.put("oauthToken", password);
          break;
        case "ABAN":
          body.put("oauthUserName", username);
          body.put("oauthToken", password);
          body.put("authMode", authMode);
          body.put("oauthUser", nameForGoogle);
          break;
      }
    }

    if (!TextUtils.isEmpty(extraId)) {
      body.put("oem_id", extraId);
    }

    return new OAuth2AuthenticationRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static OAuth2AuthenticationRequest of(String refreshToken,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, String extraId) {

    final BaseBody body = new BaseBody();

    body.put("grant_type", "refresh_token");
    body.put("client_id", "Aptoide");
    body.put("mode", "json");

    if (!TextUtils.isEmpty(extraId)) {
      body.put("oem_id", extraId);
    }
    body.put("refresh_token", refreshToken);

    return new OAuth2AuthenticationRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<OAuth> loadDataFromNetwork(Service service, boolean bypassCache) {
    return service.oauth2Authentication(map, bypassCache);
  }
}
