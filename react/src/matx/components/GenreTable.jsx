import {
    Bar,
    CartesianGrid, Legend, PolarAngleAxis, PolarGrid, PolarRadiusAxis, Radar,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from "recharts";
import {BarChart} from "recharts";
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
        // <BarChart
        //     width={500}
        //     height={300}
        //     data={genreData.genreData}
        //     margin={{
        //         top: 5,
        //         right: 30,
        //         left: 20,
        //         bottom: 5,
        //     }}
        // >
        //     <CartesianGrid strokeDasharray="3 3" />
        //     <XAxis dataKey="Name" label={{ value: 'Genre', position: 'bottom' }}/>
        //     <YAxis unit="%" label={{ value: 'Likelihood', angle: -90, position: 'left' }} domain={[genreData[9], genreData[0]]}/>
        //     <Tooltip />
        //     <Bar dataKey="PercentCertainty" fill="#7467ef"/>
        // </BarChart>

        <ResponsiveContainer width="100%" height="100%">
            <RadarChart cx="50%" cy="50%" outerRadius="70%" data={data}>
                <PolarGrid />
                <PolarAngleAxis dataKey="Name" />
                <PolarRadiusAxis angle={0} domain={['dataMin - 10','dataMax']}/>
                <Radar name="% Certainty" dataKey="PercentCertainty" stroke="#8884d8" fill="#8884d8" fillOpacity={0.6} />
                <Tooltip/>
                <Legend />
            </RadarChart>
        </ResponsiveContainer>

    )
}

export default GenreTable;
//{[genreData[9]-((genreData[0]-genreData[9])/5), ((genreData[0]-genreData[9])/5)+genreData[0]]}