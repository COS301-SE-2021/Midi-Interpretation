import {
        LineChart,
        Line,
        Brush,
        XAxis,
        YAxis,
        CartesianGrid,
        Tooltip,
        Legend,
        ResponsiveContainer,
} from 'recharts';
import React from "react";

// const CustomTooltip = (data) => {
//     console.log(data)
//     if (active && payload && payload.length) {
//                 const name = data[label]["tone_string"]
//                 const pitch = data[label].value
//                 const velocity = data[label]["on_velocity"]
//                 const octave = data[label].octave
//                 return (
//                     <div className="custom-tooltip bg-white text-primary elevation-z3 " >
//                             <div className="m-3">
//                                     <p className="label">{`Note ${label}: ${name}`}</p>
//                                     <p className="desc">{`Pitch : ${pitch}`}</p>
//                                     <p className="desc">{`Velocity : ${velocity}`}</p>
//                                     <p className="desc">{`Octave : ${octave}`}</p>
//                             </div>
//                     </div>
//                 );
//         }
//
//         return <div>{JSON.stringify(data)}</div>;
// };

/**
 * Data visualisation for track data
 *
 * @returns {JSX.Element}
 * @constructor
 */

const TrackViewer = (trackData) => {
        return (
            <ResponsiveContainer width="100%" height="100%">
                    <LineChart
                        width={500}
                        height={300}
                        data={trackData.trackData}
                        margin={{
                                top: 5,
                                right: 30,
                                left: 20,
                                bottom: 5,
                        }}
                    >
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="index" />
                            <YAxis />
                            {/*<Tooltip content={<CustomTooltip data={trackData.trackData} />} />*/}
                            <Legend verticalAlign="top" wrapperStyle={{ lineHeight: '40px' }} />
                            <Brush dataKey="index" height={30} stroke="#7467ef" />
                            <Line dataKey="value" stroke="#ff9e43" type="monotone" strokeWidth={2} />
                    </LineChart>
            </ResponsiveContainer>
        )
}

export default TrackViewer;
