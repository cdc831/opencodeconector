package com.istorming.dds.meta4_to_opencode_connector;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 ** Clase Principal
 */

public class Meta4ToOpenCodeConnectorApp
{
    private static final String  CREATE_DOCUMENT_EXECUTION_MODE = "create-document";
    private static final String  DELETE_DOCUMENT_EXECUTION_MODE = "delete-document";

    static final Logger logger = Logger.getLogger(Meta4ToOpenCodeConnectorApp.class);

    public static void main(String[] args)
    {
        logger.info("_= ----------------------------------------------------------------------------------------- =_");
        logger.info("Starting up...");

        // Load application properties
        Properties propertiesFileData = new Properties();
        InputStream propertiesFileInputStream = null;
        String propertiesFileName = "config.properties";
        File externalConfigFile = new File("./" + propertiesFileName);
        if (externalConfigFile.exists()) {
            try {
                propertiesFileInputStream = new FileInputStream("./" + propertiesFileName);
            } catch (FileNotFoundException e) {
                logger.fatal("Failed to open and load the config.properties file. Execution aborted!");
                return;
            }
        }
        else {
            propertiesFileInputStream = Meta4ToOpenCodeConnectorApp.class.getClassLoader().getResourceAsStream(propertiesFileName);
        }
        try {
            propertiesFileData.load(propertiesFileInputStream);
            propertiesFileInputStream.close();
            logger.info("Application.properties file successfully found and loaded.");
        } catch (IOException e) {
            logger.fatal("Failed to open and load the config.properties file. Execution aborted!");
            return;
        }
        ApplicationProperties properties = null;
        try {
            properties = new ApplicationProperties(propertiesFileData);
            logger.info("Configuration properties successfully parsed and validated.");
        }
        catch(CustomRuntimeException e) {
            logger.fatal(e.getErrorMessage());
            return;
        }
        catch(Exception e) {
            logger.error(e);
            logger.fatal("Failed to load the config.properties file. Execution aborted!");
            return;
        }

        // Check the execution mode and initialize the db query
        String executionMode;
        String meta4SelectQueryString;
        String argument = "create";
        if(argument.equals("create")) {
        //if(args[0].equals(properties.getCreateDocumentsActionName())) {
            executionMode = CREATE_DOCUMENT_EXECUTION_MODE;
            meta4SelectQueryString = properties.getMeta4DbCreateDocumentsSelectQueryString();
//            System.out.println(meta4SelectQueryString);
            logger.info("Create document mode detected.");
        }
        else if (args[0].equals(properties.getDeleteDocumentsActionName())) {
            executionMode = DELETE_DOCUMENT_EXECUTION_MODE;
            meta4SelectQueryString = properties.getMeta4DbDeleteDocumentsSelectQueryString();
            logger.info("Delete document mode detected.");
        }
        else {
            logger.fatal("Unknown execution mode argument. Execution aborted!");
            return;
        }

        // Connect to the Meta4 db and look for files to process
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            logger.fatal("Failed to load the Oracle´s connector dependency. Execution aborted!");
            return;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(properties.getMeta4DbConnectionString(), properties.getMeta4DbUsername(), properties.getMeta4DbPassword());
        } catch (SQLException e) {
            logger.error(e);
            logger.fatal("Failed to connect to Meta4's database. Execution aborted!");
            return;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            logger.error(e);
            try {
                connection.close();
            } catch (SQLException e1) { }
            logger.fatal("Failed to create a database statement against the Meta4's database. Execution aborted!");
            return;
        }
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(meta4SelectQueryString);
            logger.info("Data successfully retrieved from Meta4´s database.");
        } catch (SQLException e) {
            logger.error(e);
            try {
                statement.close();
            } catch (SQLException e1) { }
            try {
                connection.close();
            } catch (SQLException e1) { }
            logger.fatal("Failed to query the Meta4's database. Execution aborted!");
            return;
        }

        try {
            if (resultSet.next()) { // Check if the result set has content

                // Get the result set size and reset the iterator
                resultSet.beforeFirst();
                resultSet.last();
                Integer resultsCount = resultSet.getRow();
                resultSet.beforeFirst();
                logger.info(resultsCount.toString() + " records found.");

                //Initialize the thread pool
                Integer threadPoolSize = properties.getThreadPoolMaxSize();
                if(threadPoolSize > resultsCount) {
                    threadPoolSize = resultsCount;
                }
                ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
                logger.info("Thread pool successfully initialized. Pool size: " + threadPoolSize.toString());

                // Iterate through the results and submit the create/delete task for execution
                while (resultSet.next()) {
//                    System.out.println(resultSet.getString("COMPANY_FISCAL_ID_TYPE")); //<-------------------------------------------------
                    // Get the document data from the db record
                    Meta4Document meta4Document = null;
                    boolean continueWithTaskQueuing = true;
                    try {
                        meta4Document = ResultSetToMeta4DocumentBuilder.build(resultSet, properties);
//                        System.out.println(meta4Document.toString());
                        logger.info("Document data successfully validated. Meta4 document id: " + meta4Document.getId());
                        logger.info("--------------------------------------------");
                        logger.info("Datos obtenidos: " + meta4Document.toString());
                        logger.info("--------------------------------------------");
                    }
                    catch(CustomRuntimeException e) {
                        continueWithTaskQueuing = false;
                        logger.error(e.getErrorMessage());
                        if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                            logger.error("Unable to queue the worker for creating the document. Document skipped. Meta4 document id: unknown");
                        }
                        else {
                            logger.error("Unable to queue the worker for deleting the document. Document skipped. Meta4 document id: unknown");
                        }
                    }
                    catch(Exception e) {
                        continueWithTaskQueuing = false;
                        logger.error(e);
                        if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                            logger.error("Unable to queue the worker for creating the document. Document processing skipped. Meta4 document id: unknown");
                        }
                        else {
                            logger.error("Unable to queue the worker for deleting the document. Document processing skipped. Meta4 document id: unknown");
                        }
                    }

                    if(continueWithTaskQueuing) {
                        try {
                            if (executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                                //Submit the task for processing the document creation to the thread pool
                                executor.execute(new CreateOpenCodeDocumentTask(meta4Document, properties));
                                logger.info("OpenCode document creation worker successfully queued. Meta4 document id: " + meta4Document.getId());
                            } else { // Delete document
                                //Submit the task for processing the document deletion to the thread pool
                                executor.execute(new DeleteOpenCodeDocumentTask(meta4Document, properties));
                                logger.info("OpenCode document deletion worker successfully queued. Meta4 document id: " + meta4Document.getId());
                            }
                        }
                        catch (Exception e) {
                            logger.error(e);
                            if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                                logger.error("Unable to queue the worker for creating the document. Document processing skipped. Meta4 document id: " + meta4Document.getId());
                            }
                            else {
                                logger.error("Unable to queue the worker for deleting the document. Document processing skipped. Meta4 document id: " + meta4Document.getId());
                            }
                        }
                    }
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Close the Meta4 db connection
                resultSet.close();
                statement.close();
                connection.close();
            }
            else {
                if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document mode
                    logger.info("No records for documents pending to be created in OpenCode were found.");
                }
                else {
                    logger.info("No records for documents pending to be deleted at OpenCode were found.");
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e1) { }
            try {
                if(statement != null) {
                    statement.close();
                }
            } catch (SQLException e1) { }
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e1) { }
            if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document mode
                logger.fatal("Failed to create documents in OpenCode. Execution aborted!");
            }
            else {
                logger.fatal("Failed to delete documents at OpenCode. Execution aborted!");
            }
            return;
        }

        logger.info("Done!");
    }

    public void ejecutar(String accion){
        logger.info("Starting up...");

        // Load application properties
        Properties propertiesFileData = new Properties();
        InputStream propertiesFileInputStream = null;
        String propertiesFileName = "config.properties";
        File externalConfigFile = new File("./" + propertiesFileName);
        if (externalConfigFile.exists()) {
            try {
                propertiesFileInputStream = new FileInputStream("./" + propertiesFileName);
            } catch (FileNotFoundException e) {
                logger.fatal("Failed to open and load the config.properties file. Execution aborted!");
                return;
            }
        }
        else {
            propertiesFileInputStream = Meta4ToOpenCodeConnectorApp.class.getClassLoader().getResourceAsStream(propertiesFileName);
        }
        try {
            propertiesFileData.load(propertiesFileInputStream);
            propertiesFileInputStream.close();
            logger.info("Application.properties file successfully found and loaded.");
        } catch (IOException e) {
            logger.fatal("Failed to open and load the config.properties file. Execution aborted!");
            return;
        }
        ApplicationProperties properties = null;
        try {
            properties = new ApplicationProperties(propertiesFileData);
            logger.info("Configuration properties successfully parsed and validated.");
        }
        catch(CustomRuntimeException e) {
            logger.fatal(e.getErrorMessage());
            return;
        }
        catch(Exception e) {
            logger.error(e);
            logger.fatal("Failed to load the config.properties file. Execution aborted!");
            return;
        }

        // Check the execution mode and initialize the db query
        String executionMode;
        String meta4SelectQueryString;
        if(accion.equals(properties.getCreateDocumentsActionName())) {
            executionMode = CREATE_DOCUMENT_EXECUTION_MODE;
            meta4SelectQueryString = properties.getMeta4DbCreateDocumentsSelectQueryString();
            logger.info("Create document mode detected.");
        }
        else if (accion.equals(properties.getDeleteDocumentsActionName())) {
            executionMode = DELETE_DOCUMENT_EXECUTION_MODE;
            meta4SelectQueryString = properties.getMeta4DbDeleteDocumentsSelectQueryString();
            logger.info("Delete document mode detected.");
        }
        else {
            logger.fatal("Unknown execution mode argument. Execution aborted!");
            return;
        }

        // Connect to the Meta4 db and look for files to process
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            logger.fatal("Failed to load the Oracle´s connector dependency. Execution aborted!");
            return;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(properties.getMeta4DbConnectionString(), properties.getMeta4DbUsername(), properties.getMeta4DbPassword());
        } catch (SQLException e) {
            logger.error(e);
            logger.fatal("Failed to connect to Meta4's database. Execution aborted!");
            return;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            logger.error(e);
            try {
                connection.close();
            } catch (SQLException e1) { }
            logger.fatal("Failed to create a database statement against the Meta4's database. Execution aborted!");
            return;
        }
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(meta4SelectQueryString);
            logger.info("Data successfully retrieved from Meta4´s database.");
        } catch (SQLException e) {
            logger.error(e);
            try {
                statement.close();
            } catch (SQLException e1) { }
            try {
                connection.close();
            } catch (SQLException e1) { }
            logger.fatal("Failed to query the Meta4's database. Execution aborted!");
            return;
        }

        try {
            if (resultSet.next()) { // Check if the result set has content

                // Get the result set size and reset the iterator
                resultSet.beforeFirst();
                resultSet.last();
                Integer resultsCount = resultSet.getRow();
                resultSet.beforeFirst();
                logger.info(resultsCount.toString() + " records found.");

                //Initialize the thread pool
                Integer threadPoolSize = properties.getThreadPoolMaxSize();
                if(threadPoolSize > resultsCount) {
                    threadPoolSize = resultsCount;
                }
                ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
                logger.info("Thread pool successfully initialized. Pool size: " + threadPoolSize.toString());

                // Iterate through the results and submit the create/delete task for execution
                while (resultSet.next()) {
                    // Get the document data from the db record
                    Meta4Document meta4Document = null;
                    boolean continueWithTaskQueuing = true;
                    try {
                        meta4Document = ResultSetToMeta4DocumentBuilder.build(resultSet, properties);
                        logger.info("Document data successfully validated. Meta4 document id: " + meta4Document.getId());
                    }
                    catch(CustomRuntimeException e) {
                        continueWithTaskQueuing = false;
                        logger.error(e.getErrorMessage());
                        if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                            logger.error("Unable to queue the worker for creating the document. Document skipped. Meta4 document id: unknown");
                        }
                        else {
                            logger.error("Unable to queue the worker for deleting the document. Document skipped. Meta4 document id: unknown");
                        }
                    }
                    catch(Exception e) {
                        continueWithTaskQueuing = false;
                        logger.error(e);
                        if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                            logger.error("Unable to queue the worker for creating the document. Document processing skipped. Meta4 document id: unknown");
                        }
                        else {
                            logger.error("Unable to queue the worker for deleting the document. Document processing skipped. Meta4 document id: unknown");
                        }
                    }

                    if(continueWithTaskQueuing) {
                        try {
                            if (executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                                //Submit the task for processing the document creation to the thread pool
                                executor.execute(new CreateOpenCodeDocumentTask(meta4Document, properties));
                                logger.info("OpenCode document creation worker successfully queued. Meta4 document id: " + meta4Document.getId());
                            } else { // Delete document
                                //Submit the task for processing the document deletion to the thread pool
                                executor.execute(new DeleteOpenCodeDocumentTask(meta4Document, properties));
                                logger.info("OpenCode document deletion worker successfully queued. Meta4 document id: " + meta4Document.getId());
                            }
                        }
                        catch (Exception e) {
                            logger.error(e);
                            if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document
                                logger.error("Unable to queue the worker for creating the document. Document processing skipped. Meta4 document id: " + meta4Document.getId());
                            }
                            else {
                                logger.error("Unable to queue the worker for deleting the document. Document processing skipped. Meta4 document id: " + meta4Document.getId());
                            }
                        }
                    }
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Close the Meta4 db connection
                resultSet.close();
                statement.close();
                connection.close();
            }
            else {
                if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document mode
                    logger.info("No records for documents pending to be created in OpenCode were found.");
                }
                else {
                    logger.info("No records for documents pending to be deleted at OpenCode were found.");
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e1) { }
            try {
                if(statement != null) {
                    statement.close();
                }
            } catch (SQLException e1) { }
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e1) { }
            if(executionMode.equals(CREATE_DOCUMENT_EXECUTION_MODE)) { // Create document mode
                logger.fatal("Failed to create documents in OpenCode. Execution aborted!");
            }
            else {
                logger.fatal("Failed to delete documents at OpenCode. Execution aborted!");
            }
            return;
        }

        logger.info("Done!");
    }

}
