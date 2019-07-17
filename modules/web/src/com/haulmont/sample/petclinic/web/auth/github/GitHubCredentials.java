package com.haulmont.sample.petclinic.web.auth.github;

import com.haulmont.cuba.security.auth.AbstractClientCredentials;
import com.haulmont.sample.petclinic.service.GitHubService.GitHubUserData;

import java.util.Collections;
import java.util.Locale;

public class GitHubCredentials extends AbstractClientCredentials {

    private final GitHubUserData userData;

    public GitHubCredentials(GitHubUserData userData, Locale locale) {
        super(locale, Collections.emptyMap());
        this.userData = userData;
    }

    @Override
    public String getUserIdentifier() {
        return userData.getId();
    }

    public GitHubUserData getUserData() {
        return userData;
    }

    @Override
    public String toString() {
        return "GitHubCredentials{" +
                "id='" + userData.getId() + '\'' +
                '}';
    }
}
