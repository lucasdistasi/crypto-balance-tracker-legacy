package com.distasilucas.cryptobalancetracker.mapper.impl.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import org.springframework.stereotype.Service;

@Service
public class PlatformResponseMapperImpl implements EntityMapper<PlatformResponse, Platform> {

    @Override
    public PlatformResponse mapFrom(Platform input) {
        return new PlatformResponse(input.getName());
    }
}
