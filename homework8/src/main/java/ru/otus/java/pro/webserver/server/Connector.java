package ru.otus.java.pro.webserver.server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor
@Setter
public class Connector {
    private static final Logger logger = LogManager.getLogger(Connector.class.getName());

    private ServerSocket serverSocket;
    private int threadPoolSize;
    private int requestSize;
    @Getter
    private boolean serverOperationStatus;

    public Connector addServerSocket(ServerSocket serverSocket) {
        this.setServerSocket(serverSocket);
        return this;
    }

    public Connector addThreadPoolSize(int threadPoolSize) {
        this.setThreadPoolSize(threadPoolSize);
        return this;
    }

    public Connector addRequestSize(int requestSize) {
        this.setRequestSize(requestSize);
        return this;
    }

    public Connector addServerOperationStatus(boolean serverOperationStatus) {
        this.setServerOperationStatus(serverOperationStatus);
        return this;
    }

    public void connect() throws IOException {
        Dispatcher dispatcher = new Dispatcher(this);
        try (ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize)) {
            while (serverOperationStatus) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new RequestHandler(clientSocket, dispatcher, requestSize));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
