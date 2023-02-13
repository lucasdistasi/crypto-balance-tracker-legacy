package com.distasilucas.cryptobalancetracker.controller.swagger;

import com.distasilucas.cryptobalancetracker.model.ErrorResponse;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface PlatformControllerApi {

    @Operation(summary = "Add Platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added Platform",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PlatformDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
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
    ResponseEntity<PlatformDTO> addPlatform(PlatformDTO platformDTO);

    @Operation(summary = "Update Platform. This will also modify the platform name for those cryptos stored in the specified platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated Platform Name",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PlatformDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid data",
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
    ResponseEntity<PlatformDTO> updatePlatform(String platformName, PlatformDTO platformDTO);

    @Operation(summary = "Delete Platform. This will also delete all cryptos in the specified platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted Platform"),
            @ApiResponse(responseCode = "400", description = "Invalid data",
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
    ResponseEntity<Void> deletePlatform(String platformName);
}
