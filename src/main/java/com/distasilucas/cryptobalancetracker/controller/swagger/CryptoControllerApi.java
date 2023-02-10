package com.distasilucas.cryptobalancetracker.controller.swagger;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.model.ErrorResponse;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface CryptoControllerApi {

    @Operation(summary = "Add Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added Crypto",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Crypto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Crypto not found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<Crypto> addCrypto(@RequestBody CryptoDTO cryptoDTO);

    @Operation(summary = "Retrieve Crypto Balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved Crypto Balances",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptoBalanceResponse.class)))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<CryptoBalanceResponse> retrieveCoinsBalance();
}
