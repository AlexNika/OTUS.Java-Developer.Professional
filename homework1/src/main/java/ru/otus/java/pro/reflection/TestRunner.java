package ru.otus.java.pro.reflection;

import ru.otus.java.pro.reflection.annotations.*;
import ru.otus.java.pro.reflection.exceptions.*;

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
    private static int totalTests = 0;
    private static int successTests = 0;

    public static void run(@NotNull Class<?> testSuiteClass) {
        if (isClassHasDisabledAnnotation(testSuiteClass)) return;
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;
        int beforeSuiteAnnotationCount = 0;
        int afterSuiteAnnotationCount = 0;
        Map<Method, Integer> orderedTestsByPriority = new HashMap<>();
        Method[] methods = testSuiteClass.getDeclaredMethods();
        totalTests = methods.length;
        for (Method m : methods) {
            int annotationsCount = m.getDeclaredAnnotations().length;
            if (isMethodHasNoAnyAnnotations(m, annotationsCount)) continue;
            if (isMethodHasDisabledAnnotation(m)) continue;
            if (isMethodHasMoreThenOneAnnotation(m, annotationsCount)) continue;
            if (m.isAnnotationPresent(Test.class)) {
                int priority = m.getDeclaredAnnotation(Test.class).priority();
                if (isPriorityLimitsExceeded(priority)) continue;
                orderedTestsByPriority.put(m, priority);
            }
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuiteAnnotationCount++;
                if (isClassHasIdenticalAnnotations(beforeSuiteAnnotationCount, BeforeSuite.class)) continue;
                beforeSuiteMethod = m;
            }
            if (m.isAnnotationPresent(AfterSuite.class)) {
                afterSuiteAnnotationCount++;
                if (isClassHasIdenticalAnnotations(afterSuiteAnnotationCount, AfterSuite.class)) continue;
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
            successTests++;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
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

    private static boolean isClassHasIdenticalAnnotations(int count, Class<?> annotation) {
        try {
            if (count > 1) {
                throw new AnnotationCountException("Duplicate annotation in class!");
            }
        } catch (AnnotationCountException e) {
            logger.error("There is more than one identical annotation '@{}' in the class. Skip running it!",
                    annotation.getSimpleName(), e);
            return true;
        }
        return false;
    }

    private static boolean isMethodHasMoreThenOneAnnotation(Method m, int annotationsCount) {
        if (annotationsCount > 1 && !m.isAnnotationPresent(Disabled.class)) {
            logger.info("Method '{}' - has more than one annotation. Skip running it!", m.getName());
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

    private static boolean isMethodHasNoAnyAnnotations(@NotNull Method m, int annotationsCount) {
        if (annotationsCount == 0) {
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
        return totalTests;
    }

    public static int getSuccessTests() {
        return successTests;
    }

    public static int getFailedTests() {
        return totalTests - successTests;
    }
}
