package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService<CryptoDTO> {

    private final EntityMapper<Crypto, CryptoDTO> cryptoMapperImpl;
    private final EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImpl;
    private final EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImpl;
    private final CryptoRepository cryptoRepository;
    private final Validation<CryptoDTO> addCryptoValidation;
    private final Validation<CryptoDTO> updateCryptoValidation;

    @Override
    public CryptoDTO addCoin(CryptoDTO cryptoDTO) {
        addCryptoValidation.validate(cryptoDTO);
        Crypto crypto = cryptoMapperImpl.mapFrom(cryptoDTO);
        cryptoRepository.save(crypto);

        CryptoDTO cryptoResponse = cryptoDTOMapperImpl.mapFrom(crypto);
        log.info("Saved Crypto {}", cryptoResponse);

        return cryptoResponse;
    }

    @Override
    public CryptoBalanceResponse retrieveCoinsBalances() {
        log.info("Retrieving coins balances");
        List<Crypto> allCoins = cryptoRepository.findAll();

        return CollectionUtils.isEmpty(allCoins) ?
                null :
                cryptoBalanceResponseMapperImpl.mapFrom(allCoins);
    }

    @Override
    public CryptoDTO updateCoin(CryptoDTO cryptoDTO, String coinName) {
        updateCryptoValidation.validate(cryptoDTO);
        Optional<Crypto> cryptoOptional = cryptoRepository.findByName(coinName);

        if (cryptoOptional.isEmpty()) {
            String message = String.format(COIN_NAME_NOT_FOUND, coinName);

            throw new CoinNotFoundException(message);
        }

        Crypto crypto = cryptoOptional.get();
        crypto.setQuantity(cryptoDTO.quantity());
        cryptoRepository.save(crypto);

        CryptoDTO cryptoResponse = cryptoDTOMapperImpl.mapFrom(crypto);
        log.info("Updated coin {} to {}", cryptoDTO, cryptoResponse);

        return cryptoResponse;
    }

    @Override
    public void deleteCoin(String coinName) {
        cryptoRepository.findByName(coinName)
                .ifPresentOrElse(cryptoRepository::delete, () -> {
                    String message = String.format(COIN_NAME_NOT_FOUND, coinName);

                    throw new CoinNotFoundException(message);
                });

        log.info("Deleted coin: {}", coinName);
    }
}
