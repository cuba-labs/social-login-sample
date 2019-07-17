package com.haulmont.sample.petclinic.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.APP)
public interface VkConfig extends Config {

    @Default("id,name,screen_name")
    @Property("vk.fields")
    String getVkFields();

    @Property("vk.appId")
    String getVkAppId();

    @Property("vk.appSecret")
    String getVkAppSecret();
}