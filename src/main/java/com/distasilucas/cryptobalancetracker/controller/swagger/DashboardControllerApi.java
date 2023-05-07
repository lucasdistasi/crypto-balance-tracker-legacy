package com.distasilucas.cryptobalancetracker.controller.swagger;

import com.distasilucas.cryptobalancetracker.model.error.ErrorResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptosPlatformDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.PlatformsCryptoDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformBalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.*;

public interface DashboardControllerApi {

    @Operation(summary = "Retrieve Crypto Balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Crypto Balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CryptoBalanceResponse.class))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Cryptos Saved",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinsBalance();

    @Operation(summary = "Retrieve all Balances from the given Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Crypto Balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptoBalanceResponse.class)))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Cryptos Saved",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinBalance(String coinId);

    @Operation(summary = "Retrieve all Balances for all Cryptos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Cryptos Balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptosPlatformDistributionResponse.class)))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Cryptos Saved",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<List<CryptosPlatformDistributionResponse>>> retrieveCoinBalance();

    @Operation(summary = "Retrieve total crypto balances by platforms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Crypto Balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptoPlatformBalanceResponse.class)))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Cryptos Saved",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<CryptoPlatformBalanceResponse>> retrieveCoinsBalanceByPlatform();

    @Operation(summary = "Retrieve all coins for the given platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Platform coins",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CryptoBalanceResponse.class))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No coins saved for the given platform",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<CryptoBalanceResponse>> getCoins(String platformName);

    @Operation(summary = "Retrieve all coins balances for all platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Platforms coins",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(schema = @Schema(
                                            implementation = PlatformsCryptoDistributionResponse.class)
                                    ))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No cryptos or platforms saved",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<List<PlatformsCryptoDistributionResponse>>> getPlatformsCryptoDistributionResponse();

    @Operation(summary = "Retrieve all platforms balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Platforms balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = PlatformBalanceResponse.class))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No coins saved",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<PlatformBalanceResponse>> getPlatformsBalances();
}
