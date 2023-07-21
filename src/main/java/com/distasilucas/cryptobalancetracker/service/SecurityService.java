package com.distasilucas.cryptobalancetracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final String securityEnabled;

    public SecurityService(@Value("${security.enabled}") String securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public Boolean isSecurityDisabled() {
        return Boolean.FALSE.toString().equalsIgnoreCase(securityEnabled);
    }
}
