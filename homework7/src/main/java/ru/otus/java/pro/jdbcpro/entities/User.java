package ru.otus.java.pro.jdbcpro.entities;

import lombok.*;
import ru.otus.java.pro.jdbcpro.annotations.RepositoryField;
import ru.otus.java.pro.jdbcpro.annotations.RepositoryIdField;
import ru.otus.java.pro.jdbcpro.annotations.RepositoryTable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RepositoryTable(tableName = "users")
public class User {
    @RepositoryIdField(fieldName = "id")
    private Long id;

    @RepositoryField(fieldName = "login")
    private String login;

    @RepositoryField(fieldName = "password")
    private String password;

    @RepositoryField(fieldName = "nickname")
    private String nickname;

    public User(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }
}
