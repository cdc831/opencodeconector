package com.istorming.dds.meta4_to_opencode_connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.Thread.sleep;

/*
 ** Clase Delete
 */
public class DeleteOpenCodeDocumentTask implements Runnable {

    final static Logger logger = Logger.getLogger(CreateOpenCodeDocumentTask.class);

    private Meta4Document meta4Document;
    private ApplicationProperties properties;

    public DeleteOpenCodeDocumentTask(Meta4Document meta4Document, ApplicationProperties properties) {
        this.meta4Document = meta4Document;
        this.properties = properties;
    }

    @Override
    public void run() {

        logger.info("Processing document deletion task... Meta4 document id: " + meta4Document.getId());

        //Build OpenCode delete document request
        OpenCodeDeleteDocumentRequest openCodeRequestBody = new OpenCodeDeleteDocumentRequest(this.meta4Document.getOpenCodeId());

        // Serialize the OpenCode request to JSON
        ObjectMapper mapper = new ObjectMapper();
        String openCodeRequestJSONStringBody;
        try {
            openCodeRequestJSONStringBody = mapper.writeValueAsString(openCodeRequestBody);
            logger.info("OpenCode request successfully serialized. Meta4 document id: " + meta4Document.getId());
        } catch (JsonProcessingException e) {
            logger.error(e);
            logger.error("Failed to serialize OpenCode´s request. Document skipped. Meta4 document id: " + meta4Document.getId());
            return;
        }

        // Invoke OpenCode´s API for creating the file
        CloseableHttpClient client = HttpClients.createDefault();
        // Agregado 15-10-2020 Cristian, autentificacion para el servicio ------------------------
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("prosegurws", "asRExrQQ5FaQWXcEg2tf"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        // ----------------------------------------------------------------------------------------
        HttpPost httpPost = new HttpPost(properties.getOpencodeDeleteDocumentEndpointUrl());
        StringEntity entity = null;
        try {
            entity = new StringEntity(openCodeRequestJSONStringBody);
            logger.info("OpenCode request successfully encoded. Meta4 document id: " + meta4Document.getId());
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            logger.error("Failed to encode OpenCode´s request. Document skipped. Meta4 document id: " + meta4Document.getId());
            try {
                client.close();
            } catch (IOException ex) {
                logger.warn(e);
                logger.warn("Failed to close http connection. Error ignored. Meta4 document id: " + meta4Document.getId());
            }
            return;
        }
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = null;
        try {
            logger.info("Sending delete document request to OpenCode... Meta4 document id: " + meta4Document.getId());
            response = client.execute(httpPost);
            logger.info("Response received from OpenCode. Analyzing the results... Meta4 document id: " + meta4Document.getId());
        } catch (Exception e) {
            logger.error(e);
            logger.error("Failed to delete the document from OpenCode. Meta4 document id: " + meta4Document.getId());
            try {
                client.close();
            } catch (IOException ex) {
                logger.warn(e);
                logger.warn("Failed to close http connection. Error ignored. Meta4 document id: " + meta4Document.getId());
            }
            return;
        }
        int statusCode = response.getStatusLine().getStatusCode();
        BufferedReader rd = null;
        String jsonResponse = "";
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            jsonResponse = result.toString();
            logger.info("OpenCode response received. Response: " + jsonResponse + ". Meta4 document id: " + meta4Document.getId());
        } catch (Exception e) {
            logger.error(e);
            if (statusCode == 200 || statusCode == 201) {
                logger.error("Failed to read OpenCode´s response. Although OpenCode returned a success status code. The document may have been successfully deleted from OpenCode. Unable to update Meta4´s database with any results data. Http status code: " + statusCode + " Meta4 document id: " + meta4Document.getId());
            } else {
                logger.error("Failed to read OpenCode´s response. OpenCode returned an error status code. It appears the document wasn´t deleted from OpenCode. Unable to update Meta4´s database with any results data. Http status code:" + statusCode + " Meta4 document id: " + meta4Document.getId());
            }
            try {
                client.close();
            } catch (IOException ex) {
                logger.warn(e);
                logger.warn("Failed to close http connection. Error ignored. Meta4 document id: " + meta4Document.getId());
            }
            return;
        }
        try {
            client.close();
        } catch (IOException e) {
            logger.warn(e);
            logger.warn("Failed to close http connection to OpenCode. Error ignored. Meta4 document id: " + meta4Document.getId());
        }

        // Deserialize OpenCode´s response
        OpenCodeDeleteDocumentResponse responseContent = null;
        mapper = new ObjectMapper();
        try {
            responseContent = mapper.readValue(jsonResponse, OpenCodeDeleteDocumentResponse.class);
            logger.info("OpenCode response successfully deserialized. Meta4 document id: " + meta4Document.getId());
        } catch (IOException e) {
            logger.error(e);
            logger.error("Failed to deserialize OpenCode´s response. Unable to update Meta4´s database with any results data. Meta4 document id: " + meta4Document.getId());
            return;
        }

        // Analyze OpenCode´s response status code and initialize Meta4's query for updating Meta4 with the results
        String meta4UpdateQueryString = "";
        if (statusCode == 200 || statusCode == 201) {
            logger.info("OpenCode responded with a successfull http status code when attempting to delete a file. Udating Meta4's database with the result... Http status code: " + statusCode + " Meta4 document id: " + meta4Document.getId());
            meta4UpdateQueryString = properties.getMeta4DbUpdateDeletedDocumentRecordSuccessQueryString();
            meta4UpdateQueryString = meta4UpdateQueryString.replace("{{meta4_document_id}}", meta4Document.getId());
            meta4UpdateQueryString = meta4UpdateQueryString.replace("{{opencode_document_id}}", responseContent.getDocumentId());
            meta4UpdateQueryString = meta4UpdateQueryString.replace("{{opencode_response_code}}", responseContent.getCode());
            meta4UpdateQueryString = meta4UpdateQueryString.replace("{{opencode_response_description}}", responseContent.getDescription());
        } else {
            logger.error("OpenCode responded with an error http status code when attempting to delete a file. Udating Meta4's database with the result... Http status code: " + statusCode + " Meta4 document id: " + meta4Document.getId());
            meta4UpdateQueryString = properties.getMeta4DbUpdateDeletedDocumentRecordFailureQueryString();
            meta4UpdateQueryString = meta4UpdateQueryString.replace("{{meta4_document_id}}", meta4Document.getId());
            meta4UpdateQueryString = meta4UpdateQueryString.replace("{{opencode_response_code}}", responseContent.getCode());
            meta4UpdateQueryString = meta4UpdateQueryString.replace("{{opencode_response_description}}", responseContent.getDescription());
        }

        // Update document´s status at Meta4´s db
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load the Oracle´s connector dependency. Unable to update Meta4's database with OpenCode results. Meta4 document id: " + meta4Document.getId());
            return;
        }

