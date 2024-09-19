package ru.otus.java.pro.reflection.tests;

import ru.otus.java.pro.reflection.annotations.Disabled;
import ru.otus.java.pro.reflection.annotations.Test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Disabled(message = "Class is not needed")
public class DisabledTestSuite {
    private static final Logger logger = LogManager.getLogger(DisabledTestSuite.class);

    @Test
    public static void test() {
        logger.info("test(): test method with default priority");
    }
}
