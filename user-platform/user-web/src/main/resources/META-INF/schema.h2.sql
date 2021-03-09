DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
id int(20) NOT NULL COMMENT 'primary key id',
name varchar(64) NOT NULL COMMENT 'user name',
password varchar(128) DEFAULT NULL COMMENT 'user password',
email varchar(128) DEFAULT NULL COMMENT 'user email',
phoneNumber varchar(128) DEFAULT NULL COMMENT 'user phoneNumber',
PRIMARY KEY (id)
);