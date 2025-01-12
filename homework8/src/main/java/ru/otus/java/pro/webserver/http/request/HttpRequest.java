package ru.otus.java.pro.webserver.http.request;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Getter
public class HttpRequest {
    private static final Logger logger = LogManager.getLogger(HttpRequest.class.getName());

    private final InputStream in;
    private String uri;
    private HttpMethod method;
    private String version;
    private HttpAccept accept;
    private final Map<String, String> parameters;
    private final Map<String, String> headers;
    private String body;

    public HttpRequest(@NotNull InputStream in) {
        this.in = in;
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        this.parseHttpRequest();
        this.parseAcceptType();
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }

    private void parseHttpRequest() {
        logger.info("Method 'parseHttpRequest' started");
        InputStream2Iterator iterator = new InputStream2Iterator(in);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bodySize = 0;
        int spaceCount = 0;
        boolean requestLine = true;
        boolean headerLines = false;
        boolean bodyOrEndLines = false;
        String headerKey = "";
        int nextByte = 0;
        int previousByte;
        while (iterator.hasNext()) {
            previousByte = nextByte;
            nextByte = iterator.next();
            switch (nextByte) {
                case 32 -> {
                    if (requestLine && spaceCount == 1) {
                        this.uri = buffer.toString();
                        buffer.reset();
                    }
                    if (requestLine && spaceCount == 0) {
                        this.method = HttpMethod.valueOf(buffer.toString());
                        buffer.reset();
                        spaceCount++;
                    }
                    if (headerLines && previousByte != 58) buffer.write(nextByte);
                    if (bodyOrEndLines) {
                        buffer.write(nextByte);
                        bodySize++;
                    }
                }
                case 58 -> {
                    if (headerLines) {
                        headerKey = buffer.toString();
                        buffer.reset();
                    }
                    if (bodyOrEndLines) {
                        buffer.write(nextByte);
                        bodySize++;
                    }
                }
                case 13 -> {
                    if (headerLines && previousByte == 10) {
                        headerLines = false;
                        bodyOrEndLines = true;
                        break;
                    }
                    if (headerLines) {
                        headers.put(headerKey, buffer.toString());
                        buffer.reset();
                    }
                    if (requestLine) {
                        this.version = buffer.toString();
                        requestLine = false;
                        headerLines = true;
                        buffer.reset();
                    }
                    if (bodyOrEndLines) {
                        buffer.write(nextByte);
                        bodySize++;
                        if (headers.containsKey("Content-Length") &&
                                Integer.parseInt(headers.get("Content-Length")) == bodySize) {
                            this.body = buffer.toString();
                            buffer.reset();
                        }
                    }
                }
                case 10 -> {
                    if (bodyOrEndLines) {
                        buffer.write(nextByte);
                        bodySize++;
                    }
                }
                default -> {
                    buffer.write(nextByte);
                    if (bodyOrEndLines) bodySize++;
                }
            }
        }
        parseURI();
        logger.info("Method 'parseHttpRequest' finished");
    }

    private void parseURI() {
        if (this.uri.contains("?")) {
            logger.debug("Request parameters available in URI");
            for (String parameter : this.uri.substring(this.uri.indexOf("?") + 1).split("&")) {
                logger.debug("parameter: -> {}", parameter);
                if (parameter.contains("=")) {
                    parameters.put(
                            parameter.substring(0, parameter.indexOf("=")),
                            parameter.substring(parameter.indexOf("=") + 1));
                }
            }
            this.uri = this.uri.substring(0, this.uri.indexOf("?"));
        }
    }

    private void parseAcceptType() {
        if (this.headers.containsKey("Accept")) {
            accept = HttpAccept.getBestCompatibleAcceptType(this.headers.get("Accept"));
        } else {
            accept = HttpAccept.ANY;
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "uri='" + uri + '\'' +
                ", method=" + method +
                ", version='" + version + '\'' +
                ", parameters=" + parameters +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
