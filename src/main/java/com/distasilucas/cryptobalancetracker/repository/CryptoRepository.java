package com.distasilucas.cryptobalancetracker.repository;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CryptoRepository extends JpaRepository<Crypto, String> {

    Optional<Crypto> findByName(String coinName);
}
