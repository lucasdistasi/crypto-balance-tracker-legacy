package com.distasilucas.cryptobalancetracker.model.response.platform;

public record PlatformResponse(String name) {

    public String getName() {
        return name.toUpperCase();
    }
}
