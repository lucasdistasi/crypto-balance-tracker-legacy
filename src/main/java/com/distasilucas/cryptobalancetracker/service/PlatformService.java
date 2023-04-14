package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;

import java.util.List;

public interface PlatformService {

    List<PlatformDTO> getAllPlatforms();
    PlatformDTO addPlatForm(PlatformDTO platform);
    Platform findPlatformByName(String platformName);
    PlatformDTO updatePlatform(PlatformDTO platformDTO, String platformName);
    void deletePlatform(String platformName);
}
