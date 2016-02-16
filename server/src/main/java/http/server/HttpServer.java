package http.server;

import org.slf4j.Logger;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ForkJoinPool;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * User: adyomin
 * Date: 15.02.16
 * Time: 22:32
 */
public class HttpServer {
    private static final Logger logger = getLogger(HttpServer.class);

    private final int port;
    private ServerSocketFactory factory;
    private Status status;

    public HttpServer(int port) {
        this.port = port;
        this.status = Status.STOPPED;
        factory = ServerSocketFactory.getDefault();
    }

    public void start() {
        status = Status.STARTED;
        try {
            ServerSocket serverSocket = factory.createServerSocket(port);
            ForkJoinPool.commonPool().execute(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        serverSocket.accept();
                    }
                } catch (IOException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        logger.debug(e.getMessage());
                    } else {
                        logger.warn(e.getMessage(), e);
                    }
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        STARTED,
        STOPPED
    }
}
