package ru.otus.java.pro.webserver.server;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.webserver.http.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@AllArgsConstructor
public class RequestHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(RequestHandler.class.getName());

    private final Socket connectedSocket;
    private Dispatcher dispatcher;
    private final int requestSize;

    @Override
    public void run() {
        logger.info("New client connected IP: {}, Port: {}", connectedSocket.getInetAddress(),
                connectedSocket.getPort());
        ThreadLocal<byte[]> requestBuffer = new ThreadLocal<>();
        try (InputStream in = connectedSocket.getInputStream();
             OutputStream out = connectedSocket.getOutputStream()) {
            if (requestBuffer.get() == null) {
                requestBuffer.set(new byte[requestSize]);
            }
            byte[] buffer = requestBuffer.get();
            int n = in.read(buffer);
            if (n > 0) {
                String rawRequest = new String(buffer, 0, n);
                HttpRequest request = new HttpRequest(rawRequest);
                dispatcher.execute(request, out);
                if (logger.isDebugEnabled()) {
                    request.unhideDebugInfo();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            requestBuffer.remove();
        }
    }
}
