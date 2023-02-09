package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.swagger.CryptoControllerApi;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crypto")
public class CryptoController implements CryptoControllerApi {

    private final CryptoService<Crypto, CryptoDTO> cryptoService;

    @Override
    @PostMapping
    public ResponseEntity<Crypto> addCrypto(@RequestBody CryptoDTO cryptoDTO) {
        Crypto crypto = cryptoService.addCrypto(cryptoDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crypto);
    }

    @Override
    @GetMapping
    public ResponseEntity<CryptoBalanceResponse> retrieveCoinsBalance() {
        return ResponseEntity.ok(cryptoService.retrieveCoinsBalances());
    }
}
