create table timetable_cooperate_info
(
    active      boolean,
    coop_date   date,
    archived_at timestamp(6),
    client_id   varchar(255) not null,
    info        varchar(255),
    worker_id   varchar(255) not null,
    primary key (client_id, worker_id)
);

create table timetable_notification
(
    type                     smallint check (type between 0 and 1),
    archived_at              timestamp(6),
    created_at               timestamp(6),
    dtype                    varchar(31)  not null,
    cooperate_info_client_id varchar(255),
    cooperate_info_worker_id varchar(255),
    id                       varchar(255) not null,
    message                  varchar(255),
    sender_id                varchar(255),
    status                   varchar(255),
    user_id                  varchar(255),
    primary key (id)
);

create table timetable_schedule
(
    entry_date  date,
    entry_time  time(6),
    archived_at timestamp(6),
    client_id   varchar(255),
    id          varchar(255) not null,
    worker_id   varchar(255),
    primary key (id)
);

create table timetable_user_info
(
    reg_date     date,
    email        varchar(255),
    id           varchar(255) not null,
    name         varchar(255),
    phone_number varchar(255),
    primary key (id)
);

create table timetable_users
(
    id       varchar(255) not null,
    password varchar(255) not null,
    username varchar(255),
    primary key (id)
);

create table user_authorities
(
    user_id     varchar(255) not null,
    authorities bytea
);

alter table if exists timetable_cooperate_info
    add constraint timetable_cooperate_info_client_fkey foreign key (client_id) references timetable_user_info;

alter table if exists timetable_cooperate_info
    add constraint timetable_cooperate_info_worker_fkey foreign key (worker_id) references timetable_user_info;

alter table if exists timetable_notification
    add constraint timetable_notification_sender_fkey foreign key (sender_id) references timetable_user_info;

alter table if exists timetable_notification
    add constraint timetable_notification_user_fkey foreign key (user_id) references timetable_user_info;

alter table if exists timetable_notification
    add constraint timetable_notification_coop_info_fkey foreign key (cooperate_info_client_id, cooperate_info_worker_id) references timetable_cooperate_info;

alter table if exists timetable_schedule
    add constraint timetable_schedule_client_fkey foreign key (client_id) references timetable_user_info;

alter table if exists timetable_schedule
    add constraint timetable_schedule_worker_fkey foreign key (worker_id) references timetable_user_info;

alter table if exists user_authorities
    add constraint timetable_schedule_user_fkey foreign key (user_id) references timetable_users;
