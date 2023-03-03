package com.distasilucas.cryptobalancetracker.configuration;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;
import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COINGECKO_CRYPTOS_CACHE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.CRYPTO_PRICE_CACHE;
import static org.springframework.data.util.CastUtils.cast;

@Configuration
public class EhCacheConfig {

    @Bean
    public CacheManager ehcacheManager() {
        CacheConfiguration<SimpleKey, List<CoinInfo>> coingeckoCryptosCache = getCoingeckoCryptosCache();
        CacheConfiguration<String, CoinInfo> cryptoPriceCache = getCryptoPriceCache();

        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        cacheManager.createCache(COINGECKO_CRYPTOS_CACHE, getCoingeckoCryptosCacheConfiguration(coingeckoCryptosCache));
        cacheManager.createCache(CRYPTO_PRICE_CACHE, getCryptoPriceCacheConfiguration(cryptoPriceCache));

        return cacheManager;
    }

    private static CacheConfiguration<SimpleKey, List<CoinInfo>> getCoingeckoCryptosCache() {
        Class<List<CoinInfo>> coinListClass = cast(List.class);

        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(SimpleKey.class, coinListClass,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .offheap(2, MemoryUnit.MB)
                                .build())
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(30)))
                .build();
    }

    private static CacheConfiguration<String, CoinInfo> getCryptoPriceCache() {
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, CoinInfo.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .offheap(2, MemoryUnit.MB)
                                .build())
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
                .build();
    }

    private static javax.cache.configuration.Configuration<String, CoinInfo> getCryptoPriceCacheConfiguration(
            CacheConfiguration<String, CoinInfo> cryptoPriceCache) {
        return Eh107Configuration.fromEhcacheCacheConfiguration(cryptoPriceCache);
    }

    private static javax.cache.configuration.Configuration<SimpleKey, List<CoinInfo>> getCoingeckoCryptosCacheConfiguration(
            CacheConfiguration<SimpleKey, List<CoinInfo>> coingeckoCryptosCache) {
        return Eh107Configuration.fromEhcacheCacheConfiguration(coingeckoCryptosCache);
    }

}
