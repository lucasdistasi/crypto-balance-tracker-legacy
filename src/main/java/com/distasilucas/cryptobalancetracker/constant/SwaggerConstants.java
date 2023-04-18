package com.distasilucas.cryptobalancetracker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwaggerConstants {

    public static final String APPLICATION_JSON = "application/json";
    public static final String OK_CODE = "200";
    public static final String RESOURCE_CREATED_CODE = "201";
    public static final String NO_CONTENT_CODE = "204";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String FORBIDDEN_CODE = "403";
    public static final String NOT_FOUND_CODE = "404";
    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String INVALID_DATA = "Invalid data";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String PLATFORM_NOT_FOUND_DESCRIPTION = "Platform not found";
    public static final String CRYPTO_NOT_FOUND_DESCRIPTION = "Crypto not found";
    public static final String CRYPTO_OR_PLATFORM_NOT_FOUND_DESCRIPTION = "Crypto or platform not found";
}
