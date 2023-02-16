package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CryptoMapperImpl implements EntityMapper<Crypto, CryptoDTO> {

    private final CoingeckoService coingeckoService;
    private final PlatformService platformService;

    @Override
    public Crypto mapFrom(CryptoDTO input) {
        Crypto crypto = new Crypto();
        Platform platform = platformService.findPlatform(input.platform());
        List<Coin> coins = coingeckoService.retrieveAllCoins();
        String coinName = input.coin_name();

        coins.stream()
                .filter(coin -> coin.getName().equalsIgnoreCase(coinName))
                .findFirst()
                .ifPresentOrElse(coin -> {
                            crypto.setCoinId(coin.getId());
                            crypto.setName(coin.getName());
                            crypto.setTicker(coin.getSymbol());
                            crypto.setQuantity(input.quantity());
                            crypto.setPlatform(platform);
                        }, () -> {
                            String message = String.format(COIN_NAME_NOT_FOUND, coinName);

                            throw new CoinNotFoundException(message);
                        }
                );

        return crypto;
    }
}
