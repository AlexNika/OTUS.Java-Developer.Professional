package ru.otus.java.pro.multithreading.queue;

public interface Queue<T> {
    void enqueue(T task);

    T dequeue();
}
