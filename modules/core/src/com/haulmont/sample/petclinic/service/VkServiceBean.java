package com.haulmont.sample.petclinic.service;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.bali.util.URLEncodeUtils;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.sample.petclinic.config.VkConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service(VkService.NAME)
public class VkServiceBean implements VkService {

    private static final String VK_ACCESS_TOKEN_PATH = "https://oauth.vk.com/access_token?";

    @Inject
    private Configuration configuration;

    @Override
    public String getLoginUrl(String appUrl, VkService.OAuth2ResponseType responseType) {
        VkConfig config = configuration.getConfig(VkConfig.class);

        String authUrl = "https://oauth.vk.com/authorize";
        String appId = config.getVkAppId();

        String redirectUrl = getEncodedRedirectUrl(appUrl);

        return authUrl + "?client_id=" + appId +
                "&display=page" +
                "&response_type=" + responseType.getId() +
                "&redirect_uri=" + redirectUrl;
    }

    @Override
    public VkUserData getUserData(String appUrl, String code) {
        String accessToken = getAccessToken(code, appUrl);
        String userDataJson = getUserDataAsJson(accessToken);

        JsonParser parser = new JsonParser();

        JsonObject responseObject = parser.parse(userDataJson)
                .getAsJsonObject();

        JsonObject response = responseObject.get("response").getAsJsonArray()
                .get(0).getAsJsonObject();


        String id = Strings.nullToEmpty(response.get("id").getAsString());

        String firstName = Strings.nullToEmpty(response.get("first_name").getAsString());
        String lastName = Strings.nullToEmpty(response.get("last_name").getAsString());
        String name = StringUtils.trim(firstName + ' ' + lastName);

        String screenName = Strings.nullToEmpty(response.get("screen_name").getAsString());

        return new VkUserData(id, name, screenName);
    }

    private String getAccessToken(String code, String appUrl) {
        String accessTokenPath = getAccessTokenPath(code, appUrl);
        String response = requestAccessToken(accessTokenPath);
        JsonParser parser = new JsonParser();
        JsonObject asJsonObject = parser.parse(response).getAsJsonObject();
        return asJsonObject.get("access_token").getAsString();
    }

    private String getUserDataAsJson(String accessToken) {
        VkConfig config = configuration.getConfig(VkConfig.class);

        String fields = config.getVkFields();
        String url = "https://api.vk.com/method/users.get?" +
                "access_token=" + accessToken +
                "&fields=" + fields +
                "&v=" + "5.100";

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
                throw new RuntimeException("Unable to access Vk API. Response HTTP status: "
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
        VkConfig config = configuration.getConfig(VkConfig.class);

        String appId = config.getVkAppId();
        String appSecret = config.getVkAppSecret();
        String redirectUrl = getEncodedRedirectUrl(appUrl);

        return VK_ACCESS_TOKEN_PATH +
                "client_id=" + appId +
                "&client_secret=" + appSecret +
                "&redirect_uri=" + redirectUrl +
                "&code=" + code;
    }

    private String requestAccessToken(String accessTokenPath) {
        HttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();

        HttpGet getRequest = new HttpGet(accessTokenPath);
        try {
            HttpResponse httpResponse = httpClient.execute(getRequest);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Unable to access Vk API. Response HTTP status: "
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