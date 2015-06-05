create table user
(
 username varchar(45) primary key );
 
 create table FuzzyVault
 (
 xCood int,
 yCood int,
 username varchar(45),
 foreign key(username) references user(username));