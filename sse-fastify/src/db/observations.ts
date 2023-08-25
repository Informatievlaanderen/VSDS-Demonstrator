import {OberservationPoint} from "../types/observation-point";
import {Oberservation} from "../types/observation";

const format = require('pg-format');

export const getObservationPoints = async (bbox: GeoJSON.GeoJSON, app:any): Promise<OberservationPoint[]> => {
    return await app.pg.transact(async (client: any) => {
        const observationPoints: OberservationPoint[] = []

        const {rows} = await client.query(`select *, vsds_observations_for_point(id) as observations
                                           from observation_points
                                           where geometry_contains(st_geomfromgeojson($1), point((geometry::jsonb->'lng'):: numeric, (geometry::jsonb->'lat'):: numeric)::geometry)`, [JSON.stringify(bbox)]);

        rows.forEach((row: any) => {
            let observationPoint = new OberservationPoint(row.id, row.wkt, row.lane)
            observationPoint.observations = row.observations
            observationPoints.push(observationPoint)
        })

        return observationPoints
    })
}

export const saveObservationPoint = (observationPoint: OberservationPoint, app:any) => {
    return app.pg.transact(async (client: { query: (arg0: string, arg1: any[]) => any }) => {
        // will resolve to an id, or reject with an error
        client.query('INSERT INTO observation_points(id, geometry, wkt, lane) VALUES($1, $2, $3, $4) ON CONFLICT DO NOTHING',
            [observationPoint.id, observationPoint.geometry, observationPoint.wkt, observationPoint.lane])
    })
}

export const addObservationToPoint = async (observations: Oberservation[], app:any): Promise<OberservationPoint> => {
    return app.pg.transact(async (client: any) => {
        let values:any[] = []
        observations.forEach(observation => values.push([observation.observationPoint, observation.vehicleTypeId, observation.measureType, observation.value, observation.startTime, observation.endTime]))

        client.query(format('INSERT INTO observations (observation_point, vehicle_type, measurement_type, "value", start_time, end_time) VALUES %L ON CONFLICT DO NOTHING',
            values));

        return getObservationPoint(observations[0].observationPoint, app);
    })
}

export const getObservationPoint = async (id: string, app:any): Promise<OberservationPoint> => {
    return app.pg.transact(async (client: any) => {
        const {rows} = await client.query(`select *, vsds_observations_for_point(id) as observations
                                           from observation_points
                                           where id = $1 LIMIT 1`, [id]);

        let observationPoint = new OberservationPoint(rows[0].id, rows[0].wkt, rows[0].lane)
        observationPoint.observations = rows[0].observations
        return observationPoint
    })
}