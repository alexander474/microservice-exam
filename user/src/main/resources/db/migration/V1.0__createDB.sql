create table user_entity_friends (user_entity_user_id varchar(255) not null, friends varchar(255));
create table user_entity_requests_in (user_entity_user_id varchar(255) not null, requests_in varchar(255));
create table user_entity_requests_out (user_entity_user_id varchar(255) not null, requests_out varchar(255));
create table users (user_id varchar(255) not null, email varchar(255), middle_name varchar(255), name varchar(255), surname varchar(255), primary key (user_id));
alter table user_entity_friends add constraint FK6j0hpklofxchnk0od2b30dnxm foreign key (user_entity_user_id) references users;
alter table user_entity_requests_in add constraint FKolqg9yku8q24g95jx9jlt73b7 foreign key (user_entity_user_id) references users;
alter table user_entity_requests_out add constraint FK3du8tl2bm3n5xboi9gufydi83 foreign key (user_entity_user_id) references users;