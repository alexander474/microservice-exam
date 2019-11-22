create table user_entity_roles
(
    user_entity_username varchar(255) not null,
    roles                varchar(255)
);
create table users
(
    username varchar(255) not null,
    enabled  boolean      not null,
    password varchar(255),
    primary key (username)
);
alter table user_entity_roles
    add constraint FKsn3bllbt5h2wue4tckbylorlj foreign key (user_entity_username) references users;