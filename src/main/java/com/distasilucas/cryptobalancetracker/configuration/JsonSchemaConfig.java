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
    public Schema cryptoJsonSchemaValidator() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schemas/cryptoSchema.json")) {
            if (inputStream == null) {
                throw new ApiException("Json Schema file not found");
            }

            JSONTokener jsonTokener = new JSONTokener(inputStream);
            JSONObject jsonSchema = new JSONObject(jsonTokener);

            return SchemaLoader.load(jsonSchema);
        } catch (ApiException ex) {
            throw new ApiException(ex.getErrorMessage());
        } catch (Exception ex) {
            throw new ApiException("Error reading Json Schema");
        }
    }

}
