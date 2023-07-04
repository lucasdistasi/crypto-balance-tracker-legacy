package com.distasilucas.cryptobalancetracker.mapper.impl.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PlatformMapperImpl implements EntityMapper<Platform, PlatformRequest> {

    @Override
    public Platform mapFrom(PlatformRequest input) {
        Function<PlatformRequest, Platform> platformFunction = platformRequest -> new Platform(platformRequest.getName());

        return platformFunction.apply(input);
    }
}
