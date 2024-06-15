
create table owners (
    id          bigint          not null    primary key,
    email       varchar(100)    not null    unique,
    name        varchar(50),
    birthday    date
);