package com.renseki.orm.exception;

public class ModelNotFoundException extends RuntimeException {

    public ModelNotFoundException(String modelName) {
        super(modelName);
    }
}
