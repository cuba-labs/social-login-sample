package com.haulmont.sample.petclinic.web.auth.vk;

import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.cuba.web.security.LoginProvider;
import com.haulmont.cuba.web.security.providers.ExternalUserLoginProvider;
import com.haulmont.sample.petclinic.service.SocialRegistrationService;
import com.haulmont.sample.petclinic.service.VkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Locale;

@Component(VkLoginProvider.NAME)
public class VkLoginProvider extends ExternalUserLoginProvider implements LoginProvider {

    public static final String NAME = "petclinic_VkLoginProvider";

    private static final Logger log = LoggerFactory.getLogger(VkLoginProvider.class);

    @Inject
    private SocialRegistrationService socialRegistrationService;

    @Nullable
    @Override
    public AuthenticationDetails login(Credentials credentials) throws LoginException {
        log.info("Logging in VK user: '{}'", credentials);

        VkCredentials vkCredentials = (VkCredentials) credentials;
        VkService.VkUserData userData = vkCredentials.getUserData();

        User user = socialRegistrationService.findOrRegisterVkUser(
                userData.getId(),
                userData.getName(),
                userData.getScreenName());

        Locale defaultLocale = vkCredentials.getLocale();

        return super.login(new ExternalUserCredentials(user.getLogin(), defaultLocale));
    }

    @Override
    public boolean supports(Class<?> credentialsClass) {
        return VkCredentials.class.isAssignableFrom(credentialsClass);
    }
}
