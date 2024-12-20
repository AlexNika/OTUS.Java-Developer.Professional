package ru.otus.java.pro.webserver.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.webserver.http.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(RequestHandler.class.getName());

    private final Socket connectedSocket;
    private final Dispatcher dispatcher;

    public RequestHandler(Socket connectedSocket,
                          Dispatcher dispatcher) {
        this.connectedSocket = connectedSocket;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        logger.info("New client connected IP: {}, Port: {}", connectedSocket.getInetAddress(),
                connectedSocket.getPort());
        try (InputStream inputStream = connectedSocket.getInputStream();
             OutputStream outputStream = connectedSocket.getOutputStream()) {
            HttpRequest request = new HttpRequest(inputStream);
            logger.debug("Request: {}", request);
            dispatcher.execute(request, outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
