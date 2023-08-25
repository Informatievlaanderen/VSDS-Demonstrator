import {MobilityHindrance} from "../types/mobility-hindrance";

export const saveMobilityHindrance = (mobilityHindrance: MobilityHindrance, app: any) => {
    return app.pg.transact(async (client: { query: (arg0: string, arg1: any[]) => any }) => {
        // will resolve to an id, or reject with an error
        client.query('INSERT INTO mobility_hindrances(id, zones, period, maintainer, description) VALUES($1, $2, $3, $4, $5) ON CONFLICT DO NOTHING',
            [mobilityHindrance.id, JSON.stringify(mobilityHindrance.zones), mobilityHindrance.periode, mobilityHindrance.maintainer, mobilityHindrance.description])
    })
}

export const getMobilityHindrancesForBBox = (bbox: GeoJSON.GeoJSON, app: any): Promise<MobilityHindrance[]> => {
    return app.pg.transact(async (client: { query: (arg0: string, arg1: any[]) => any }) => {
        let mobilityHindrances : MobilityHindrance[] = [];

        const { rows } = await client.query(`select * from vsds_mobility_hindrances_for_bbox($1)`, [JSON.stringify(bbox)]);
        // will resolve to an id, or reject with an error
        rows.forEach((row: any) => {
            mobilityHindrances.push(new MobilityHindrance(row.id, row.zones, row.description, row.period, row.maintainer))
        })

        return mobilityHindrances
    })


}