package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;

public interface PlatformService {

    PlatformDTO addPlatForm(PlatformDTO platform);
    Platform findPlatform(String platformName);
    PlatformDTO updatePlatform(PlatformDTO platformDTO, String platformName);
    void deletePlatform(String platformName);
}
