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


const data = [
        { name: '1', pitch: 300, velocity: 456, octave: 2 },
        { name: '2', pitch: 145, velocity: 230, octave: 3 },
        { name: '3', pitch: 100, velocity: 345, octave: 4 },
        { name: '4', pitch: 8, velocity: 450, octave: 2 },
        { name: '5', pitch: 100, velocity: 321, octave: 1 },
        { name: '6', pitch: 9, velocity: 235, octave: 2 },
        { name: '7', pitch: 53, velocity: 267, octave: 1 },
        { name: '8', pitch: 252, velocity: -378, octave: 3 },
        { name: '9', pitch: 79, velocity: -210, octave: 4 },
        { name: '10', pitch: 294, velocity: -23, octave: 5 },
        { name: '11', pitch: 244, velocity: -43, octave: 2 },
        { name: '12', pitch: 43, velocity: 45, octave: 4 },
        { name: '13', pitch: 74, velocity: 90, octave: 3 },
        { name: '14', pitch: 71, velocity: 130, octave: 6 },
        { name: '15', pitch: 117, velocity: 11, octave: 3 },
        { name: '16', pitch: 186, velocity: 107, octave: 1 },
        { name: '17', pitch: 16, velocity: 926, octave: 2 },
        { name: '18', pitch: 125, velocity: 653, octave: 3 },
        { name: '19', pitch: 222, velocity: 366, octave: 2 },
        { name: '20', pitch: 372, velocity: 486, octave: 4 },
        { name: '21', pitch: 182, velocity: 512, octave: 5 },
        { name: '22', pitch: 164, velocity: 302, octave: 6 },
        { name: '23', pitch: 316, velocity: 425, octave: 4 },
        { name: '24', pitch: 131, velocity: 467, octave: 3 },
        { name: '25', pitch: 291, velocity: -190, octave: 1 },
        { name: '26', pitch: 47, velocity: 194, octave: 2 },
        { name: '27', pitch: 415, velocity: 371, octave: 3 },
        { name: '28', pitch: 182, velocity: 376, octave: 4 },
        { name: '29', pitch: 93, velocity: 295, octave: 6 },
        { name: '30', pitch: 99, velocity: 322, octave: 3 },
        { name: '31', pitch: 52, velocity: 246, octave: 4 },
        { name: '32', pitch: 154, velocity: 33, octave: 2 },
        { name: '33', pitch: 205, velocity: 354, octave: 1 },
        { name: '34', pitch: 70, velocity: 258, octave: 2 },
        { name: '35', pitch: 25, velocity: 359, octave: 5 },
        { name: '36', pitch: 59, velocity: 192, octave: 4 },
        { name: '37', pitch: 63, velocity: 464, octave: 6 },
        { name: '38', pitch: 91, velocity: -2, octave: 3 },
        { name: '39', pitch: 66, velocity: 154, octave: 5 },
        { name: '40', pitch: 50, velocity: 186, octave: 3 },
];

const CustomTooltip = ({ active, payload, label}) => {
        if (active && payload && payload.length && label-1 !== data.length) {
                const pitch = data[label-1].pitch
                const velocity = data[label-1].velocity
                const octave = data[label-1].octave
                return (
                    <div className="custom-tooltip bg-white text-primary elevation-z3 " >
                            <div className="m-3">
                                    <p className="label">{`Note : ${label}`}</p>
                                    <p className="desc">{`Pitch : ${pitch}`}</p>
                                    <p className="desc">{`Velocity : ${velocity}`}</p>
                                    <p className="desc">{`Octave : ${octave}`}</p>
                            </div>
                    </div>
                );
        }

        return null;
};

/**
 * Data visualisation for track data
 *
 * @returns {JSX.Element}
 * @constructor
 */

const TrackViewer = () => {
        return (
            <ResponsiveContainer width="100%" height="100%">
                    <LineChart
                        width={500}
                        height={300}
                        data={data}
                        margin={{
                                top: 5,
                                right: 30,
                                left: 20,
                                bottom: 5,
                        }}
                    >
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="name" />
                            <YAxis />
                            <Tooltip content={<CustomTooltip />} />
                            <Legend verticalAlign="top" wrapperStyle={{ lineHeight: '40px' }} />
                            <Brush dataKey="name" height={30} stroke="#7467ef" />
                            <Line dataKey="pitch" stroke="#ff9e43" type="monotone" strokeWidth={2} />
                    </LineChart>
            </ResponsiveContainer>
        )
}

export default TrackViewer;
