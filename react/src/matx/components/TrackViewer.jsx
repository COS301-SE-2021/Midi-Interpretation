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
import {Grid} from "@material-ui/core";
import SVGMusicNotation from "svg-music-notation";

const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
        if(!payload[0].payload["is_rest"]) {
            const name = payload[0].payload["tone_string"]
            const pitch = payload[0].payload["value"]
            const on_velocity = payload[0].payload["on_velocity"]
            const off_velocity = payload[0].payload["off_velocity"]
            const octave = payload[0].payload["octave"]
            const duration = parseFloat(payload[0].payload["duration"])
            const notes = ['𝅝','𝅗𝅥','𝅘𝅥','𝅘𝅥𝅮','𝅘𝅥𝅯','𝅘𝅥𝅰']
            return (
                <div className="custom-tooltip bg-white text-primary elevation-z3 ">
                    <div className="m-3">
                        <Grid container
                              justifycontent="center"
                              alignItems="center"
                        >
                            <Grid item xs={12}>
                                <p className="label">{`Note ${label}: ${name}`}</p>
                            </Grid>
                            <Grid item xs={6}>
                                <div className="text-32">{notes[2]}</div>
                            </Grid>
                            <Grid item xs={6}>
                                <p className="desc">{`Pitch : ${pitch}`}</p>
                            </Grid>
                            <Grid item xs={6}>
                                <p className="desc">{`Octave : ${octave}`}</p>
                            </Grid>
                            <Grid item xs={6}>
                                <p className="desc">{`On Velocity : ${on_velocity}`}</p>
                            </Grid>
                            <Grid item xs={6}>
                                <p className="desc">{`Off Velocity : ${off_velocity}`}</p>
                            </Grid>
                            <Grid item xs={6}>
                                <p className="desc">{`Duration : ${duration.toFixed(3)}`}</p>
                            </Grid>
                        </Grid>
                    </div>
                </div>
            );
        }
        else{
            const duration = parseFloat(payload[0].payload["duration"])
            const rest = ['𝄺','𝄻','𝄼','𝄽','𝄾','𝄿','𝅀','𝅁','𝅂']
            return (
                <div className="custom-tooltip bg-white text-primary elevation-z3 ">
                        <div className="m-3">
                        <Grid container
                              justifycontent="center"
                              alignItems="center"
                        >
                            <Grid item xs={12}>
                                <p className="label">{`Note ${label}: Rest`}</p>
                            </Grid>
                            <Grid item xs={6}>
                                <Grid item xs={6}>
                                    <div className="text-32">{rest[3]}</div>
                                </Grid>
                                <p className="desc">{`Duration : ${duration.toFixed(3)}`}</p>
                            </Grid>
                        </Grid>
                    </div>
                </div>
            );
        }
    }

    return null
};

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
                            <Tooltip content={<CustomTooltip/>} />
                            <Legend verticalAlign="top" wrapperStyle={{ lineHeight: '40px' }} />
                            <Brush dataKey="index" height={30} stroke="#7467ef" />
                            <Line dataKey="value" connectNulls stroke="#ff9e43" type="monotone" strokeWidth={2}/>
                    </LineChart>
            </ResponsiveContainer>
        )
}

export default TrackViewer;
