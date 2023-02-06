package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.model.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<Coin> coins = coingeckoService.retrieveAllCryptos();
        coins.stream()
                .filter(coin -> coin.symbol().equalsIgnoreCase(cryptoDTO.getTicker()))
                .findFirst()
                .ifPresentOrElse(coin -> {
                            crypto.setCoinId(coin.id());
                            crypto.setName(coin.name());
                            crypto.setTicker(coin.symbol());
                            crypto.setQuantity(cryptoDTO.getQuantity());
                        }, () -> {
                            String message = String.format("Coin not found for ticker %s", cryptoDTO.getTicker());

                            throw new CoinNotFoundException(message);
                        }
                );

        cryptoRepository.save(crypto);
        log.info("Saved Crypto {}", crypto);

        return crypto;
    }
}
