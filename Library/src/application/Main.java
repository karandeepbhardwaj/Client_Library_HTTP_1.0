package application;

import java.io.*;
import java.util.*;

import configuration.*;

public class Main {

    private static String URL = "";
    private static String OFileName = null;
    public static String IFileName = null;
    private static HashMap<String, String> headers = new HashMap<>();
    public static CAttributes attributesObj;

    public static void main(String[] args) throws IOException {

        attributesObj = new CAttributes();
        attributesObj.setPort(Constants.DEFAULT_PORT);

        Scanner sc = new Scanner(System.in);

        String[] command = sc.nextLine().split(" ");

        if (!command[1].equals("help")) {

            if (command[command.length - 2] == "-o") {
                URL = command[command.length - 3];
                Constants.SAVE_OUTPUT = 1;

            } else {
                URL = command[command.length - 1];
            }
            parseURL(URL);
        }
        parseConsoleCommand(command);
        sc.close();
    }

    public static void parseURL(String URL) {

        String url = URL;
        String path = null;
        String host = null;

        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        } else if (url.startsWith("'http://")) {
            url = url.substring(8, url.length() - 1);
        } else if (url.startsWith("'https://")) {
            url = url.substring(9, url.length() - 1);
        } else if (url.startsWith("\'")) {
            url = url.substring(1, url.length() - 1);
        } else {
            int index = url.indexOf("/");
            host = url.substring(0, index);
            path = url.substring(index);
        }
        if (url.indexOf("/") != -1) {
            attributesObj.setHost(host);
            attributesObj.setPath(path);
        } else {
            attributesObj.setHost(url);
        }
    }

    public static void parseConsoleCommand(String[] args) throws IOException {

        Generator generatorobj = new Generator();
        String[] command = args.clone();
        String[] url = {};

        if (Constants.SAVE_OUTPUT == 1) {

            OFileName = command[command.length - 1];
            attributesObj.setFileForHttpResponse(OFileName);
            url = Arrays.copyOf(command, command.length - 2);
        } else {
            url = command.clone();
        }

        String wordAfterHttpc = url[1];

        if (wordAfterHttpc.equals("help")) {
            if (url.length == 2)
                System.out.println(Constants.HELP);
            else if (url.length == 3 && url[2].equals("get"))
                System.out.println(Constants.HELP_GET);
            else if (url.length == 3 && url[2].equals("post"))
                System.out.println(Constants.HELP_POST);
            else
                System.out.println(Constants.INVALID);
        } else if (wordAfterHttpc.equals("get")) {

            if (url.length == 3) {
                generatorobj.getRequest(attributesObj);
            } else if (url.length == 4) {
                generatorobj.verboseGetRequest(attributesObj);
            } else {
                if (url[2] == "-v") {
                    int headerNumber = (url.length - 3) / 2;
                    int startingIndex = 3;
                    headersManager(url, headerNumber, startingIndex);
                    generatorobj.getRequest(attributesObj);
                }
            }
        }else if(wordAfterHttpc.equals("post")){
            if(url.length == 3){
                generatorobj.postRequest(attributesObj);
            }else if(url.length == 4){
                generatorobj.verbosePostRequest(attributesObj);
            }else{
                String wordAfterPost = url[2];
                if(wordAfterPost == "-v"){
                    int headerNumber = 0;
                    int startingIndex = 4;
                    if(url[url.length-3].equals("-d")) {
                        attributesObj.setInlineData(url[url.length-2]);
                        headerNumber = (url.length-6)/2;

                    }else if(url[url.length-3].equals("-f")) {
                        IFileName = url[url.length-2];
                        File file = new File(IFileName);
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        StringBuilder input = new StringBuilder();
                        String nextLine;
                        while ((nextLine = br.readLine()) != null) {
                            input.append(nextLine);
                        }
                        attributesObj.setInlineData(input.toString());
                        br.close();
                        headerNumber = (url.length-6)/2;
                    }
                    else if (url[url.length-3].equals("-h")) {
                        headerNumber = (url.length-4)/2;
                    }
                    if(headerNumber > 0) {
                        headersManager(url, headerNumber, startingIndex);
                    }
                    generatorobj.verbosePostRequest(attributesObj); //command -> httpc post -v (-h key:value)* [-d] [-f] URL
                }
                else { 	//without verbose
                    int numHeaders = 0;
                    int startIndex = 3;
                    if(url[url.length-3].equals("-d")) {
                        attributesObj.setInlineData(url[url.length-2]);
                        numHeaders = (url.length-5)/2;
                    }
                    else if(url[url.length-3].equals("-f")) {
                        IFileName = url[url.length-2];
                        File file = new File(IFileName);
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String input = "";
                        String nextLine;
                        while ((nextLine = br.readLine()) != null) {
                            input+=nextLine;
                        }
                        attributesObj.setInlineData(input);
                        br.close();
                        numHeaders = (url.length-5)/2;
                    }
                    else if (url[url.length-3].equals("-h")) {
                        numHeaders = (url.length-3)/2;
                    }
                    if(numHeaders > 0) {
                        headersManager(url, numHeaders, startIndex);
                    }
                    generatorobj.postRequest(attributesObj); //command -> httpc post (-h key:value)* [-d] [-f] URL
                }
            }
        }else{
            System.out.println(Constants.INVALID);
        }
    }

    private static void headersManager(String[] url, int headerNumber, int startingIndex) {
        headers = new HashMap<>();
        for(int i = 0; i< headerNumber; i++){
            int urlPosition = startingIndex+i*2;
            String[] keyValue = url[urlPosition].split(":");
            headers.put(keyValue[0], keyValue[2]);
        }
    }
}