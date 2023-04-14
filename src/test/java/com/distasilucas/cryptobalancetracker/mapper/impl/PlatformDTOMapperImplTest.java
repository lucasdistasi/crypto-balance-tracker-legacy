package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlatformDTOMapperImplTest {

    EntityMapper<PlatformDTO, Platform> entityMapper = new PlatformDTOMapperImpl();

    @Test
    void shouldMapSuccessfully() {
        var platform = Platform.builder()
                .name("LEDGER")
                .build();

        var platformDTO = entityMapper.mapFrom(platform);

        assertEquals(platform.getName(), platformDTO.getName());
    }
}