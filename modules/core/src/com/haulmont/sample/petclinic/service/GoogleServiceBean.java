package com.haulmont.sample.petclinic.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.bali.util.URLEncodeUtils;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.sample.petclinic.config.GoogleConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(GoogleService.NAME)
public class GoogleServiceBean implements GoogleService {

    private static final String GOOGLE_ACCESS_TOKEN_PATH = "https://www.googleapis.com/oauth2/v4/token";

    @Inject
    private Configuration configuration;

    @Override
    public String getLoginUrl(String appUrl, GoogleService.OAuth2ResponseType responseType) {
        GoogleConfig config = configuration.getConfig(GoogleConfig.class);

        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth";
        String appId = config.getGoogleAppId();

        String redirectUrl = getEncodedRedirectUrl(appUrl);

        return authUrl + "?client_id=" + appId +
                "&response_type=code" +
                "&access_type=offline" +
                "&scope=" +
                    getEncodedRedirectUrl("https://www.googleapis.com/auth/userinfo.profile " +
                                    "https://www.googleapis.com/auth/userinfo.email") +
                "&redirect_uri=" + redirectUrl;
    }

    @Override
    public GoogleService.GoogleUserData getUserData(String appUrl, String code) {
        String accessToken = getAccessToken(code, appUrl);
        String userDataJson = getUserDataAsJson(accessToken);

        JsonParser parser = new JsonParser();

        JsonObject response = parser.parse(userDataJson)
                .getAsJsonObject();

        String id = Strings.nullToEmpty(response.get("id").getAsString());
        String name = Strings.nullToEmpty(response.get("name").getAsString());
        String email = Strings.nullToEmpty(response.get("email").getAsString());

        return new GoogleUserData(id, name, email);
    }

    private String getAccessToken(String code, String appUrl) {
        String response = requestAccessToken(getAccessTokenRequestParams(appUrl, code));

        JsonParser parser = new JsonParser();
        JsonObject asJsonObject = parser.parse(response).getAsJsonObject();

        return asJsonObject.get("access_token").getAsString();
    }

    private String getUserDataAsJson(String accessToken) {
        String url = "https://www.googleapis.com/userinfo/v2/me?" +
                "fields=" + URLEncodeUtils.encodeUtf8("id,name,email") +
                "&alt=json" +
                "&access_token=" + accessToken;

        return requestUserData(url);
    }

    private String getEncodedRedirectUrl(String appUrl) {
        return URLEncodeUtils.encodeUtf8(appUrl);
    }

    private String requestUserData(String url) {
        HttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();

        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(getRequest);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Unable to access Google API. Response HTTP status: "
                        + httpResponse.getStatusLine().getStatusCode());
            }
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            getRequest.releaseConnection();
        }
    }

    private Map<String, String> getAccessTokenRequestParams(String appUrl, String code) {
        GoogleConfig config = configuration.getConfig(GoogleConfig.class);

        String appId = config.getGoogleAppId();
        String appSecret = config.getGoogleAppSecret();

        return ImmutableMap.of(
                "client_id", appId,
                "client_secret", appSecret,
                "redirect_uri", appUrl,
                "code", code,
                "grant_type", "authorization_code"
        );
    }

    private String requestAccessToken(Map<String, String> params) {
        HttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();

        HttpPost postRequest = new HttpPost(GOOGLE_ACCESS_TOKEN_PATH);

        List<BasicNameValuePair> requestParams = params.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        postRequest.setEntity(new UrlEncodedFormEntity(requestParams, StandardCharsets.UTF_8));

        try {
            HttpResponse httpResponse = httpClient.execute(postRequest);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Unable to access Google API. Response HTTP status: "
                        + httpResponse.getStatusLine().getStatusCode());
            }
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            postRequest.releaseConnection();
        }
    }
}