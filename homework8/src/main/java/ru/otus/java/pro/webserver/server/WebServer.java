package ru.otus.java.pro.webserver.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.webserver.config.Config;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger logger = LogManager.getLogger(WebServer.class.getName());

    private InetAddress serverIpAddress;
    private int serverPort;
    private int threadPoolSize;
    private int requestSize;

    public WebServer(Config config) {
        if (config == null) {
            logger.error("Simple HTTP WEB SERVER can't start due to the configuration reading error.");
            throw new IllegalArgumentException("Config is null!");
        }
        try {
            this.serverIpAddress = InetAddress.getByName(config.getProperty("server.host", "127.0.0.1"));
            this.serverPort = Integer.parseInt(config.getProperty("server.port", "8080"));
            this.threadPoolSize = Integer.parseInt(config.getProperty("threadPoolSize", "4"));
            this.requestSize = Integer.parseInt(config.getProperty("requestSize", "8192"));
        } catch (NumberFormatException | UnknownHostException e) {
            logger.warn("Parameters 'server.port', 'threadPoolSize', 'requestSize' " +
                    "in file 'config.properties' must be a string consisting only of digits", e);
        }
    }

    public void start() {
        logger.info("Web Server started - Host: {}, Port: {}.", serverIpAddress, serverPort);
        Dispatcher dispatcher = new Dispatcher();
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReceiveBufferSize(requestSize);
            serverSocket.bind(new InetSocketAddress(serverIpAddress, serverPort));
            ExecutorService threadPool = null;
            try {
                threadPool = Executors.newFixedThreadPool(threadPoolSize);
                while (serverSocket.isBound() && !serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setReceiveBufferSize(requestSize);
                    if (!dispatcher.isServerOperationStatus()) {
                        logger.debug("Web Server shutdown forcibly on demand command '/shutdown'");
                        clientSocket.close();
                        break;
                    }
                    threadPool.execute(new RequestHandler(clientSocket, dispatcher));
                }
            } finally {
                if (threadPool != null) threadPool.shutdown();
            }
        } catch (IOException e) {
            logger.error("Socket accept error on the Web Server - Host: {}, Port: {}.", serverIpAddress,
                    serverPort);
        }
        logger.info("Web Server finished");
    }
}