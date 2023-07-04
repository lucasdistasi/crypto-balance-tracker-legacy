package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.DashboardControllerApi;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptosPlatformDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.PlatformsCryptoDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${allowed.origins}")
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
    @GetMapping("/cryptos")
    public ResponseEntity<Optional<List<CryptosPlatformDistributionResponse>>> retrieveCoinBalance() {
        Optional<List<CryptosPlatformDistributionResponse>> response = dashboardService.getCryptosPlatformDistribution();
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
    @GetMapping("/platforms/coins")
    public ResponseEntity<Optional<List<PlatformsCryptoDistributionResponse>>> getPlatformsCryptoDistributionResponse() {
        Optional<List<PlatformsCryptoDistributionResponse>> response = dashboardService.getPlatformsCryptoDistributionResponse();
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
