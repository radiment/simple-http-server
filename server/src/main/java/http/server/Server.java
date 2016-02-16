package http.server;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * User: adyomin
 * Date: 15.02.16
 * Time: 22:32
 */
public class Server {
    private static final Logger logger = getLogger(Server.class);

    private final int port;
    private Status status;
    private SocketHandler handler;
    private AsynchronousServerSocketChannel listener;

    public Server(int port) {
        this.port = port;
        this.status = Status.STOPPED;
    }

    public void setHandler(SocketHandler handler) {
        this.handler = handler;
    }

    public void start() throws IOException {
        listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
        listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                // accept the next connection
                listener.accept(null, this);
                // handle this connection
                handler.handle(result);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                logger.error("Failed: " + exc.getMessage(), exc);
            }
        });
        logger.info("Server started on port {}", port);
        status = Status.STARTED;
    }

    public Status getStatus() {
        return status;
    }

    public void stop() {
        try {
            listener.close();
        } catch (IOException e) {
            throw new ServerException("Server stop error: " + e.getMessage(), e);
        }
        this.status = Status.STOPPED;
    }

    public enum Status {
        STARTED,
        STOPPED
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = new Server(8080);
        server.setHandler(new HttpSocketHandler());
        server.start();
        while (Status.STARTED.equals(server.getStatus())) {
            Thread.sleep(1000);
        }
    }
}
