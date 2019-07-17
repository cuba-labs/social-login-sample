package com.haulmont.sample.petclinic.web.auth.vk;

import com.haulmont.cuba.security.auth.AbstractClientCredentials;
import com.haulmont.sample.petclinic.service.VkService.VkUserData;

import java.util.Collections;
import java.util.Locale;

public class VkCredentials extends AbstractClientCredentials {

    private final VkUserData userData;

    public VkCredentials(VkUserData userData, Locale locale) {
        super(locale, Collections.emptyMap());
        this.userData = userData;
    }

    @Override
    public String getUserIdentifier() {
        return userData.getId();
    }

    public VkUserData getUserData() {
        return userData;
    }

    @Override
    public String toString() {
        return "VkCredentials{" +
                "id='" + userData.getId() + '\'' +
                '}';
    }
}
