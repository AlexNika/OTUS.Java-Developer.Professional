package ru.otus.java.pro.webserver.http.request;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

@Getter
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private static final Logger logger = LogManager.getLogger(HttpMethod.class.getName());
    private static final Iterator<HttpMethod> httpMethodValues = Arrays.stream(values()).iterator();
    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static @NotNull String getAllMethods() {
        StringBuilder methodsSB = new StringBuilder();
        int count = 0;
        while (httpMethodValues.hasNext()) {
            methodsSB.append(httpMethodValues.next());
            if (count < values().length - 1) {
                methodsSB.append(", ");
                count++;
            }
        }
        logger.debug("All available HTTP methods: {}", methodsSB);
        return methodsSB.toString();
    }
}

