package com.haulmont.sample.petclinic.service;

import java.io.Serializable;

public interface VkService {

    String NAME = "petclinic_VkService";

    String getLoginUrl(String appUrl, VkService.OAuth2ResponseType responseType);

    VkUserData getUserData(String appUrl, String code);

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

    class VkUserData implements Serializable {

        private String id;
        private String name;
        private String screenName;

        public VkUserData(String id, String name, String screenName) {
            this.id = id;
            this.name = name;
            this.screenName = screenName;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getScreenName() {
            return screenName;
        }

        @Override
        public String toString() {
            return "VkUserData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", screenName='" + screenName + '\'' +
                    '}';
        }
    }
}