package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlatformMapperImplTest {

    EntityMapper<Platform, PlatformDTO> entityMapper = new PlatformMapperImpl();

    @Test
    void shouldMapSuccessfully() {
        var platformDTO = new PlatformDTO("Ledger");

        var platform = entityMapper.mapFrom(platformDTO);

        assertEquals(platformDTO.getName().toUpperCase(), platform.getName());
    }

}