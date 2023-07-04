package com.distasilucas.cryptobalancetracker.controller.swagger;

import com.distasilucas.cryptobalancetracker.model.error.ErrorResponse;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.APPLICATION_JSON;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.BAD_REQUEST_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.CRYPTO_NOT_FOUND_DESCRIPTION;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.CRYPTO_OR_PLATFORM_NOT_FOUND_DESCRIPTION;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.FORBIDDEN_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INTERNAL_SERVER_ERROR;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INVALID_DATA;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.NOT_FOUND_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.NO_CONTENT_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.OK_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.RESOURCE_CREATED_CODE;

public interface CryptoControllerApi {

    @Operation(summary = "Returns Crypto information for the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Crypto Response",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CryptoResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
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
    ResponseEntity<CryptoResponse> getCoin(String coinId);

    @Operation(summary = "Retrieve Cryptos by page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "All Cryptos",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = PageCryptoResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No cryptos saved",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Optional<PageCryptoResponse>> getCoins(int page);

    @Operation(summary = "Add Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESOURCE_CREATED_CODE, description = "Crypto Added",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CryptoResponse.class))
                    }),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<CryptoResponse> addCoin(AddCryptoRequest cryptoRequest);

    @Operation(summary = "Update Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Crypto Updated",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CryptoResponse.class))
                    }),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden",
                    content = @Content(schema = @Schema())),
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
    ResponseEntity<CryptoResponse> updateCoin(UpdateCryptoRequest cryptoRequest, String coinId);

    @Operation(summary = "Delete Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "Crypto deleted",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden",
                    content = @Content(schema = @Schema())),
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
