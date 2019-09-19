package application;

import java.io.*;
import java.util.*;

import configuration.*;

public class Main {

    private static String URL = "";
    private static HashMap<String, String> headers = new HashMap<>();
    ;
    public static CAttributes attributesObj;
    public static String OFileName = null;
    public static String IFileName = null;

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

    public static void parseConsoleCommand(String[] command) throws IOException {

        Generator generatorobj = new Generator();
        String[] url = {};
        if (Constants.SAVE_OUTPUT == 1) {

            OFileName = command[command.length - 1];
            attributesObj.setFileForHttpResponse(OFileName);
            url = Arrays.copyOf(command, command.length - 2);
        } else {
            for (int i = 0; i < command.length; i++)
                url[i] = command[i];
        }

        String firstWord = url[1];

        if (firstWord.equals("help")) {
            if (url.length == 2)
                System.out.println(Constants.HELP);
            else if (url.length == 3 && url[2].equals("get"))
                System.out.println(Constants.HELP_GET);
            else if (url.length == 3 && url[2].equals("post"))
                System.out.println(Constants.HELP_POST);
            else
                System.out.println(Constants.INVALID);
        } else if (firstWord.equals("get")) {

        }

    }

    public static void main(String[] args) throws IOException {

        attributesObj = new CAttributes();

        attributesObj.setPort(Constants.DEFAULT_PORT);

        Scanner sc = new Scanner(System.in);

        String input = sc.nextLine();
        String command = input.toLowerCase();
        if (command != "help") {

            String[] inputs = command.split(" ");

            if (inputs[inputs.length - 2] == "-o") {

                URL = inputs[inputs.length - 3];
                Constants.SAVE_OUTPUT = 1;

            } else {
                URL = inputs[inputs.length - 1];
            }
            parseURL(URL);
        }
        parseConsoleCommand(input.split(" "));
        sc.close();
    }
}