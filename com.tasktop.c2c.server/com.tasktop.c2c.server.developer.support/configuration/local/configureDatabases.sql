create database bugstest_tasks;
create database profile;

create user 'bugsTester'; GRANT ALL PRIVILEGES ON *.* TO 'bugsTester'@'localhost';
create user 'tasks' identified by 'tasks'; GRANT ALL PRIVILEGES ON *.* TO 'tasks'@'localhost';
create user 'wiki' identified by 'wiki'; GRANT ALL PRIVILEGES ON *.* TO 'wiki'@'localhost';
create user 'profile' identified by 'profile'; GRANT ALL PRIVILEGES ON *.* TO 'profile'@'localhost';
commit; 
flush privileges;