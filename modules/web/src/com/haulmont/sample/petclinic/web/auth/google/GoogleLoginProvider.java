package com.haulmont.sample.petclinic.web.auth.google;

import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.cuba.web.security.LoginProvider;
import com.haulmont.cuba.web.security.providers.ExternalUserLoginProvider;
import com.haulmont.sample.petclinic.service.GoogleService;
import com.haulmont.sample.petclinic.service.SocialRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Locale;

@Component(GoogleLoginProvider.NAME)
public class GoogleLoginProvider extends ExternalUserLoginProvider implements LoginProvider {

    public static final String NAME = "petclinic_GoogleLoginProvider";

    private static final Logger log = LoggerFactory.getLogger(GoogleLoginProvider.class);

    @Inject
    private SocialRegistrationService socialRegistrationService;

    @Nullable
    @Override
    public AuthenticationDetails login(Credentials credentials) throws LoginException {
        log.info("Logging in Google user: '{}'", credentials);

        GoogleCredentials googleCredentials = (GoogleCredentials) credentials;
        GoogleService.GoogleUserData userData = googleCredentials.getUserData();

        User user = socialRegistrationService.findOrRegisterGoogleUser(
                userData.getId(),
                userData.getName(),
                userData.getEmail());

        Locale defaultLocale = googleCredentials.getLocale();

        return super.login(new ExternalUserCredentials(user.getLogin(), defaultLocale));
    }

    @Override
    public boolean supports(Class<?> credentialsClass) {
        return GoogleCredentials.class.isAssignableFrom(credentialsClass);
    }
}
