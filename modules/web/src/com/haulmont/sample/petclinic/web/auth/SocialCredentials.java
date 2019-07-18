package com.haulmont.sample.petclinic.web.auth;

import com.haulmont.cuba.security.auth.AbstractClientCredentials;
import com.haulmont.sample.petclinic.auth.SocialService;
import com.haulmont.sample.petclinic.service.SocialLoginService.SocialUserData;

import java.util.Collections;
import java.util.Locale;

public class SocialCredentials extends AbstractClientCredentials {

    private final SocialUserData userData;
    private final SocialService socialService;

    public SocialCredentials(SocialUserData userData, SocialService socialService, Locale locale) {
        super(locale, Collections.emptyMap());
        this.userData = userData;
        this.socialService = socialService;
    }

    @Override
    public String getUserIdentifier() {
        return userData.getId();
    }

    public SocialUserData getUserData() {
        return userData;
    }

    public SocialService getSocialService() {
        return socialService;
    }

    @Override
    public String toString() {
        return "SocialCredentials{" +
                "id='" + userData.getId() + '\'' +
                "login='" + userData.getLogin() + '\'' +
                "name='" + userData.getName() + '\'' +
                '}';
    }
}
