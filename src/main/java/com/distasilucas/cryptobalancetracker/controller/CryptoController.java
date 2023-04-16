package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.CryptoControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/cryptos")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CryptoController implements CryptoControllerApi, ControllerHelper {

    private final CryptoService cryptoService;

    @GetMapping("/{coinId}")
    public ResponseEntity<Optional<CryptoDTO>> getCoin(@PathVariable String coinId) {
        Optional<CryptoDTO> coin = cryptoService.getCoin(coinId);
        HttpStatus httpStatus = coin.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return ResponseEntity.status(httpStatus)
                .body(coin);
    }

    @GetMapping
    public ResponseEntity<Optional<List<CryptoDTO>>> getCoins() {
        Optional<List<CryptoDTO>> coins = cryptoService.getCoins();
        HttpStatus httpStatus = getHttpStatusCode(coins);

        return ResponseEntity.status(httpStatus)
                .body(coins);
    }

    @Override
    @PostMapping
    public ResponseEntity<CryptoDTO> addCoin(@RequestBody CryptoDTO cryptoDTO) {
        CryptoDTO crypto = cryptoService.addCoin(cryptoDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crypto);
    }

    @Override
    @PutMapping("/{coinId}")
    public ResponseEntity<CryptoDTO> updateCoin(@RequestBody CryptoDTO cryptoDTO,
                                                @PathVariable String coinId) {
        CryptoDTO updatedCrypto = cryptoService.updateCoin(cryptoDTO, coinId);

        return ResponseEntity.ok(updatedCrypto);
    }

    @Override
    @DeleteMapping("/{coinId}")
    public ResponseEntity<Void> deleteCoin(@PathVariable String coinId) {
        cryptoService.deleteCoin(coinId);

        return ResponseEntity.noContent().build();
    }
}
