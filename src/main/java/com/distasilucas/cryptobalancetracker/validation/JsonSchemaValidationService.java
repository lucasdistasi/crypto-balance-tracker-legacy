package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.ERROR_VALIDATING_JSON_SCHEMA;

@RequiredArgsConstructor
public class JsonSchemaValidationService<T> implements EntityValidation<T> {

    private final Schema schema;

    @Override
    public void validate(T objectToValidate) {
        try {
            Gson gson = new Gson();
            String toJson = gson.toJson(objectToValidate);
            JSONObject jsonObject = new JSONObject(toJson);

            schema.validate(jsonObject);
        } catch (ValidationException ex) {
            String message = String.format(ERROR_VALIDATING_JSON_SCHEMA, ex.getMessage());

            throw new ApiValidationException(ex.getCausingExceptions(), ex.getErrorMessage(), message);
        }
    }
}
