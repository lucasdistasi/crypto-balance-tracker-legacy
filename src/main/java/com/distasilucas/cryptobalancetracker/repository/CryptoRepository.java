package com.distasilucas.cryptobalancetracker.repository;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CryptoRepository extends MongoRepository<Crypto, String> {

    Page<Crypto> findAll(Pageable pageable);
    Optional<Crypto> findByNameAndPlatformId(String coinName, String platformId);
    Optional<List<Crypto>> findAllByPlatformId(String platformId);
    Optional<List<Crypto>> findAllByCoinId(String coinId);

    @Aggregation(pipeline = {
            "{ $match: { lastPriceUpdatedAt: { $lte: :#{#dateFilter} } } }",
            "{ $sort: { lastPriceUpdatedAt: 1 } }",
            "{ $limit: :#{#limit} }"
    })
    List<Crypto> findTopNCryptosOrderByLastPriceUpdatedAtAsc(LocalDateTime dateFilter, int limit);
}
