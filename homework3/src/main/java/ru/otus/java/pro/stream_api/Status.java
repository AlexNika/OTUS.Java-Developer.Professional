package ru.otus.java.pro.stream_api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

enum Status {
    NONE("None"),
    SCHEDULED("Scheduled"),
    QUEUED("Queued"),
    RUNNING("Running"),
    SUCCESS("Success"),
    RESTARTING("Restarting"),
    FAILED("Failed"),
    SKIPPED("Skipped"),
    DEFERRED("Deferred"),
    REMOVED("Removed");

    private final String title;

    Status(String title) {
        this.title = title;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Status{" +
                "title='" + title + '\'' +
                '}';
    }
}
