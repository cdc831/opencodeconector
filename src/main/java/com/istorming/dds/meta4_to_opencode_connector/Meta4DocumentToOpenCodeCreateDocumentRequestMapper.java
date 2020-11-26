package com.istorming.dds.meta4_to_opencode_connector;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 ** Mapea los datos
 */

public class Meta4DocumentToOpenCodeCreateDocumentRequestMapper {

    final static Logger logger = Logger.getLogger(Meta4DocumentToOpenCodeCreateDocumentRequestMapper.class);

    public static void map(Meta4Document meta4Document, String base64EncodedFileData, OpenCodeCreateDocumentRequest openCodeCreateDocumentRequest, ApplicationProperties properties) throws CustomRuntimeException {
//        System.out.println("-------: " + meta4Document.getCompanyFiscalIdType());
        openCodeCreateDocumentRequest.setCompanyFiscalIdType(meta4Document.getCompanyFiscalIdType());

//        System.out.println("-------: " + meta4Document.getCompanyFiscalId());
        openCodeCreateDocumentRequest.setCompanyFiscalId(meta4Document.getCompanyFiscalId());

//        System.out.println("-------: " + meta4Document.getConsignmentId());
        openCodeCreateDocumentRequest.setRemesaCod(meta4Document.getRemesaCod());

//        System.out.println("-------: " + meta4Document.getEmployeeFiscalIdType());
        openCodeCreateDocumentRequest.setEmployeeFiscalIdType(mapToOpenCodeFiscalIdType(meta4Document.getEmployeeFiscalIdType(), properties));

//        System.out.println("-------: " + meta4Document.getEmployeeFiscalId());
        openCodeCreateDocumentRequest.setEmployeeFiscalId(meta4Document.getEmployeeFiscalId());

//        System.out.println("-------: " + meta4Document.getDocumentType());
        openCodeCreateDocumentRequest.setDocumentType(meta4Document.getDocumentType());

//        System.out.println("-------: " + meta4Document.getDate());
        openCodeCreateDocumentRequest.setDate(convertToOpenCodeDateFormat(meta4Document.getDate(), properties));


        openCodeCreateDocumentRequest.setDocumentBase64EncodedData(base64EncodedFileData);
    }

    private static String mapToOpenCodeFiscalIdType(String meta4FiscalIdType, ApplicationProperties properties) throws CustomRuntimeException {
        try {
            String opencodeFiscalIdType = properties.getMeta4ToOpenCodeFiscalIdTypeMapping().get(meta4FiscalIdType);
            if(opencodeFiscalIdType == null || opencodeFiscalIdType.equals("")) {
                throw new CustomRuntimeException("Failed to map a Meta4 fiscal id type to a OpenCode fiscal id type. No mapping found. Meta4 fiscal id type: " + meta4FiscalIdType);
            }
            return opencodeFiscalIdType;
        }
        catch(Exception e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed to map a Meta4 fiscal id type to a OpenCode fiscal id type. Meta4 fiscal id type: " + meta4FiscalIdType);
        }
    }

    private static String mapToOpenCodeDocumentType(String meta4DocumentType, ApplicationProperties properties) throws CustomRuntimeException {
        try {
            String opencodeDocumentType = properties.getMeta4ToOpenCodeDocumentTypeMapping().get(meta4DocumentType);
            if(opencodeDocumentType == null || opencodeDocumentType.equals("")) {
                throw new CustomRuntimeException("Failed to map a Meta4 document type to a OpenCode document type. No mapping found. Meta4 document type: " + meta4DocumentType);
            }
            return opencodeDocumentType;
        }
        catch(Exception e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed to map a Meta4 document type to a OpenCode document type. Meta4 document type: " + meta4DocumentType);
        }
    }

    private static String convertToOpenCodeDateFormat(Date date, ApplicationProperties properties) throws CustomRuntimeException {
        try {
            DateFormat format = new SimpleDateFormat(properties.getOpenCodeDateFormat());
            String dateString = format.format(date);
            return dateString;
        }
        catch(Exception e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed to convert date to OpenCode´s date format.");
        }
    }

}
