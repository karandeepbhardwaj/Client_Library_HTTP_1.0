
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Httpc {

    private static String host;
    private static int port;
    private static String path = "/";
    private static HashMap<String, String> headers;
    private static String inlineData;
    private static String fileForHttpRequest;
    private static String httpResponseFile;
    private static final int DEFAULT_PORT = 80;
    private static boolean storeOutputToFile = false;

    private static String URL;
    private static String outputFileName;
    private static String inputFileName;
    private static String request = null;
    private static boolean fileOpen = false;
    private static BufferedWriter write;

    private static void parseURL(String URL) {
        String str = URL;

        //check if URL starts with http:// or http://
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

        //checking first occurrence of '/' in the string without http:// or https://
        int index1 = str.indexOf('/');

        //splitting the string into host, path based on index of '/'
        if (index1 != -1) {
            host = (str.substring(0, index1));
            path = (str.substring(index1));
        } else {
            host = (URL);
        }
    }

    private static void parseCommand(String[] cmdArgs) throws IOException {
        String[] args;
        if (storeOutputToFile) {
            outputFileName = cmdArgs[cmdArgs.length - 1];
            httpResponseFile = (outputFileName);
            args = Arrays.copyOf(cmdArgs, cmdArgs.length - 2);
        } else {
            args = cmdArgs.clone();
        }

        String command = args[1];
        switch (command) {
            case "help":
                if (args.length == 2)


                    System.out.println("httpc is a curl-like application but supports HTTP protocol only. "
                            + "\nUsage:\t" +
                            "httpc command [arguments]\n\n" +
                            "The commands are:\n" +
                            "\nget\t\texecutes a HTTP GET request and prints the response. " +
                            "\npost\texecutes a HTTP POST request and prints the response. " +
                            "\nhelp\tprints this screen.\n" +
                            "\nUse \"httpc help [command]\" for more information about a command.");                    //command -> httpc help


                else if (args.length == 3 && args[2].equals("get"))


                    System.out.println("httpc help get\n" +
                            "\nusage:\thttpc get [-v] [-h key:value] URL\n\n"
                            + "Get executes a HTTP GET request for a given URL.\n\n"
                            + "-v\t\t\t\tprints the detail of the response such as protocol, status, and headers.\n"
                            + "-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.");                //command -> httpc help get


                else if (args.length == 3 && args[2].equals("post"))


                    System.out.println("httpc help post\n"
                            + "\nusage: "
                            + "\thttpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n\n"
                            + "Post executes a HTTP POST request for a given URL with inline data or from file.\n"
                            + "\n-v\t\t\t\tPrints the detail of the response such as protocol, status, and headers.\n"
                            + "-h key:vtalue\tAssociates headers to HTTP Request with the format 'key:value'.\n"
                            + "-d string\t\tAssociates an inline data to the body HTTP POST request.\n"
                            + "-f file\t\t\tAssociates the content of a file to the body HTTP POST request.\n\n"
                            + "Either [-d] or [-f] can be used but not both.");


                else
                    System.out.println("INVALID COMMAND LINE ARGUMENTS\n\n"
                            + "For information about usage type \"httpc help\"");
                break;
            case "get":
                if (args.length == 3)
                    getRequest();                //command -> httpc get URL
                else if (args.length == 4)
                    getRequestWithVerbose();    //command -> httpc get -v URL
                else {
                    if (args[2].equals("-v")) {    //with verbose
                        int numHeaders = (args.length - 4) / 2;
                        int startIndex = 4;
                        processHeaders(args, numHeaders, startIndex);
                        getRequestWithVerbose(); //command -> httpc get -v (-h key:value)* URL
                    } else {    //without verbose
                        int numHeaders = (args.length - 3) / 2;
                        int startIndex = 3;
                        processHeaders(args, numHeaders, startIndex);
                        getRequest(); //command -> httpc get (-h key:value)* URL
                    }
                }
                break;
            case "post":
                if (args.length == 3)
                    postRequest();                //command -> httpc post URL
                else if (args.length == 4)
                    postRequestWithVerbose();    //command -> httpc post -v URL
                else {
                    if (args[2].equals("-v")) {    //with verbose
                        int numHeaders = 0;
                        int startIndex = 4;
                        if (args[args.length - 3].equals("-d")) {
                            inlineData = (args[args.length - 2]);
                            numHeaders = (args.length - 6) / 2;
                        } else if (args[args.length - 3].equals("-f")) {
                            inputFileName = args[args.length - 2];
                            File file = new File(inputFileName);
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String input = "";
                            String nextLine;
                            while ((nextLine = br.readLine()) != null) {
                                input += nextLine;
                            }
                            inlineData = (input);
                            br.close();
                            numHeaders = (args.length - 6) / 2;
                        } else if (args[args.length - 3].equals("-h")) {
                            numHeaders = (args.length - 4) / 2;
                        }
                        if (numHeaders > 0) {
                            processHeaders(args, numHeaders, startIndex);
                        }
                        postRequestWithVerbose(); //command -> httpc post -v (-h key:value)* [-d] [-f] URL
                    } else {    //without verbose
                        int numHeaders = 0;
                        int startIndex = 3;
                        if (args[args.length - 3].equals("-d")) {
                            inlineData = (args[args.length - 2]);
                            numHeaders = (args.length - 5) / 2;
                        } else if (args[args.length - 3].equals("-f")) {
                            inputFileName = args[args.length - 2];
                            File file = new File(inputFileName);
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String input = "";
                            String nextLine;
                            while ((nextLine = br.readLine()) != null) {
                                input += nextLine;
                            }
                            inlineData = (input);
                            br.close();
                            numHeaders = (args.length - 5) / 2;
                        } else if (args[args.length - 3].equals("-h")) {
                            numHeaders = (args.length - 3) / 2;
                        }
                        if (numHeaders > 0) {
                            processHeaders(args, numHeaders, startIndex);
                        }
                        postRequest(); //command -> httpc post (-h key:value)* [-d] [-f] URL
                    }
                }
                break;
            default:
                System.out.println("INVALID COMMAND LINE ARGUMENTS\n\n"
                        + "For information about usage type \"httpc help\"");
                break;
        }
    }

    public static void processHeaders(String[] args, int numHeaders, int startIndex) {
        headers = new HashMap<>();
        for (int i = 0; i < numHeaders; i++) {
            int argNumber = startIndex + i * 2;
            String[] keyValue = args[argNumber].split(":");
            headers.put(keyValue[0], keyValue[1]);
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
//        String inputFromConsole = sc.nextLine();


        String testFun = "httpc get http://httpbin.org/status/418";
        String testHelp = "httpc help";
        String testHelpGet = "httpc help get";
        String testHelpPost = "httpc help post";

        String testGet = "httpc get 'http://httpbin.org/get?course=networking&assignment=1'";
        String testVerboseGet = "httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'";
        String testGetVerboseJson = "httpc get -v -h Content-Type:application/json 'http://httpbin.org/get?course=networking&assignment=1'";

        String testPostSimple = "httpc post http://httpbin.org/post";
        String testPostH = "httpc post -h Content-Type:application/json -d '{\"Assignment\":1}' http://httpbin.org/post";
        String testPostF = "httpc post -h Content-Type:application/json -f /Users/karandeep/IdeaProjects/Client_Library_HTTP_1.0/input.txt http://httpbin.org/post";
        String testPostVerbose = "httpc post -v -h Content-Type:application/json -f /Users/karandeep/IdeaProjects/Client_Library_HTTP_1.0/input.txt http://httpbin.org/post";


        String testRedirection = "httpc get -v amazon.com -o save.txt";
        String testOutputToFile = "httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' -o save.txt";
        String testOutputToFile2 = "httpc post -v -d {\"Assignment\":\"1\"} http://httpbin.org/post -o save2.txt";


        //----------------------------------------------------------------------------------------

        String testCommand = testRedirection;

        args = testCommand.split("\\s+");

        port = (DEFAULT_PORT);

        if (!args[1].equals("help")) {

            //check if output is to be stored into file
            if (args[args.length - 2].equals("-o")) {
                //getting URL i.e. the third last element of the command line arguments
                URL = args[args.length - 3];
                storeOutputToFile = true;
            } else {
                //getting URL i.e. the last element of the command line arguments
                URL = args[args.length - 1];
            }
            //split URL into host, path & query
            parseURL(URL);
        }

        //parse arguments
        parseCommand(args);

    }

    public static void getRequest() throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(host, port);
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a GET request
            request += "GET " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
            // adding headers
            if (headers != null) {
                addHeaders(headers);
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
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.close();
                }
            } else {
                //redirecting
                redirect(response);
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.write("\nRedirecting to... http://" + host + path + "\n\n");
                }
                getRequest();
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    public static void getRequestWithVerbose() throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(host, port);
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a GET request
            request += "GET " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
            // adding headers
            if (headers != null) {
                addHeaders(headers);
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
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.close();
                }
            } else {
                //redirecting
                redirect(response);
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.write("\nRedirecting to... http://" + host + path + "\n\n");
                }
                getRequestWithVerbose();
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    private static void redirect(String response) {
        System.out.println(response);
        System.out.println("\nRedirecting to... https://" + host + path + "\n\n");
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
            host = (newURL.substring(0, index3));
            path = (newURL.substring(index3));
        } else {
            host = (newURL);
            path = ("/");
        }

    }

    public static void postRequest() throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(host, port);
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a POST request
            request += "POST " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
            // adding headers
            if (headers != null) {
                addHeaders(headers);
            }
            // setting up the length of inline data
            if (inlineData != null) {
                request += "Content-Length:" + inlineData.length() + "\r\n";
            }
            request += "Connection: close\r\n";
            request += "\r\n";
            // adding inline data
            if (inlineData != null) {
                request += inlineData;
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
            if (httpResponseFile != null) {
                saveResponse(httpResponseFile, response);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    public static void postRequestWithVerbose() throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            socket = new Socket(host, port);
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            request = "";

            // building a POST request
            request += "POST " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
            // adding headers
            if (headers != null) {
                addHeaders(headers);
            }
            // setting up the length of inline data
            if (inlineData != null) {
                request += "Content-Length:" + inlineData.length() + "\r\n";
            }
            request += "Connection: close\r\n";
            request += "\r\n";
            // adding inline data
            if (inlineData != null) {
                request += inlineData;
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
            if (httpResponseFile != null) {
                saveResponse(httpResponseFile, response);
                write.close();
            }
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    public static void addHeaders(HashMap<String, String> headers) {
        try {
            headers.forEach((key, value) -> {
                request += key + ": " + value + "\r\n";
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveResponse(String httpResponseFile, String response) throws IOException {
        if (!fileOpen) {
            write = new BufferedWriter(new FileWriter(new File(httpResponseFile)));
            fileOpen = true;
        }
        try {
            write.write(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}