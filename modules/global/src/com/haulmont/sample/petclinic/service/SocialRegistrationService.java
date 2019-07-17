package com.haulmont.sample.petclinic.service;

import com.haulmont.cuba.security.entity.User;

public interface SocialRegistrationService {

    String NAME = "petclinic_SocialRegistrationService";

    User findOrRegisterFacebookUser(String facebookId, String name, String email);

    User findOrRegisterGitHubUser(String githubId, String name, String login);

    User findOrRegisterGoogleUser(String googleId, String name, String email);

    User findOrRegisterVkUser(String vkId, String name, String screenName);
}