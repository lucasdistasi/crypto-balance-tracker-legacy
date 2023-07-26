package com.distasilucas.cryptobalancetracker.repository;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CryptoRepository extends MongoRepository<Crypto, String> {

    @Aggregation(pipeline = {
            "{ $match: { lastPriceUpdatedAt: { $lte: :#{#dateFilter} } } }",
            "{ $group: { _id: '$coinId', doc: { $first: '$$ROOT' } } }",
            "{ $replaceWith: '$doc' }",
            "{ $sort: { lastPriceUpdatedAt: 1 } }",
            "{ $limit: :#{#limit} }"
    })
    List<Crypto> findTopNCryptosOrderByLastPriceUpdatedAtAsc(LocalDateTime dateFilter, int limit);

}
