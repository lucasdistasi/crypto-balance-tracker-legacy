package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.Constants.MAX_RATE_LIMIT_REACHED;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoMapperImpl implements EntityMapper<Crypto, CryptoDTO> {

    private final CoingeckoService coingeckoService;
    private final PlatformService platformService;

    @Override
    public Crypto mapFrom(CryptoDTO cryptoDTO) {
        try {
            log.info("Attempting to retrieve [{}] information from Coingecko or cache", cryptoDTO.coin_name());
            List<Coin> coins = coingeckoService.retrieveAllCoins();

            return getCrypto(cryptoDTO, coins);
        } catch (WebClientResponseException ex) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(ex.getStatusCode())) {
                log.warn("To many requests. Rate limit reached.");

                throw new ApiException(MAX_RATE_LIMIT_REACHED, ex.getStatusCode());
            }

            throw new ApiException(UNKNOWN_ERROR, ex);
        }
    }

    private Crypto getCrypto(CryptoDTO cryptoDTO, List<Coin> coins) {
        Crypto crypto = new Crypto();
        Platform platform = platformService.findPlatformByName(cryptoDTO.platform());
        String coinName = cryptoDTO.coin_name();

        coins.stream()
                .filter(coin -> coin.getName().equalsIgnoreCase(coinName))
                .findFirst()
                .ifPresentOrElse(coin -> {
                            CoinInfo coinInfo = getCoinInfo(coin.getId());
                            MarketData marketData = coinInfo.getMarketData();

                            crypto.setCoinId(coin.getId());
                            crypto.setName(coin.getName());
                            crypto.setTicker(coin.getSymbol());
                            crypto.setQuantity(cryptoDTO.quantity());
                            crypto.setPlatformId(platform.getId());
                            crypto.setLastPriceUpdatedAt(LocalDateTime.now());
                            crypto.setLastKnownPrice(marketData.currentPrice().usd());
                            crypto.setTotalSupply(marketData.totalSupply());
                            crypto.setMaxSupply(marketData.maxSupply());
                        }, () -> {
                            String message = String.format(COIN_NAME_NOT_FOUND, coinName);

                            throw new CoinNotFoundException(message);
                        }
                );

        return crypto;
    }

    private CoinInfo getCoinInfo(String coinId) {
        try {
            log.info("Attempting to retrieve information for [{}] from Coingecko or cache", coinId);

            return coingeckoService.retrieveCoinInfo(coinId);
        } catch (WebClientResponseException ex) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(ex.getStatusCode())) {
                log.warn("To many requests. Rate limit reached.");

                throw new ApiException(MAX_RATE_LIMIT_REACHED, ex.getStatusCode());
            }

            throw new ApiException(UNKNOWN_ERROR, ex);
        }
    }
}
