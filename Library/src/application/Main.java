package application;

import java.io.*;
import java.util.*;

import configuration.*;

public class Main {

    public static void main(String[] args) throws IOException {

        CAttributes attributesObj = new CAttributes();
        attributesObj.setPort(Constants.DEFAULT_PORT);

        Scanner sc = new Scanner(System.in);
        String[] command = sc.nextLine().split(" ");

        if (!command[1].equals("help")) {

            String URL = "";
            if (command[command.length - 2] == "-o") {
                URL = command[command.length - 3];
                Constants.SAVE_OUTPUT = 1;

            } else {
                URL = command[command.length - 1];
            }
            Parser.parseURL(URL);
        }
        Parser.parseConsoleCommand(command);
        sc.close();
    }
}