        // Try to connect to Meta4's db
        Connection connection = null;
        int attempts = 0;
        int maxAttempts = properties.getMeta4DbMaxConnectionAttempts();
        long waitTime = properties.getMeta4DbMaxConnectionAttemptsWaitTime();
        while (connection == null) {
            attempts++;
            try {
                connection = DriverManager.getConnection(properties.getMeta4DbConnectionString(), properties.getMeta4DbUsername(), properties.getMeta4DbPassword());
            } catch (SQLException e) {
                if(attempts <= maxAttempts) {
                    logger.warn("Failed to connect to Meta4's database for updating it with OpenCode results. Will attempt to connect again later. Meta4 document id: " + meta4Document.getId() + ". Attempt " + String.valueOf(attempts) + " of " + String.valueOf(maxAttempts) + ". Waiting " + String.valueOf(waitTime) + "ms before trying to connect again.");
                    try {
                        sleep(100);
                    } catch (InterruptedException e1) {
                        logger.error(e);
                        logger.error("Failed to connect to Meta4's database. Failed when waiting for a new connection attempt. Meta4 document id: " + meta4Document.getId());
                        return;
                    }
                }
                else {
                    logger.error(e);
                    logger.error("Failed to connect to Meta4's database. Unable to update Meta4's db with OpenCode results. Meta4 document id: " + meta4Document.getId());
                    return;
                }
            }
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            logger.error(e);
            try {
                connection.close();
            } catch (SQLException e1) { }
            logger.error("Failed to create a database statement against the Meta4´s database. Unable to update Meta4's db with OpenCode results. Meta4 document id: " + meta4Document.getId());
            return;
        }
        try {
            statement.execute(meta4UpdateQueryString);

            // Close the Meta4 db connection
            statement.close();
            connection.close();

        } catch (SQLException e) {
            logger.error(e);
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
            logger.error("Failed to update Meta4´s database. Unable to update Meta4's db with OpenCode results. Meta4 document id: " + meta4Document.getId());
        }

        logger.info("Document deletion successfully completed. Meta4 document id: " + meta4Document.getId());

    }

}