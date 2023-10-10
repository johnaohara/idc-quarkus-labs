
CREATE TABLE Category (
  id BIGSERIAL NOT NULL,
  name VARCHAR(255),
  PRIMARY KEY(id)
);

CREATE TABLE Todo_User (
    id BIGSERIAL NOT NULL,
    email VARCHAR(255),
    firstname VARCHAR(255),
    surname VARCHAR(255),
    PRIMARY KEY(id)
);

CREATE TABLE Todo (
    id BIGSERIAL NOT NULL,
    completed BOOLEAN NOT NULL,
    ordering INTEGER,
    title VARCHAR(255),
    url VARCHAR(255),
    user_id BIGINT,
    PRIMARY KEY(id)
);

CREATE TABLE Todo_Categories (
    todo_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY(todo_id, category_id)
);

ALTER TABLE IF EXISTS Todo ADD CONSTRAINT unique_title_constraint unique (title);
ALTER TABLE Todo ADD CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES todo_user;
ALTER TABLE Todo_Categories ADD CONSTRAINT fk_category_id FOREIGN KEY(category_id) REFERENCES Category;
ALTER TABLE Todo_Categories ADD CONSTRAINT fk_todo_id FOREIGN KEY(todo_id) REFERENCES Todo;


