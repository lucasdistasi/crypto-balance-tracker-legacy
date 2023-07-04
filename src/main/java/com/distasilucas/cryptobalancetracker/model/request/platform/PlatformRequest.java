package com.distasilucas.cryptobalancetracker.model.request.platform;

public record PlatformRequest(String name) {

    public String getName() {
        return name.toUpperCase();
    }
}
