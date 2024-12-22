CREATE TABLE IF NOT EXISTS migrations (id bigserial PRIMARY KEY, filename varchar(255));
CREATE TABLE IF NOT EXISTS users (id bigserial PRIMARY KEY, login varchar(255), password varchar(255), nickname varchar(255));
CREATE TABLE IF NOT EXISTS bonuses (id bigserial PRIMARY KEY, owner_login varchar(255), amount int);
