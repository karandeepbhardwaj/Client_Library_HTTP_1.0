
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

    public static void processHeaders(String[] args, int numHeaders, int startIndex) {
        headers = new HashMap<>();
        for (int i = 0; i < numHeaders; i++) {
            int argNumber = startIndex + i * 2;
            String[] keyValue = args[argNumber].split(":");
            headers.put(keyValue[0], keyValue[1]);
        }
    }

    public static void main(String[] args) throws IOException {

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


        String testRedirection = "httpc get -v googl.com -o save.txt";
        String testOutputToFile = "httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' -o save.txt";
        String testOutputToFile2 = "httpc post -v -d {\"Assignment\":\"1\"} http://httpbin.org/post -o save2.txt";

        Scanner sc = new Scanner(System.in);
//        String inputFromConsole = sc.nextLine();

        String testCommand = testRedirection;

        args = testCommand.split("\\s+");

        port = (DEFAULT_PORT);

        if (!args[1].equals("help")) {
            if (args[args.length - 2].equals("-o")) {
                URL = args[args.length - 3];
                storeOutputToFile = true;
            } else {
                URL = args[args.length - 1];
            }

            String urlString = URL;
            if (urlString.startsWith("http://"))
                urlString = urlString.substring(7);
            else if (urlString.startsWith("https://"))
                urlString = urlString.substring(8);
            else if (urlString.startsWith("'https://"))
                urlString = urlString.substring(9, urlString.length() - 1);
            else if (urlString.startsWith("'http://"))
                urlString = urlString.substring(8, urlString.length() - 1);
            else if (urlString.startsWith("\'"))
                urlString = urlString.substring(1, urlString.length() - 1);

            int index1 = urlString.indexOf('/');

            if (index1 != -1) {
                host = (urlString.substring(0, index1));
                path = (urlString.substring(index1));
            } else {
                host = (URL);
            }
        }
        String[] commandWithURl;
        if (storeOutputToFile) {
            outputFileName = args[args.length - 1];
            httpResponseFile = (outputFileName);
            commandWithURl = Arrays.copyOf(args, args.length - 2);
        } else {
            commandWithURl = Arrays.copyOf(args, args.length);
        }
        String command = commandWithURl[1];
        switch (command) {
            case "help":
                if (commandWithURl.length == 2)

                    System.out.println("httpc is a curl-like application but supports HTTP protocol only. "
                            + "\nUsage:\t" +
                            "httpc command [arguments]\n\n" +
                            "The commands are:\n" +
                            "\nget\t\texecutes a HTTP GET request and prints the response. " +
                            "\npost\texecutes a HTTP POST request and prints the response. " +
                            "\nhelp\tprints this screen.\n" +
                            "\nUse \"httpc help [command]\" for more information about a command.");

                else if (commandWithURl.length == 3 && commandWithURl[2].equals("get"))


                    System.out.println("httpc help get\n" +
                            "\nusage:\thttpc get [-v] [-h key:value] URL\n\n"
                            + "Get executes a HTTP GET request for a given URL.\n\n"
                            + "-v\t\t\t\tprints the detail of the response such as protocol, status, and headers.\n"
                            + "-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.");

                else if (commandWithURl.length == 3 && commandWithURl[2].equals("post"))

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
                if (commandWithURl.length == 3)
                    requestGet();
                else if (commandWithURl.length == 4)
                    verboseGetRequest();
                else {
                    if (commandWithURl[2].equals("-v")) {
                        int numberOfHeaders = (commandWithURl.length - 4) / 2;
                        int headerFirstLocation = 4;
                        processHeaders(commandWithURl, numberOfHeaders, headerFirstLocation);
                        verboseGetRequest();
                    } else {
                        int numHeaders = (commandWithURl.length - 3) / 2;
                        int startIndex = 3;
                        processHeaders(commandWithURl, numHeaders, startIndex);
                        requestGet();
                    }
                }
                break;
            case "post":
                if (commandWithURl.length == 3)
                    postRequest();
                else if (commandWithURl.length == 4)
                    postRequestWithVerbose();
                else {
                    int locationOfVar = commandWithURl.length - 3;
                    boolean presenceOfd = commandWithURl[locationOfVar].equals("-d");
                    boolean presenceOfF = commandWithURl[locationOfVar].equals("-f");
                    boolean presenceOfh = commandWithURl[locationOfVar].equals("-h");

                    if (commandWithURl[2].equals("-v")) {
                        int numHeaders = 0;
                        int startIndex = 4;
                        if (presenceOfd) {
                            inlineData = (commandWithURl[commandWithURl.length - 2]);
                            numHeaders = (commandWithURl.length - 6) / 2;
                        } else if (presenceOfF) {
                            inputFileName = commandWithURl[commandWithURl.length - 2];
                            File file = new File(inputFileName);
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String temp = "";
                            String nextLine;
                            while ((nextLine = br.readLine()) != null) {
                                temp += nextLine;
                            }
                            inlineData = temp;
                            br.close();
                            numHeaders = (commandWithURl.length - 6) / 2;
                        } else if (presenceOfh) {
                            numHeaders = (commandWithURl.length - 4) / 2;
                        }
                        if (numHeaders > 0) {
                            processHeaders(commandWithURl, numHeaders, startIndex);
                        }
                        postRequestWithVerbose();
                    } else {
                        int numHeaders = 0;
                        int startIndex = 3;
                        if (commandWithURl[commandWithURl.length - 3].equals("-d")) {
                            inlineData = (commandWithURl[commandWithURl.length - 2]);
                            numHeaders = (commandWithURl.length - 5) / 2;
                        } else if (commandWithURl[commandWithURl.length - 3].equals("-f")) {
                            inputFileName = commandWithURl[commandWithURl.length - 2];
                            File file = new File(inputFileName);
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String input = "";
                            String nextLine;
                            while ((nextLine = br.readLine()) != null) {
                                input += nextLine;
                            }
                            inlineData = (input);
                            br.close();
                            numHeaders = (commandWithURl.length - 5) / 2;
                        } else if (commandWithURl[commandWithURl.length - 3].equals("-h")) {
                            numHeaders = (commandWithURl.length - 3) / 2;
                        }
                        if (numHeaders > 0) {
                            processHeaders(commandWithURl, numHeaders, startIndex);
                        }
                        postRequest();
                    }
                }
                break;
            default:
                System.out.println("INVALID COMMAND LINE ARGUMENTS\n\n"
                        + "For information about usage type \"httpc help\"");
                break;
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

    public static void requestGet() throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            request = "";
            socket = new Socket(host, port);
            System.out.println("Connected");
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            request += "GET " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
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
                System.out.println(response);
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.close();
                }
            } else {
                redirect(response);
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.write("\nRedirecting to... https://" + host + path + "\n\n");
                }
                requestGet();
                write.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            request = null;
            bufferReader.close();
            bufferWriter.close();
            socket.close();
        }
    }

    public static void verboseGetRequest() throws IOException {

        Socket socket = null;
        BufferedWriter bufferWriter = null;
        BufferedReader bufferReader = null;
        try {
            request = "";
            socket = new Socket(host, port);
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            System.out.println("Connected");

            request += "GET " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
            if (headers != null) {
                addHeaders(headers);
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

            if (response.isEmpty() || (!response.substring(9, 12).equals("302") && !response.substring(9, 12).equals("301"))) {
                System.out.println(response);
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.close();
                }
            } else {
                redirect(response);
                if (httpResponseFile != null) {
                    saveResponse(httpResponseFile, response);
                    write.write("\nRedirecting to... http://" + host + path + "\n\n");
                }
                verboseGetRequest();
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

        int locationOfNewURL = response.indexOf("Location");
        int url = response.indexOf("\n", locationOfNewURL);
        String newURL = response.substring(locationOfNewURL + 10, url);

//        System.out.println(newURL);
        System.out.println("\nRedirecting to... " + newURL);

        if (newURL.startsWith("http://"))
            newURL = newURL.substring(7);
        else if (newURL.startsWith("https://"))
            newURL = newURL.substring(8);
        else if (newURL.startsWith("'https://"))
            newURL = newURL.substring(9, newURL.length() - 1);
        else if (newURL.startsWith("'http://"))
            newURL = newURL.substring(8, newURL.length() - 1);

        int index = newURL.indexOf('/');

        if (index == -1)
            index = newURL.indexOf(".com") + 4;

        if (index != -1) {
            host = (newURL.substring(0, index));
            path = (newURL.substring(index));
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

            request += "POST " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
            if (headers != null) {
                addHeaders(headers);
            }
            if (inlineData != null) {
                request += "Content-Length:" + inlineData.length() + "\r\n";
            }
            request += "Connection: close\r\n";
            request += "\r\n";
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
            request += "POST " + path + " HTTP/1.0\r\n";
            request += "Host: " + host + "\r\n";
            if (headers != null) {
                addHeaders(headers);
            }
            if (inlineData != null) {
                request += "Content-Length:" + inlineData.length() + "\r\n";
            }
            request += "Connection: close\r\n";
            request += "\r\n";
            if (inlineData != null) {
                request += inlineData;
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
}