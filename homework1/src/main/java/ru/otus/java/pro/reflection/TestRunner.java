package ru.otus.java.pro.reflection;

import ru.otus.java.pro.reflection.annotations.*;
import ru.otus.java.pro.reflection.exceptions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class TestRunner {
    private static final Logger logger = LogManager.getLogger(TestRunner.class);
    private static final int MIN_PRIORITY = 1;
    private static final int MAX_PRIORITY = 10;
    private static int successTests = 0;
    private static int failedTests = 0;

    public static void run(@NotNull Class<?> testSuiteClass) {
        if (isClassHasDisabledAnnotation(testSuiteClass)) {
            logger.info("The reason for disable is: {}", testSuiteClass.getAnnotation(Disabled.class).message());
            return;
        }
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;
        Map<Method, Integer> orderedTestsByPriority = new HashMap<>();
        Method[] methods = testSuiteClass.getDeclaredMethods();
        for (Method m : methods) {
            if (isMethodHasIncorrectMarkUp(m)) {
                throw new AnnotationMarkupException("Incorrect annotation markup in the method '" + m.getName() + "'");
            }
            if (isMethodHasNoAnyAnnotations(m)) continue;
            if (isMethodHasDisabledAnnotation(m)) continue;
            if (m.isAnnotationPresent(Test.class)) {
                int priority = m.getDeclaredAnnotation(Test.class).priority();
                if (isPriorityLimitsExceeded(priority)) continue;
                orderedTestsByPriority.put(m, priority);
            }
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                if (isClassHasIdenticalAnnotations(beforeSuiteMethod, BeforeSuite.class)) continue;
                beforeSuiteMethod = m;
            }
            if (m.isAnnotationPresent(AfterSuite.class)) {
                if (isClassHasIdenticalAnnotations(afterSuiteMethod, AfterSuite.class)) continue;
                afterSuiteMethod = m;
            }
        }
        runMethod(beforeSuiteMethod);
        orderedTestsByPriority
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Method, Integer>comparingByValue().reversed())
                .forEach(entry -> runMethod(entry.getKey()));
        runMethod(afterSuiteMethod);
    }

    private static void runMethod(Method m) {
        if (m == null) {
            return;
        }
        try {
            m.invoke(null);
            if (m.isAnnotationPresent(Test.class)) successTests++;
        } catch (InvocationTargetException | IllegalAccessException e) {
            if (m.isAnnotationPresent(Test.class)) failedTests++;
            logger.error("Test '{}' with annotation failed", m.getName());
        }
    }

    private static boolean isPriorityLimitsExceeded(int priority) {
        try {
            if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
                throw new IllegalPriorityException("Priority range exceeded!");
            }
        } catch (IllegalPriorityException e) {
            logger.error("There is illegal priority catching in annotation. Skip running it!", e);
            return true;
        }
        return false;
    }

    private static boolean isClassHasIdenticalAnnotations(Method method, Class<?> annotation) {
        try {
            if (method != null) {
                throw new AnnotationCountException("Duplicate annotation '@" + annotation.getSimpleName() +
                        "' in class!");
            }
        } catch (AnnotationCountException e) {
            logger.error("There is more than one identical annotation '@{}' in the class. Skip running it!",
                    annotation.getSimpleName(), e);
            return true;
        }
        return false;
    }

    private static boolean isMethodHasIncorrectMarkUp(@NotNull Method m) {
        Annotation[] annotations = m.getDeclaredAnnotations();
        if (annotations.length <= 1) return false;
        if (m.isAnnotationPresent(Disabled.class)) return false;
        if ((m.isAnnotationPresent(BeforeSuite.class) && (m.isAnnotationPresent(AfterSuite.class)))) {
            logger.info("Method '{}' - has incorrect markup. It has '@BeforeSuite' and '@AfterSuite' " +
                    "annotations at the same time!", m.getName());
            return true;
        }
        if ((m.isAnnotationPresent(BeforeSuite.class) || m.isAnnotationPresent(AfterSuite.class)) &&
                m.isAnnotationPresent(Test.class)) {
            logger.info("Method '{}' - has incorrect markup. It has '@BeforeSuite' or '@AfterSuite' and " +
                    "'@Test' annotations at the same time!", m.getName());
            return true;
        }
        return false;
    }

    private static boolean isMethodHasDisabledAnnotation(@NotNull Method m) {
        if (m.isAnnotationPresent(Disabled.class)) {
            logger.info("Method '{}' - has 'Disabled' annotation. Skip running it!", m.getName());
            return true;
        }
        return false;
    }

    private static boolean isMethodHasNoAnyAnnotations(@NotNull Method m) {
        Annotation[] annotations = m.getDeclaredAnnotations();
        if (annotations.length == 0) {
            logger.info("Method '{}' - has no any annotation. Skip running it!", m.getName());
            return true;
        }
        return false;
    }

    private static boolean isClassHasDisabledAnnotation(@NotNull Class<?> testSuiteClass) {
        if (testSuiteClass.isAnnotationPresent(Disabled.class)) {
            logger.info("Class '{}' - has 'Disabled' annotation. Skip running it!",
                    testSuiteClass.getSimpleName());
            return true;
        }
        return false;
    }

    public static int getTotalTests() {
        return successTests + failedTests;
    }

    public static int getSuccessTests() {
        return successTests;
    }

    public static int getFailedTests() {
        return failedTests;
    }
}
