package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinsResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService<Crypto, CryptoDTO> {

    private final CoingeckoService coingeckoService;
    private final CryptoRepository cryptoRepository;
    private final Validation<CryptoDTO> addCryptoValidation;

    @Override
    public Crypto addCrypto(CryptoDTO cryptoDTO) {
        addCryptoValidation.validate(cryptoDTO);

        Crypto crypto = new Crypto();
        List<Coin> coins = coingeckoService.retrieveAllCoins();
        coins.stream()
                .filter(coin -> coin.getName().equalsIgnoreCase(cryptoDTO.getName()))
                .findFirst()
                .ifPresentOrElse(coin -> {
                            crypto.setCoinId(coin.getId());
                            crypto.setName(coin.getName());
                            crypto.setTicker(coin.getSymbol());
                            crypto.setQuantity(cryptoDTO.getQuantity());
                        }, () -> {
                            String message = String.format(COIN_NAME_NOT_FOUND, cryptoDTO.getName());

                            throw new CoinNotFoundException(message);
                        }
                );

        cryptoRepository.save(crypto);
        log.info("Saved Crypto {}", crypto);

        return crypto;
    }

    @Override
    public CryptoBalanceResponse retrieveCoinsBalances() {
        log.info("Retrieving coins balances");
        List<Crypto> allCoins = cryptoRepository.findAll();

        return CollectionUtils.isEmpty(allCoins) ? null : getCryptoBalanceResponse(allCoins);
    }

    private CryptoBalanceResponse getCryptoBalanceResponse(List<Crypto> allCoins) {
        List<CoinsResponse> coins = allCoins.stream()
                .map(coin -> {
                    CoinInfo coinInfo = coingeckoService.retrieveCoinInfo(coin.getCoinId());
                    BigDecimal quantity = coin.getQuantity();
                    BigDecimal balance = coinInfo.getMarketData().getCurrentPrice().getUsd().multiply(quantity);

                    return new CoinsResponse(coinInfo, quantity, balance);
                })
                .toList();

        BigDecimal totalMoney = getTotalMoney(coins);
        coins.forEach(crypto -> setPercentage(totalMoney, crypto));
        BigDecimal totalBalance = totalMoney.setScale(2, RoundingMode.HALF_UP);

        return new CryptoBalanceResponse(totalBalance, coins);
    }

    private static void setPercentage(BigDecimal totalMoney, CoinsResponse coinsResponse) {
        double percentage = coinsResponse.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMoney, RoundingMode.HALF_UP)
                .doubleValue();

        coinsResponse.setPercentage(percentage);
    }

    private static BigDecimal getTotalMoney(List<CoinsResponse> coins) {
        return coins.stream()
                .map(CoinsResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }
}
