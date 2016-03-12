/**
 * Copyright
 */
package com.kyanlife.code.evolis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;
import java.net.*;

import com.fasterxml.jackson.databind.*;

/**
 * This is the ESPFRequestToolController.
 * This class handles all events from the FX form.
 */
public class ESPFRequestToolController {

    @FXML private TextArea requestText;
    @FXML private TextArea responseText;
    @FXML private TextField evolisServerName;
    @FXML private TextField evolisServerPort;

    private int requestCounter = 1;


    @FXML protected void sendSingleRequest(ActionEvent event) {
        String responseString;
        try {

            // get server name, throw exception if name is empty
            String evolisServer = evolisServerName.getText().trim();
            if (evolisServer.length() < 1) {
                throw new Exception("Invalid server name/IP!");
            }

            // get server port, throw exception if port is not a valid integer
            Integer evolisPort = parseServerPort();
            if (evolisPort < 1) {
                throw new Exception("Invalid port number!");
            }


            // get request string, throw exception if request is empty
            String evolisJSONRequest = requestText.getText().trim();
            if ( evolisJSONRequest.length() < 2 ) {
                throw new Exception("Invalid request!");
            }

            responseString = sendRequestToServer(evolisServer, evolisPort, evolisJSONRequest);
        } catch (Exception e) {
            responseString = e.toString();
        }

        responseText.setText(responseString);
    }
    @FXML protected void clearResponse(ActionEvent event) {
        responseText.setText("");
    }

    @FXML protected void sendMultipleRequests(ActionEvent event) {
        StringBuilder responseString = new StringBuilder();
        try {

            String jobNumber = null;

            // get server name, throw exception if name is empty
            final String evolisServer = evolisServerName.getText().trim();
            if (evolisServer.length() < 1) {
                throw new Exception("Invalid server name/IP!");
            }

            // get server port, throw exception if port is not a valid integer
            final Integer evolisPort = parseServerPort();
            if (evolisPort < 1) {
                throw new Exception("Invalid port number!");
            }

            String testFile = "/com/kyanlife/code/evolis/SampleRequestBatch1.xml";
            if ( requestCounter % 2 == 0 ) {
                testFile = "/com/kyanlife/code/evolis/SampleRequestBatch2.xml";
            }
            requestCounter++;

            System.out.println("Sending test:" + testFile);

            DocumentBuilder xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDocument = xmlDocBuilder.parse(
                    this.getClass()
                            .getResourceAsStream(testFile));

            // execute all requests
            NodeList allNodes = xmlDocument.getElementsByTagName("request");
            for (int i=0; i<allNodes.getLength(); i++) {

                String requestString = allNodes.item(i).getTextContent();
                // replace and update job number obtained from previous request
                if ( jobNumber != null ) {
                    requestString = requestString.replace("JOB000001", jobNumber);
                }

                ObjectMapper jsonMapper = new ObjectMapper();

                // map response into Java object
                ESPFResponse responseObj = jsonMapper.readValue(
                        sendRequestToServer(evolisServer, evolisPort, requestString)
                        , ESPFResponse.class);

                if ( jobNumber == null && responseObj.getResult().startsWith("JOB") ) {
                    jobNumber = responseObj.getResult();
                }

                responseString.append(responseObj.toString());
                responseString.append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseString.append(e.toString());
        }

        responseText.setText(responseString.toString());
    }

    /**
     * Send request to Evolis SDK server
     *
     * @return server response
     */
    private String sendRequestToServer(String serverName, Integer portNumber, String requestString) {
        StringBuilder sdkResponse = new StringBuilder();

        //sdkResponse.append(requestText.getText());

        try {


            Socket socket;

            // create socket connection to server
            socket = new Socket(serverName, portNumber);
            socket.setSoTimeout(5000);

            // create response reader
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            // create request output stream
            PrintWriter requestStream = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);

            // send request to server
            requestStream.print(requestString); // Send command
            requestStream.flush();

            socket.shutdownOutput(); // The printer sends the answer only if the
            // output port is closed

            // read response from server
            boolean hasMoreResponse = true;
            while ( hasMoreResponse ) // Srs command don't answers
            {
                String responsePart = responseReader.readLine(); // reading of the answer
                if ( responsePart == null ) {
                    hasMoreResponse = false;
                } else {
                    sdkResponse.append(responsePart).append("\n");
                }
            }

            // close connections
            requestStream.close();
            responseReader.close();
            socket.close();

        } catch (Exception e) {
            sdkResponse.append(e.toString());
        }

        return sdkResponse.toString();
    }

    /**
     * Get server port
     *
     * @return server port
     */
    private Integer parseServerPort() {
        Integer evolisPort = -1;

        try {
            evolisPort = Integer.parseInt(evolisServerPort.getText());
        } catch (Exception e) {
            // do nothing
        }

        return evolisPort;
    }

}
