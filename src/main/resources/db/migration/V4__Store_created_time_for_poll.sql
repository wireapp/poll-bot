alter table polls
    add column time_stamp timestamp not null default now();
