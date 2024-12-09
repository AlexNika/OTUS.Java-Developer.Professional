package ru.otus.java.pro.webserver.http.request;

import lombok.Getter;

@Getter
public enum HttpAccept {
    ANY("*/*"),
    HTML("text/html"),
    JSON("application/json");

    public final String literal;

    HttpAccept(String literal) {
        this.literal = literal;
    }

    public static HttpAccept getBestCompatibleAcceptType(String acceptType) {
        for (HttpAccept type : HttpAccept.values()) {
            if (type.literal.equalsIgnoreCase(acceptType)) {
                return type;
            }
        }
        return HttpAccept.ANY;
    }
}
