package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;

import java.util.List;
import java.util.Optional;

public interface PlatformService {

    Optional<Platform> findById(String id);
    Optional<Platform> findByName(String name);
    List<PlatformResponse> getAllPlatformsResponse();
    PlatformResponse addPlatForm(PlatformRequest platform);
    Platform findPlatformByName(String platformName);
    PlatformResponse updatePlatform(String platformName, PlatformRequest platformRequest);
    void deletePlatform(String platformName);
}
