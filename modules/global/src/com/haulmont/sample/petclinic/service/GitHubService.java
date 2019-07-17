package com.haulmont.sample.petclinic.service;

import java.io.Serializable;

public interface GitHubService {

    String NAME = "petclinic_GitHubService";

    String getLoginUrl(String appUrl, OAuth2ResponseType responseType);

    GitHubUserData getUserData(String appUrl, String code);

    enum OAuth2ResponseType {

        CODE("code");

        private final String id;

        OAuth2ResponseType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    class GitHubUserData implements Serializable {

        private String id;
        private String name;
        private String login;

        public GitHubUserData(String id, String name, String login) {
            this.id = id;
            this.name = name;
            this.login = login;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLogin() {
            return login;
        }

        @Override
        public String toString() {
            return "GitHubUserData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}