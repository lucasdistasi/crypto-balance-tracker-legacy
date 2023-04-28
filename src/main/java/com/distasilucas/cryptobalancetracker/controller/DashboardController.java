package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.DashboardControllerApi;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/v1/dashboards")
public class DashboardController implements DashboardControllerApi, ControllerHelper {

    private final DashboardService dashboardService;

    @Override
    @GetMapping("/crypto/balances")
    public ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinsBalance() {
        Optional<CryptoBalanceResponse> response = dashboardService.retrieveCoinsBalances();
        HttpStatus httpStatus = getOkOrNoContentHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }

    @Override
    @GetMapping("/crypto/{coinId}")
    public ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinBalance(@PathVariable String coinId) {
        Optional<CryptoBalanceResponse> response = dashboardService.retrieveCoinBalance(coinId);
        HttpStatus httpStatus = getOkOrNoContentHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }

    @Override
    @GetMapping("/crypto/balances/platforms")
    public ResponseEntity<Optional<CryptoPlatformBalanceResponse>> retrieveCoinsBalanceByPlatform() {
        Optional<CryptoPlatformBalanceResponse> response = dashboardService.retrieveCoinsBalanceByPlatform();
        HttpStatus httpStatus = getOkOrNoContentHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }

    @Override
    @GetMapping("/platform/{platformName}/coins")
    public ResponseEntity<Optional<CryptoBalanceResponse>> getCoins(@PathVariable String platformName) {
        Optional<CryptoBalanceResponse> response = dashboardService.getAllCoins(platformName);
        HttpStatus httpStatus = getOkOrNoContentHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }

    @Override
    @GetMapping("/platform/balances")
    public ResponseEntity<Optional<PlatformBalanceResponse>> getPlatformsBalances() {
        Optional<PlatformBalanceResponse> response = dashboardService.getPlatformsBalances();
        HttpStatus httpStatus = getOkOrNoContentHttpStatusCode(response);

        return ResponseEntity.status(httpStatus)
                .body(response);
    }
}
