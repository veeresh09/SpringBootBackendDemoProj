drop database sampleappdb;
drop user sampleapp;
create user sampleapp with password 'password';
create database sampleappdb with template=template0 owner = sampleapp;
\connect sampleappdb;
alter default privileges grant all on tables to sampleapp;
alter default privileges grant all on sequences to sampleapp;

create table sample_users(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
email varchar(30) not null,
password text not null
) ;

create sequence sample_users_seq increment 1 start 1;