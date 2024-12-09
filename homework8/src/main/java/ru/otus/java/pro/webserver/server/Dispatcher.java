package ru.otus.java.pro.webserver.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.webserver.http.processors.*;
import ru.otus.java.pro.webserver.http.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private static final Logger logger = LogManager.getLogger(Dispatcher.class.getName());
    private final RequestProcessor defaultNotFoundRequestProcessor;
    private final RequestProcessor defaultInternalServerErrorProcessor;
    private final Map<String, RequestProcessor> processors;
    private final Connector connector;

    public Dispatcher(Connector connector) {
        this.connector = connector;
        this.processors = new HashMap<>();
        this.processors.put("GET /", new RootPageRequestProcessor());
        this.processors.put("GET /shutdown", new ShutdownRequestProcessor(connector));
        this.processors.put("OPTIONS /", new OptionsProcessor());
        this.defaultNotFoundRequestProcessor = new DefaultNotFoundRequestProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorRequestProcessor();
    }

    public void execute(HttpRequest request, OutputStream out) throws IOException {
        logger.debug("serverOperationStatus: {}", connector.isServerOperationStatus());
        try {
            if (!processors.containsKey(request.getRoutingKey())) {
                defaultNotFoundRequestProcessor.execute(request, out);
                return;
            }
            processors.get(request.getRoutingKey()).execute(request, out);
        } catch (Exception e) {
            logger.error("I/O error occurs", e);
            defaultInternalServerErrorProcessor.execute(request, out);
        }
    }
}
