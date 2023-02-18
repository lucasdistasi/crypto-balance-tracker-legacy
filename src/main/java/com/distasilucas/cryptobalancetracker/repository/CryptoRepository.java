package com.distasilucas.cryptobalancetracker.repository;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CryptoRepository extends MongoRepository<Crypto, String> {

    Optional<Crypto> findByName(String coinName);
    Optional<Crypto> findByNameAndPlatformId(String coinName, String platformId);

    Optional<List<Crypto>> findAllByPlatformId(String platformId);
}
