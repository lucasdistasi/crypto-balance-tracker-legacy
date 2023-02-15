package com.distasilucas.cryptobalancetracker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Platforms")
public class Platform {

    @Id
    @Column(name = "platform_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 24, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.MERGE)
    private List<Crypto> cryptos;

    public Platform(String name) {
        this.name = name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }
}
