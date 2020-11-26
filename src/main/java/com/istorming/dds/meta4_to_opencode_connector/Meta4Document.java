package com.istorming.dds.meta4_to_opencode_connector;

import java.util.Date;
/*
** Clase POJO para la Entidad
*/
public class Meta4Document {

    private String id;

    private String openCodeId;

    private String companyFiscalIdType;

    private String companyFiscalId;

    private String remesaCod;

    private String employeeFiscalIdType;

    private String employeeFiscalId;

    private String documentType;

    private Date date;

    private String documentPath;

    public Meta4Document() { }

    public Meta4Document(String id, String openCodeId, String companyFiscalIdType, String companyFiscalId, String remesaCod, String employeeFiscalIdType, String employeeFiscalId, String documentType, Date date, String documentPath) {
        this.id = id;
        this.openCodeId = openCodeId;
        this.companyFiscalIdType = companyFiscalIdType;
        this.companyFiscalId = companyFiscalId;
        //this.consignmentId = consignmentId;
        this.remesaCod = remesaCod;
        this.employeeFiscalIdType = employeeFiscalIdType;
        this.employeeFiscalId = employeeFiscalId;
        this.documentType = documentType;
        this.date = date;
        this.documentPath = documentPath;
    }

    public String getId() {
        return id;
    }

    public String getOpenCodeId() {
        return openCodeId;
    }

    public String getCompanyFiscalIdType() {
        return companyFiscalIdType;
    }

    public String getCompanyFiscalId() {
        return companyFiscalId;
    }

    public String getRemesaCod() {
        return remesaCod;
    }

    public String getEmployeeFiscalIdType() {
        return employeeFiscalIdType;
    }

    public String getEmployeeFiscalId() {
        return employeeFiscalId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public Date getDate() {
        return date;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    @Override
    public String toString() {
        return "Meta4Document{" +
                "id='" + id + '\'' +
                ", openCodeId='" + openCodeId + '\'' +
                ", companyFiscalIdType='" + companyFiscalIdType + '\'' +
                ", companyFiscalId='" + companyFiscalId + '\'' +
                ", remesaCod='" + remesaCod + '\'' +
                ", employeeFiscalIdType='" + employeeFiscalIdType + '\'' +
                ", employeeFiscalId='" + employeeFiscalId + '\'' +
                ", documentType='" + documentType + '\'' +
                ", date=" + date +
                ", documentPath='" + documentPath + '\'' +
                '}';
    }
}