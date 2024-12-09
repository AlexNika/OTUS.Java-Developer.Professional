package ru.otus.java.pro.webserver;

import ru.otus.java.pro.webserver.config.Config;
import ru.otus.java.pro.webserver.server.WebServer;

public class WebServerLauncher {

    public static void main(String[] args) {
        new WebServer(new Config()).start();
    }
}
