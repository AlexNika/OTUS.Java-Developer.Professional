package ru.otus.java.pro.webserver.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.otus.java.pro.webserver.http.request.HttpAccept;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private int statusCode;
    private String codeDescription;
    private HttpAccept acceptType;
    private String responseBody;

    public Response(int statusCode, String codeDescription, HttpAccept acceptType) {
        this.statusCode = statusCode;
        this.codeDescription = codeDescription;
        this.acceptType = acceptType;
    }
}
