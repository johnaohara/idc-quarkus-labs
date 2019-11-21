CREATE TABLE Category (
  id int8 NOT NULL,
  name VARCHAR(255),
  PRIMARY KEY(id),
  UNIQUE (title)
);

CREATE TABLE Todo_User (
    id int8 NOT NULL,
    email VARCHAR(255),
    firstname VARCHAR(255),
    surname VARCHAR(255),
    PRIMARY KEY(id)
);
    
CREATE TABLE Todo (
    id int8 NOT NULL,
    completed BOOLEAN NOT NULL,
    ordering INTEGER,
    title VARCHAR(255),
    url VARCHAR(255),
    user_id BIGINT NOT NULL REFERENCES Todo_User(id),
    PRIMARY KEY(id)
);
    
CREATE TABLE Todo_Categories (
    todo_id int8 NOT NULL references Todo(id),
    category_id int8 NOT NULL references Category(id),
    UNIQUE (todo_id, category_id)
);


