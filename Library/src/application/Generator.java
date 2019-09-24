package application;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

import configuration.*;

public class Generator {

    private String request = null;
    private int openFile = 0;
    private BufferedWriter write;

    public void getRequest(CAttributes attributesObj) throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(attributesObj.getHost(), attributesObj.getPort());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            request += "GET " + attributesObj.getPath() + " HTTP/1.0\r\n";
            request += "Host: " + attributesObj.getHost() + "\r\n";

            if (attributesObj.getHeaders() != null) {
                addHeaders(attributesObj.getHeaders());
            }
            request += "\r\n";

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
            String ResponseLine;
            boolean isVerbose = true;

            while ((ResponseLine = bufferReader.readLine()) != null) {

                if (ResponseLine.trim().isEmpty()) {
                    isVerbose = false;
                    continue;
                }
                if (!isVerbose) {
                    response += ResponseLine + "\n";
                }
            }

            if (response.isEmpty() || (!response.substring(9, 12).equals("302")
                    && !response.substring(9, 12).equals("301"))) {

                System.out.println(response);

                if (attributesObj.getFileForHttpResponse() != null) {
                    saveResponse(attributesObj.getFileForHttpResponse(), response);
                    write.close();
                }
            } else {

                redirect(attributesObj, response);

                if (attributesObj.getFileForHttpResponse() != null) {
                    saveResponse(attributesObj.getFileForHttpResponse(), response);
                    write.write("\nRedirecting to another website... http://" + attributesObj.getHost()
                            + attributesObj.getPath() + "\n\n");
                }
                getRequest(attributesObj);
                write.close();
            }

        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }

    }

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
                addHeaders(attributesObj.getHeaders());
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
                    saveResponse(attributesObj.getFileForHttpResponse(), response);
                    write.close();
                }
            } else {
                redirect(attributesObj, response);
                if (attributesObj.getFileForHttpResponse() != null) {
                    saveResponse(attributesObj.getFileForHttpResponse(), response);
                    write.write("\nRedirecting to... http://" + attributesObj.getHost() + attributesObj.getPath() + "\n\n");
                }
                verboseGetRequest(attributesObj);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    private void redirect(CAttributes attributesObj, String response) {
        // TODO Auto-generated method stub

        System.out.println(response);
        System.out.println("\nRedirecting to... http://" + attributesObj.getHost() + attributesObj.getPath() + "\n\n");
        int index1 = response.indexOf("Location");
        int index2 = response.indexOf("\n", index1);
        String newURL = response.substring(index1 + 10, index2);

        //check if URL starts with http:// or http://
        if (newURL.startsWith("http://"))
            newURL = newURL.substring(7);
        else if (newURL.startsWith("https://"))
            newURL = newURL.substring(8);
        else if (newURL.startsWith("'https://"))
            newURL = newURL.substring(9, newURL.length() - 1);
        else if (newURL.startsWith("'http://"))
            newURL = newURL.substring(8, newURL.length() - 1);

        //checking first occurence of '/' in the string without http:// or https://
        int index3 = newURL.indexOf('/');
        if (index3 == -1)
            index3 = newURL.indexOf(".com") + 4;

        //splitting the string into host, path based on index of '/'
        if (index3 != -1) {
            attributesObj.setHost(newURL.substring(0, index3));
            attributesObj.setPath(newURL.substring(index3));
        } else {
            attributesObj.setHost(newURL);
            attributesObj.setPath("/");
        }
    }

    private void saveResponse(String fileForHttpResponse, String response) throws IOException {

        if (openFile == 1) {
            write = new BufferedWriter(new FileWriter(new File(fileForHttpResponse)));
            openFile = 1;
        }
        try {
            write.write(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addHeaders(HashMap<String, String> headers) {
        // TODO Change the way the value is calculated in the loop.
        try {
            headers.forEach((key, value) -> {
                request += key + ": " + value + "\r\n";
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postRequest(CAttributes attributesObj) throws IOException {

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
                addHeaders(attributesObj.getHeaders());
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
            boolean isVerbose = true;

            while ((line = bufferReader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    isVerbose = false;
                    continue;
                }
                if (!isVerbose) {
                    response += line + "\n";
                }
            }

            System.out.println(response);

            if (attributesObj.getFileForHttpResponse() != null) {
                saveResponse(attributesObj.getFileForHttpResponse(), response);
                write.close();
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
                addHeaders(attributesObj.getHeaders());
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
                saveResponse(attributesObj.getFileForHttpResponse(), response);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }
}