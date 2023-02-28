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
        Function<PlatformDTO, Platform> platformFunction = platformDTO -> new Platform(platformDTO.getName());

        return platformFunction.apply(input);
    }
}
