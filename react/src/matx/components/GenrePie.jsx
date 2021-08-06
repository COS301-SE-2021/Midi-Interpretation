import React, { PureComponent } from 'react';
import {PieChart, Pie, Sector, Cell, ResponsiveContainer, Tooltip, Legend} from 'recharts';

const data = [
    { name: 'Rock', value: 40 },
    { name: 'Pop', value: 30 },
    { name: 'Punk', value: 30 },
];

const COLORS = ['#7467EF', '#ABA4F4', '#D3D0F4'];


/**
 * Data visualisation for track data
 *
 * @returns {JSX.Element}
 * @constructor
 */

const GenrePie = () => {

        return (
            <ResponsiveContainer width="100%" height="100%">
                <PieChart width={400} height={400}>
                    <Pie
                        data={data}
                        cx="50%"
                        cy="50%"
                        outerRadius={80}
                        fill="#8884d8"
                        dataKey="value"
                    >
                        {data.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                    </Pie>
                    <Legend verticalAlign="top" height={36}/>
                    <Tooltip/>
                </PieChart>
            </ResponsiveContainer>
        );
}

export default GenrePie;
