package com.distasilucas.cryptobalancetracker.repository;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CryptoRepository extends JpaRepository<Crypto, String> {

    Optional<Crypto> findByName(String coinName);

    Optional<List<Crypto>> findAllByPlatform(Platform platform);
}
