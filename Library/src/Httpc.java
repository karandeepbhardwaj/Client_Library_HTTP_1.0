import java.io.*;
import java.net.Socket;
import java.util.*;

public class Httpc {

    private static final int DEFAULT_PORT = 80;
    private static String URL;
    private static String outputFileName;
    private static String inputFileName;
    private static String request;
    private static final String HELP = "httpc is a curl-like application but supports HTTP protocol only.\n"
            + "Usage:\nhttpc command [arguments]\n"
            + "The commands are:\n"
            + "get\texecutes a HTTP GET request and prints the response.\n"
            + "post\texecutes a HTTP POST request and prints the response.\n"
            + "help\tprints this screen.\n\n"
            + "Use \"httpc help [command]\" for more information about a command.";
    private static final String INVALID = "INVALID COMMAND LINE ARGUMENTS\n\n"
            + "For information about usage type \"httpc help\"";
    private static final String GETHELP = "httpc help get\n" +
            "usage: httpc get [-v] [-h key:value] URL\n"
            + "Get executes a HTTP GET request for a given URL.\n"
            + "-v\tPrints the detail of the response such as protocol, status, and headers.\n"
            + "-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.";
    private static final String POSTHELP = "httpc help post\n"
            + "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
            + "Post executes a HTTP POST request for a given URL with inline data or from file.\n"
            + "-v\tPrints the detail of the response such as protocol, status, and headers.\n"
            + "-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.\n"
            + "-d string\tAssociates an inline data to the body HTTP POST request.\n"
            + "-f file\tAssociates the content of a file to the body HTTP POST request.\n\n"
            + "Either [-d] or [-f] can be used but not both.";
    private static HashMap<String, String> headers = null;
    private static boolean fileOpen = false;
    private static boolean storeOutputToFile = false;
    private static BufferedWriter write;
    private static GetSet getSetObj;

