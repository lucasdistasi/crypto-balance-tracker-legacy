package com.distasilucas.cryptobalancetracker;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MockData {

    private static final BigDecimal TOTAL_BALANCE_USD = BigDecimal.valueOf(1000);
    private static final BigDecimal TOTAL_BALANCE_EUR = BigDecimal.valueOf(800);
    private static final BigDecimal TOTAL_BALANCE_BTC = BigDecimal.valueOf(0.01);

    public MockData() {
        throw new IllegalArgumentException();
    }

    public static CryptoBalanceResponse getCryptoBalanceResponse() {
        var coinInfo = getCoinInfo();
        var coinResponse = getCoinResponse(coinInfo);
        var cryptoBalanceResponse = new CryptoBalanceResponse(TOTAL_BALANCE_USD, TOTAL_BALANCE_EUR,
                TOTAL_BALANCE_BTC, Collections.singletonList(coinResponse));
        setPercentage(coinResponse);

        return cryptoBalanceResponse;
    }

    public static CryptoPlatformBalanceResponse getCryptoPlatformBalanceResponse() {
        var coinInfo = getCoinInfoResponse();

        return new CryptoPlatformBalanceResponse(TOTAL_BALANCE_USD, Collections.singletonList(coinInfo));
    }

    public static CoinInfoResponse getCoinInfoResponse() {
        return new CoinInfoResponse("bitcoin", BigDecimal.valueOf(0.15), BigDecimal.valueOf(1000),
                10, Set.of("Trezor", "Ledger"));
    }

    public static CoinResponse getCoinResponse(CoinInfo coinInfo) {
        return new CoinResponse("ABC123", coinInfo, BigDecimal.valueOf(5),
                TOTAL_BALANCE_USD, TOTAL_BALANCE_EUR, TOTAL_BALANCE_BTC, "LEDGER");
    }

    public static CoinInfo getCoinInfo() {
        var currentPrice = new CurrentPrice(BigDecimal.valueOf(150_000), BigDecimal.valueOf(170_000), BigDecimal.valueOf(1));
        var marketData = new MarketData(currentPrice, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        var coinInfo = new CoinInfo();
        coinInfo.setMarketData(marketData);
        coinInfo.setSymbol("btc");
        coinInfo.setName("Bitcoin");
        coinInfo.setId("bitcoin");

        return coinInfo;
    }

    public static List<Crypto> getAllCryptos() {
        return Collections.singletonList(
                Crypto.builder()
                        .ticker("btc")
                        .name("Bitcoin")
                        .coinId("bitcoin")
                        .quantity(BigDecimal.valueOf(1.15))
                        .platformId("1234")
                        .build()
        );
    }

    public static BigDecimal getTotalMoney(List<CoinResponse> coinsResponse) {
        return coinsResponse.stream()
                .map(CoinResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    public static PlatformRequest getPlatformRequest(String platformName) {
        return new PlatformRequest(platformName);
    }

    public static PlatformResponse getPlatformResponse(String platformName) {
        return new PlatformResponse(platformName);
    }

    public static Platform getPlatform(String platformName) {
        return Platform.builder()
                .id("1234")
                .name(platformName)
                .build();
    }

    public static List<Coin> getAllCoins() {
        var coin = new Coin();
        coin.setId("ethereum");
        coin.setSymbol("ETH");
        coin.setName("Ethereum");

        return Collections.singletonList(coin);
    }

    public static CryptoRequest getCryptoRequest() {
        return new CryptoRequest("Ethereum", BigDecimal.valueOf(1), "Ledger");
    }

    public static CryptoResponse getCryptoResponse() {
        return CryptoResponse.builder()
                .coinId("ABC123")
                .coinName("Ethereum")
                .quantity(BigDecimal.valueOf(1))
                .platform("Ledger")
                .build();
    }

    public static Crypto getCrypto(String platformId) {
        return Crypto.builder()
                .id("id")
                .name("Bitcoin")
                .ticker("BTC")
                .platformId(platformId)
                .quantity(BigDecimal.valueOf(1))
                .coinId("bitcoin")
                .lastKnownPrice(BigDecimal.valueOf(22000))
                .lastKnownPriceInEUR(BigDecimal.valueOf(24000))
                .lastKnownPriceInBTC(BigDecimal.ONE)
                .build();
    }

    private static void setPercentage(CoinResponse coinResponse) {
        double percentage = coinResponse.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(TOTAL_BALANCE_USD, RoundingMode.HALF_UP)
                .doubleValue();

        coinResponse.setPercentage(percentage);
    }
}
