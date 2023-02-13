package com.distasilucas.cryptobalancetracker.model.request;

import lombok.Data;

@Data
public class PlatformDTO {

    private String name;

    public String getName() {
        return name.toUpperCase();
    }
}
