package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import com.distasilucas.cryptobalancetracker.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlatformMapperImplTest {

    EntityMapper<Platform, PlatformRequest> entityMapper = new PlatformMapperImpl();

    @Test
    void shouldMapSuccessfully() {
        var platformRequest = MockData.getPlatformRequest("Ledger");
        var platform = entityMapper.mapFrom(platformRequest);

        assertEquals(platformRequest.getName().toUpperCase(), platform.getName());
    }

}