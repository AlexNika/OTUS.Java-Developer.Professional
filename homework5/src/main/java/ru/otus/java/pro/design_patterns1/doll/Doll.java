package ru.otus.java.pro.design_patterns1.doll;

import java.util.ArrayList;
import java.util.List;

public class Doll {
    private List<String> nestedDollsName;

    public Doll(Color color, int nestedDollsQry) {
        this.nestedDollsName = setNestedDollsName(color, nestedDollsQry);
    }

    public List<String> getNestedDollsName() {
        return this.nestedDollsName;
    }

    private List<String> setNestedDollsName(Color color, int nestedDollsQty) {
        nestedDollsName = new ArrayList<>();
        for (int i = 0; i < nestedDollsQty; i++) {
            nestedDollsName.add(String.valueOf(color).toLowerCase() + i);
        }
        return nestedDollsName;
    }
}
