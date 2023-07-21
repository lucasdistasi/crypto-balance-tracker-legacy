package com.distasilucas.cryptobalancetracker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityServiceTest {

    @Test
    void shouldReturnTrueIfSecurityIsDisabled() {
        SecurityService securityService = new SecurityService("false");

        assertTrue(securityService.isSecurityDisabled());
    }

    @Test
    void shouldReturnFalseIfSecurityIsEnabled() {
        SecurityService securityService = new SecurityService("true");

        assertFalse(securityService.isSecurityDisabled());
    }

}