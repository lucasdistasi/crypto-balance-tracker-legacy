package com.distasilucas.cryptobalancetracker.controller.swagger;

import com.distasilucas.cryptobalancetracker.model.error.ErrorResponse;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.APPLICATION_JSON;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.BAD_REQUEST_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.FORBIDDEN_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INTERNAL_SERVER_ERROR;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INVALID_DATA;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.NOT_FOUND_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.NO_CONTENT_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.OK_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.PLATFORM_NOT_FOUND_DESCRIPTION;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.RESOURCE_CREATED_CODE;

public interface PlatformControllerApi {

    @Operation(summary = "Retrieve all platforms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Platforms",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = PlatformRequest.class))
                    })
    })
    ResponseEntity<List<PlatformResponse>> getAllPlatforms();

    @Operation(summary = "Retrieve single platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Platform",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = PlatformRequest.class))
                    })
    })
    ResponseEntity<PlatformResponse> getPlatform(String platformName);

    @Operation(summary = "Add Platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESOURCE_CREATED_CODE, description = "Added Platform",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = PlatformRequest.class))
                    }),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    ResponseEntity<PlatformResponse> addPlatform(PlatformRequest platformRequest);

    @Operation(summary = "Update Platform. This will also modify the platform name for those cryptos stored in the specified platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Updated Platform Name",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = PlatformRequest.class))
                    }),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = PLATFORM_NOT_FOUND_DESCRIPTION,
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
    ResponseEntity<PlatformResponse> updatePlatform(String platformName, PlatformRequest platformRequest);

    @Operation(summary = "Delete Platform. This will also delete all cryptos in the specified platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "Deleted Platform"),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = FORBIDDEN_CODE, description = "Access is forbidden"),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = PLATFORM_NOT_FOUND_DESCRIPTION,
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
    ResponseEntity<Void> deletePlatform(String platformName);
}
