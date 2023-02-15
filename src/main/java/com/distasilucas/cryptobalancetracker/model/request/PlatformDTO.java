package com.distasilucas.cryptobalancetracker.model.request;

public record PlatformDTO(String name) {

    public String getName() {
        return name.toUpperCase();
    }
}
