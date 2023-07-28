package com.distasilucas.cryptobalancetracker;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
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
        var coinInfo = getBitcoinCoinInfo();
        var coinResponse = getCryptoResponse(coinInfo);
        var cryptoBalanceResponse = new CryptoBalanceResponse(TOTAL_BALANCE_USD, TOTAL_BALANCE_EUR,
                TOTAL_BALANCE_BTC, Collections.singletonList(coinResponse));
        setPercentage(coinResponse);

        return cryptoBalanceResponse;
    }

    public static CryptoPlatformBalanceResponse getCryptoPlatformBalanceResponse() {
        var cryptoInfo = getCryptoInfoResponse();

        return new CryptoPlatformBalanceResponse(TOTAL_BALANCE_USD, Collections.singletonList(cryptoInfo));
    }

    public static CryptoInfoResponse getCryptoInfoResponse() {
        return new CryptoInfoResponse("bitcoin", BigDecimal.valueOf(0.15), BigDecimal.valueOf(1000),
                BigDecimal.valueOf(10), Set.of("Trezor", "Ledger"));
    }

    public static CryptoResponse getCryptoResponse(CoinInfo coinInfo) {
        return new CryptoResponse("ABC123", coinInfo, BigDecimal.valueOf(5),
                TOTAL_BALANCE_USD, TOTAL_BALANCE_EUR, TOTAL_BALANCE_BTC, "LEDGER");
    }

    public static CoinInfo getBitcoinCoinInfo() {
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
                        .id("bitcoin")
                        .build()
        );
    }

    public static BigDecimal getTotalMoney(List<CryptoResponse> cryptosResponse) {
        return cryptosResponse.stream()
                .map(CryptoResponse::getBalance)
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

    public static List<Coin> getAllCoins(String cryptoName, String cryptoId) {
        var coin = new Coin();
        coin.setId(cryptoId);
        coin.setSymbol("BTC");
        coin.setName(cryptoName);

        return Collections.singletonList(coin);
    }

    public static AddCryptoRequest getAddCryptoRequest() {
        return new AddCryptoRequest("Ethereum", BigDecimal.valueOf(1), "Ledger");
    }

    public static UserCryptoResponse getCryptoResponse() {
        return UserCryptoResponse.builder()
                .id("ABC123")
                .cryptoName("Ethereum")
                .quantity(BigDecimal.valueOf(1))
                .platform("Ledger")
                .build();
    }

    public static PageCryptoResponse getPageCryptoResponse() {
        return new PageCryptoResponse(1, 1, Collections.singletonList(getCryptoResponse()));
    }

    public static UserCrypto getUserCrypto(String platformId) {
        return UserCrypto.builder()
                .id("ABC1234")
                .cryptoId("bitcoin")
                .quantity(BigDecimal.valueOf(1))
                .platformId(platformId)
                .build();
    }

    public static UserCrypto getUserCrypto() {
        return UserCrypto.builder()
                .id("ABC1234")
                .cryptoId("bitcoin")
                .quantity(BigDecimal.valueOf(1))
                .platformId("1234")
                .build();
    }

    public static Platform getPlatform() {
        return Platform.builder()
                .id("1234")
                .name("BINANCE")
                .build();
    }

    public static Crypto getCrypto() {
        return Crypto.builder()
                .id("bitcoin")
                .ticker("BTC")
                .name("Bitcoin")
                .lastKnownPrice(BigDecimal.valueOf(30000))
                .lastKnownPriceInEUR(BigDecimal.valueOf(28000))
                .lastKnownPriceInBTC(BigDecimal.valueOf(1))
                .build();
    }

    public static GoalResponse getGoalResponse() {
        return new GoalResponse(
                "ABC123",
                "bitcoin",
                BigDecimal.valueOf(0.5),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(0.5),
                BigDecimal.ONE,
                BigDecimal.valueOf(10_000)
        );
    }

    public static Goal getGoal() {
        return new Goal(
                "ABC123",
                "bitcoin",
                BigDecimal.ONE
        );
    }

    private static void setPercentage(CryptoResponse cryptoResponse) {
        BigDecimal percentage = cryptoResponse.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(TOTAL_BALANCE_USD, RoundingMode.HALF_UP);

        cryptoResponse.setPercentage(percentage);
    }
}
