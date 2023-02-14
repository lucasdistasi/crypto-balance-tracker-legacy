package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PlatformMapperImpl implements EntityMapper<Platform, PlatformDTO> {

    @Override
    public Platform mapFrom(PlatformDTO input) {
        Function<PlatformDTO, Platform> platformFunction = this::getPlatform;

        return platformFunction.apply(input);
    }

    private Platform getPlatform(PlatformDTO platformDTO) {
        return new Platform(platformDTO.getName());
    }
}
