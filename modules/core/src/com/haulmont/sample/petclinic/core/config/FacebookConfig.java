package com.haulmont.sample.petclinic.core.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

/**
 * Facebook Login Configuration Interface
 */
@Source(type = SourceType.APP)
public interface FacebookConfig extends Config {

    /**
     * Defines fields that should be fetched while loading user info.
     */
    @Default("id,name,email")
    @Property("facebook.fields")
    String getFacebookFields();

    /**
     * Defines App ID.
     */
    @Property("facebook.appId")
    String getFacebookAppId();

    /**
     * Defines App Secret.
     */
    @Property("facebook.appSecret")
    String getFacebookAppSecret();
}