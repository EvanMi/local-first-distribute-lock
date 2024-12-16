create table if not exists t_distributed_lock
(
    id bigint primary key,
    lock_key  varchar(255) default '' not null comment 'lock key',
    thread_id varchar(255) default '' not null comment 'thread id',
    timeout bigint not null comment 'timeout',
    reentrant_count int default 0  not null comment 'reentrant count',
    unique key (lock_key)
) comment 't_distribute_lock';