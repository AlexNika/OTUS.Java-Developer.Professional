package ru.otus.java.pro.webserver.http.processors;

import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.webserver.http.request.HttpAccept;
import ru.otus.java.pro.webserver.http.request.HttpRequest;
import ru.otus.java.pro.webserver.http.response.HttpResponse;
import ru.otus.java.pro.webserver.http.response.Response;

import java.io.OutputStream;

import static ru.otus.java.pro.webserver.http.processors.RequestProcessor.send;

public class DefaultInternalServerErrorRequestProcessor implements RequestProcessor {

    @Override
    public void execute(@NotNull HttpRequest request, @NotNull OutputStream out) {
        logger.info("DefaultInternalServerErrorRequest processor executed");
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        HttpAccept acceptType = request.getAccept();
        Response httpresponse = HttpResponse.error500(acceptType);
        String response = templateRequest.prepareResponse(httpresponse);
        send(out, response);
    }
}
