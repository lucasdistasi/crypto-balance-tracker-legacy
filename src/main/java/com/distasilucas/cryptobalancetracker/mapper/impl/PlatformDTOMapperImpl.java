package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import org.springframework.stereotype.Service;

@Service
public class PlatformDTOMapperImpl implements EntityMapper<PlatformDTO, Platform> {

    @Override
    public PlatformDTO mapFrom(Platform input) {
        return new PlatformDTO(input.getName());
    }
}