    public static void main(String[] input) throws IOException {

        String testFun = "httpc get http://httpbin.org/status/418";
        String testHelp = "httpc help";
        String testHelpGet = "httpc help get";
        String testHelpPost = "httpc help post";

        String testGet = "httpc get 'http://httpbin.org/get?course=networking&assignment=1'";
        String testVerboseGet = "httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'";
        String testVerboseWithFile = "httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' -o hello.txt";
        String testPost = "httpc post -h Content-Type:application/json -d '{\"Assignment\":1}' http://httpbin.org/post";
        String testRedirection = "httpc get https://www.amazon.com/";

        String testCommand = testFun;

        input = testCommand.split("\\s+");

        getSetObj = new GetSet();
        getSetObj.setPort(DEFAULT_PORT);

        if (!input[1].equals("help")) {

            //check if output is to be stored into file
            if (input[input.length - 2].equals("-o")) {
                //getting URL i.e. the third last element of the command line arguments
                URL = input[input.length - 3];
                storeOutputToFile = true;
            } else {
                //getting URL i.e. the last element of the command line arguments
                URL = input[input.length - 1];
            }
            //split URL into host, path & query

            String str = URL;
            if (str.startsWith("http://"))
                str = str.substring(7);
            else if (str.startsWith("https://"))
                str = str.substring(8);
            else if (str.startsWith("'https://"))
                str = str.substring(9, str.length() - 1);
            else if (str.startsWith("'http://"))
                str = str.substring(8, str.length() - 1);
            else if (str.startsWith("\'"))
                str = str.substring(1, str.length() - 1);

            int index1 = str.indexOf('/');

            if (index1 != -1) {
                getSetObj.setHost(str.substring(0, index1));
                getSetObj.setPath(str.substring(index1));
            } else {
                getSetObj.setHost(URL);
            }
        }
        //parse arguments

        String[] args;
        if (storeOutputToFile) {
            outputFileName = input[input.length - 1];
            getSetObj.setFileForHttpResponse(outputFileName);
            args = Arrays.copyOf(input, input.length - 2);
        } else {
            args = input.clone();
        }
        String command = args[1];
        switch (command) {
            case "help":
                if (args.length == 2)
                    System.out.println(HELP);                    //command -> httpc help
                else if (args.length == 3 && args[2].equals("get"))
                    System.out.println(GETHELP);                //command -> httpc help get
                else if (args.length == 3 && args[2].equals("post"))
                    System.out.println(POSTHELP);                //command -> httpc help post
                else
                    System.out.println(INVALID);                //Invalid Command
                break;
            case "get":
                if (args.length == 3)
                    getRequest(getSetObj);                //command -> httpc get URL
                else if (args.length == 4)
                    getRequestWithVerbose(getSetObj);    //command -> httpc get -v URL
                else {
                    if (args[2].equals("-v")) {    //with verbose
                        int numHeaders = (args.length - 4) / 2;
                        int startIndex = 4;
                        processHeaders(args, numHeaders, startIndex);
                        getRequestWithVerbose(getSetObj); //command -> httpc get -v (-h key:value)* URL
                    } else {    //without verbose
                        int numHeaders = (args.length - 3) / 2;
                        int startIndex = 3;
                        processHeaders(args, numHeaders, startIndex);
                        getRequest(getSetObj); //command -> httpc get (-h key:value)* URL
                    }
                }
                break;
            case "post":
                if (args.length == 3)
                    postRequest(getSetObj);                //command -> httpc post URL
                else if (args.length == 4)
                    postRequestWithVerbose(getSetObj);    //command -> httpc post -v URL
                else {
                    if (args[2].equals("-v")) {    //with verbose
                        int numHeaders = 0;
                        int startIndex = 4;
                        if (args[args.length - 3].equals("-d")) {
                            getSetObj.setInlineData(args[args.length - 2]);
                            numHeaders = (args.length - 6) / 2;

                        } else if (args[args.length - 3].equals("-f")) {
                            inputFileName = args[args.length - 2];
                            File file = new File(inputFileName);
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String bufferInput = "";
                            String nextLine;
                            while ((nextLine = br.readLine()) != null) {
                                bufferInput += nextLine;
                            }
                            getSetObj.setInlineData(bufferInput);
                            br.close();
                            numHeaders = (args.length - 6) / 2;
                        } else if (args[args.length - 3].equals("-h")) {
                            numHeaders = (args.length - 4) / 2;
                        }
                        if (numHeaders > 0) {
                            processHeaders(args, numHeaders, startIndex);
                        }
                        postRequestWithVerbose(getSetObj); //command -> httpc post -v (-h key:value)* [-d] [-f] URL
                    } else {
                        //without verbose
                        int numHeaders = 0;
                        int startIndex = 3;
                        switch (args[args.length - 3]) {

                            case "-d":
                                getSetObj.setInlineData(args[args.length - 2]);
                                numHeaders = (args.length - 5) / 2;
                                break;
                            case "-f":
                                inputFileName = args[args.length - 2];
                                File file = new File(inputFileName);
                                BufferedReader br = new BufferedReader(new FileReader(file));
                                String bufferInput1 = "";
                                String nextLine;
                                while ((nextLine = br.readLine()) != null) {
                                    bufferInput1 += nextLine;
                                }
                                getSetObj.setInlineData(bufferInput1);
                                br.close();
                                numHeaders = (args.length - 5) / 2;
                                break;
                            case "-h":
                                numHeaders = (args.length - 3) / 2;
                                break;
                            default:
                                break;
                        }
                        if (numHeaders > 0) {
                            processHeaders(args, numHeaders, startIndex);
                        }
                        postRequest(getSetObj); //command -> httpc post (-h key:value)* [-d] [-f] URL
                    }
                }
                break;
            default:
                System.out.println(INVALID);
                break;
        }
    }

    private static void processHeaders(String[] args, int numHeaders, int startIndex) {
        headers = new HashMap<>();
        for (int i = 0; i < numHeaders; i++) {
            int argNumber = startIndex + i * 2;
            String[] keyValue = args[argNumber].split(":");
            headers.put(keyValue[0], keyValue[1]);
        }
        getSetObj.setHeaders(headers);
    }

    private static void getRequest(GetSet getSetObj) throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(getSetObj.getHost(), getSetObj.getPort());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a GET request
            request += "GET " + getSetObj.getPath() + " HTTP/1.0\r\n";
            request += "Host: " + getSetObj.getHost() + "\r\n";
            // adding headers
            if (getSetObj.getHeaders() != null) {
                addHeaders(getSetObj.getHeaders());
            }
            request += "\r\n";

            bufferWriter.write(request);
            bufferWriter.flush();

            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = "";
            String line;
            boolean isVerbose = true;

