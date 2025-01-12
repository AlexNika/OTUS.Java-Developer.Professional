package ru.otus.java.pro.webserver.http.response;

import lombok.Getter;

@Getter
public enum HttpStatusCode {

    SERVER_OK_200(200, "OK"),
    CLIENT_ERROR_404_NOT_FOUND(404, "Page Not Found"),
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    public final int statusCode;
    public final String message;

    HttpStatusCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
