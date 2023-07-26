package com.distasilucas.cryptobalancetracker.repository;

import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserCryptoRepository extends MongoRepository<UserCrypto, String> {

    Page<UserCrypto> findAll(Pageable pageable);
    Optional<UserCrypto> findByCryptoIdAndPlatformId(String cryptoId, String platformId);
    Optional<List<UserCrypto>> findAllByPlatformId(String platformId);
    Optional<List<UserCrypto>> findAllByCryptoId(String cryptoId);
    Optional<UserCrypto> findFirstByCryptoId(String cryptoId);
    Optional<UserCrypto> findByIdAndPlatformId(String id, String platformId);
}
