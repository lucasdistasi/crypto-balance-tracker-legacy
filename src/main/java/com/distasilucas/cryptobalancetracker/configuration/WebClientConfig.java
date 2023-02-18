package com.distasilucas.cryptobalancetracker.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${coingecko.url}")
    private String coingeckoUrl;

    @Value("${coingecko.pro.url}")
    private String coingeckoProUrl;

    @Value("${coingecko.pro.api-key}")
    private String coingeckoApiKey;

    @Bean
    public WebClient coingeckoWebClient() {
        String baseUrl = StringUtils.isNotBlank(coingeckoApiKey) ?
                coingeckoProUrl :
                coingeckoUrl;

        return WebClient.builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                        .maxInMemorySize(700 * 1024))
                .baseUrl(baseUrl)
                .build();
    }
}
