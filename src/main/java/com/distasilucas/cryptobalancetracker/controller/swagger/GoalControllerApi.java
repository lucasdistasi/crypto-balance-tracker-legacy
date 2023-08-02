package com.distasilucas.cryptobalancetracker.controller.swagger;

import com.distasilucas.cryptobalancetracker.model.error.ErrorResponse;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
import com.distasilucas.cryptobalancetracker.model.response.goal.PageGoalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.APPLICATION_JSON;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.BAD_REQUEST_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INTERNAL_SERVER_ERROR;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.INVALID_DATA;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.NOT_FOUND_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.NO_CONTENT_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.OK_CODE;
import static com.distasilucas.cryptobalancetracker.constant.SwaggerConstants.RESOURCE_CREATED_CODE;

public interface GoalControllerApi {

    @Operation(summary = "Return all Goals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "All Goals",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(schema = @Schema(implementation = GoalResponse.class)))
                    }
            ),
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "No Goals saved",
                    content = {
                            @Content(schema = @Schema())
                    }
            ),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)))
                    }
            )
    })
    ResponseEntity<Optional<PageGoalResponse>> getGoals(int page);

    @Operation(summary = "Return Goal for the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Goal information",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = GoalResponse.class))
                    }
            ),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = "Goal not found",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            ),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            ),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            )
    })
    ResponseEntity<GoalResponse> getGoal(String goalId);

    @Operation(summary = "Add a Goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESOURCE_CREATED_CODE, description = "Goal added",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = GoalResponse.class))
                    }
            ),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            ),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            )
    })
    ResponseEntity<GoalResponse> addGoal(AddGoalRequest addGoalRequest);

    @Operation(summary = "Edit a Goal quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK_CODE, description = "Goal quantity updated",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    array = @ArraySchema(schema = @Schema(implementation = GoalResponse.class)))
                    }
            ),
            @ApiResponse(responseCode = BAD_REQUEST_CODE, description = INVALID_DATA,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            ),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = "Goal not found",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            ),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            )
    })
    ResponseEntity<GoalResponse> updateGoalQuantity(UpdateGoalRequest updateGoalRequest, String goalId);

    @Operation(summary = "Delete a Goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = NO_CONTENT_CODE, description = "Goal deleted",
                    content = {
                            @Content(schema = @Schema())
                    }
            ),
            @ApiResponse(responseCode = NOT_FOUND_CODE, description = "Goal not found",
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            ),
            @ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR,
                    content = {
                            @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = ErrorResponse.class))
                    }
            )
    })
    ResponseEntity<Void> deleteGoal(String goalId);
}
