package http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousSocketChannel;

public class HttpSocketHandler implements SocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpSocketHandler.class);

    @Override
    public void handle(AsynchronousSocketChannel accept) {
        logger.trace("Handling request");
    }
}
