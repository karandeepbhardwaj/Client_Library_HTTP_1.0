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

            request += "GET " +attributesObj.getPath()+" HTTP/1.0\r\n";
            request += "Host: "+attributesObj.getHost()+"\r\n";

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

            while ((ResponseLine = bufferReader.readLine())!= null){

                if (ResponseLine.trim().isEmpty()) {
                    isVerbose = false;
                    continue;
                }
                if (!isVerbose) {
                    response += ResponseLine+"\n";	}
            }

            if(response.isEmpty() || (!response.substring(9, 12).equals("302")
                    && !response.substring(9, 12).equals("301"))) {

                System.out.println(response);

                if (attributesObj.getFileForHttpResponse() != null) {
                    saveResponse(attributesObj.getFileForHttpResponse(), response);
                    write.close();
                }
            }else {

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


    }

    private void redirect(CAttributes attributesObj, String response) {
        // TODO Auto-generated method stub

    }

    private void saveResponse(String fileForHttpResponse, String response) throws IOException {

        if(openFile == 1) {
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
            headers.forEach((key,value) -> {
                request += key+": "+value+"\r\n";
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postRequest(CAttributes attributesObj) {
    }

    public void verbosePostRequest(CAttributes attributesObj) {
    }
}