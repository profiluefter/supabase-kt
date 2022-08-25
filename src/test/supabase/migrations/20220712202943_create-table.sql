create table vehicles
(
    id      serial  not null
        constraint vehicles_pk
            primary key,
    user_id uuid    not null
        default auth.uid()
        constraint vehicles_user_id_fk
            references auth.users (id),
    name    varchar not null
        default ''
);

alter table vehicles
    enable row level security;

create policy vehicles_user_id_policy on vehicles
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

alter publication supabase_realtime add table vehicles;
