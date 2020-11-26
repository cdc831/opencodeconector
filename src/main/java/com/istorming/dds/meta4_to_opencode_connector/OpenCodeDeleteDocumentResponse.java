package com.istorming.dds.meta4_to_opencode_connector;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 ** Clase POJO para la respuesta
 */

public class OpenCodeDeleteDocumentResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("reciboCod")
    private String documentId;

    public OpenCodeDeleteDocumentResponse() { }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
