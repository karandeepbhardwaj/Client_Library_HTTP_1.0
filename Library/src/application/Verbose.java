package application;

import configuration.CAttributes;

import java.io.*;
import java.net.Socket;

public class Verbose {

    public static Generator obj;
    public static String request = obj.request;

    public void verboseGetRequest(CAttributes attributesObj) throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(attributesObj.getHost(), attributesObj.getPort());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            request += "GET " + attributesObj.getPath() + " HTTP/1.0\r\n";
            request += "Host: " + attributesObj.getHost() + "\r\n";
            if (attributesObj.getHeaders() != null) {
                obj.addHeaders(attributesObj.getHeaders());
            }
            request += "\r\n";

            bufferWriter.write(request);
            bufferWriter.flush();

            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = "";
            String line;

            while ((line = bufferReader.readLine()) != null) {

                response += line + "\n";
            }

            if (response.isEmpty() || (!response.substring(9, 12).equals("302")
                    && !response.substring(9, 12).equals("301"))) {
                System.out.println(response);

                if (attributesObj.getFileForHttpResponse() != null) {
                    obj.saveResponse(attributesObj.getFileForHttpResponse(), response);
                    obj.write.close();
                }
            } else {
                obj.redirect(attributesObj, response);
                if (attributesObj.getFileForHttpResponse() != null) {
                    obj.saveResponse(attributesObj.getFileForHttpResponse(), response);
                    obj.write.write("\nRedirecting to... http://" + attributesObj.getHost() + attributesObj.getPath() + "\n\n");
                }
                verboseGetRequest(attributesObj);
                obj.write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    public void verbosePostRequest(CAttributes attributesObj) throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(attributesObj.getHost(), attributesObj.getPort());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            request += "POST " + attributesObj.getPath() + " HTTP/1.0\r\n";
            request += "Host: " + attributesObj.getHost() + "\r\n";
            if (attributesObj.getHeaders() != null) {
                obj.addHeaders(attributesObj.getHeaders());
            }
            if (attributesObj.getInlineData() != null) {
                request += "Content-Length:" + attributesObj.getInlineData().length() + "\r\n";
            }
            request += "Connection: close\r\n";
            request += "\r\n";
            if (attributesObj.getInlineData() != null) {
                request += attributesObj.getInlineData();
            }

            bufferWriter.write(request);
            bufferWriter.flush();

            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = "";
            String line;

            while ((line = bufferReader.readLine()) != null) {

                response += line + "\n";
            }

            System.out.println(response);
            if (attributesObj.getFileForHttpResponse() != null) {
                obj.saveResponse(attributesObj.getFileForHttpResponse(), response);
                obj.write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

}
