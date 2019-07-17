package com.haulmont.sample.petclinic.web.auth.facebook;

import com.haulmont.cuba.security.auth.AbstractClientCredentials;
import com.haulmont.sample.petclinic.service.FacebookService.FacebookUserData;

import java.util.Collections;
import java.util.Locale;

public class FacebookCredentials extends AbstractClientCredentials {

    private final FacebookUserData userData;

    public FacebookCredentials(FacebookUserData userData, Locale locale) {
        super(locale, Collections.emptyMap());
        this.userData = userData;
    }

    @Override
    public String getUserIdentifier() {
        return userData.getId();
    }

    public FacebookUserData getUserData() {
        return userData;
    }

    @Override
    public String toString() {
        return "FacebookCredentials{" +
                "id='" + userData.getId() + '\'' +
                '}';
    }
}
