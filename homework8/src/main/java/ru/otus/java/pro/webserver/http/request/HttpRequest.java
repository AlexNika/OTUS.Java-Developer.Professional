package ru.otus.java.pro.webserver.http.request;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class HttpRequest {
    private static final Logger logger = LogManager.getLogger(HttpRequest.class.getName());

    @Getter(AccessLevel.NONE)
    private final String rawRequest;
    private String uri;
    private HttpMethod method;
    private HttpAccept accept;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private String body;
    @Getter(AccessLevel.NONE)
    private List<String> lines;
    @Getter(AccessLevel.NONE)
    private final int splitLinesIndex;

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.splitLinesIndex = getSplitLinesIndex();
        this.parseRequestLine();
        this.parseRequestHeader();
        this.parseAcceptType();
        this.parseRequestBody();
    }

    private void parseRequestLine() {
        String requestLine = lines.getFirst();
        int startIndex = requestLine.indexOf(" ");
        int endIndex = requestLine.indexOf(" ", startIndex + 1);
        this.uri = requestLine.substring(startIndex + 1, endIndex);
        this.method = HttpMethod.valueOf(requestLine.substring(0, startIndex));
        this.parameters = new HashMap<>();
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            this.uri = elements[0];
            String[] keysValues = elements[1].split("&");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                this.parameters.put(keyValue[0], keyValue[1]);
            }
        }
    }

    private void parseRequestHeader() {
        this.headers = new HashMap<>();
        for (String line : lines.subList(1, splitLinesIndex)) {
            String[] headerComponents = line.split(": ");
            headers.put(headerComponents[0].trim(), headerComponents[1].trim());
        }
    }

    private void parseRequestBody() {
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String line : lines.subList(splitLinesIndex, lines.size())) {
                stringBuilder.append(line);
            }
            this.body = stringBuilder.toString();
        }
    }

    public void unhideDebugInfo() {
        logger.debug("Uri: {}", this.uri);
        logger.debug("Http Method: {}", this.method);
        if (this.body != null) {
            logger.debug("body: {}", this.body);
        } else {
            logger.debug("body: null");
        }
        if (this.parameters != null && !this.parameters.isEmpty()) {
            for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                logger.debug("Uri parameters: {}: {}", entry.getKey(), entry.getValue());
            }
        }
        if (this.headers != null && !this.headers.isEmpty()) {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                logger.debug("Headers: {}: {}", entry.getKey(), entry.getValue());
            }
        }
        logger.debug("rawRequest:\n{}", rawRequest);
    }

    private int getSplitLinesIndex() {
        if (this.lines == null) {
            this.lines = rawRequest.lines().toList();
        }
        return (int) lines.stream()
                .takeWhile(s -> !s.isEmpty())
                .count();
    }

    private void parseAcceptType() {
        final String acceptHeader = "Accept";
        if (this.headers.containsKey(acceptHeader)) {
            accept = HttpAccept.getBestCompatibleAcceptType(this.headers.get(acceptHeader));
        } else {
            accept = HttpAccept.ANY;
        }
    }
}
