create table if not exists post (
    id serial,
    name varchar(255),
    description text,
    link varchar(500),
    created timestamp,
    primary key (id, link)
);