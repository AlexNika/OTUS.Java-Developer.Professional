package ru.otus.java.pro.reflection.tests;

import ru.otus.java.pro.reflection.annotations.AfterSuite;
import ru.otus.java.pro.reflection.annotations.BeforeSuite;
import ru.otus.java.pro.reflection.annotations.Disabled;
import ru.otus.java.pro.reflection.annotations.Test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestSuite {
    private static final Logger logger = LogManager.getLogger(TestSuite.class);

    @BeforeSuite
    public static void setUpTest() {
        logger.info("setUpTest(): test method with an annotation '@BeforeSuite'");
    }

    public static void test() {
        logger.info("test(): test method without any annotation");
    }

    @BeforeSuite
    public static void setUpTest2() {
        logger.info("setUpTest2(): test method with second annotation '@BeforeSuite'");
    }

    @Test(priority = 100)
    public static void testIllegalPriority() {
        logger.info("testIllegalPriority(): test method with illegal(100) priority in annotation '@Test'");
    }

    @Test
    public static void test1DefaultPriority() {
        logger.info("test1DefaultPriority(): test method with default(5) priority in annotation '@Test'");
    }

    @Test
    public static void test2DefaultPriority() {
        logger.info("test2DefaultPriority(): test method with default(5) priority in annotation '@Test'");
    }

    @Test(priority = 10)
    public static void testMaxPriority() {
        logger.info("testMaxPriority(): test method with max(10) priority in annotation '@Test'");
    }

    @Test(priority = 8)
    public static void testNonDefaultPriority8() {
        logger.info("testNonDefaultPriority8(): test method with non default priority = 8");
    }

    @Test(priority = 3)
    public static void testNonDefaultPriority3() {
        logger.info("testNonDefaultPriority3(): test method with non default priority = 3");
    }

    @Test(priority = 1)
    public static void testMinPriority() {
        logger.info("testMinPriority(): test method with min(1) priority in annotation '@Test'");
    }

    @Test(priority = 9)
    @Disabled(message = "test with non default priority 9 has been disabled")
    public static void disabledTest() {
        logger.info("disabledTest(): test method with disabled annotation");

    }

    @AfterSuite
    public static void tearDownTest() {
        logger.info("tearDownTest(): test method with an annotation '@AfterSuite'");
    }

    @Disabled(message = "Disable incorrectly marked up test")
    @Test
    @AfterSuite
    public static void testWithIncorrectMarkUp() {
        logger.info("testWithIncorrectMarkUp(): test method with two annotations ('@Test' and '@AfterSuite')");
    }

    @Disabled(message = "Disable incorrectly marked up test")
    @BeforeSuite
    @AfterSuite
    public static void test2WithIncorrectMarkUp() {
        logger.info("test2WithIncorrectMarkUp(): test method with two annotations ('@Test' and '@AfterSuite')");
    }
}
