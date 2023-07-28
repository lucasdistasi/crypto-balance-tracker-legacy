package com.distasilucas.cryptobalancetracker.mapper.impl.scheduler;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCryptoSchedulerMapperImpl implements EntityMapper<Crypto, Crypto> {
    
    private final Clock clock;
    private final CoingeckoService coingeckoService;
    
    @Override
    public Crypto mapFrom(Crypto input) {
        log.info("Updating information for {}", input.getName());
        String id = input.getId();

        try {
            CoinInfo coinInfo = coingeckoService.retrieveCoinInfo(id);
            MarketData marketData = coinInfo.getMarketData();
            BigDecimal currentUSDPrice = marketData.currentPrice().usd();
            BigDecimal currentEURPrice = marketData.currentPrice().eur();
            BigDecimal currentBTCPrice = marketData.currentPrice().btc();
            BigDecimal maxSupply = marketData.maxSupply();
            BigDecimal circulatingSupply = marketData.circulatingSupply();

            Crypto cryptoToUpdate = new Crypto();
            cryptoToUpdate.setId(input.getId());
            cryptoToUpdate.setName(input.getName());
            cryptoToUpdate.setTicker(input.getTicker());
            cryptoToUpdate.setLastKnownPrice(currentUSDPrice);
            cryptoToUpdate.setLastKnownPriceInEUR(currentEURPrice);
            cryptoToUpdate.setLastKnownPriceInBTC(currentBTCPrice);
            cryptoToUpdate.setCirculatingSupply(circulatingSupply);
            cryptoToUpdate.setMaxSupply(maxSupply);
            cryptoToUpdate.setLastPriceUpdatedAt(LocalDateTime.now(clock));

            log.info("Updated information for {}", input.getName());

            return cryptoToUpdate;
        } catch (WebClientResponseException ex) {
            log.warn("A WebClientResponseException occurred and [{}] price could not be updated {}", id, ex);
            return input;
        } catch (Exception ex) {
            log.warn("An uncaught exception occurred and [{}] price could not be updated {}", id, ex);
            return input;
        }
    }
}
