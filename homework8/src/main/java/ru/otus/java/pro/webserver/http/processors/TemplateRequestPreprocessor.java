package ru.otus.java.pro.webserver.http.processors;

import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.webserver.http.response.Response;

public class TemplateRequestPreprocessor {

    public String prepareResponse(@NotNull Response response) {
        String responseTemplate = """
                HTTP/1.1 %d %s\r
                Content-Type: %s\r
                \r
                %s
                """;
        return (responseTemplate.formatted(response.getStatusCode(),
                response.getCodeDescription(), response.getAcceptType().getLiteral(), response.getResponseBody()));
    }

    public String prepareResponseWithoutBody(@NotNull Response response) {
        String responseTemplate = """
                HTTP/1.1 %d %s\r
                \r
                """;
        return (responseTemplate.formatted(response.getStatusCode(), response.getCodeDescription()));
    }

    public String prepareOptionsResponse(int statusCode, String statusText, String localDate, String allowedMethods,
                                         String accessControlAllowOrigin, String accessControlAllowMethods,
                                         String accessControlAllowHeaders) {
        String responseTemplate = """
                HTTP/1.1 %d %s\r
                Date: %s\r
                Allow: %s\r
                Access-Control-Allow-Origin: %s\r
                Access-Control-Allow-Methods: %s\r
                Access-Control-Allow-Headers: %s\r
                \r
                """;
        return (responseTemplate.formatted(statusCode, statusText, localDate, allowedMethods, accessControlAllowOrigin,
                accessControlAllowMethods, accessControlAllowHeaders));
    }
}
