package com.distasilucas.cryptobalancetracker.controller.swagger;

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

public interface CryptoControllerApi {

    @Operation(summary = "Add Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added Crypto",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CryptoDTO.class))
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
    ResponseEntity<CryptoDTO> addCoin(CryptoDTO cryptoDTO);

    @Operation(summary = "Retrieve Crypto Balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved Crypto Balances",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CryptoBalanceResponse.class)))
                    }),
            @ApiResponse(responseCode = "204", description = "No Cryptos Saved"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<CryptoBalanceResponse> retrieveCoinsBalance();

    @Operation(summary = "Update Crypto Quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated Crypto Quantity",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CryptoDTO.class))
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
    ResponseEntity<CryptoDTO> updateCrypto(CryptoDTO cryptoDTO, String coinName);

    @Operation(summary = "Delete Crypto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Crypto Deleted"),
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
    ResponseEntity<Void> deleteCoin(String coinName);
}
