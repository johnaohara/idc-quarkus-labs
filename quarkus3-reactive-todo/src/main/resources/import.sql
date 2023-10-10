INSERT INTO Category(id,name) VALUES (nextval('category_seq'), 'Work');
INSERT INTO Category(id,name) VALUES (nextval('category_seq'), 'Private');
INSERT INTO Category(id,name) VALUES (nextval('category_seq'), 'Family');

INSERT INTO Todo_User(id,firstname,surname,email) VALUES (nextval('todo_user_seq'), 'Thomas','Qvarnstrom','no-reply@redhat.com');
INSERT INTO Todo_User(id,firstname,surname,email) VALUES (nextval('todo_user_seq'), 'John','OHara','no-reply@redhat.com');

INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (nextval('todo_seq'), 'Introduction to Quarkus', true, 0, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (nextval('todo_seq'), 'Write Evaluation Plan', true, 1, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (nextval('todo_seq'), 'Run Lab 1.1 - Startup memory', false, 2, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (nextval('todo_seq'), 'Run Lab 1.2 - Container density', false, 3, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (nextval('todo_seq'), 'Run Lab 1.3 - Memory usage under load', false, 3, 51, null);

INSERT INTO Todo_Categories(todo_id,category_id) VALUES (1,1);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (51,51);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (101,101);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (151,1);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (201,51);