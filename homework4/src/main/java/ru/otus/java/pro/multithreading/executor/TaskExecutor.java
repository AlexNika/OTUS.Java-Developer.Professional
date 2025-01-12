package ru.otus.java.pro.multithreading.executor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.java.pro.multithreading.queue.TaskQueue;

@Slf4j
@AllArgsConstructor
public class TaskExecutor implements Runnable {

    private TaskQueue<Runnable> queue;
    private volatile boolean isShutdown;

    @Override
    public void run() {
        while (!isShutdown) {
            String name = Thread.currentThread().getName();
            Runnable task = queue.dequeue();
            log.info("Task Started by Thread :{}", name);
            task.run();
            log.info("Task Finished by Thread :{}", name);
        }
    }
}
