package ru.otus.java.pro.design_patterns1.box;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.NoSuchElementException;

public class Box implements Collection {
    private static final Logger logger = LogManager.getLogger(Box.class);

    private final int nestedDollsQty;
    private final List<String> red;
    private final List<String> green;
    private final List<String> blue;
    private final List<String> magenta;

    public Box(int nestedDollsQty, List<String> red, List<String> green, List<String> blue, List<String> magenta) {
        this.nestedDollsQty = nestedDollsQty;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.magenta = magenta;
    }

    @Override
    public Iterator getSmallFirstIterator() {
        return new SmallFirstIterator();
    }

    @Override
    public Iterator getColorFirstIterator() {
        return new ColorFirstIterator();
    }

    private class SmallFirstIterator implements Iterator {
        int nestingIndex = 0;
        int dollIndex = 0;
        private final List<List<String>> listOfDolls = List.of(red, green, blue, magenta);

        @Override
        public boolean hasNext() {
            return nestingIndex < nestedDollsQty && dollIndex < listOfDolls.size();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                logger.error("The error occurs when calling next() on SmallFirstIterator when there are no more elements");
                throw new NoSuchElementException();
            }
            String next = listOfDolls.get(dollIndex).get(nestingIndex);
            dollIndex++;
            if (dollIndex == listOfDolls.size()) {
                nestingIndex++;
                dollIndex = 0;
            }
            return next;
        }
    }

    private class ColorFirstIterator implements Iterator {
        int nestingIndex = 0;
        int dollIndex = 0;
        private final List<List<String>> listOfDolls = List.of(red, green, blue, magenta);

        @Override
        public boolean hasNext() {
            return nestingIndex < nestedDollsQty && dollIndex < listOfDolls.size();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                logger.error("The error occurs when calling next() on ColorFirstIterator when there are no more elements");
                throw new NoSuchElementException();
            }
            String next = listOfDolls.get(dollIndex).get(nestingIndex++);
            if (nestingIndex == nestedDollsQty) {
                dollIndex++;
                nestingIndex = 0;
            }
            return next;
        }
    }
}
