package com.istorming.dds.meta4_to_opencode_connector;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 ** Clase POJO para el rhId
 */

public class OpenCodeDeleteDocumentRequest {

    @JsonProperty("reciboCod")
    private String documentId;

    public OpenCodeDeleteDocumentRequest() {  }

    public OpenCodeDeleteDocumentRequest(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }
}
