package com.distasilucas.cryptobalancetracker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("coingecko.url")
    private String coingeckoUrl;

    @Bean
    public WebClient coingeckoWebClient() {
        return WebClient.builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                        .maxInMemorySize(700 * 1024))
                .baseUrl("https://api.coingecko.com/api/v3")
                .build();
    }
}
