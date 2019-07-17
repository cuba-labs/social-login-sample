package com.haulmont.sample.petclinic.web.auth.google;

import com.haulmont.cuba.security.auth.AbstractClientCredentials;
import com.haulmont.sample.petclinic.service.GoogleService.GoogleUserData;

import java.util.Collections;
import java.util.Locale;

public class GoogleCredentials extends AbstractClientCredentials {

    private final GoogleUserData userData;

    public GoogleCredentials(GoogleUserData userData, Locale locale) {
        super(locale, Collections.emptyMap());
        this.userData = userData;
    }

    @Override
    public String getUserIdentifier() {
        return userData.getId();
    }

    public GoogleUserData getUserData() {
        return userData;
    }

    @Override
    public String toString() {
        return "GoogleCredentials{" +
                "id='" + userData.getId() + '\'' +
                '}';
    }
}
