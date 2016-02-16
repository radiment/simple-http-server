package http.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static http.server.Server.Status.STARTED;
import static http.server.Server.Status.STOPPED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * User: adyomin
 * Date: 15.02.16
 * Time: 22:37
 */
public class ServerTest {

    private Server server;

    @Before
    public void setUp() throws Exception {
        server = new Server(8080);
    }

    @After
    public void tearDown() throws Exception {
        if (!STOPPED.equals(server.getStatus())) {
            server.stop();
        }
    }

    @Test
    public void testStartServer() throws IOException {
        assertEquals(STOPPED, server.getStatus());
        server.start();
        assertEquals(STARTED, server.getStatus());
        try {
            connect();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private URLConnection connect() throws IOException {
        URL url = new URL("http", "localhost", 8080, "/");
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        return urlConnection;
    }

    @Test(expected = IOException.class)
    public void testNotStartedBefore() throws IOException {
        assertEquals(STOPPED, server.getStatus());
        connect();
        server.start();
    }

    @Test(expected = IOException.class)
    public void testStopped() throws IOException {
        server.start();
        assertEquals(STARTED, server.getStatus());
        try {
            connect();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        assertEquals(STARTED, server.getStatus());
        try {
            connect();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        server.stop();
        assertEquals(STOPPED, server.getStatus());
        connect();
    }

    @Test
    public void testListener() throws IOException, InterruptedException {
        SocketHandler handler = mock(SocketHandler.class);
        server.setHandler(handler);
        server.start();
        connect();
        synchronized (this) {
            wait(100);
        }
        verify(handler).handle(any());
    }
}