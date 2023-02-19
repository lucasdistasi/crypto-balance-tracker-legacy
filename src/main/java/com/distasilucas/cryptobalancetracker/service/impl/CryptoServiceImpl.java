package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService<CryptoDTO> {

    private final EntityMapper<Crypto, CryptoDTO> cryptoMapperImpl;
    private final EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImpl;
    private final EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImpl;
    private final CryptoRepository cryptoRepository;
    private final Validation<CryptoDTO> addCryptoValidation;

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
    public Optional<CryptoBalanceResponse> retrieveCoinsBalances() {
        log.info("Retrieving coins balances");
        List<Crypto> allCoins = cryptoRepository.findAll();

        return CollectionUtils.isEmpty(allCoins) ?
                Optional.empty() :
                Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(allCoins));
    }

    @Override
    public Optional<CryptoBalanceResponse> retrieveCoinBalance(String coinId) {
        log.info("Retrieving balances for coin {}", coinId);
        List<Crypto> allCoins = cryptoRepository.findAll()
                .stream()
                .filter(crypto -> crypto.getCoinId().equals(coinId))
                .toList();

        return CollectionUtils.isEmpty(allCoins) ?
                Optional.empty() :
                Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(allCoins));
    }
}
