package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                            String message = String.format("Coin not found with name %s", cryptoDTO.getName());

                            throw new CoinNotFoundException(message);
                        }
                );

        cryptoRepository.save(crypto);
        log.info("Saved Crypto {}", crypto);

        return crypto;
    }

    @Override
    public List<CryptoBalanceResponse> retrieveCoinsBalances() {
        log.info("Retrieving coins balances");
        List<Crypto> allCoins = cryptoRepository.findAll();

        return CollectionUtils.isEmpty(allCoins) ?
                Collections.emptyList() :
                getCryptoBalanceResponse(allCoins);
    }

    private List<CryptoBalanceResponse> getCryptoBalanceResponse(List<Crypto> allCoins) {
        List<CryptoBalanceResponse> cryptoBalanceResponse = allCoins.stream()
                .map(coin -> {
                    CoinInfo coinInfo = coingeckoService.retrieveCoinInfo(coin.getCoinId());
                    BigDecimal quantity = coin.getQuantity();
                    BigDecimal balance = coinInfo.getMarketData().getCurrentPrice().getUsd().multiply(quantity);

                    return new CryptoBalanceResponse(coinInfo, quantity, balance);
                })
                .collect(Collectors.toList());

        BigDecimal totalMoney = getTotalMoney(cryptoBalanceResponse);
        cryptoBalanceResponse.forEach(crypto -> setPercentage(totalMoney, crypto));

        return cryptoBalanceResponse;
    }

    private static void setPercentage(BigDecimal totalMoney, CryptoBalanceResponse crypto) {
        double percentage = crypto.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMoney, RoundingMode.HALF_UP)
                .doubleValue();

        crypto.setPercentage(percentage);
    }

    private static BigDecimal getTotalMoney(List<CryptoBalanceResponse> cryptoBalanceResponse) {
        return cryptoBalanceResponse.stream()
                .map(CryptoBalanceResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }
}
