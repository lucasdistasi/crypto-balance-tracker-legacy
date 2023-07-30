package com.distasilucas.cryptobalancetracker.mapper.impl.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlatformResponseMapperImpl implements EntityMapper<PlatformResponse, Platform> {

    @Override
    public PlatformResponse mapFrom(Platform input) {
        log.info("Mapping Platform {}", input.getName());
        return new PlatformResponse(input.getName());
    }
}
