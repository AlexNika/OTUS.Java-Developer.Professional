package ru.otus.java.pro.webserver.http.processors;

import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.webserver.http.request.HttpAccept;
import ru.otus.java.pro.webserver.http.request.HttpRequest;
import ru.otus.java.pro.webserver.http.response.HttpResponse;
import ru.otus.java.pro.webserver.http.response.Response;

import java.io.OutputStream;

import static ru.otus.java.pro.webserver.http.processors.RequestProcessor.send;

public class RootPageRequestProcessor implements RequestProcessor {

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        logger.info("Root homepage processor executed");
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String responseBody = "<html><body><h1>Hello Client!...</h1><h2>Welcome!</h2></body></html>";
        HttpAccept acceptType = request.getAccept();
        Response httpresponse = HttpResponse.ok(acceptType, responseBody);
        String response = templateRequest.prepareResponse(httpresponse);
        send(out, response);
    }
}
