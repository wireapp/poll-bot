create table mentions
(
    id           serial      not null primary key,
    poll_id      varchar(36) not null references polls (id),
    user_id      varchar(36) not null,
    offset_shift integer     not null,
    length       integer     not null
);
