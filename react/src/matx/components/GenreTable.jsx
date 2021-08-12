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
                <XAxis dataKey="Name" />
                <YAxis domain={[genreData[9], genreData[0]]}/>
                <Tooltip />
                <Bar dataKey="Certainty" fill="#7467ef" />
            </BarChart>
        </ResponsiveContainer>
    )
}

export default GenreTable;
//{[genreData[9]-((genreData[0]-genreData[9])/5), ((genreData[0]-genreData[9])/5)+genreData[0]]}