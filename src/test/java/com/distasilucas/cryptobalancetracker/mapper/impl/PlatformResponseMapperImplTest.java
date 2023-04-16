package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlatformResponseMapperImplTest {

    EntityMapper<PlatformResponse, Platform> entityMapper = new PlatformResponseMapperImpl();

    @Test
    void shouldMapSuccessfully() {
        var platform = Platform.builder()
                .name("LEDGER")
                .build();

        var platformResponse = entityMapper.mapFrom(platform);

        assertEquals(platform.getName(), platformResponse.getName());
    }
}