package com.istorming.dds.meta4_to_opencode_connector;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 ** Clase POJO para armado de JSON
 */

public class OpenCodeCreateDocumentRequest {

    @JsonProperty("empresaDocumentoTipo")
    private String companyFiscalIdType;

    @JsonProperty("empresaDocumento")
    private String companyFiscalId;

    @JsonProperty("remesaCod")
    private String remesaCod;

    @JsonProperty("empleadoDocumentoTipo")
    private String employeeFiscalIdType;

    @JsonProperty("empleadoDocumento")
    private String employeeFiscalId;

    @JsonProperty("reciboTipo")
    private String documentType;

    @JsonProperty("reciboFecha")
    private String date;

    @JsonProperty("reciboPDF")
    private String documentBase64EncodedData;

    public OpenCodeCreateDocumentRequest() {  }

    public String getCompanyFiscalIdType() {
        return companyFiscalIdType;
    }

    public void setCompanyFiscalIdType(String companyFiscalIdType) {
        this.companyFiscalIdType = companyFiscalIdType;
    }

    public String getCompanyFiscalId() {
        return companyFiscalId;
    }

    public void setCompanyFiscalId(String companyFiscalId) {
        this.companyFiscalId = companyFiscalId;
    }

    public String getRemesaCod() {
        return remesaCod;
    }

    public void setRemesaCod(String remesaCod) {
        this.remesaCod = remesaCod;
    }

    public String getEmployeeFiscalIdType() {
        return employeeFiscalIdType;
    }

    public void setEmployeeFiscalIdType(String employeeFiscalIdType) {
        this.employeeFiscalIdType = employeeFiscalIdType;
    }

    public String getEmployeeFiscalId() {
        return employeeFiscalId;
    }

    public void setEmployeeFiscalId(String employeeFiscalId) {
        this.employeeFiscalId = employeeFiscalId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocumentBase64EncodedData() {
        return documentBase64EncodedData;
    }

    public void setDocumentBase64EncodedData(String documentBase64EncodedData) {
        this.documentBase64EncodedData = documentBase64EncodedData;
    }
}
