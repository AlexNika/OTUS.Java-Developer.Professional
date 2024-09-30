package ru.otus.java.pro.reflection;

import ru.otus.java.pro.reflection.tests.TestSuite;
import ru.otus.java.pro.reflection.tests.DisabledTestSuite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        TestRunner.run(DisabledTestSuite.class);
        TestRunner.run(TestSuite.class);
        logger.info("Total tests have been running: {}", TestRunner.getTotalTests());
        logger.info("Number of successful tests: {}", TestRunner.getSuccessTests());
        logger.info("Number if failed tests: {}", TestRunner.getFailedTests());
    }
}
