package ru.otus.java.pro.design_patterns1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.design_patterns1.box.Box;
import ru.otus.java.pro.design_patterns1.box.Iterator;
import ru.otus.java.pro.design_patterns1.doll.Color;
import ru.otus.java.pro.design_patterns1.doll.Doll;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        int nestedDollsQty = 10;
        Box box = new Box(
                nestedDollsQty,
                new Doll(Color.RED, nestedDollsQty).getNestedDollsName(),
                new Doll(Color.GREEN, nestedDollsQty).getNestedDollsName(),
                new Doll(Color.BLUE, nestedDollsQty).getNestedDollsName(),
                new Doll(Color.MAGENTA, nestedDollsQty).getNestedDollsName());

        logger.info("SmallFirstIterator implemented");
        Iterator smallFirstIterator = box.getSmallFirstIterator();
        while (smallFirstIterator.hasNext()) {
            logger.info(smallFirstIterator.next());
        }
        logger.info("ColorFirstIterator implemented");
        Iterator colorFirstIterator = box.getColorFirstIterator();
        while (colorFirstIterator.hasNext()) {
            logger.info(colorFirstIterator.next());
        }
    }
}