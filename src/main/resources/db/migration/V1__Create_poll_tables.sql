create table polls
(
    id              varchar(36) unique not null,
    conversation_id varchar(36)        not null,
    owner_id        varchar(36)        not null,
    is_active       boolean            not null,
    body            text               not null,
    primary key (id)
);

create table poll_option
(
    -- artificial value for easier links between votes and poll_option
    id             serial primary key,
    poll_id        varchar(36)  not null references polls (id),
    option_order   integer      not null,
    option_content varchar(256) not null,
    unique (poll_id, option_order)
);

create table votes
(
    poll_option integer     not null references poll_option (id),
    user_id     varchar(36) not null,
    primary key (poll_option, user_id)
);
