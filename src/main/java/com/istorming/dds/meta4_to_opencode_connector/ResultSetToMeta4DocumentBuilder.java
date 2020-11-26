package com.istorming.dds.meta4_to_opencode_connector;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 ** Clase Principal para el Create o Delete
 */

public class ResultSetToMeta4DocumentBuilder {

    final static Logger logger = Logger.getLogger(Meta4ToOpenCodeConnectorApp.class);

    public static Meta4Document build(ResultSet resultSet, ApplicationProperties properties) {
        String id;
        try {
            id = resultSet.getString(properties.getMeta4DbDocumentIdColumnName());
        } catch (SQLException e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed get the Meta4 document id from Meta4´s database.");
        }

        String openCodeId;
        try {
            openCodeId = resultSet.getString(properties.getMeta4DbOpenCodeDocumentIdColumnName());
        } catch (SQLException e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed get the Meta4 OpenCode document id from Meta4´s database.");
        }

        String companyFiscalIdType;
        try {
            companyFiscalIdType = resultSet.getString(properties.getMeta4DbCompanyFiscalIdTypeColumnName());
//            System.out.println("companyFiscalIdType = " + companyFiscalIdType);
        } catch (SQLException e) {
            logger.info("Company fiscal id type column not found. Attempting to use the company fiscal id type from the application properties...");
            if(properties.getDefaultCompanyFiscalIdType() == null || properties.getDefaultCompanyFiscalIdType().equals("")) {
                throw new CustomRuntimeException("Failed get the Meta4 company fiscal id type from Meta4´s database and its default value from the application properties.");
            }
            companyFiscalIdType = properties.getDefaultCompanyFiscalIdType();
        }

        String companyFiscalId;
        try {
            companyFiscalId = resultSet.getString(properties.getMeta4DbCompanyFiscalIdColumnName());
//            System.out.println("companyFiscalId = " + companyFiscalId);
        } catch (SQLException e) {
            logger.info("Company fiscal id column not found. Attempting to use the company fiscal id from the application properties...");
            if(properties.getDefaultCompanyFiscalId() == null || properties.getDefaultCompanyFiscalId().equals("")) {
                throw new CustomRuntimeException("Failed get the Meta4 company fiscal id from Meta4´s database and its default from the application properties.");
            }
            companyFiscalId = properties.getDefaultCompanyFiscalId();
        }

        String remesaCod = null;
        if(properties.getSendRemesaCodOnCreateDocumentRequest()) {
            try {
                remesaCod = resultSet.getString(properties.getMeta4DbRemesaCodColumnName());
//                System.out.println("remesaCod = " + remesaCod);
            }
            catch(SQLException e) {
                logger.error(e);
                throw new CustomRuntimeException("Failed get the Meta4 remesaCod id from Meta4´s database.");
            }
        }else{
            /* Este else no estaba y rompia se agrego */
            try {
                remesaCod = resultSet.getString(properties.getMeta4DbRemesaCodColumnName());
//                System.out.println("remesaCod = " + remesaCod);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

        String employeeFiscalIdType;
        try {
            employeeFiscalIdType = resultSet.getString(properties.getMeta4DbEmployeeFiscalIdTypeColumnName());
//            System.out.println("employeeFiscalIdType = " + employeeFiscalIdType);
        }
        catch(SQLException e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed get the Meta4 employee fiscal id type from Meta4´s database.");
        }

        String employeeFiscalId;
        try {
            employeeFiscalId = resultSet.getString(properties.getMeta4DbEmployeeFiscalIdColumnName());
//            System.out.println("employeeFiscalId = " + employeeFiscalId);
        }
        catch(SQLException e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed get the Meta4 employee fiscal id from Meta4´s database.");
        }

        String documentType;
        try {
            documentType = resultSet.getString(properties.getMeta4DbDocumentTypeColumnName());
//            System.out.println("documentType = " + documentType);
        }
        catch(SQLException e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed get the Meta4 document type from Meta4´s database.");
        }

        Date date;
        //String dateString = "02/10/2020";
        try {
            String dateString = resultSet.getString(properties.getMeta4DbDateColumnName());
            //logger.info("dateString: ------> " + properties.getMeta4DbDateColumnName());
            //logger.info("DOCUMENT_DATE: ------> " + resultSet.getString("DOCUMENT_DATE"));
            SimpleDateFormat formatter = new SimpleDateFormat(properties.gtMeta4DateFormat());
            //logger.info("fecha: ------> " + properties.gtMeta4DateFormat());
            try {
                date = formatter.parse(dateString);
//                System.out.println("date = " + date);
            } catch (ParseException e) {
                throw new CustomRuntimeException("Failed get the Meta4 date from Meta4´s database. Invalid date format.");
            }
        }
        catch(SQLException e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed get the Meta4 date from Meta4´s database.");
        }

        String documentPath;
        try {
            documentPath = resultSet.getString(properties.getMeta4DbDocumentPathColumnName());
//            System.out.println("documentPath = " + documentPath);
        }
        catch(SQLException e) {
            logger.error(e);
            throw new CustomRuntimeException("Failed get the Meta4 document path from Meta4´s database.");
        }
        return new Meta4Document(id, openCodeId, companyFiscalIdType, companyFiscalId, remesaCod, employeeFiscalIdType, employeeFiscalId, documentType, date, documentPath);

    }

}
