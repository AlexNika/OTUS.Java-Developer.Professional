package ru.otus.java.pro.webserver.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.webserver.config.Config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

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
        logger.debug("Web Server started - Host: {}, Port: {}.", serverIpAddress, serverPort);
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(serverIpAddress, serverPort));
            Connector connector = new Connector()
                    .addServerSocket(serverSocket)
                    .addThreadPoolSize(threadPoolSize)
                    .addRequestSize(requestSize)
                    .addServerOperationStatus(true);
            connector.connect();
        } catch (IOException e) {
            logger.error("Socket accept error on the Web Server - Host: {}, Port: {}.", serverIpAddress,
                    serverPort);
        }
    }
}