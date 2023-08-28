CREATE OR REPLACE FUNCTION vsds_observations_for_point(
    id varchar,
    OUT observations json)
AS $$
BEGIN
    observations := COALESCE((SELECT json_agg(json_build_object('observationPoint', observation_point, 'vehicleType', vehicle_type, 'measurementType', measurement_type, 'value', value, 'startTime', start_time, 'endTime', end_time))
                                 FROM observations where observations.observation_point = id and end_time in (select max(end_time) from observations where observation_point = id)
                             ), '[]'::json) vals;
END; $$

LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION vsds_mobility_hindrances_for_bbox(geoJson varchar)
    RETURNS setof mobility_hindrances
AS $$
BEGIN
    return query
    select * from mobility_hindrances where id IN (
            select distinct x.id
            from (select jsonb_array_elements(m.zones::jsonb->'features') as elem, id from mobility_hindrances m) x
            where geometry_contains(st_geomfromgeojson(geoJson), st_geomfromgeojson(elem::jsonb->'geometry'))
        );
END; $$

LANGUAGE plpgsql;

create table if not exists mobility_hindrances
(
    id          varchar not null
        constraint mobility_hindrances_pk
            primary key,
    zones       jsonb,
    period      jsonb,
    maintainer  varchar,
    updated     timestamp default now(),
    description text
);

create unique index if not exists mobility_hindrances_id_uindex
    on mobility_hindrances (id);

create table if not exists observation_points
(
    id       varchar not null
        constraint observation_points_pk
            primary key,
    geometry json,
    wkt      varchar,
    lane     varchar
);

create unique index if not exists observation_points_id_uindex
    on observation_points (id);

create table if not exists observations
(
    observation_point varchar,
    vehicle_type      integer,
    measurement_type  varchar,
    value             numeric,
    start_time        timestamp,
    end_time          timestamp
);

create index if not exists observations_end_time_index
    on observations (end_time);

create index if not exists observations_observation_point_index
    on observations (observation_point);