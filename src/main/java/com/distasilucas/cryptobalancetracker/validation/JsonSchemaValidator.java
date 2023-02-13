package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public interface JsonSchemaValidator {

    default Schema validateJsonSchema(InputStream schemaToValidate) {
        try (schemaToValidate) {
            if (schemaToValidate == null) {
                throw new ApiException("Json Schema file not found");
            }

            JSONTokener jsonTokener = new JSONTokener(schemaToValidate);
            JSONObject jsonSchema = new JSONObject(jsonTokener);

            return SchemaLoader.load(jsonSchema);
        } catch (ApiException ex) {
            throw new ApiException(ex.getErrorMessage());
        } catch (Exception ex) {
            throw new ApiException("Error reading Json Schema");
        }
    }
}
