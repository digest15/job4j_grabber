create table if not exists rabbit (
    id serial primary key,
    created_date timestamp
);

select
    r.created_date,
    r."id"
from
    rabbit r;