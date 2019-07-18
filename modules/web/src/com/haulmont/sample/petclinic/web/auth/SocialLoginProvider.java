package com.haulmont.sample.petclinic.web.auth;

import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.cuba.web.security.LoginProvider;
import com.haulmont.cuba.web.security.providers.ExternalUserLoginProvider;
import com.haulmont.sample.petclinic.service.SocialLoginService;
import com.haulmont.sample.petclinic.service.SocialRegistrationService;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Locale;

@Component(SocialLoginProvider.NAME)
public class SocialLoginProvider extends ExternalUserLoginProvider implements LoginProvider {

    public static final String NAME = "petclinic_SocialLoginProvider";

    @Inject
    private SocialRegistrationService socialRegistrationService;

    @Nullable
    @Override
    public AuthenticationDetails login(Credentials credentials) throws LoginException {
        SocialCredentials socialCredentials = (SocialCredentials) credentials;

        SocialLoginService.SocialUserData userData = socialCredentials.getUserData();

        User user = socialRegistrationService.findOrRegisterUser(
                userData.getId(),
                userData.getLogin(),
                userData.getName(),
                socialCredentials.getSocialService());

        Locale defaultLocale = socialCredentials.getLocale();

        return super.login(new ExternalUserCredentials(user.getLogin(), defaultLocale));
    }

    @Override
    public boolean supports(Class<?> credentialsClass) {
        return SocialCredentials.class.isAssignableFrom(credentialsClass);
    }
}
