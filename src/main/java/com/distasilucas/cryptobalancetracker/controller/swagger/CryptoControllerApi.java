package com.distasilucas.cryptobalancetracker.controller.swagger;

import com.distasilucas.cryptobalancetracker.model.ErrorResponse;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoPlatformBalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.APPLICATION_JSON;
import static com.distasilucas.cryptobalancetracker.constant.Constants.BAD_REQUEST_CODE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.CRYPTO_NOT_FOUND_DESCRIPTION;
import static com.distasilucas.cryptobalancetracker.constant.Constants.CRYPTO_OR_PLATFORM_NOT_FOUND_DESCRIPTION;
import static com.distasilucas.cryptobalancetracker.constant.Constants.FORBIDDEN_CODE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.INTERNAL_SERVER_ERROR;
import static com.distasilucas.cryptobalancetracker.constant.Constants.INTERNAL_SERVER_ERROR_CODE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.INVALID_DATA;
import static com.distasilucas.cryptobalancetracker.constant.Constants.NOT_FOUND_CODE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.NO_CONTENT_CODE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.OK_CODE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.RESOURCE_CREATED_CODE;

public interface CryptoControllerApi {

    @Operation(summary = "Add Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESOURCE_CREATED_CODE, description = "Added Crypto",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CryptoDTO.class))
                    }),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = CRYPTO_NOT_FOUND_DESCRIPTION,
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
    ResponseEntity<CryptoDTO> addCoin(CryptoDTO cryptoDTO);

    @Operation(summary = "Retrieve Crypto Balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Retrieved Crypto Balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptoBalanceResponse.class)))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Cryptos Saved"),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinsBalance();

    @Operation(summary = "Retrieve total crypto balances by platforms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Retrieved Crypto Balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptoBalanceResponse.class)))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Cryptos Saved"),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<CryptoPlatformBalanceResponse>> retrieveCoinsBalanceByPlatform();

    @Operation(summary = "Retrieve all Balances from the given Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Retrieved Crypto Balances",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptoBalanceResponse.class)))
                    }),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Cryptos Saved"),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<CryptoBalanceResponse>> retrieveCoinBalance(String coinId);

    @Operation(summary = "Update Crypto from a specific Platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Updated Crypto",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CryptoDTO.class))
                    }),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = CRYPTO_OR_PLATFORM_NOT_FOUND_DESCRIPTION,
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
    ResponseEntity<CryptoDTO> updateCoin(CryptoDTO cryptoDTO, String coinId);

    @Operation(summary = "Delete Crypto from a specific Platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "Crypto deleted from Platform"),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = CRYPTO_NOT_FOUND_DESCRIPTION,
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
    ResponseEntity<Void> deleteCoin(String coinId);
}
