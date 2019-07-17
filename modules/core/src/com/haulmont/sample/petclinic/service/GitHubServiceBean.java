package com.haulmont.sample.petclinic.service;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.bali.util.URLEncodeUtils;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.sample.petclinic.config.GitHubConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service(GitHubService.NAME)
public class GitHubServiceBean implements GitHubService {

    private static final String GITHUB_ACCESS_TOKEN_PATH = "https://github.com/login/oauth/access_token?";

    @Inject
    private Configuration configuration;

    @Override
    public String getLoginUrl(String appUrl, GitHubService.OAuth2ResponseType responseType) {
        GitHubConfig config = configuration.getConfig(GitHubConfig.class);

        String authUrl = "https://github.com/login/oauth/authorize";
        String appId = config.getGitHubAppId();
        String scope = "user" +
                "%20" +
                URLEncodeUtils.encodeUtf8("user:email");

        return authUrl + "?client_id=" + appId +
                "&scope=" + scope +
                "&redirect_uri=" + getEncodedRedirectUrl(appUrl);
    }

    @Override
    public GitHubUserData getUserData(String appUrl, String code) {
        String accessToken = getAccessToken(code, appUrl);
        String userDataJson = getUserDataAsJson(accessToken);

        JsonParser parser = new JsonParser();

        JsonObject response = parser.parse(userDataJson)
                .getAsJsonObject();


        String id = Strings.nullToEmpty(response.get("id").getAsString());
        String name = Strings.nullToEmpty(response.get("name").getAsString());
        String email = Strings.nullToEmpty(response.get("email").getAsString());

        if (StringUtils.isEmpty(email)) {
            String login = Strings.nullToEmpty(response.get("login").getAsString());
            return new GitHubUserData(id, name, login);
        } else {
            return new GitHubUserData(id, name, email);
        }
    }

    private String getAccessToken(String code, String appUrl) {
        String accessTokenPath = getAccessTokenPath(code, appUrl);
        String response = requestAccessToken(accessTokenPath);
        JsonParser parser = new JsonParser();
        JsonObject asJsonObject = parser.parse(response).getAsJsonObject();
        return asJsonObject.get("access_token").getAsString();
    }

    private String getUserDataAsJson(String accessToken) {
        String url = "https://api.github.com/user?access_token=" +
                accessToken;
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
                throw new RuntimeException("Unable to access GitHub API. Response HTTP status: "
                        + httpResponse.getStatusLine().getStatusCode());
            }
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            getRequest.releaseConnection();
        }
    }

    private String getAccessTokenPath(String code, String appUrl) {
        GitHubConfig config = configuration.getConfig(GitHubConfig.class);

        String appId = config.getGitHubAppId();
        String appSecret = config.getGitHubAppSecret();
        String redirectUrl = getEncodedRedirectUrl(appUrl);

        return GITHUB_ACCESS_TOKEN_PATH +
                "client_id=" + appId +
                "&client_secret=" + appSecret +
                "&redirect_uri=" + redirectUrl +
                "&code=" + code;
    }

    private String requestAccessToken(String accessTokenPath) {
        HttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();

        HttpGet getRequest = new HttpGet(accessTokenPath);
        getRequest.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        try {
            HttpResponse httpResponse = httpClient.execute(getRequest);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Unable to access GitHub API. Response HTTP status: "
                        + httpResponse.getStatusLine().getStatusCode());
            }
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            getRequest.releaseConnection();
        }
    }
}