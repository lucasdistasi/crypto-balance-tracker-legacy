package com.distasilucas.cryptobalancetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CryptoBalanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoBalanceTrackerApplication.class, args);
    }

}
