import {Brush, CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis,} from 'recharts';
import React from "react";
import {Grid} from "@material-ui/core";
import * as ReactDOMServer from "react-dom/server";

function voiceName (index){
    return "Line "+(index+1)
}

function velocityToDynamic(vel){
    vel = vel % 128
    let dynamics = ['pppp','ppp','pp','p','mp','mf','f','ff','fff','ffff']
    return dynamics[Math.ceil((vel-10)/13)]
}

function getPercussiveInstrument(value){
    let instruments = ["Acoustic Bass Drum","Bass Drum 1","Side Stick","Acoustic Snare","Hand Clap","Electric Snare","Low Floor Tom","Closed Hi Hat","High Floor Tom","Pedal Hi-Hat","Low Tom","Open Hi-Hat","Low-Mid Tom","Hi Mid Tom","Crash Cymbal 1","High Tom","Ride Cymbal 1","Chinese Cymbal","Ride Bell","Tambourine","Splash Cymbal","Cowbell","Crash Cymbal 2","Vibraslap","Ride Cymbal 2","Hi Bongo","Low Bongo","Mute Hi Conga","Open Hi Conga","Low Conga","High Timbale","Low Timbale","High Agogo","Low Agogo","Cabasa","Maracas","Short Whistle","Long Whistle","Short Guiro","Long Guiro","Claves","Hi Wood Block","Low Wood Block","Mute Cuica","Open Cuica","Mute Triangle","Open Triangle"]
    return instruments[(value-35) % instruments.length]
}

function valueToNote(k){
    let noteArray = ["C","C#/Db","D","D#/Eb","E","F","F#/Gb","G","G#/Ab","A","A#/Bb","B"]
    let offset = k % 12
    let octave = Math.floor((k / 12) % 128)
    let note = noteArray[offset]
    return {"pitch": note, "octave":octave}
}

function frequency(value){
    return Math.floor(100*440*Math.pow(2,(value-57)/12))/100
}


function CustomTooltip (props) {
    if(props.payload.length !== 0) {
        //props.callSelectChild(props.payload[0].payload.n)

        //Alter the
        document.getElementById("dataDisplay").innerHTML = ReactDOMServer.renderToString(
            <Grid container
                  direction="row"
                  justifyContent="flex-start"
                  alignItems="flex-start"
                  >
                {props.payload.map((item,index)=>{
                    const comp = item.payload['composite'][index]
                    let isPercussive = comp['isPercussive']
                    let onVelocity = (comp['on_velocity']===-1)?"Unknown":comp['on_velocity']
                    let offVelocity = (comp['off_velocity']===-1)?"Unknown":comp['off_velocity']
                    let onDynamic = (comp['on_velocity']===-1)?"No Dynamic Info":velocityToDynamic(comp['on_velocity'])
                    let offDynamic = (comp['off_velocity']===-1)?"No Dynamic Info":velocityToDynamic(comp['off_velocity'])
                    let duration = (comp['duration_beats']<0)?"No Duration Info":(comp['duration_beats']+" beats")
                    if(isPercussive){
                        return(<div key={index} style={{padding:"10px"}}>
                            <div className="text-16" style={{color: item.color}}><b>{voiceName(index)}</b></div>
                            <div className="text-14">Instrument: {getPercussiveInstrument(item.value)} </div>
                            <div className="text-14">On Velocity: {onVelocity} ({onDynamic})</div>
                            <div className="text-14">Off Velocity: {offVelocity} ({offDynamic})</div>
                            <div className="text-14">Duration: {duration}</div>
                        </div>)
                    }
                    else return(
                        <div key={index} style={{padding:"10px"}}>
                            <div className="text-16" style={{color: item.color}}><b>{voiceName(index)}</b></div>
                            <div className="text-14">Frequency: {frequency(item.value)} Hz</div>
                            <div className="text-14">Pitch: {valueToNote(item.value)['pitch']}</div>
                            <div className="text-14">Octave: {valueToNote(item.value)['octave']}</div>
                            <div className="text-14">On Velocity: {onVelocity} ({onDynamic})</div>
                            <div className="text-14">Off Velocity: {offVelocity} ({offDynamic})</div>
                            <div className="text-14">Duration: {duration}</div>
                        </div>
                    )
                })}

            </Grid>
        )


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
                            <p className="label">Beat {props.payload[0].payload['beat']}</p>
                        </Grid>
                        {props.payload.map((item, index) => {
                            const note = valueToNote(item.value)
                            const comp = item.payload['composite'][index]
                            let isPercussive = comp['isPercussive']
                            if(isPercussive) {
                                return(
                                    <Grid item>
                                        <aside style={{color: `${item.stroke}`}}>{getPercussiveInstrument(item.value)}</aside>
                                    </Grid>
                                )
                            }
                            else return (
                                <Grid item>
                                    <aside style={{color: `${item.stroke}`}}>{note['pitch']}|{note['octave']}</aside>
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
    props = props.trackData
    if(props.trackData.length === 0)
        return(<div/>);
    else{

        let ticksPerBeat = props.ticksPerBeat
        let isPercussive = (props.instrument.toUpperCase() === "DRUMSET")

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
            trackDataStore['beat'] = 1 + Math.round(tick.tick/ticksPerBeat * 100) / 100
            trackDataStore['index'] = counter
            let current_voices = tick.notes.length
            let notes = tick.notes
            for(let n of notes){
                n['duration_beats'] = Math.floor(100*n['duration']/ticksPerBeat)/100
                n['isPercussive'] = isPercussive
            }

            trackDataStore['composite'] = notes
            notes.sort((a,b)=>{return b.value-a.value})
            for(let voice=0; voice<maxVoices; voice++){
                trackDataStore[voiceName(voice)] = (voice < current_voices) ? notes[voice].value : null
            }
            lineData.push(trackDataStore)
            counter++
        }

        const items = []
        for(let x = 0; x < maxVoices; x++){
            items.push(x)
        }
        return (
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
                        <CartesianGrid strokeDasharray="1 1" />
                        <XAxis domain={`[0,${lineData.length}]`} scale="linear" interval="preserveStart" type="number" dataKey="beat" label={{ value: 'Beat number', position: 'bottom' }}/>
                        <YAxis label={{ value: 'Pitch (~log Hz)', angle: -90, position: 'left' }}/>
                        <Tooltip content={ <CustomTooltip payload props/> }/>
                        <Legend verticalAlign="top" wrapperStyle={{ lineHeight: '40px' }} />
                        <Brush dataKey="beat" height={30} stroke="#7467ef"/>

                        {items.map((value,index)=>{
                            return <Line key={voiceName(value)} dataKey={voiceName(value)} stroke={color[index%13]} type="monotone" strokeWidth={2}/>
                        })}
                    </LineChart>
                </ResponsiveContainer>
            </div>
        )
    }
}

export default TrackViewer;
