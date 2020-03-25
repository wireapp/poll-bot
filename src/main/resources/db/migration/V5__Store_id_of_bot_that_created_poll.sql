alter table polls
    add column bot_id varchar(36);

-- noinspection SqlWithoutWhere
update polls
set bot_id = '00000000-0000-0000-0000-000000000000';

alter table polls
    alter column bot_id set not null;
