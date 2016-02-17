package http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        logger.debug("Buffer read with: {}, {}, {}", buffer.position(), buffer.limit(),
                new String(buffer.array(), 0, buffer.position()));
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        writeBuffer.put("HTTP/1.1 200 OK".getBytes());
        writeBuffer.flip();
        accept.write(writeBuffer);
        try {
            accept.close();
        } catch (IOException e) {
            logger.error("Error on close socket.", e);
        }
    }
}
