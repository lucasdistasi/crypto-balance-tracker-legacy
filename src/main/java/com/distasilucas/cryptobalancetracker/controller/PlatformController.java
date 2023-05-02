package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.helper.ControllerHelper;
import com.distasilucas.cryptobalancetracker.controller.swagger.PlatformControllerApi;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
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

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${allowed.origins}")
@RequestMapping("/api/v1/platforms")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class PlatformController implements PlatformControllerApi, ControllerHelper {

    private final PlatformService platformService;

    @Override
    @GetMapping
    public ResponseEntity<List<PlatformResponse>> getAllPlatforms() {
        List<PlatformResponse> platforms = platformService.getAllPlatforms();
        HttpStatus httpStatus = CollectionUtils.isNotEmpty(platforms) ? HttpStatus.OK : HttpStatus.NO_CONTENT;

        return ResponseEntity.status(httpStatus)
                .body(platforms);
    }

    @Override
    @GetMapping("/{platformName}")
    public ResponseEntity<PlatformResponse> getPlatform(@PathVariable String platformName) {
        Platform platform = platformService.findPlatformByName(platformName);
        PlatformResponse platformResponse = new PlatformResponse(platform.getName());

        return ResponseEntity.ok(platformResponse);
    }

    @Override
    @PostMapping
    public ResponseEntity<PlatformResponse> addPlatform(@RequestBody PlatformRequest platformRequest) {
        PlatformResponse platForm = platformService.addPlatForm(platformRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platForm);
    }

    @Override
    @PutMapping("/{platformName}")
    public ResponseEntity<PlatformResponse> updatePlatform(@PathVariable String platformName,
                                                          @RequestBody PlatformRequest platformRequest) {
        PlatformResponse updatedPlatform = platformService.updatePlatform(platformName, platformRequest);

        return ResponseEntity.ok(updatedPlatform);
    }

    @Override
    @DeleteMapping("/{platformName}")
    public ResponseEntity<Void> deletePlatform(@PathVariable String platformName) {
        platformService.deletePlatform(platformName);

        return ResponseEntity.noContent().build();
    }
}
