package ru.otus.java.pro.reflection.tests;

import ru.otus.java.pro.reflection.annotations.Disabled;
import ru.otus.java.pro.reflection.annotations.Test;

@Disabled
public class DisabledTestSuite {
    @Test
    public static void test() {
        System.out.println("processing test with default priority");
    }
}
