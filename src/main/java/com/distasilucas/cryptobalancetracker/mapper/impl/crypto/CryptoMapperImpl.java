package com.distasilucas.cryptobalancetracker.mapper.impl.crypto;

import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.MAX_RATE_LIMIT_REACHED;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.UNKNOWN_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoMapperImpl implements EntityMapper<UserCrypto, AddCryptoRequest> {

    private final CoingeckoService coingeckoService;
    private final PlatformService platformService;

    @Override
    public UserCrypto mapFrom(AddCryptoRequest cryptoRequest) {
        try {
            log.info("Attempting to retrieve [{}] information from Coingecko or cache", cryptoRequest.getCryptoName());
            List<Coin> coins = coingeckoService.retrieveAllCoins();

            return getUserCrypto(cryptoRequest, coins);
        } catch (WebClientResponseException ex) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(ex.getStatusCode())) {
                log.warn("To many requests. Rate limit reached.");

                throw new ApiException(MAX_RATE_LIMIT_REACHED, ex.getStatusCode());
            }

            throw new ApiException(UNKNOWN_ERROR, ex);
        }
    }

    private UserCrypto getUserCrypto(AddCryptoRequest cryptoRequest, List<Coin> coins) {
        UserCrypto userCrypto = new UserCrypto();
        Platform platform = platformService.findPlatformByName(cryptoRequest.getPlatform());
        String cryptoName = cryptoRequest.getCryptoName();

        coins.stream()
                .filter(coin -> coin.getName().equalsIgnoreCase(cryptoName))
                .findFirst()
                .ifPresentOrElse(coin -> {
                            userCrypto.setCryptoId(coin.getId());
                            userCrypto.setQuantity(cryptoRequest.getQuantity());
                            userCrypto.setPlatformId(platform.getId());
                        }, () -> {
                            String message = String.format(CRYPTO_NAME_NOT_FOUND, cryptoName);

                            throw new CryptoNotFoundException(message);
                        }
                );

        return userCrypto;
    }
}
