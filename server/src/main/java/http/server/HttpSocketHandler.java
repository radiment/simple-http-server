package http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class HttpSocketHandler implements SocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpSocketHandler.class);
    public static final int REQUEST_SIZE = 1024 * 8;

    @Override
    public void handle(AsynchronousSocketChannel accept) {
        ByteBuffer buffer = ByteBuffer.allocate(REQUEST_SIZE);
        accept.read(buffer);
        logger.trace("Handling request");
        try {
            SocketAddress remoteAddress = accept.getRemoteAddress();
            logger.debug("Info: ", remoteAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("Buffer read with: {}, {}, {}", buffer.position(), buffer.limit(),
                new String(buffer.array(), 0, buffer.position()));
    }
}
