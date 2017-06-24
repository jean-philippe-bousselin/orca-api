create table rcm.championships
(
  id int auto_increment
    primary key,
  name varchar(250) not null,
  description text null
)
;

create table rcm.sessions
(
  id int auto_increment
    primary key,
  name int null,
  date varchar(10) null,
  time varchar(10) null,
  championship_id int not null,
  constraint sessions_championships_id_fk
  foreign key (championship_id) references rcm.championships (id)
)
;

create index sessions_championships_id_fk
  on sessions (championship_id)
;

