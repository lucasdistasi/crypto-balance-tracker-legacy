package com.distasilucas.cryptobalancetracker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexConstants {

    public static final String ID_MONGO_REGEX_VALIDATION = "^[a-zA-Z0-9]{24}$";
    public static final String PLATFORM_NAME_REGEX_VALIDATION = "^[a-zA-Z]{1,24}$";
    public static final String CRYPTO_NAME_REGEX_VALIDATION = "^(?! )(?!.* {2})[a-zA-Z0-9]+(?:[ {2}][a-zA-Z0-9]+)*$(?<! )";
}
