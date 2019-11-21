INSERT INTO Category(id,name) VALUES (1, 'Work');
INSERT INTO Category(id,name) VALUES (2, 'Private');
INSERT INTO Category(id,name) VALUES (3, 'Family');

INSERT INTO Todo_User(id,firstname,surname,email) VALUES (1, 'Thomas','Qvarnstrom','no-reply@redhat.com');
INSERT INTO Todo_User(id,firstname,surname,email) VALUES (2, 'John','OHare','no-reply@redhat.com');

INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (1, 'Introduction to Quarkus', true, 0, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (2, 'Write Evaluation Plan', true, 1, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (3, 'Run Lab 1.1 - Startup memory', false, 2, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (4, 'Run Lab 1.2 - Container density', false, 3, 1, null);
INSERT INTO Todo(id, title, completed, ordering, user_id, url) VALUES (5, 'Run Lab 1.3 - Memory usage under load', false, 3, 2, null);

INSERT INTO Todo_Categories(todo_id,category_id) VALUES (1,1);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (2,1);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (3,1);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (4,1);
INSERT INTO Todo_Categories(todo_id,category_id) VALUES (5,1);