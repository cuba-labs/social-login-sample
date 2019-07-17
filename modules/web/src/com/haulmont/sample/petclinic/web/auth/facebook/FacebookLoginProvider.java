package com.haulmont.sample.petclinic.web.auth.facebook;

import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.cuba.web.security.LoginProvider;
import com.haulmont.cuba.web.security.providers.ExternalUserLoginProvider;
import com.haulmont.sample.petclinic.service.FacebookService.FacebookUserData;
import com.haulmont.sample.petclinic.service.SocialRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Locale;

@Component(FacebookLoginProvider.NAME)
public class FacebookLoginProvider extends ExternalUserLoginProvider implements LoginProvider {

    public static final String NAME = "petclinic_FacebookLoginProvider";

    private static final Logger log = LoggerFactory.getLogger(FacebookLoginProvider.class);

    @Inject
    private SocialRegistrationService socialRegistrationService;

    @Nullable
    @Override
    public AuthenticationDetails login(Credentials credentials) throws LoginException {
        log.info("Logging in Facebook user: '{}'", credentials);

        FacebookCredentials facebookCredentials = (FacebookCredentials) credentials;
        FacebookUserData userData = facebookCredentials.getUserData();

        User user = socialRegistrationService.findOrRegisterFacebookUser(
                userData.getId(),
                userData.getName(),
                userData.getEmail());

        Locale defaultLocale = facebookCredentials.getLocale();

        return super.login(new ExternalUserCredentials(user.getLogin(), defaultLocale));
    }

    @Override
    public boolean supports(Class<?> credentialsClass) {
        return FacebookCredentials.class.isAssignableFrom(credentialsClass);
    }
}
