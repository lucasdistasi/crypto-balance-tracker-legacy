package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.swagger.PlatformControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/platform")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class PlatformController implements PlatformControllerApi {

    private final PlatformService platformService;

    @Override
    @GetMapping("/{platformName}/coins")
    public ResponseEntity<Optional<CryptoBalanceResponse>> getCoins(@PathVariable String platformName) {
        Optional<CryptoBalanceResponse> allCoins = platformService.getAllCoins(platformName);
        HttpStatus httpStatus = allCoins.isPresent() ? HttpStatus.OK : HttpStatus.NO_CONTENT;

        return ResponseEntity.status(httpStatus)
                .body(allCoins);
    }

    @Override
    @PostMapping
    public ResponseEntity<PlatformDTO> addPlatform(@RequestBody PlatformDTO platformDTO) {
        PlatformDTO platForm = platformService.addPlatForm(platformDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platForm);
    }

    @Override
    @PutMapping("/{platformName}")
    public ResponseEntity<PlatformDTO> updatePlatform(@PathVariable String platformName,
                                                      @RequestBody PlatformDTO platformDTO) {
        PlatformDTO updatedPlatform = platformService.updatePlatform(platformDTO, platformName);

        return ResponseEntity.ok(updatedPlatform);
    }

    @Override
    @PutMapping("/{platformName}/{coinId}")
    public ResponseEntity<CryptoDTO> updatePlatformCoin(@RequestBody CryptoDTO cryptoDTO,
                                                        @PathVariable String platformName,
                                                        @PathVariable String coinId) {
        CryptoDTO updatedCrypto = platformService.updatePlatformCoin(cryptoDTO, platformName, coinId);

        return ResponseEntity.ok(updatedCrypto);
    }

    @Override
    @DeleteMapping("/{platformName}")
    public ResponseEntity<Void> deletePlatform(@PathVariable String platformName) {
        platformService.deletePlatform(platformName);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{platformName}/{coinId}")
    public ResponseEntity<Void> deletePlatformCoin(@PathVariable String platformName,
                                                   @PathVariable String coinId) {
        platformService.deletePlatformCoin(platformName, coinId);

        return ResponseEntity.noContent().build();
    }
}
