package com.haulmont.sample.petclinic.auth;

import com.google.common.collect.ImmutableMap;
import com.haulmont.bali.util.URLEncodeUtils;

import java.util.Map;

public final class SocialLoginHelper {

    private static final String FACEBOOK_AUTH_ENDPOINT = "https://www.facebook.com/v3.3/dialog/oauth?";
    private static final String GITHUB_AUTH_ENDPOINT = "https://github.com/login/oauth/authorize?";
    private static final String GOOGLE_AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth?";

    private static final String GOOGLE_ACCESS_TOKEN_PATH = "https://www.googleapis.com/oauth2/v4/token";
    private static final String FACEBOOK_ACCESS_TOKEN_PATH = "https://graph.facebook.com/v3.3/oauth/access_token?";
    private static final String GITHUB_ACCESS_TOKEN_PATH = "https://github.com/login/oauth/access_token?";

    private static final String GOOGLE_USER_DATA_ENDPOINT = "https://www.googleapis.com/userinfo/v2/me?";
    private static final String FACEBOOK_USER_DATA_ENDPOINT = "https://graph.facebook.com/v3.3/me?";
    private static final String GITHUB_USER_DATA_ENDPOINT = "https://api.github.com/user?";

    private SocialLoginHelper() {
    }

    public static String getAuthEndpoint(SocialService socialService) {
        switch (socialService) {
            case GOOGLE:
                return GOOGLE_AUTH_ENDPOINT;
            case FACEBOOK:
                return FACEBOOK_AUTH_ENDPOINT;
            case GITHUB:
                return GITHUB_AUTH_ENDPOINT;
            default:
                throw new IllegalArgumentException("No endpoint found for service: " + socialService);
        }
    }

    public static String getAuthParams(SocialService socialService,
                                       String clientId, String redirectUri) {
        switch (socialService) {
            case GOOGLE:
                return getGoogleAuthParams(clientId, redirectUri);
            case FACEBOOK:
                return getFacebookAuthParams(clientId, redirectUri);
            case GITHUB:
                return getGitHubAuthParams(clientId, redirectUri);
            default:
                throw new IllegalArgumentException("No endpoint found for service: " + socialService);
        }
    }

    public static String getAccessTokenPath(SocialService socialService, String clientId, String clientSecret,
                                            String redirectUri, String authCode) {
        switch (socialService) {
            case GOOGLE:
                return GOOGLE_ACCESS_TOKEN_PATH;
            case FACEBOOK:
                return FACEBOOK_ACCESS_TOKEN_PATH +
                        getFacebookAccessTokenParams(clientId, clientSecret, redirectUri, authCode);
            case GITHUB:
                return GITHUB_ACCESS_TOKEN_PATH +
                        getGitHubAccessTokenParams(clientId, clientSecret, redirectUri, authCode);
            default:
                throw new IllegalArgumentException("Unable to find access token path for social service: " + socialService);
        }
    }

    public static String getUserDataEndpoint(SocialService socialService) {
        switch (socialService) {
            case GOOGLE:
                return GOOGLE_USER_DATA_ENDPOINT;
            case FACEBOOK:
                return FACEBOOK_USER_DATA_ENDPOINT;
            case GITHUB:
                return GITHUB_USER_DATA_ENDPOINT;
            default:
                throw new IllegalArgumentException("No user data endpoint found for service: " + socialService);
        }
    }

    private static String getGoogleAuthParams(String clientId, String redirectUri) {
        return "client_id=" + clientId +
                "&response_type=code" +
                "&access_type=offline" +
                "&scope=" + encode(
                "https://www.googleapis.com/auth/userinfo.profile " +
                        "https://www.googleapis.com/auth/userinfo.email") +
                "&redirect_uri=" + encode(redirectUri);
    }

    private static String getFacebookAuthParams(String clientId, String redirectUri) {
        return "client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + encode(redirectUri);
    }

    private static String getGitHubAuthParams(String clientId, String redirectUri) {
        return "client_id=" + clientId +
                "&scope=user%20" + encode("user:email") +
                "&redirect_uri=" + encode(redirectUri);
    }

    public static Map<String, String> getGoogleAccessTokenParams(String clientId, String clientSecret,
                                                                 String redirectUri, String authCode) {
        return ImmutableMap.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", redirectUri,
                "code", authCode,
                "grant_type", "authorization_code"
        );
    }

    private static String getFacebookAccessTokenParams(String clientId, String clientSecret, String redirectUri,
                                                       String authCode) {
        return "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + encode(redirectUri) +
                "&code=" + authCode;
    }

    private static String getGitHubAccessTokenParams(String clientId, String clientSecret, String redirectUri,
                                                     String authCode) {
        return "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + encode(redirectUri) +
                "&code=" + authCode;
    }

    private static String encode(String s) {
        return URLEncodeUtils.encodeUtf8(s);
    }
}
