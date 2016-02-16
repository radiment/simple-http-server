package http.server;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static http.server.HttpServer.Status.STARTED;
import static http.server.HttpServer.Status.STOPPED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: adyomin
 * Date: 15.02.16
 * Time: 22:37
 */
public class HttpServerTest {

    @Test
    public void testStartServer() {
        HttpServer server = new HttpServer(8080);
        assertEquals(STOPPED, server.getStatus());
        server.start();
        assertEquals(STARTED, server.getStatus());
        try {
            connect();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private void connect() throws IOException {
        URL url = new URL("http", "localhost", 8080, "/");
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
    }

    @Test(expected = IOException.class)
    public void testNotStartedBefore() throws IOException {
        HttpServer server = new HttpServer(8080);
        assertEquals(STOPPED, server.getStatus());
        connect();
        server.start();
    }

}