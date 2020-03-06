-- clear poll table, we don't care right now about data
-- noinspection SqlWithoutWhere
delete
from polls;

-- it is much easier to delete and create again as we are dropping primary keys columns
drop table votes;
drop table poll_option;

create table poll_option
(
    poll_id        varchar(36)  not null references polls (id),
    option_order   integer      not null,
    option_content varchar(256) not null,
    primary key (poll_id, option_order)
);

create table votes
(
    poll_id     varchar(36) not null,
    poll_option integer     not null,
    user_id     varchar(36) not null,
--     allow single vote for user/poll
    primary key (poll_id, user_id),
    foreign key (poll_id, poll_option) references poll_option (poll_id, option_order)
);
