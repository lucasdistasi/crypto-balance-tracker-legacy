package com.distasilucas.cryptobalancetracker.mapper.impl.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
public class PlatformMapperImpl implements EntityMapper<Platform, PlatformRequest> {

    @Override
    public Platform mapFrom(PlatformRequest input) {
        log.info("Mapping PlatformRequest for {}", input.getName());
        Function<PlatformRequest, Platform> platformFunction = platformRequest -> new Platform(platformRequest.getName());

        return platformFunction.apply(input);
    }
}
