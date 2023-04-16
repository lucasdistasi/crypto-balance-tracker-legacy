package com.distasilucas.cryptobalancetracker.model.request;

public record PlatformRequest(String name) {

    public String getName() {
        return name.toUpperCase();
    }
}
