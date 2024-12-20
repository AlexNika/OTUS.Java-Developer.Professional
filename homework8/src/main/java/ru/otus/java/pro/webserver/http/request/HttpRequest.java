package ru.otus.java.pro.webserver.http.request;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
public class HttpRequest {
    private static final Logger logger = LogManager.getLogger(HttpRequest.class.getName());

    private final InputStream in;
    private final int initialRequestSize;
    private String uri;
    private HttpMethod method;
    private String version;
    private HttpAccept accept;
    private final Map<String, String> parameters;
    private final Map<String, String> headers;
    private String body;

    public HttpRequest(@NotNull InputStream in) throws IOException {
        this.in = in;
        this.initialRequestSize = in.available();
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        this.parseHttpRequest();
        this.parseAcceptType();
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public void parseHttpRequest() {
        logger.info("Method 'parseHttpRequest' started");
        try {
            int chunk;
            int bufferSize;
            int currentRequestSize = initialRequestSize;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while (currentRequestSize != 0) {
                for (chunk = in.read(); chunk != '\n' && chunk != -1; chunk = in.read()) {
                    buffer.write(chunk);
                }
                bufferSize = buffer.size() - 1;
                byte[] bufferByteArray = Arrays.copyOfRange(buffer.toByteArray(), 0, bufferSize);
                logger.debug("buffer (+CR) -> {}", buffer);
                if (currentRequestSize == initialRequestSize) {
                    parseFirstLine(bufferByteArray);
                }
                if (currentRequestSize != initialRequestSize && bufferSize != 0) {
                    parseHeaderLines(bufferByteArray);
                }
                if (currentRequestSize != initialRequestSize && bufferSize == 0 && parseBodyLines(currentRequestSize))
                    bufferSize = currentRequestSize - 2;

                currentRequestSize = currentRequestSize - bufferSize - 2;
                buffer.reset();
            }
            logger.info("Method 'parseHttpRequest' finished");
        } catch (IOException e) {
            logger.error("The error occurred during http request parsing process: {}", e.getMessage());
        }
    }

    private void parseFirstLine(byte @NotNull [] buffer) {
        logger.debug("Try to parse byte array of request first line");
        List<byte[]> bytesList;
        bytesList = splitLine(buffer, new byte[]{32});
        if (bytesList.isEmpty()) return;
        this.method = HttpMethod.valueOf(new String(bytesList.get(0), StandardCharsets.UTF_8));
        this.uri = new String(bytesList.get(1), StandardCharsets.UTF_8);
        this.version = new String(bytesList.get(2), StandardCharsets.UTF_8);
        bytesList = splitLine(bytesList.get(1), new byte[]{63});
        if (bytesList.size() > 1) {
            this.uri = new String(bytesList.get(0), StandardCharsets.UTF_8);
            bytesList = splitLine(bytesList.get(1), new byte[]{38});
            for (byte[] bytes : bytesList) {
                List<byte[]> bytesParameters;
                bytesParameters = splitLine(bytes, new byte[]{61});
                this.parameters.put(new String(bytesParameters.get(0), StandardCharsets.UTF_8),
                        new String(bytesParameters.get(1), StandardCharsets.UTF_8));
            }
        }
    }

    private void parseHeaderLines(byte @NotNull [] buffer) {
        logger.debug("Try to parse byte array of request headers line");
        List<byte[]> bytesList;
        bytesList = splitLine(buffer, new byte[]{58, 32});
        if (bytesList.isEmpty()) return;
        this.headers.put(new String(bytesList.get(0), StandardCharsets.UTF_8),
                new String(bytesList.get(1), StandardCharsets.UTF_8));
    }

    private boolean parseBodyLines(int currentRequestSize) throws IOException {
        logger.debug("Try to parse byte array of request body line");
        byte[] bodyBuffer = new byte[currentRequestSize - 2];
        if (in.read(bodyBuffer) != -1) {
            this.body = new String(bodyBuffer, StandardCharsets.UTF_8);
            return true;
        }
        return false;
    }

    private void parseAcceptType() {
        final String acceptHeader = "Accept";
        if (this.headers.containsKey(acceptHeader)) {
            accept = HttpAccept.getBestCompatibleAcceptType(this.headers.get(acceptHeader));
        } else {
            accept = HttpAccept.ANY;
        }
    }

    private @NotNull List<byte[]> splitLine(byte @NotNull [] input, byte[] pattern) {
        logger.debug("Try to split input byte array by pattern byte array");
        if (input.length == 0) {
            return Collections.emptyList();
        }
        List<byte[]> bytes = new LinkedList<>();
        int blockStart = 0;
        for (int i = 0; i < input.length; i++) {
            if (isMatch(pattern, input, i)) {
                bytes.add(Arrays.copyOfRange(input, blockStart, i));
                blockStart = i + pattern.length;
                i = blockStart;
            }
        }
        bytes.add(Arrays.copyOfRange(input, blockStart, input.length));
        return bytes;
    }

    @Contract(pure = true)
    private boolean isMatch(byte @NotNull [] pattern, byte[] input, int pos) {
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[pos + i]) {
                return false;
            }
        }
        return true;
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
