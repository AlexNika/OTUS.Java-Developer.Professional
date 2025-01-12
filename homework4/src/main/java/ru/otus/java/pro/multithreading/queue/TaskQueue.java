package ru.otus.java.pro.multithreading.queue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Slf4j
@AllArgsConstructor
public class TaskQueue<T> implements Queue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxTasksInQueue;

    @Override
    public synchronized void enqueue(T task) {
        while (this.queue.size() == this.maxTasksInQueue) {
            try {
                log.debug("Queue size exceeded. Waiting for a place in the queue to be vacated");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("The error occurs when to add task to the queue: {}", e.getMessage());
            }
        }
        if (this.queue.isEmpty()) {
            log.debug("Queue is empty. Notify all threads about it");
            notifyAll();
        }
        this.queue.addLast(task);
        log.debug("Add task to the queue");
    }

    @Override
    public synchronized T dequeue() {
        while (this.queue.isEmpty()) {
            try {
                log.debug("Queue is empty. Waiting for task");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("The error occurs when to remove task from the queue: {}", e.getMessage());
            }
        }
        if (this.queue.size() == this.maxTasksInQueue) {
            log.debug("Task queue size exceeded. Notify all threads about it");
            notifyAll();
        }
        log.debug("Take task from the queue");
        return this.queue.removeFirst();
    }
}
