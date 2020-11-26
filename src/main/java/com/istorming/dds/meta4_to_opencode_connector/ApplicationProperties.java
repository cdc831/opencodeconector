package com.istorming.dds.meta4_to_opencode_connector;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/*
 ** Clase POJO Config
 */

public class ApplicationProperties {

    private String createDocumentsActionName;

    private String deleteDocumentsActionName;

    private int threadPoolMaxSize = 0;

    private String meta4DbConnectionString;

    private String meta4DbUsername;

    private String meta4DbPassword;

    private String  meta4DbCreateDocumentsSelectQueryString;

    private String meta4DbUpdateCreatedDocumentRecordSuccessQueryString;

    private String meta4DbUpdateCreatedDocumentRecordFailureQueryString;

    private String meta4DbDeleteDocumentsSelectQueryString;

    private String meta4DbUpdateDeletedDocumentRecordSuccessQueryString;

    private String meta4DbUpdateDeletedDocumentRecordFailureQueryString;

    private String meta4DbDocumentIdColumnName;

    private String meta4DbOpenCodeDocumentIdColumnName;

    private String meta4DbCompanyFiscalIdTypeColumnName;

    private String meta4DbCompanyFiscalIdColumnName;

    private String meta4DbRemesaCodColumnName;

    private String meta4DbEmployeeFiscalIdTypeColumnName;

    private String meta4DbEmployeeFiscalIdColumnName;

    private String meta4DbDocumentTypeColumnName;

    private String meta4DbDateColumnName;

    private String meta4DbDocumentPathColumnName;

    private Map<String, String> meta4ToOpenCodeFiscalIdTypeMapping;

    private Map<String, String> meta4ToOpenCodeDocumentTypeMapping;

    private String meta4DateFormat;

    private String openCodeDateFormat;

    private String defaultCompanyFiscalId;

    private String defaultCompanyFiscalIdType;

    private Boolean sendRemesaCodOnCreateDocumentRequest;

    private String opencodeCreateDocumentEndpointUrl;

    private String opencodeDeleteDocumentEndpointUrl;

    private int meta4DbMaxConnectionAttempts;

    private long meta4DbMaxConnectionAttemptsWaitTime;

    private String loadRequiredStringProperty(String propertyName, Properties properties) throws CustomRuntimeException {
        try {
            String propertyValue = properties.getProperty(propertyName);
            if(propertyValue == null || propertyValue.equals("")) {
                throw new CustomRuntimeException("Failed to read configuration properties. The '" + propertyName + "' property is required and it is not set. Execution aborted!");
            }
            return propertyValue;
        }
        catch (Exception e) {
            throw new CustomRuntimeException("Failed to read configuration properties. Failed to read the '" + propertyName + "' property. Execution aborted!");
        }
    }

