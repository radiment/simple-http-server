package http.server;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HttpSocketHandlerTest {
    @Test
    public void testHandle() throws Exception {
        SocketHandler handler = new HttpSocketHandler();
        AsynchronousSocketChannel socketChannel = mock(AsynchronousSocketChannel.class);
        handler.handle(socketChannel);
        verify(socketChannel).read(any(ByteBuffer.class));
    }
}