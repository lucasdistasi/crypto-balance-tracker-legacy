package com.distasilucas.cryptobalancetracker.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.function.Consumer;

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
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));
        ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .codecs(getCodecs())
                .baseUrl(baseUrl)
                .clientConnector(httpConnector)
                .build();
    }

    private Consumer<ClientCodecConfigurer> getCodecs() {
        return clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                .maxInMemorySize(700 * 1024);
    }
}
