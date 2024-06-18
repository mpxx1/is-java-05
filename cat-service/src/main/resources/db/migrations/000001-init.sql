
create table cats (
    id          bigint          not null    primary key,
    name        varchar(50)     not null,
    birthday    date            not null,
    breed       varchar(50)     not null,
    color       varchar(10)     not null    check (
                                                color in (
                'RED', 'BLACK', 'BLUE', 'ORANGE', 'BROWN', 'GREY', 'WHITE'
                                                )
                                            ),
    owner       bigint          not null
);


create table friends (
    first       bigint      not null        references cats (id),
    second      bigint      not null        references cats (id),

    primary key (first, second)
);


create table friendship_requests (
    src         bigint      not null        references cats (id),
    tgt         bigint      not null        references cats (id),

    primary key (src, tgt)
)