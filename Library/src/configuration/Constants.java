package configuration;

public class Constants {

    public static final int DEFAULT_PORT =80;
    public static int SAVE_OUTPUT = 0;
    public static final String HELP = "httpc is a curl-like application but supports HTTP protocol only. "
            + "Usage:\n" +
            "httpc command [arguments]\n" +
            "The commands are:\n" +
            "get --- executes a HTTP GET request and prints the response. "+
            "post --- executes a HTTP POST request and prints the response. "+
            "help --- prints this screen.\n"+
            "Use \"httpc help [command]\" for more information about a command.";

    public static final String INVALID = "INVALID COMMAND LINE ARGUMENTS\n\n"
            + "For information about usage type \"httpc help\"";

    public static final String HELP_POST = "httpc help post\n"
            + "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
            + "Post executes a HTTP POST request for a given URL with inline data or from file.\n"
            + "-v --- Prints the detail of the response such as protocol, status, and headers.\n"
            + "-h key:value --- Associates headers to HTTP Request with the format 'key:value'.\n"
            + "-d string --- Associates an inline data to the body HTTP POST request.\n"
            + "-f file --- Associates the content of a file to the body HTTP POST request.\n\n"
            + "Either [-d] or [-f] can be used but not both.";

    public static final String HELP_GET = "httpc help get\n" +
            "usage: httpc get [-v] [-h key:value] URL\n"
            + "Get executes a HTTP GET request for a given URL.\n"
            + "-v --- Prints the detail of the response such as protocol, status, and headers.\n"
            + "-h key:value --- Associates headers to HTTP Request with the format 'key:value'.";
}
