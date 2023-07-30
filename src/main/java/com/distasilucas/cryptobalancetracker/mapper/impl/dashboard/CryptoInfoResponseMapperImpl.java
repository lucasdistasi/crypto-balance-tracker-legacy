package com.distasilucas.cryptobalancetracker.mapper.impl.dashboard;

import com.distasilucas.cryptobalancetracker.comparator.DescendingBalanceComparator;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoBalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CryptoInfoResponseMapperImpl implements BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CryptoInfoResponse>> {

    @Override
    public BiFunction<Map<String, BigDecimal>, CryptoBalanceResponse, List<CryptoInfoResponse>> map() {
        log.info("Mapping to List<CryptoInfoResponse>");
        List<CryptoInfoResponse> cryptoInfoResponses = new ArrayList<>();

        return (cryptoByPlatform, cryptoBalanceResponse) -> {
            List<CryptoResponse> cryptos = cryptoBalanceResponse.cryptos();

            cryptoByPlatform.forEach((cryptoName, cryptoTotalBalance) -> {
                List<CryptoResponse> cryptosResponse = getCryptosResponse(cryptos, cryptoName);
                BigDecimal totalQuantity = getTotalQuantity(cryptosResponse);
                BigDecimal totalPercentage = getTotalPercentage(cryptosResponse);
                Set<String> platforms = getPlatforms(cryptosResponse);

                CryptoInfoResponse cryptoInfoResponse = new CryptoInfoResponse(cryptoName, totalQuantity, cryptoTotalBalance,
                        totalPercentage, platforms);

                cryptoInfoResponses.add(cryptoInfoResponse);
            });

            cryptoInfoResponses.sort(new DescendingBalanceComparator());

            return cryptoInfoResponses;
        };
    }

    private List<CryptoResponse> getCryptosResponse(List<CryptoResponse> cryptos, String cryptoName) {
        return cryptos.stream()
                .filter(crypto -> crypto.getCryptoInfo().getName().equals(cryptoName))
                .toList();
    }

    private BigDecimal getTotalQuantity(List<CryptoResponse> cryptosResponse) {
        return cryptosResponse.stream()
                .map(CryptoResponse::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalPercentage(List<CryptoResponse> cryptosResponse) {
        return cryptosResponse.stream()
                .map(CryptoResponse::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<String> getPlatforms(List<CryptoResponse> cryptosResponse) {
        return cryptosResponse.stream()
                .map(CryptoResponse::getPlatform)
                .collect(Collectors.toSet());
    }
}
