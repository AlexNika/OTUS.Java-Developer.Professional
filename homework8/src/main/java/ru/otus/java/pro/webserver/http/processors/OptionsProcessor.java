package ru.otus.java.pro.webserver.http.processors;

import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.webserver.http.request.HttpMethod;
import ru.otus.java.pro.webserver.http.request.HttpRequest;

import java.io.OutputStream;
import java.time.LocalDateTime;

import static ru.otus.java.pro.webserver.http.processors.RequestProcessor.send;

public class OptionsProcessor implements RequestProcessor {

    @Override
    public void execute(HttpRequest request, @NotNull OutputStream out) {
        logger.info("'OPTION' method processor executed");
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String datetime = LocalDateTime.now().toString();
        String methods = HttpMethod.getAllMethods();
        String response = templateRequest.prepareOptionsResponse(200, "OK", datetime, methods,
                "http://localhost:8080", methods,
                "Origin, Content-Type, Accept");
        send(out, response);
    }
}
