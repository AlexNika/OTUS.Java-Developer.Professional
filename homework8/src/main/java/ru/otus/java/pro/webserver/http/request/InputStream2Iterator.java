package ru.otus.java.pro.webserver.http.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class InputStream2Iterator implements Iterator<Byte> {
    private final InputStream inputStream;

    public InputStream2Iterator(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public boolean hasNext() {
        try {
            return inputStream.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Byte next() {
        try {
            return (byte) inputStream.read();
        } catch (IOException e) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
