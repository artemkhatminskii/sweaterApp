create sequence hibernate_sequence start with 2 increment by 1

create table message (
    id int8 not null,
    filename varchar(255),
    tag varchar(255),
    text varchar(2048) not null,
    user_id int8,
    primary key (id)
    );

create table usr (
    id int8 not null,
    activation_code varchar(255),
    active boolean not null,
    email varchar(255),
    password varchar(255) not null,
    role varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
    );

alter table if exists message
    add constraint message_user_fk
    foreign key (user_id) references usr;