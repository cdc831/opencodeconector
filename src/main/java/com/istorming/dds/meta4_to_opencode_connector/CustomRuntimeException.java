package com.istorming.dds.meta4_to_opencode_connector;

/*
 ** Clase Manejo de Errores
 */

public class CustomRuntimeException extends RuntimeException {

    private String errorMessage;

    public CustomRuntimeException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() { return this.errorMessage; }

}
