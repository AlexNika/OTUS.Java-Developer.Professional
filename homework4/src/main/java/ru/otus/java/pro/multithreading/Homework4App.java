package ru.otus.java.pro.multithreading;

import lombok.extern.slf4j.Slf4j;
import ru.otus.java.pro.multithreading.pool.ThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Homework4App {
    private static final AtomicInteger taskCount = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(1000);
        ThreadPool threadPool = new ThreadPool(3, 4);
        for (int taskNumber = 1; taskNumber <= 15; taskNumber++) {
            final int finalTaskNumber = taskNumber;
            threadPool.execute(() -> task(finalTaskNumber));
        }
        Thread.sleep(1000);
        threadPool.shutdown();
        threadPool.execute(() -> task(16));
        log.info("Total tasks executed: {}", taskCount.get());
    }

    private static void task(int taskNumber) {
        taskCount.incrementAndGet();
        log.info("Start executing of task number: {}", taskNumber);
        log.debug("Some work is being done in the task number : {}", taskNumber);
        log.info("End executing of task number : {}", taskNumber);
    }
}
