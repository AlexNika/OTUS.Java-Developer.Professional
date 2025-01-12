package ru.otus.java.pro.webserver.http.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.webserver.http.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface RequestProcessor {
    Logger logger = LogManager.getLogger(RequestProcessor.class.getName());

    void execute(HttpRequest request, OutputStream out) throws IOException;

    static void send(@NotNull OutputStream out, @NotNull String response) {
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}