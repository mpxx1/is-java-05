

create table users (
    id          bigint                          primary key,
    username    varchar(100)    not null        unique,
    pass_hash    varchar(200)   not null,
    salt        varchar(50)     not null,
    role        varchar(10)     not null        check ( role in ( 'ADMIN', 'BASE' ) )
);