package http.server;

import java.nio.channels.AsynchronousSocketChannel;

public interface SocketHandler {

    void handle(AsynchronousSocketChannel accept);
}
