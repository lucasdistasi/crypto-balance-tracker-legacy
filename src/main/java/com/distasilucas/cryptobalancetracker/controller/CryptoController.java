package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.CryptoControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${allowed.origins}")
@RequestMapping("/api/v1/cryptos")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CryptoController implements CryptoControllerApi, ControllerHelper {

    private final CryptoService cryptoService;

    @Override
    @GetMapping("/{coinId}")
    public ResponseEntity<CryptoResponse> getCoin(@PathVariable String coinId) {
        CryptoResponse coin = cryptoService.getCoin(coinId);

        return ResponseEntity.ok(coin);
    }

    @Override
    @GetMapping
    public ResponseEntity<Optional<List<CryptoResponse>>> getCoins() {
        Optional<List<CryptoResponse>> coins = cryptoService.getCoins();
        HttpStatus httpStatus = getOkOrNoContentHttpStatusCode(coins);

        return ResponseEntity.status(httpStatus)
                .body(coins);
    }

    @Override
    @PostMapping
    public ResponseEntity<CryptoResponse> addCoin(@RequestBody AddCryptoRequest cryptoRequest) {
        CryptoResponse crypto = cryptoService.addCoin(cryptoRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crypto);
    }

    @Override
    @PutMapping("/{coinId}")
    public ResponseEntity<CryptoResponse> updateCoin(@RequestBody UpdateCryptoRequest updateCryptoRequest,
                                                     @PathVariable String coinId) {
        CryptoResponse updatedCrypto = cryptoService.updateCoin(updateCryptoRequest, coinId);

        return ResponseEntity.ok(updatedCrypto);
    }

    @Override
    @DeleteMapping("/{coinId}")
    public ResponseEntity<Void> deleteCoin(@PathVariable String coinId) {
        cryptoService.deleteCoin(coinId);

        return ResponseEntity.noContent().build();
    }
}
