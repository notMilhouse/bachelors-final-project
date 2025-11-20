-- create database
-- create database profile_measurements;
-- use profile_measurements;

CREATE EXTENSION IF NOT EXISTS vector;

-- table: profile
-- stores user account information
create table profile
(
    id                   uuid primary key default gen_random_uuid(),
    name                 varchar(100)        not null
);

-- table: profile_measurement
-- stores weight measurements for users
create table profile_measurement
(
    id           uuid primary key         default gen_random_uuid(),
    profile_id   uuid          not null,
    weight_value decimal(5, 2) not null,
    measured_at  timestamp with time zone default current_timestamp, -- at esp32
    recorded_at  timestamp with time zone default current_timestamp, -- at database

    -- foreign key relationship
    constraint fk_profile_measurement_profile_id
        foreign key (profile_id) references profile (id)
            on delete cascade
);

-- table: profile_embedding
-- stores image embeddings for profile identification
create table profile_embedding
(
    id         uuid primary key,
    profile_id uuid not null,
    embedding  vector(128),
    -- foreign key relationship
    constraint fk_profile_embeddings_profile_id
        foreign key (profile_id) references profile (id)
            on delete cascade
);


-- indexes for better query performance
create index idx_profile_measurement_profile_id on profile_measurement (profile_id);
create index idx_profile_embedding_cosine_ops on profile_embedding
    using hnsw (embedding vector_cosine_ops)
    with (m = 16, ef_construction = 64);
