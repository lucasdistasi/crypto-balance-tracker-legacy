package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.CryptoControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;
import com.distasilucas.cryptobalancetracker.service.UserCryptoService;
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
@PreAuthorize("@securityService.isSecurityDisabled() OR hasAuthority('ROLE_ADMIN')")
public class CryptoController implements CryptoControllerApi, ControllerHelper {

    private final UserCryptoService userCryptoService;
    private final TransferCryptoService transferCryptoService;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserCryptoResponse> getCrypto(@PathVariable String id) {
        UserCryptoResponse crypto = userCryptoService.getUserCryptoResponse(id);

        return ResponseEntity.ok(crypto);
    }

    @Override
    @GetMapping
    public ResponseEntity<Optional<PageCryptoResponse>> getCryptos(@RequestParam int page) {
        Optional<PageCryptoResponse> cryptos = userCryptoService.getCryptos(page);
        HttpStatus httpStatus = getOkOrNoContentHttpStatusCode(cryptos);

        return ResponseEntity.status(httpStatus)
                .body(cryptos);
    }

    @Override
    @PostMapping
    public ResponseEntity<UserCryptoResponse> addCrypto(@RequestBody AddCryptoRequest cryptoRequest) {
        UserCryptoResponse crypto = userCryptoService.saveUserCrypto(cryptoRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crypto);
    }

    @Override
    @PutMapping("/{cryptoId}")
    public ResponseEntity<UserCryptoResponse> updateCrypto(@RequestBody UpdateCryptoRequest updateCryptoRequest,
                                                           @PathVariable String cryptoId) {
        UserCryptoResponse updatedCrypto = userCryptoService.updateUserCrypto(updateCryptoRequest, cryptoId);

        return ResponseEntity.ok(updatedCrypto);
    }

    @Override
    @DeleteMapping("/{cryptoId}")
    public ResponseEntity<Void> deleteCrypto(@PathVariable String cryptoId) {
        userCryptoService.deleteUserCrypto(cryptoId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/transfer")
    public ResponseEntity<TransferCryptoResponse> transferCrypto(@RequestBody TransferCryptoRequest transferCryptoRequest) {
        TransferCryptoResponse response = transferCryptoService.transferCrypto(transferCryptoRequest);

        return ResponseEntity.ok(response);
    }
}
