package com.zetra.econsig.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SysConfig implements InitializingBean {
    private static SysConfig instance;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${server.servlet.session.time-to-live-hours}")
    private int sessionTimeToLiveHours;

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    public static SysConfig get() {
        return instance;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    public static boolean isTestProfile() {
        return "test".equals(get().getActiveProfile());
    }

    public int getSessionTimeToLiveHours() {
        return sessionTimeToLiveHours;
    }
}