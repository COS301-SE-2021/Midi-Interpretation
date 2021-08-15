import {
    Bar,
    CartesianGrid,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from "recharts";
import {BarChart} from "recharts";
import React from "react";


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

    return (
        <ResponsiveContainer width="100%" height="100%">
            <BarChart
                width={500}
                height={300}
                data={genreData.genreData}
                margin={{
                    top: 5,
                    right: 30,
                    left: 20,
                    bottom: 5,
                }}
            >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="Name" label={{ value: 'Genre', position: 'bottom' }}/>
                <YAxis unit="%" label={{ value: 'Likelihood', angle: -90, position: 'left' }} domain={[genreData[9], genreData[0]]}/>
                <Tooltip />
                <Bar dataKey="PercentCertainty" fill="#7467ef"/>
            </BarChart>
        </ResponsiveContainer>
    )
}

export default GenreTable;
//{[genreData[9]-((genreData[0]-genreData[9])/5), ((genreData[0]-genreData[9])/5)+genreData[0]]}