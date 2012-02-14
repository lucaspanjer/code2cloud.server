DELETE FROM `profile`.`servicehostconfigurationservices`;
DELETE FROM `profile`.`servicehost`;
DELETE FROM `profile`.`servicehostconfiguration`;
INSERT INTO `profile`.`servicehostconfiguration`
VALUES (1, 0, 0, 1);
INSERT INTO `profile`.`servicehostconfigurationservices`
VALUES (1, 'BUILD'), (1, 'TASKS'), (1, 'WIKI'), (1,'SCM'), (1,'MAVEN');
INSERT INTO `profile`.`servicehost`
VALUES (1, '127.0.0.1', 1, 1, 1);