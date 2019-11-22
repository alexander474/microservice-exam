create sequence hibernate_sequence start with 1 increment by 1;
create table posts
(
    id      bigint not null,
    date    bigint,
    message varchar(255),
    title   varchar(255),
    user_id varchar(255),
    primary key (id)
);