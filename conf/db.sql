create table rcm.categories
(
  id int auto_increment
    primary key,
  name varchar(30) not null
)
;

create table rcm.championships
(
  id int auto_increment
    primary key,
  name varchar(250) not null,
  description text null,
  thumbnailUrl varchar(2000) null
)
;

create table rcm.competitors
(
  id int auto_increment
    primary key,
  driver_id int null,
  championship_id int null,
  team_id int null,
  category_id int null,
  constraint championships_competitors_fk
  foreign key (championship_id) references rcm.championships (id),
  constraint category_competitors_fk
  foreign key (category_id) references rcm.categories (id)
)
;

create index category_competitors_fk
  on competitors (category_id)
;

create index championships_competitors_fk
  on competitors (championship_id)
;

create index driver_fk
  on competitors (driver_id)
;

create index teams_competitors_fk
  on competitors (team_id)
;

create table rcm.drivers
(
  id int auto_increment
    primary key,
  name varchar(250) not null
)
;

alter table competitors
  add constraint driver_fk
foreign key (driver_id) references rcm.drivers (id)
;

create table rcm.results
(
  id int auto_increment
    primary key,
  position int null,
  class_position int null,
  class_car varchar(100) null,
  start_position int null,
  laps_led int null,
  average_lap varchar(50) null,
  fastest_lap varchar(50) null,
  fastest_lap_number varchar(50) null,
  total_laps int null,
  incidents int null,
  club varchar(50) null,
  points int null,
  penalty_points int null,
  final_points int null,
  session_id int null,
  interval_time varchar(50) null,
  car_number varchar(10) null,
  bonus_points int null,
  competitor_id int null,
  constraint results_competitors__fk
  foreign key (competitor_id) references rcm.competitors (id)
)
;

create index results_sessions_id_fk
  on results (session_id)
;

create index results_competitors__fk
  on results (competitor_id)
;

create table rcm.session_type
(
  id int auto_increment
    primary key,
  name varchar(50) not null,
  points varchar(250) not null,
  incidents_limit int null,
  penalty_points int null,
  championship_id int null,
  bonus_points int null,
  constraint session_type_championships_id_fk
  foreign key (championship_id) references rcm.championships (id)
)
;

create index session_type_championships_id_fk
  on session_type (championship_id)
;

create table rcm.sessions
(
  id int auto_increment
    primary key,
  name varchar(100) null,
  date varchar(10) null,
  time varchar(10) null,
  championship_id int not null,
  track_id int null,
  session_type_id int null,
  constraint sessions_session_type_id_fk
  foreign key (session_type_id) references rcm.session_type (id),
  constraint sessions_championships_id_fk
  foreign key (championship_id) references rcm.championships (id)
)
;

create index sessions_championships_id_fk
  on sessions (championship_id)
;

create index sessions_tracks_id_fk
  on sessions (track_id)
;

create index sessions_session_type_id_fk
  on sessions (session_type_id)
;

alter table results
  add constraint results_sessions_id_fk
foreign key (session_id) references rcm.sessions (id)
;

create table rcm.standings
(
  id int auto_increment
    primary key,
  position int null,
  behind_next int null,
  bonus_points int null,
  penalty_points int null,
  starts int null,
  wins int null,
  poles int null,
  top5s int null,
  top10s int null,
  incidents int null,
  corners int null,
  inc_per_race float null,
  inc_per_lap float null,
  inc_per_corner float null,
  championship_id int null,
  points int null,
  competitor_id int null,
  constraint standings_championships_id_fk
  foreign key (championship_id) references rcm.championships (id)
)
;

create index standings_championships_id_fk
  on standings (championship_id)
;

create table rcm.teams
(
  id int auto_increment
    primary key,
  name varchar(250) not null
)
;

alter table competitors
  add constraint teams_competitors_fk
foreign key (team_id) references rcm.teams (id)
;

create table rcm.tracks
(
  name varchar(250) not null,
  id int auto_increment
    primary key,
  thumbnail_url varchar(2000) null
)
;

alter table sessions
  add constraint sessions_tracks_id_fk
foreign key (track_id) references rcm.tracks (id)
;

