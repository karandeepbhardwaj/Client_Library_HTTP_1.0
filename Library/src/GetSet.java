import java.util.HashMap;

public class GetSet {

    // Host name
    private String host;
    // HTTP Port Number
    private int port;
    // Directory at the host
    private String path = "/";
    // Collection of request headers with key-value pair
    private HashMap<String, String> headers;
    // Inline data for HTTP POST Request with key-value pair
    private String inlineData;
    // File name for associating its content to the body HTTP POST Request
    private String fileForHttpRequest;
    // File name for storing the HTTP Response
    private String fileForHttpResponse;

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the headers
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    /**
     * @return the inlineData
     */
    public String getInlineData() {
        return inlineData;
    }

    /**
     * @param inlineData the inlineData to set
     */
    public void setInlineData(String inlineData) {
        this.inlineData = inlineData;
    }

    /**
     * @return the fileForHttpRequest
     */
    public String getFileForHttpRequest() {
        return fileForHttpRequest;
    }

    /**
     * @param fileForHttpRequest the fileForHttpRequest to set
     */
    public void setFileForHttpRequest(String fileForHttpRequest) {
        this.fileForHttpRequest = fileForHttpRequest;
    }

    /**
     * @return the fileForHttpResponse
     */
    public String getFileForHttpResponse() {
        return fileForHttpResponse;
    }

    /**
     * @param fileForHttpResponse the fileForHttpResponse to set
     */
    public void setFileForHttpResponse(String fileForHttpResponse) {
        this.fileForHttpResponse = fileForHttpResponse;
    }
}
