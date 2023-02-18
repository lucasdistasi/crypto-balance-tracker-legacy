package com.distasilucas.cryptobalancetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document("Platforms")
@NoArgsConstructor
@AllArgsConstructor
public class Platform {

    @Id
    private String id;
    private String name;

    public Platform(String name) {
        this.name = name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }
}
