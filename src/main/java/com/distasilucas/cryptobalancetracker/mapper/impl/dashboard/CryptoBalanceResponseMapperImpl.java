package com.distasilucas.cryptobalancetracker.mapper.impl.dashboard;

import com.distasilucas.cryptobalancetracker.comparator.DescendingPercentageComparator;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCryptoInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoBalanceResponseMapperImpl implements EntityMapper<CryptoBalanceResponse, List<UserCrypto>> {

    private final CryptoService cryptoService;
    private final PlatformService platformService;

    @Override
    public CryptoBalanceResponse mapFrom(List<UserCrypto> input) {
        log.info("Mapping CryptoBalanceResponse for {} cryptos", input.size());
        List<CryptoResponse> cryptos = input.stream()
                .map(this::mapCryptoResponse)
                .collect(Collectors.toList());

        BigDecimal totalMoneyInUSD = getTotalMoneyInUSD(cryptos).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalMoneyInEUR = getTotalMoneyInEUR(cryptos).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalMoneyInBTC = getTotalMoneyInBTC(cryptos).setScale(10, RoundingMode.HALF_UP);
        cryptos.forEach(crypto -> setPercentage(totalMoneyInUSD, crypto));
        cryptos.sort(new DescendingPercentageComparator());

        return new CryptoBalanceResponse(totalMoneyInUSD, totalMoneyInEUR, totalMoneyInBTC, cryptos);
    }

    private CryptoResponse mapCryptoResponse(UserCrypto userCrypto) {
        Crypto crypto = cryptoService.findById(userCrypto.getCryptoId())
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));
        CoingeckoCryptoInfo coingeckoCryptoInfo = mapCoingeckoCryptoInfo().apply(crypto);

        return getCryptoResponse(userCrypto, coingeckoCryptoInfo);
    }

    private void setPercentage(BigDecimal totalMoney, CryptoResponse cryptoResponse) {
        BigDecimal percentage = cryptoResponse.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMoney, RoundingMode.HALF_UP);

        cryptoResponse.setPercentage(percentage);
    }

    private BigDecimal getTotalMoneyInUSD(List<CryptoResponse> cryptos) {
        return cryptos.stream()
                .map(CryptoResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    private BigDecimal getTotalMoneyInEUR(List<CryptoResponse> cryptos) {
        return cryptos.stream()
                .map(CryptoResponse::getBalanceInEUR)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    private BigDecimal getTotalMoneyInBTC(List<CryptoResponse> cryptos) {
        return cryptos.stream()
                .map(CryptoResponse::getBalanceInBTC)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    private CryptoResponse getCryptoResponse(UserCrypto userCrypto, CoingeckoCryptoInfo coingeckoCryptoInfo) {
        BigDecimal quantity = userCrypto.getQuantity();
        BigDecimal balanceInUSD = coingeckoCryptoInfo.getMarketData()
                .currentPrice()
                .usd()
                .multiply(quantity)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal balanceInEUR = coingeckoCryptoInfo.getMarketData()
                .currentPrice()
                .eur()
                .multiply(quantity)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal balanceInBTC = coingeckoCryptoInfo.getMarketData()
                .currentPrice()
                .btc()
                .multiply(quantity)
                .setScale(10, RoundingMode.HALF_UP);
        Platform platform = platformService.findById(userCrypto.getPlatformId())
                .orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND));

        return new CryptoResponse(userCrypto.getId(), coingeckoCryptoInfo, quantity, balanceInUSD, balanceInEUR, balanceInBTC, platform.getName());
    }

    private Function<Crypto, CoingeckoCryptoInfo> mapCoingeckoCryptoInfo() {
        return crypto -> {
            CurrentPrice currentPrice = new CurrentPrice(crypto.getLastKnownPrice(), crypto.getLastKnownPriceInEUR(), crypto.getLastKnownPriceInBTC());
            MarketData marketData = new MarketData(currentPrice, crypto.getCirculatingSupply(), crypto.getMaxSupply());

            CoingeckoCryptoInfo coingeckoCryptoInfo = new CoingeckoCryptoInfo();
            coingeckoCryptoInfo.setId(crypto.getId());
            coingeckoCryptoInfo.setSymbol(crypto.getTicker());
            coingeckoCryptoInfo.setName(crypto.getName());
            coingeckoCryptoInfo.setMarketData(marketData);

            return coingeckoCryptoInfo;
        };
    }
}
