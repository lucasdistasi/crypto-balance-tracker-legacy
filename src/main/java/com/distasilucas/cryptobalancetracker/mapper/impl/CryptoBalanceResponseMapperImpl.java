package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.comparators.DescendingPercentageComparator;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.Constants.MAX_RATE_LIMIT_REACHED;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoBalanceResponseMapperImpl implements EntityMapper<CryptoBalanceResponse, List<Crypto>> {

    private final CoingeckoService coingeckoService;
    private final PlatformRepository platformRepository;

    @Override
    public CryptoBalanceResponse mapFrom(List<Crypto> input) {
        List<CoinResponse> coins = input.stream()
                .map(this::getCoinResponse)
                .collect(Collectors.toList());

        BigDecimal totalMoney = getTotalMoney(coins);
        coins.forEach(crypto -> setPercentage(totalMoney, crypto));
        coins.sort(new DescendingPercentageComparator());
        BigDecimal totalBalance = totalMoney.setScale(2, RoundingMode.HALF_UP);

        return new CryptoBalanceResponse(totalBalance, coins);
    }

    private CoinResponse getCoinResponse(Crypto coin) {
        try {
            log.info("Attempting to retrieve {} information from Coingecko or cache", coin.getName());

            CoinInfo coinInfo = coingeckoService.retrieveCoinInfo(coin.getCoinId());
            BigDecimal quantity = coin.getQuantity();
            BigDecimal balance = coinInfo.getMarketData().currentPrice().usd().multiply(quantity);
            Optional<Platform> platform = platformRepository.findById(coin.getPlatformId());
            String platformName = platform.isPresent() ? platform.get().getName() : "Unknown";

            return new CoinResponse(coinInfo, quantity, balance, platformName);
        } catch (WebClientResponseException ex) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(ex.getStatusCode())) {
                log.error("To many requests. Rate limit reached.");

                throw new ApiException(MAX_RATE_LIMIT_REACHED, ex.getStatusCode());
            }

            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    private void setPercentage(BigDecimal totalMoney, CoinResponse coinResponse) {
        double percentage = coinResponse.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMoney, RoundingMode.HALF_UP)
                .doubleValue();

        coinResponse.setPercentage(percentage);
    }

    private BigDecimal getTotalMoney(List<CoinResponse> coins) {
        return coins.stream()
                .map(CoinResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }
}
