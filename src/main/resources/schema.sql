drop table if exists players cascade;
drop table if exists clubs cascade;
create table clubs(id serial primary key, name varchar(255), balance decimal);
create table players(id serial primary key, club_id integer references clubs(id) on delete cascade, name varchar(255), price decimal);