    private String loadOptionalStringProperty(String propertyName, Properties properties) throws CustomRuntimeException {
//        System.out.println("Properties: " + propertyName);
        try {
            String propertyValue = properties.getProperty(propertyName);
//            System.out.println("propertyValue = " + propertyValue);
            if(propertyValue != null && !propertyValue.equals("")) {
                return propertyValue;
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private Integer loadRequiredIntegerProperty(String propertyName, Properties properties) throws CustomRuntimeException {
        try {
            String propertyStringValue = properties.getProperty(propertyName);
            if(propertyStringValue == null || propertyStringValue.equals("")) {
                throw new CustomRuntimeException("Failed to read configuration properties. The '" + propertyName + "' property is required and it is not set. Execution aborted!");
            }
            Integer propertyValue = null;
            try {
                propertyValue = Integer.parseInt(propertyStringValue);
            }
            catch(Exception e) {
                throw new CustomRuntimeException("Failed to read configuration properties. Failed to parse the '" + propertyName + "' property to integer. Execution aborted!");
            }
            return propertyValue;
        }
        catch (Exception e) {
            throw new CustomRuntimeException("Failed to read configuration properties. Failed to read the '" + propertyName + "' property. Execution aborted!");
        }
    }

    private Long loadRequiredLongProperty(String propertyName, Properties properties) throws CustomRuntimeException {
        try {
            String propertyStringValue = properties.getProperty(propertyName);
            if(propertyStringValue == null || propertyStringValue.equals("")) {
                throw new CustomRuntimeException("Failed to read configuration properties. The '" + propertyName + "' property is required and it is not set. Execution aborted!");
            }
            Long propertyValue = null;
            try {
                propertyValue = Long.parseLong(propertyStringValue);
            }
            catch(Exception e) {
                throw new CustomRuntimeException("Failed to read configuration properties. Failed to parse the '" + propertyName + "' property to long. Execution aborted!");
            }
            return propertyValue;
        }
        catch (Exception e) {
            throw new CustomRuntimeException("Failed to read configuration properties. Failed to read the '" + propertyName + "' property. Execution aborted!");
        }
    }

    private Map<String,String> loadRequiredStringToStringMapProperty(String propertyName, Properties properties) throws CustomRuntimeException {
        try {
            String propertyStringValue = properties.getProperty(propertyName);
            if(propertyStringValue == null || propertyStringValue.equals("")) {
                throw new CustomRuntimeException("Failed to read configuration properties. The '" + propertyName + "' property is required and it is not set. Execution aborted!");
            }
            Map<String, String> propertyValue = new HashMap<String, String>();
            String[] itemsArray = propertyStringValue.split(";");
            for(String item : itemsArray) {
                String[] keyValueArray = item.split(":");
                if(keyValueArray.length != 2) {
                    throw new CustomRuntimeException("Failed to read configuration properties. The '" + propertyName + "' property format is not valid. Execution aborted!");
                }
                propertyValue.put(keyValueArray[0], keyValueArray[1]);
            }
            return propertyValue;
        }
        catch (Exception e) {
            throw new CustomRuntimeException("Failed to read configuration properties. Failed to read the '" + propertyName + "' property. Execution aborted!");
        }
    }

    private Boolean loadRequiredBooleanProperty(String propertyName, Properties properties) throws CustomRuntimeException {
        try {
            String propertyStringValue = properties.getProperty(propertyName);
            if(propertyStringValue == null || propertyStringValue.equals("")) {
                throw new CustomRuntimeException("Failed to read configuration properties. The '" + propertyName + "' property is required and it is not set. Execution aborted!");
            }
            Boolean propertyValue = null;
            try {
                propertyValue = Boolean.parseBoolean(propertyStringValue);
            }
            catch(Exception e) {
                throw new CustomRuntimeException("Failed to read configuration properties. Failed to parse the '" + propertyName + "' property to boolean. Execution aborted!");
            }
            return propertyValue;
        }
        catch (Exception e) {
            throw new CustomRuntimeException("Failed to read configuration properties. Failed to read the '" + propertyName + "' property. Execution aborted!");
        }
    }

    public ApplicationProperties(Properties properties) throws CustomRuntimeException {
        this.createDocumentsActionName = this.loadRequiredStringProperty("createDocumentsActionName", properties);
        this.deleteDocumentsActionName = this.loadRequiredStringProperty("deleteDocumentsActionName", properties);
        this.threadPoolMaxSize = this.loadRequiredIntegerProperty("threadPoolMaxSize", properties);
        this.meta4DbConnectionString = this.loadRequiredStringProperty("meta4DbConnectionString", properties);
        this.meta4DbUsername = this.loadRequiredStringProperty("meta4DbUsername", properties);
        this.meta4DbPassword = this.loadRequiredStringProperty("meta4DbPassword", properties);
        this.meta4DbCreateDocumentsSelectQueryString = this.loadRequiredStringProperty("meta4DbCreateDocumentsSelectQueryString", properties);
        this.meta4DbUpdateCreatedDocumentRecordSuccessQueryString = this.loadRequiredStringProperty("meta4DbUpdateCreatedDocumentRecordSuccessQueryString", properties);
        this.meta4DbUpdateCreatedDocumentRecordFailureQueryString = this.loadRequiredStringProperty("meta4DbUpdateCreatedDocumentRecordFailureQueryString", properties);
        this.meta4DbDeleteDocumentsSelectQueryString = this.loadRequiredStringProperty("meta4DbDeleteDocumentsSelectQueryString", properties);
        this.meta4DbUpdateDeletedDocumentRecordSuccessQueryString = this.loadRequiredStringProperty("meta4DbUpdateDeletedDocumentRecordSuccessQueryString", properties);
        this.meta4DbUpdateDeletedDocumentRecordFailureQueryString = this.loadRequiredStringProperty("meta4DbUpdateDeletedDocumentRecordFailureQueryString", properties);
        this.meta4DbDocumentIdColumnName = this.loadRequiredStringProperty("meta4DbDocumentIdColumnName", properties);
        this.meta4DbOpenCodeDocumentIdColumnName = this.loadRequiredStringProperty("meta4DbOpenCodeDocumentIdColumnName", properties);
        this.meta4DbCompanyFiscalIdTypeColumnName = this.loadOptionalStringProperty("meta4DbCompanyFiscalIdTypeColumnName", properties);
        this.meta4DbCompanyFiscalIdColumnName = this.loadOptionalStringProperty("meta4DbCompanyFiscalIdColumnName", properties);
        this.meta4DbRemesaCodColumnName = this.loadOptionalStringProperty("meta4DbRemesaCodColumnName", properties);
        this.meta4DbEmployeeFiscalIdTypeColumnName = this.loadRequiredStringProperty("meta4DbEmployeeFiscalIdTypeColumnName", properties);
        this.meta4DbEmployeeFiscalIdColumnName = this.loadRequiredStringProperty("meta4DbEmployeeFiscalIdColumnName", properties);
        this.meta4DbDocumentTypeColumnName = this.loadRequiredStringProperty("meta4DbDocumentTypeColumnName", properties);
        this.meta4DbDateColumnName = this.loadRequiredStringProperty("meta4DbDateColumnName", properties);
        this.meta4DbDocumentPathColumnName = this.loadRequiredStringProperty("meta4DbDocumentPathColumnName", properties);
        this.meta4ToOpenCodeFiscalIdTypeMapping = this.loadRequiredStringToStringMapProperty("meta4ToOpenCodeFiscalIdTypeMapping", properties);
        this.meta4ToOpenCodeDocumentTypeMapping = this.loadRequiredStringToStringMapProperty("meta4ToOpenCodeDocumentTypeMapping", properties);
        this.meta4DateFormat = this.loadRequiredStringProperty("meta4DateFormat", properties);
        this.openCodeDateFormat = this.loadRequiredStringProperty("openCodeDateFormat", properties);
        this.defaultCompanyFiscalId = this.loadRequiredStringProperty("defaultCompanyFiscalId", properties);
        this.defaultCompanyFiscalIdType = this.loadRequiredStringProperty("defaultCompanyFiscalIdType", properties);
        this.sendRemesaCodOnCreateDocumentRequest = this.loadRequiredBooleanProperty("sendRemesaCodOnCreateDocumentRequest", properties);
        this.opencodeCreateDocumentEndpointUrl = this.loadRequiredStringProperty("opencodeCreateDocumentEndpointUrl", properties);
        this.opencodeDeleteDocumentEndpointUrl = this.loadRequiredStringProperty("opencodeDeleteDocumentEndpointUrl", properties);
        this.meta4DbMaxConnectionAttempts = this.loadRequiredIntegerProperty("meta4DbMaxConnectionAttempts", properties);
        this.meta4DbMaxConnectionAttemptsWaitTime = this.loadRequiredLongProperty("meta4DbMaxConnectionAttemptsWaitTime", properties);
    }

    public String getCreateDocumentsActionName() {
        return createDocumentsActionName;
    }

    public String getDeleteDocumentsActionName() {
        return deleteDocumentsActionName;
    }

    public int getThreadPoolMaxSize() {
        return threadPoolMaxSize;
    }

    public String getMeta4DbConnectionString() {
        return meta4DbConnectionString;
    }

    public String getMeta4DbUsername() {
        return meta4DbUsername;
    }

    public String getMeta4DbPassword() {
        return meta4DbPassword;
    }

    public String getMeta4DbCreateDocumentsSelectQueryString() {
        return meta4DbCreateDocumentsSelectQueryString;
    }

    public String getMeta4DbUpdateCreatedDocumentRecordSuccessQueryString() { return meta4DbUpdateCreatedDocumentRecordSuccessQueryString; }

    public String getMeta4DbUpdateCreatedDocumentRecordFailureQueryString() { return meta4DbUpdateCreatedDocumentRecordFailureQueryString; }

    public String getMeta4DbDeleteDocumentsSelectQueryString() { return meta4DbDeleteDocumentsSelectQueryString; }

    public String getMeta4DbUpdateDeletedDocumentRecordSuccessQueryString() { return meta4DbUpdateDeletedDocumentRecordSuccessQueryString; }

    public String getMeta4DbUpdateDeletedDocumentRecordFailureQueryString() { return meta4DbUpdateDeletedDocumentRecordFailureQueryString; }

    public String getMeta4DbDocumentIdColumnName() {
        return meta4DbDocumentIdColumnName;
    }

    public String getMeta4DbOpenCodeDocumentIdColumnName() {
        return meta4DbOpenCodeDocumentIdColumnName;
    }

    public String getMeta4DbCompanyFiscalIdTypeColumnName() { return meta4DbCompanyFiscalIdTypeColumnName; }

    public String getMeta4DbCompanyFiscalIdColumnName() {
        return meta4DbCompanyFiscalIdColumnName;
    }

    public String getMeta4DbRemesaCodColumnName() {
        return meta4DbRemesaCodColumnName;
    }

    public String getMeta4DbEmployeeFiscalIdTypeColumnName() {
        return meta4DbEmployeeFiscalIdTypeColumnName;
    }

    public String getMeta4DbEmployeeFiscalIdColumnName() {
        return meta4DbEmployeeFiscalIdColumnName;
    }

    public String getMeta4DbDocumentTypeColumnName() {
        return meta4DbDocumentTypeColumnName;
    }

    public String getMeta4DbDateColumnName() {
        return meta4DbDateColumnName;
    }

    public String getMeta4DbDocumentPathColumnName() {
        return meta4DbDocumentPathColumnName;
    }

    public Map<String, String> getMeta4ToOpenCodeFiscalIdTypeMapping() {
        return meta4ToOpenCodeFiscalIdTypeMapping;
    }

    public Map<String, String> getMeta4ToOpenCodeDocumentTypeMapping() {
        return meta4ToOpenCodeDocumentTypeMapping;
    }

    public String gtMeta4DateFormat() {
        return meta4DateFormat;
    }

    public String getOpenCodeDateFormat() {
        return openCodeDateFormat;
    }

    public String getDefaultCompanyFiscalId() {
        return defaultCompanyFiscalId;
    }

    public String getDefaultCompanyFiscalIdType() {
        return defaultCompanyFiscalIdType;
    }

    public Boolean getSendRemesaCodOnCreateDocumentRequest() {
        return sendRemesaCodOnCreateDocumentRequest;
    }

    public String getOpencodeCreateDocumentEndpointUrl() {
        return opencodeCreateDocumentEndpointUrl;
    }

    public String getOpencodeDeleteDocumentEndpointUrl() {
        return opencodeDeleteDocumentEndpointUrl;
    }

    public int getMeta4DbMaxConnectionAttempts() { return meta4DbMaxConnectionAttempts; }

    public long getMeta4DbMaxConnectionAttemptsWaitTime() { return meta4DbMaxConnectionAttemptsWaitTime; }

}
