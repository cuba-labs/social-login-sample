package com.haulmont.sample.petclinic.service;

import com.haulmont.sample.petclinic.auth.SocialService;

import java.io.Serializable;

public interface SocialLoginService {

    String NAME = "petclinic_SocialLoginService";

    String getLoginUrl(SocialService socialService);

    SocialUserData getUserData(SocialService socialService, String authCode);

    class SocialUserData implements Serializable {

        private String id;
        private String login;
        private String name;

        public SocialUserData(String id, String login, String name) {
            this.id = id;
            this.login = login;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "SocialUserData{" +
                    "id='" + id + '\'' +
                    ", login='" + login + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}