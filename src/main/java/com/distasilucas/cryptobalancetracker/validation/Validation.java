package com.distasilucas.cryptobalancetracker.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Validation<T> {

    private final List<EntityValidation<T>> entityValidations = new ArrayList<>();

    @SafeVarargs
    public Validation(EntityValidation<T>... validations) {
        entityValidations.addAll(Arrays.asList(validations));
    }
    public void validate(T objectToValidate) {
        entityValidations.forEach(validator -> validator.validate(objectToValidate));
    }
}
