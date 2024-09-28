package ru.otus.java.pro.stream_api;

import static ru.otus.java.pro.stream_api.Status.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Stream<Task> tasks = getTaskStream();
        logger.info("Tasks with status = '{}': {}", SUCCESS.name(), getTasksFilteredByStatus(tasks, SUCCESS));
        tasks = getTaskStream();
        logger.info("Tasks with status = '{}': {}", FAILED.name(), getTasksFilteredByStatus(tasks, FAILED));
        tasks = getTaskStream();
        logger.info("Task with id = {}: {}", 9, (isTaskAvailableByID(tasks, 9) ? "available":"not available"));
        tasks = getTaskStream();
        logger.info("Task with id = {}: {}", 19, (isTaskAvailableByID(tasks, 19) ? "available":"not available"));
        tasks = getTaskStream();
        logger.info("Tasks sorted by status {}", getTasksSortedByStatus(tasks));
        tasks = getTaskStream();
        logger.info("Tasks count with status = '{}': {}", RUNNING.name(), countTasksByStatus(tasks, RUNNING));
        tasks = getTaskStream();
        logger.info("Tasks count with status = '{}': {} ", REMOVED.name(), countTasksByStatus(tasks, REMOVED));

    }

    private static @NotNull List<Task> getTasksFilteredByStatus(@NotNull Stream<Task> tasks, Status status) {
        return tasks
                .filter(task -> task.status().equals(status))
                .toList();
    }

    private static @NotNull Boolean isTaskAvailableByID(@NotNull Stream<Task> tasks, long id) {
        return tasks
                .anyMatch(task -> task.id() == id);
    }

    private static @NotNull List<Task> getTasksSortedByStatus(@NotNull Stream<Task> tasks) {
        return tasks
                .sorted(Comparator.comparing(Task::status))
                .toList();
    }

    private static long countTasksByStatus(@NotNull Stream<Task> tasks, Status status) {
        return tasks
                .filter(task -> task.status().equals(status))
                .count();
    }

    private static @NotNull Stream<Task> getTaskStream() {
        return Stream.of(
                new Task(1L, "Task1", RUNNING),
                new Task(2L, "Task2", SUCCESS),
                new Task(3L, "Task3", SUCCESS),
                new Task(4L, "Task4", FAILED),
                new Task(5L, "Task5", REMOVED),
                new Task(6L, "Task6", DEFERRED),
                new Task(7L, "Task7", NONE),
                new Task(8L, "Task8", RUNNING),
                new Task(9L, "Task9", SKIPPED),
                new Task(10L, "Task10", QUEUED)
        );
    }
}
