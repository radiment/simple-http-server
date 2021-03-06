package http.client;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private static final byte[] HTTP_TYPE = "HTTP/1.1".getBytes();
    private static final byte[] END_LINE = "\n".getBytes();
    private static final byte[] HOST = "Host".getBytes();
    private static final byte[] IS = ": ".getBytes();
    private static final byte[] SPACE = " ".getBytes();
    private static final byte[] USER_AGENT = "User-Agent".getBytes();
    private static final byte[] CONNECTION = "Connection".getBytes();
    private static final byte[] ACCEPT_LANGUAGE = "Accept-Language".getBytes();
    private static final byte[] COOKIE = "Cookie".getBytes();
    private static final byte[] EQUAL = "=".getBytes();
    private static final byte[] SEMICOLON = ";".getBytes();

    public static String defaultHost = "localhost";
    public static int defaultPort = 8080;
    public static String userAgent = "Mozilla/5.0";
    public static String acceptLanguage = "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4";

    private Map<String, Map<String, String>> cookiesMap = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    private ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
    private String connection = "keep-alive";
    private String host = defaultHost;
    private int port = defaultPort;
    private String method;
    private String path;
    private String content;

    public String get(String path) throws IOException {
        return path(path).get();
    }

    public String get() throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path not found");
        }
        this.method = "GET";
        SocketChannel socket = getSocketChannel();
        socket.write(prepareWrite());
        buffer.clear();
        StringBuilder builder = new StringBuilder();
        socket.read(buffer);
        builder.append(new String(buffer.array(), 0, buffer.position()));
        buffer.clear();
        socket.close();
        content = builder.toString();
        return content;
    }

    private ByteBuffer prepareWrite() {
        buffer.clear();
        buffer.put(method.getBytes()).put(SPACE).put(path.getBytes())
                .put(SPACE).put(HTTP_TYPE).put(END_LINE);
        buffer.put(HOST).put(IS).put(host.getBytes());
        if (port != 80) {
            buffer.put((":" + port).getBytes());
        }
        buffer.put(END_LINE);
        buffer.put(CONNECTION).put(IS).put(connection.getBytes()).put(END_LINE);
        buffer.put(USER_AGENT).put(IS).put(userAgent.getBytes()).put(END_LINE);
        buffer.put(ACCEPT_LANGUAGE).put(IS).put(acceptLanguage.getBytes()).put(END_LINE);
        params.forEach(this::putProperty);
        pasteCookies();
        buffer.put(END_LINE);
        buffer.flip();
        return buffer;
    }

    private SocketChannel getSocketChannel() throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(host, port));
        return channel;
    }

    private void pasteCookies() {
        Map<String, String> cookies = this.cookiesMap.get(host);
        if (cookies != null) {
            buffer.put(COOKIE).put(IS);
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                buffer.put(entry.getKey().getBytes()).put(EQUAL).put(entry.getValue().getBytes());
                buffer.put(SEMICOLON);
            }
            buffer.position(buffer.position() - 1);
            buffer.put(END_LINE);
        }
    }

    private ByteBuffer putProperty(String key, String value) {
        return buffer.put(key.getBytes()).put(IS).put(value.getBytes()).put(END_LINE);
    }

    public HttpClient host(String host) {
        this.host = host;
        return this;
    }

    public HttpClient port(int port) {
        this.port = port;
        return this;
    }

    public HttpClient path(String path) {
        this.path = path;
        return this;
    }

    public HttpClient cookie(String key, String value) {
        Map<String, String> cookies = cookiesMap.get(host);
        cookies.put(key, value);
        return this;
    }

    public Map<String, String> getCookies() {
        return cookiesMap.get(host);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();
        System.out.println(client.path("/").get());
    }

}
