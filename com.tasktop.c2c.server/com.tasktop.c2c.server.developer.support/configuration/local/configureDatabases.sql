create database bugstest_tasks;
create database profile;

create user 'bugsTester'; GRANT ALL PRIVILEGES ON *.* TO 'bugsTester'@'localhost';
create user 'bugs' identified by 'bugs'; GRANT ALL PRIVILEGES ON *.* TO 'bugs'@'localhost';
create user 'wiki' identified by 'wiki'; GRANT ALL PRIVILEGES ON *.* TO 'calm'@'localhost';
create user 'profile' identified by 'profile'; GRANT ALL PRIVILEGES ON *.* TO 'profile'@'localhost';
commit; 
flush privileges;