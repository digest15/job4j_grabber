create table if not exists post (
    id serial,
    name varchar(255),
    description text,
    link varchar(500) unique,
    created timestamp,
);