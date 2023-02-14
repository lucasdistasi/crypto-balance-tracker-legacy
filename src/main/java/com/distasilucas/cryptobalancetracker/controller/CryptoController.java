package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.swagger.CryptoControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crypto")
public class CryptoController implements CryptoControllerApi {

    private final CryptoService<CryptoDTO> cryptoService;

    @Override
    @PostMapping
    public ResponseEntity<CryptoDTO> addCoin(@RequestBody CryptoDTO cryptoDTO) {
        CryptoDTO crypto = cryptoService.addCoin(cryptoDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crypto);
    }

    @Override
    @GetMapping
    public ResponseEntity<CryptoBalanceResponse> retrieveCoinsBalance() {
        CryptoBalanceResponse cryptoBalanceResponse = cryptoService.retrieveCoinsBalances();
        HttpStatus httpStatus = cryptoBalanceResponse != null ? HttpStatus.OK : HttpStatus.NO_CONTENT;

        return ResponseEntity.status(httpStatus)
                .body(cryptoBalanceResponse);
    }

    @Override
    @PutMapping("/{coinName}")
    public ResponseEntity<CryptoDTO> updateCrypto(@RequestBody CryptoDTO cryptoDTO, @PathVariable String coinName) {
        CryptoDTO updatedCrypto = cryptoService.updateCoin(cryptoDTO, coinName);

        return ResponseEntity.ok(updatedCrypto);
    }

    @Override
    @DeleteMapping("/{coinName}")
    public ResponseEntity<Void> deleteCoin(@PathVariable String coinName) {
        cryptoService.deleteCoin(coinName);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
