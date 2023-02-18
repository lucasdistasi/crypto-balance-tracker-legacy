package com.distasilucas.cryptobalancetracker;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class MockData {

    private static final BigDecimal TOTAL_BALANCE = BigDecimal.valueOf(1000);

    public MockData() {
        throw new IllegalArgumentException();
    }

    public static CryptoBalanceResponse getCryptoBalanceResponse() {
        var coinInfo = getCoinInfo();
        var coinResponse = getCoinResponse(coinInfo);
        var cryptoBalanceResponse = new CryptoBalanceResponse();
        cryptoBalanceResponse.setTotalBalance(TOTAL_BALANCE);
        cryptoBalanceResponse.setCoins(Collections.singletonList(coinResponse));
        setPercentage(coinResponse);

        return cryptoBalanceResponse;
    }

    public static CoinResponse getCoinResponse(CoinInfo coinInfo) {
        return new CoinResponse(coinInfo, BigDecimal.valueOf(5), TOTAL_BALANCE, "LEDGER");
    }

    public static CoinInfo getCoinInfo() {
        var currentPrice = new CurrentPrice(BigDecimal.valueOf(150_000));
        var marketData = new MarketData(currentPrice);
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

    public static PlatformDTO getPlatformDTO(String platformName) {
        return new PlatformDTO(platformName);
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

    public static CryptoDTO getCryptoDTO() {
        return CryptoDTO.builder()
                .coin_name("Ethereum")
                .coinId("ethereum")
                .ticker("ETH")
                .platform("Ledger")
                .quantity(BigDecimal.valueOf(1))
                .build();
    }

    public static Crypto getCrypto(String platformId) {
        return Crypto.builder()
                .name("Bitcoin")
                .ticker("BTC")
                .platformId(platformId)
                .quantity(BigDecimal.valueOf(1))
                .coinId("bitcoin")
                .build();
    }

    private static void setPercentage(CoinResponse coinResponse) {
        double percentage = coinResponse.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(TOTAL_BALANCE, RoundingMode.HALF_UP)
                .doubleValue();

        coinResponse.setPercentage(percentage);
    }
}
