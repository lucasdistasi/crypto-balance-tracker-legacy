package com.distasilucas.cryptobalancetracker.schedulers;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateCryptoPriceScheduler {

    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;
    private final CoingeckoService coingeckoService;

    @Scheduled(cron = "0 */3 * ? * *")
    public void updateCryptoLastKnownPrice() {
        List<Crypto> cryptos = cryptoRepository.findTopNCryptosOrderByLastPriceUpdatedAtAsc(5);

        cryptos.forEach(crypto -> {
            String coinId = crypto.getCoinId();
            String platformName = getPlatformName(crypto.getPlatformId());
            log.info("Running cron to update last known price for [{}] in [{}]", coinId, platformName);

            try {
                CoinInfo coinInfo = coingeckoService.retrieveCoinInfo(coinId);
                MarketData marketData = coinInfo.getMarketData();
                BigDecimal currentPrice = marketData.currentPrice().usd();
                BigDecimal maxSupply = marketData.maxSupply();
                BigDecimal totalSupply = marketData.totalSupply();

                if (!currentPrice.equals(crypto.getLastKnownPrice())) {
                    crypto.setLastKnownPrice(currentPrice);

                    log.info("[{}] is now the last known price for [{}]", currentPrice, coinId);
                } else {
                    log.info("Price for [{}] remains the same or it was retrieved from cache, therefore has not changed", coinId);
                }

                crypto.setMaxSupply(maxSupply);
                crypto.setTotalSupply(totalSupply);
                crypto.setLastPriceUpdatedAt(LocalDateTime.now());
                cryptoRepository.save(crypto);
            } catch (WebClientResponseException ex) {
                log.warn("A WebClientResponseException occurred and [{}] price could not be updated {}", crypto.getCoinId(), ex.getMessage());
            } catch (Exception ex) {
                log.warn("An uncaught exception occurred and [{}] price could not be updated {}", crypto.getCoinId(), ex.getMessage());
            }
        });
    }

    private String getPlatformName(String platformId) {
        Platform platform = platformRepository.findById(platformId).orElse(new Platform(UNKNOWN));

        return platform.getName();
    }
}
