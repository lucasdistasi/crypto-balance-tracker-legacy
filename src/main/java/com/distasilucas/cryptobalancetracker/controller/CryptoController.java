package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.CryptoControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crypto")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CryptoController implements CryptoControllerApi, ControllerHelper {

    private final CryptoService<CryptoDTO> cryptoService;

    @Override
    @PostMapping
    public ResponseEntity<CryptoDTO> addCoin(@RequestBody CryptoDTO cryptoDTO) {
        CryptoDTO crypto = cryptoService.addCoin(cryptoDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crypto);
    }

    @Override
    @GetMapping("/balances")
    public ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinsBalance() {
        Optional<CryptoBalanceResponse> response = cryptoService.retrieveCoinsBalances();
        HttpStatus httpStatus = getHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }

    @Override
    @GetMapping("/balances/platforms")
    public ResponseEntity<Optional<CryptoPlatformBalanceResponse>> retrieveCoinsBalanceByPlatform() {
        Optional<CryptoPlatformBalanceResponse> response = cryptoService.retrieveCoinsBalanceByPlatform();
        HttpStatus httpStatus = getHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }

    @Override
    @GetMapping("/{coinId}")
    public ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinBalance(@PathVariable String coinId) {
        Optional<CryptoBalanceResponse> response = cryptoService.retrieveCoinBalance(coinId);
        HttpStatus httpStatus = getHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }
}
