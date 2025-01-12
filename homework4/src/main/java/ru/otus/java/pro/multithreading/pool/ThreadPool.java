package ru.otus.java.pro.multithreading.pool;

import lombok.extern.slf4j.Slf4j;
import ru.otus.java.pro.multithreading.executor.TaskExecutor;
import ru.otus.java.pro.multithreading.queue.TaskQueue;

@Slf4j
public class ThreadPool {
    private final TaskQueue<Runnable> queue;
    private volatile boolean isShutdown = false;

    public ThreadPool(int queueSize, int nThread) {
        queue = new TaskQueue<>(queueSize);
        String threadName;
        TaskExecutor task;
        for (int count = 0; count < nThread; count++) {
            threadName = "Thread-" + count;
            task = new TaskExecutor(queue, isShutdown);
            Thread thread = new Thread(task, threadName);
            thread.start();
            if (isShutdown) {
                thread.interrupt();
                log.debug("Thread interrupted: {}", threadName);
            }
        }
    }

    public synchronized void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("Thread pool is down. New tasks are not accepted.");
        }
        queue.enqueue(task);
    }

    public synchronized void shutdown() {
        isShutdown = true;
        notifyAll();
    }
}
