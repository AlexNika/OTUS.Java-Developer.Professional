package ru.otus.java.pro.bank.entity;

public class Agreement {

    private Long id;

    private String name;

    public Agreement() {}

    public Agreement(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
