import {
    Legend, PolarAngleAxis, PolarGrid, Radar,
    ResponsiveContainer,
    Tooltip,
} from "recharts";
import React from "react";
import RadarChart from "recharts/lib/chart/RadarChart";


/**
 * Data visualisation for track data
 *
 * @returns {JSX.Element}
 * @constructor
 */

const GenreTable = (genreData) => {

    let decimalCertainty = 3

    genreData.genreData.forEach((item)=>{
        item['PercentCertainty'] = Math.round(Math.pow(10,decimalCertainty+2) * item['Certainty']) / Math.pow(10,decimalCertainty)
    })

    let data = genreData.genreData

    return (
        <ResponsiveContainer width="100%" height="100%">
            <RadarChart cx="50%" cy="50%" outerRadius="70%" data={data}>
                <PolarGrid />
                <PolarAngleAxis dataKey="Name" />
                <Radar name="% Certainty" dataKey="PercentCertainty" stroke="#8884d8" fill="#8884d8" fillOpacity={0.6} />
                <Tooltip/>
                <Legend />
            </RadarChart>
        </ResponsiveContainer>
    )
}

export default GenreTable;