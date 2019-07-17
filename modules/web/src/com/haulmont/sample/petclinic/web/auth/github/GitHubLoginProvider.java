package com.haulmont.sample.petclinic.web.auth.github;

import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.cuba.web.security.LoginProvider;
import com.haulmont.cuba.web.security.providers.ExternalUserLoginProvider;
import com.haulmont.sample.petclinic.service.GitHubService;
import com.haulmont.sample.petclinic.service.SocialRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Locale;

@Component(GitHubLoginProvider.NAME)
public class GitHubLoginProvider extends ExternalUserLoginProvider implements LoginProvider {

    public static final String NAME = "petclinic_GitHubLoginProvider";

    private static final Logger log = LoggerFactory.getLogger(GitHubLoginProvider.class);

    @Inject
    private SocialRegistrationService socialRegistrationService;

    @Nullable
    @Override
    public AuthenticationDetails login(Credentials credentials) throws LoginException {
        log.info("Logging in GitHub user: '{}'", credentials);

        GitHubCredentials gitHubCredentials = (GitHubCredentials) credentials;
        GitHubService.GitHubUserData userData = gitHubCredentials.getUserData();

        User user = socialRegistrationService.findOrRegisterGitHubUser(
                userData.getId(),
                userData.getName(),
                userData.getLogin());

        Locale defaultLocale = gitHubCredentials.getLocale();

        return super.login(new ExternalUserCredentials(user.getLogin(), defaultLocale));
    }

    @Override
    public boolean supports(Class<?> credentialsClass) {
        return GitHubCredentials.class.isAssignableFrom(credentialsClass);
    }
}
