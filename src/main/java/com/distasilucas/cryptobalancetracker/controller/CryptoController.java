package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.CryptoControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.TransferCryptoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${allowed.origins}")
@RequestMapping("/api/v1/cryptos")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CryptoController implements CryptoControllerApi, ControllerHelper {

    private final CryptoService cryptoService;
    private final TransferCryptoService transferCryptoService;

    @Override
    @GetMapping("/{coinId}")
    public ResponseEntity<CryptoResponse> getCoin(@PathVariable String coinId) {
        CryptoResponse coin = cryptoService.getCoin(coinId);

        return ResponseEntity.ok(coin);
    }

    @Override
    @GetMapping
    public ResponseEntity<Optional<PageCryptoResponse>> getCoins(@RequestParam int page) {
        Optional<PageCryptoResponse> coins = cryptoService.getCoins(page);
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

    @Override
    @PostMapping("/transfer")
    public ResponseEntity<TransferCryptoResponse> transferCrypto(@RequestBody TransferCryptoRequest transferCryptoRequest) {
        TransferCryptoResponse response = transferCryptoService.transferCrypto(transferCryptoRequest);

        return ResponseEntity.ok(response);
    }
}
