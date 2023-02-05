package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.model.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crypto")
public class CryptoController {

    private final CryptoService<Crypto, CryptoDTO> cryptoService;

    @PostMapping
    public ResponseEntity<Crypto> addCrypto(@RequestBody CryptoDTO cryptoDTO) {
        Crypto crypto = cryptoService.add(cryptoDTO);

        return ResponseEntity.ok(crypto);
    }
}
