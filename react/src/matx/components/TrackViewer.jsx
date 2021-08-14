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
import SimpleCard from "./cards/SimpleCard";

function CustomTooltip (props) {
    if(props.payload.length !== 0) {
        props.callSelectChild(props.payload[0].payload.n)
        return (
            <div className="custom-tooltip bg-white text-primary elevation-z3 ">
                <div className="m-3">
                    <Grid container
                          direction="column"
                          justifyContent="space-between"
                          alignItems="baseline"
                          spacing={1}
                    >
                        <Grid item>
                            <p className="label">{`Note: ${props.payload[0].payload.n}`}</p>
                        </Grid>
                        {props.payload.map((value) => {
                            return (
                                <Grid item>
                                    <aside style={{color: `${value.stroke}`}}>{`${value.name}: ${value.value}`}</aside>
                                </Grid>
                            )
                        })}
                    </Grid>
                </div>
            </div>
        );
    }
    else
        return null
}

/**
 * Data visualisation for track data
 *
 * @returns {JSX.Element}
 * @constructor
 */

function TrackViewer (props) {
    if(props.trackData.length === 0)
        return(<div/>);
    else{
        const color = [
            "#37A2DA",
            "#32C5E9",
            "#67E0E3",
            "#9FE6B8",
            "#FFDB5C",
            "#ff9f7f",
            "#fb7293",
            "#E062AE",
            "#E690D1",
            "#e7bcf3",
            "#9d96f5",
            "#8378EA",
            "#96BFFF"
        ]
        let maxVoices = 0
        for(let tick of props.trackData){
            if(tick.notes.length>maxVoices){
                maxVoices = tick.notes.length
            }
        }
        let lineData = []
        let counter = 0
        for(let tick of props.trackData){
            let trackDataStore = {}
            trackDataStore['tick'] = tick.tick
            trackDataStore['n'] = counter
            let current_voices = tick.notes.length
            let notes = tick.notes
            notes.sort((a,b)=>{return b.value-a.value})
            for(let voice=0; voice<maxVoices; voice++){
                trackDataStore['voice_'+voice] = (voice < current_voices) ? notes[voice].value:null
            }
            lineData.push(trackDataStore)
            counter++
        }

        const items = []
        for(let x = 0; x < maxVoices; x++){
            items.push(x)
        }
        return (
            <SimpleCard title="Display">
                <div style={{ height: '400px', width: '100%'}}>
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart
                                width={500}
                                height={300}
                                data={lineData}
                                margin={{
                                        top: 5,
                                        right: 30,
                                        left: 20,
                                        bottom: 5,
                                }}
                            >
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="tick" />
                            <YAxis />
                            <Tooltip content={ <CustomTooltip payload callSelectChild={props.callSelect}/> }/>
                            <Legend verticalAlign="top" wrapperStyle={{ lineHeight: '40px' }} />
                            <Brush dataKey="tick" height={30} stroke="#7467ef"/>

                            {items.map((value,index)=>{
                                return <Line key={"voice_"+value} dataKey={"voice_"+value} stroke={color[index%13]} type="monotone" strokeWidth={2}/>
                            })}
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </SimpleCard>
        )
    }
}

export default TrackViewer;
