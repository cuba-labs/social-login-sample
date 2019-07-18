package com.haulmont.sample.petclinic.service;

import com.haulmont.cuba.security.entity.User;
import com.haulmont.sample.petclinic.auth.SocialService;

public interface SocialRegistrationService {

    String NAME = "petclinic_SocialRegistrationService";

    User findOrRegisterUser(String socialServiceId, String login, String name, SocialService socialService);
}