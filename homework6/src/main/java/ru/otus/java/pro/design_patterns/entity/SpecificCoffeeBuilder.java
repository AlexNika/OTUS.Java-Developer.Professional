package ru.otus.java.pro.design_patterns.entity;

import java.math.BigDecimal;

public final class SpecificCoffeeBuilder implements CoffeeBuilder {
    private int id;
    private String blendName;
    private String body;
    private String country;
    private String intensifier;
    private String descriptor;
    private String variety;
    private BigDecimal price;

    public SpecificCoffeeBuilder id(int id) {
        this.id = id;
        return this;
    }

    public SpecificCoffeeBuilder blendName(String blendName) {
        this.blendName = blendName;
        return this;
    }

    public SpecificCoffeeBuilder body(String body) {
        this.body = body;
        return this;
    }

    public SpecificCoffeeBuilder country(String country) {
        this.country = country;
        return this;
    }

    public SpecificCoffeeBuilder intensifier(String intensifier) {
        this.intensifier = intensifier;
        return this;
    }

    public SpecificCoffeeBuilder descriptor(String descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public SpecificCoffeeBuilder variety(String variety) {
        this.variety = variety;
        return this;
    }

    public SpecificCoffeeBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Coffee build() {
        return new Coffee(id, blendName, body, country, intensifier, descriptor, variety, price);
    }
}
