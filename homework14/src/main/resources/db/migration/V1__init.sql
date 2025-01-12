CREATE TABLE products (
    id bigserial primary key,
    title varchar(255),
    price int
);

INSERT INTO products (title, price) VALUES ('Milk', 80), ('Bread', 35), ('Cheese', 320);