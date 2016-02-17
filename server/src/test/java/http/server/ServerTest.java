package http.server;

import http.client.HttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

import static http.server.Server.Status.STARTED;
import static http.server.Server.Status.STOPPED;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: adyomin
 * Date: 15.02.16
 * Time: 22:37
 */
public class ServerTest {

    public static final int PORT = 8080;

    private Server server;
    private HttpClient client;

    @Before
    public void setUp() throws Exception {
        server = new Server(PORT).withHandler(new HttpSocketHandler());
        client = new HttpClient().host("localhost").port(PORT);
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
            assertNotNull(connect());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private String connect() throws IOException {
        String result = client.get("/");
        System.out.println(result);
        return result;
    }

    @Test(expected = IOException.class)
    public void testNotStartedBefore() throws IOException {
        assertEquals(STOPPED, server.getStatus());
        assertNotNull(connect());
        server.start();
    }

    @Test(expected = IOException.class)
    public void testStopped() throws IOException {
        server.start();
        assertEquals(STARTED, server.getStatus());
        try {
            assertNotNull(connect());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        assertEquals(STARTED, server.getStatus());
        try {
            assertNotNull(connect());
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

        doAnswer(invocation -> {
            ((AsynchronousSocketChannel) invocation.getArguments()[0]).close();
            return null;
        }).when(handler).handle(any());

        server.withHandler(handler);
        server.start();
        assertNotNull(connect());
        synchronized (this) {
            wait(100);
        }
        verify(handler).handle(any());
    }
}