            // Getting response from host
            while ((line = bufferReader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    isVerbose = false;
                    continue;
                }
                if (!isVerbose) {
                    response += line + "\n";
                }
            }
            if (response.isEmpty() || (!response.substring(9, 12).equals("302") && !response.substring(9, 12).equals("301"))) {
                // print response in console
                System.out.println(response);
                // save response in external file
                if (getSetObj.getFileForHttpResponse() != null) {
                    saveResponse(getSetObj.getFileForHttpResponse(), response);
                    write.close();
                }
            } else {
                //redirecting
                redirect(getSetObj, response);
                if (getSetObj.getFileForHttpResponse() != null) {
                    saveResponse(getSetObj.getFileForHttpResponse(), response);
                    write.write("\nRedirecting to... http://" + getSetObj.getHost() + getSetObj.getPath() + "\n\n");
                }
                getRequest(getSetObj);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    private static void postRequest(GetSet getSetObj) throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(getSetObj.getHost(), getSetObj.getPort());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a POST request
            request += "POST " + getSetObj.getPath() + " HTTP/1.0\r\n";
            request += "Host: " + getSetObj.getHost() + "\r\n";
            // adding headers
            if (getSetObj.getHeaders() != null) {
                addHeaders(getSetObj.getHeaders());
            }
            // setting up the length of inline data
            if (getSetObj.getInlineData() != null) {
                request += "Content-Length:" + getSetObj.getInlineData().length() + "\r\n";
            }
            request += "Connection: close\r\n";
            request += "\r\n";
            // adding inline data
            if (getSetObj.getInlineData() != null) {
                request += getSetObj.getInlineData();
            }

            bufferWriter.write(request);
            bufferWriter.flush();

            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = "";
            String line;
            boolean isVerbose = true;

            // Getting response from host
            while ((line = bufferReader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    isVerbose = false;
                    continue;
                }
                if (!isVerbose) {
                    response += line + "\n";
                }
            }

            // print response in console
            System.out.println(response);
            // save response in external file
            if (getSetObj.getFileForHttpResponse() != null) {
                saveResponse(getSetObj.getFileForHttpResponse(), response);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    private static void getRequestWithVerbose(GetSet getSetObj) throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(getSetObj.getHost(), getSetObj.getPort());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a GET request
            request += "GET " + getSetObj.getPath() + " HTTP/1.0\r\n";
            request += "Host: " + getSetObj.getHost() + "\r\n";
            // adding headers
            if (getSetObj.getHeaders() != null) {
                addHeaders(getSetObj.getHeaders());
            }
            request += "\r\n";

            bufferWriter.write(request);
            bufferWriter.flush();

            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = "";
            String line;

            // Getting response from host
            while ((line = bufferReader.readLine()) != null) {

                response += line + "\n";
            }

            if (response.isEmpty() || (!response.substring(9, 12).equals("302") && !response.substring(9, 12).equals("301"))) {
                // print response in console
                System.out.println(response);
                // save response in external file
                if (getSetObj.getFileForHttpResponse() != null) {
                    saveResponse(getSetObj.getFileForHttpResponse(), response);
                    write.close();
                }
            } else {
                //redirecting
                redirect(getSetObj, response);
                if (getSetObj.getFileForHttpResponse() != null) {
                    saveResponse(getSetObj.getFileForHttpResponse(), response);
                    write.write("\nRedirecting to... http://" + getSetObj.getHost() + getSetObj.getPath() + "\n\n");
                }
                getRequestWithVerbose(getSetObj);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    private static void postRequestWithVerbose(GetSet getSetObj) throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(getSetObj.getHost(), getSetObj.getPort());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a POST request
            request += "POST " + getSetObj.getPath() + " HTTP/1.0\r\n";
            request += "Host: " + getSetObj.getHost() + "\r\n";
            // adding headers
            if (getSetObj.getHeaders() != null) {
                addHeaders(getSetObj.getHeaders());
            }
            // setting up the length of inline data
            if (getSetObj.getInlineData() != null) {
                request += "Content-Length:" + getSetObj.getInlineData().length() + "\r\n";
            }
            request += "Connection: close\r\n";
            request += "\r\n";
            // adding inline data
            if (getSetObj.getInlineData() != null) {
                request += getSetObj.getInlineData();
            }

            bufferWriter.write(request);
            bufferWriter.flush();

            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = "";
            String line;

            // Getting response from host
            while ((line = bufferReader.readLine()) != null) {

                response += line + "\n";
            }

            // print response in console
            System.out.println(response);
            // save response in external file
            if (getSetObj.getFileForHttpResponse() != null) {
                saveResponse(getSetObj.getFileForHttpResponse(), response);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    private static void redirect(GetSet getSetObj, String response) {
        System.out.println(response +
                "\nRedirecting to... http://" + getSetObj.getHost() + getSetObj.getPath() + "\n\n");
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
            getSetObj.setHost(newURL.substring(0, index3));
            getSetObj.setPath(newURL.substring(index3));
        } else {
            getSetObj.setHost(newURL);
            getSetObj.setPath("/");
        }
    }

    private static void addHeaders(HashMap<String, String> headers) {
        try {
            headers.forEach((key, value) -> {
                request += key + ": " + value + "\r\n";
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveResponse(String fileForHttpResponse, String response) throws IOException {
        if (!fileOpen) {
            write = new BufferedWriter(new FileWriter(new File(fileForHttpResponse)));
            fileOpen = true;
        }
        try {
            write.write(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}