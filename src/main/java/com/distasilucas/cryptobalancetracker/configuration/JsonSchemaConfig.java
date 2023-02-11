package com.distasilucas.cryptobalancetracker.configuration;

import com.distasilucas.cryptobalancetracker.exception.ApiException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class JsonSchemaConfig {

    @Bean
    public Schema addCryptoJsonSchemaValidator() {
        return cryptoJsonSchemaValidator(getClass().getResourceAsStream("/schemas/addCryptoSchema.json"));
    }

    @Bean
    public Schema updateCryptoJsonSchemaValidator() {
        return cryptoJsonSchemaValidator(getClass().getResourceAsStream("/schemas/updateCryptoSchema.json"));
    }

    public Schema cryptoJsonSchemaValidator(InputStream schemaToValidate) {
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
