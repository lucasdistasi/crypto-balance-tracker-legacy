package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;

import java.util.List;

public interface PlatformService {

    List<PlatformResponse> getAllPlatforms();
    PlatformResponse addPlatForm(PlatformRequest platform);
    Platform findPlatformByName(String platformName);
    PlatformResponse updatePlatform(String platformName, PlatformRequest platformRequest);
    void deletePlatform(String platformName);
}
