package com.distasilucas.cryptobalancetracker.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Server URL in Local environment");

        Info openApiInfo = new Info()
                .title("Crypto Balance Tracker")
                .version("v1.0.0")
                .description("REST API to add cryptocurrencies with their respective balance and retrieve the percentage of each coin");

        return new OpenAPI()
                .info(openApiInfo)
                .servers(Collections.singletonList(localServer));
    }
}
