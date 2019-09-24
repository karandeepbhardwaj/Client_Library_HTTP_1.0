package configuration;

public class Constants {

    public static final int DEFAULT_PORT = 80;
    public static int SAVE_OUTPUT = 0;
    public static final String HELP = "httpc is a curl-like application but supports HTTP protocol only. "
            + "\nUsage:\t" +
            "httpc command [arguments]\n\n" +
            "The commands are:\n" +
            "\nget\t\texecutes a HTTP GET request and prints the response. " +
            "\npost\texecutes a HTTP POST request and prints the response. " +
            "\nhelp\tprints this screen.\n" +
            "\nUse \"httpc help [command]\" for more information about a command.";

    public static final String INVALID = "INVALID COMMAND LINE ARGUMENTS\n\n"
            + "For information about usage type \"httpc help\"";

    public static final String HELP_POST = "httpc help post\n"
            + "\nusage: "
            + "\thttpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n\n"
            + "Post executes a HTTP POST request for a given URL with inline data or from file.\n"
            + "\n-v\t\t\t\tPrints the detail of the response such as protocol, status, and headers.\n"
            + "-h key:vtalue\tAssociates headers to HTTP Request with the format 'key:value'.\n"
            + "-d string\t\tAssociates an inline data to the body HTTP POST request.\n"
            + "-f file\t\t\tAssociates the content of a file to the body HTTP POST request.\n\n"
            + "Either [-d] or [-f] can be used but not both.";

    public static final String HELP_GET = "httpc help get\n" +
            "\nusage:\thttpc get [-v] [-h key:value] URL\n\n"
            + "Get executes a HTTP GET request for a given URL.\n\n"
            + "-v\t\t\t\tprints the detail of the response such as protocol, status, and headers.\n"
            + "-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.";
}
