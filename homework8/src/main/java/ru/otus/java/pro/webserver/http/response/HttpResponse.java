package ru.otus.java.pro.webserver.http.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.webserver.http.processors.TemplateRequestPreprocessor;
import ru.otus.java.pro.webserver.http.request.HttpAccept;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static ru.otus.java.pro.webserver.http.response.HttpStatusCode.*;

public class HttpResponse extends Response {
    private static final Logger logger = LogManager.getLogger(HttpResponse.class.getName());

    @Contract("_ -> new")
    public static @NotNull Response ok(HttpAccept acceptType) {
        return new Response(SERVER_OK_200.getStatusCode(), SERVER_OK_200.getMessage(), acceptType);
    }

    @Contract("_, _ -> new")
    public static @NotNull Response ok(HttpAccept acceptType, String responseBody) {
        return new Response(SERVER_OK_200.getStatusCode(), SERVER_OK_200.getMessage(), acceptType, responseBody);
    }

    public static @NotNull Response error404(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>404 Page Not Found</h1></body></html>";
        return new Response(CLIENT_ERROR_404_NOT_FOUND.getStatusCode(),
                CLIENT_ERROR_404_NOT_FOUND.getMessage(), acceptType, responseBody);
    }

    @Contract("_ -> new")
    public static @NotNull Response error500(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>500 INTERNAL SERVER ERROR</h1></body></html>";
        return new Response(SERVER_ERROR_500_INTERNAL_SERVER_ERROR.getStatusCode(),
                SERVER_ERROR_500_INTERNAL_SERVER_ERROR.getMessage(), acceptType, responseBody);
    }

    public static void sendResponse(Response httpresponse, @NotNull OutputStream out) {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String response = templateRequest.prepareResponse(httpresponse);
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
