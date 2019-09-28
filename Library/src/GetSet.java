import java.util.HashMap;

public class GetSet {

    private int port;
    private String host;
    private String path = "/";
    private String inlineData;
    private String fileForHttpRequest;
    private String fileForHttpResponse;
    private HashMap<String, String> headers;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getInlineData() {
        return inlineData;
    }

    public void setInlineData(String inlineData) {
        this.inlineData = inlineData;
    }

    public String getFileForHttpRequest() {
        return fileForHttpRequest;
    }

    public void setFileForHttpRequest(String fileForHttpRequest) {
        this.fileForHttpRequest = fileForHttpRequest;
    }

    public String getFileForHttpResponse() {
        return fileForHttpResponse;
    }

    public void setFileForHttpResponse(String fileForHttpResponse) {
        this.fileForHttpResponse = fileForHttpResponse;
    }
}
