import {
    Brush,
    CartesianGrid,
    Legend,
    Line,
    LineChart, ReferenceArea,
    ReferenceLine,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from 'recharts';
import React from "react";
import {Grid, Icon} from "@material-ui/core";
import * as ReactDOMServer from "react-dom/server";
import MidiSenseService from "../../app/services/MidiSenseService";
import {Label} from "@material-ui/icons";
import Button from "@material-ui/core/Button";


const zoomProportion = 0.2

const signatureColors = {
    time: "#43aa8b",
    key: "#184589",
    tempo: "#f8961e",
    acc: "#74bb25",
    rit: "#F94144"
}


/**
 * voiceName
 * @param index
 * @returns {string}
 */
function voiceName (index){
    return "Voice "+(index+1)
}

/**
 * velocityToDynamic
 * @param vel
 * @returns {string}
 */
function velocityToDynamic(vel){
    vel = vel % 128
    let dynamics = ['pppp','ppp','pp','p','mp','mf','f','ff','fff','ffff']
    return dynamics[Math.ceil((vel-10)/13)]
}

/**
 * getPercussiveInstrument
 * @param value
 * @returns {string}
 */
function getPercussiveInstrument(value){
    let instruments = ["Acoustic Bass Drum","Bass Drum 1","Side Stick","Acoustic Snare","Hand Clap","Electric Snare","Low Floor Tom","Closed Hi Hat","High Floor Tom","Pedal Hi-Hat","Low Tom","Open Hi-Hat","Low-Mid Tom","Hi Mid Tom","Crash Cymbal 1","High Tom","Ride Cymbal 1","Chinese Cymbal","Ride Bell","Tambourine","Splash Cymbal","Cowbell","Crash Cymbal 2","Vibraslap","Ride Cymbal 2","Hi Bongo","Low Bongo","Mute Hi Conga","Open Hi Conga","Low Conga","High Timbale","Low Timbale","High Agogo","Low Agogo","Cabasa","Maracas","Short Whistle","Long Whistle","Short Guiro","Long Guiro","Claves","Hi Wood Block","Low Wood Block","Mute Cuica","Open Cuica","Mute Triangle","Open Triangle"]
    return instruments[(value-35) % instruments.length]
}

/**
 * valueToNote
 * @param k
 * @returns {{octave: number, pitch: string}}
 */
function valueToNote(k){
    let noteArray = ["C","C#/Db","D","D#/Eb","E","F","F#/Gb","G","G#/Ab","A","A#/Bb","B"]
    let offset = k % 12
    let octave = Math.floor((k / 12) % 128)-1
    let note = noteArray[offset]
    return {"pitch": note, "octave":octave}
}

/**
 * frequency
 * @param value
 * @returns {number}
 */
function frequency(value){
    return Math.floor(100*440*Math.pow(2,(value-57)/12))/100
}

/**
 * getInterval
 * @param notes
 * @param dictionary
 * @returns {string}
 */
function getInterval(notes, dictionary){
    const backendService = new MidiSenseService()

    if(dictionary[notes.toString()] !== undefined){
        return dictionary[notes.toString()]
    }

    let res = backendService.intelligenceAnalyseInterval(notes)
    dictionary[notes.toString()] = res
    return res

}

function getCurrentMapItem(tick,map){
    if(map.length===0) return null
    let last = map[0]
    for(let item of map){
        if(item['tick'] > tick) break
        last = item
    }
    return last
}

/**
 * getChord
 * @param notes
 * @param dictionary
 * @returns {*}
 */
function getChord(notes, dictionary){

    if(dictionary[notes.toString()] !== undefined){
        return dictionary[notes.toString()]
    }

    const backendService = new MidiSenseService()
    backendService.intelligenceAnalyseChord(
        notes,

        /**
         * onSuccess
         * @param res
         */
        (res)=>{
            dictionary[notes.toString()] = res['chord']['simpleName']
            return res['chord']['simpleName']
        },

        /**
         * onFailure
         * (room for extended error handling)
         * @param error
         */
        (error)=>{
            return ""
        }
    )
}

/**
 * CustomToolTip
 * @param props
 * @returns {JSX.Element|null}
 * @constructor
 */
function CustomTooltip (props) {
    // is not empty
    if(props.payload.length !== 0) {
        let multi_note_type = ""
        let currentTick = props.payload[0].payload['tick']

        if(props.payload[0].payload['composite'] !== undefined && !props.payload[0].payload["composite"][0].isPercussive) {
            if (props.payload.length === 2) {
                let interval = []

                interval.push(props.payload[0].value)
                interval.push(props.payload[1].value)

                multi_note_type = getInterval(interval, props.intervalDictionary)
            }
            else if (props.payload.length >= 2) {
                let chord = []

                for (let p = 0; p < props.payload.length; p++) {
                    chord.push(props.payload[p].value)
                }
                multi_note_type = getChord(chord, props.chordDictionary)
            }
        }

        document.getElementById("dataDisplay").innerHTML = ReactDOMServer.renderToString(
            <Grid container
                  direction="column"
                  justifyContent="flex-start"
                  alignItems="flex-start"
                  spacing={3}
                  >
                <Grid item style={{height:"20px"}}>
                    <div className="text-18 text-primary"><b>{multi_note_type}</b></div>
                </Grid>
                <Grid item>
                    <Grid container>
                    {props.payload.map((item,index)=>{
                        const comp = item.payload['composite'][index]
                        let isPercussive = comp['isPercussive']
                        let onVelocity = (comp['on_velocity']===-1)?"Unknown":comp['on_velocity']
                        let offVelocity = (comp['off_velocity']===-1)?"Unknown":comp['off_velocity']
                        let onDynamic = (comp['on_velocity']===-1)?"No Dynamic Info":velocityToDynamic(comp['on_velocity'])
                        let offDynamic = (comp['off_velocity']===-1)?"No Dynamic Info":velocityToDynamic(comp['off_velocity'])
                        let duration = (comp['duration_beats']<0)?"No Duration Info":(comp['duration_beats']+" beats")

                        //handle differently when drum set is detected
                        if(isPercussive){
                            return(
                                <Grid item>
                                    <div key={index} style={{padding:"10px"}}>
                                    <div className="text-16" style={{color: item.color}}><b>{voiceName(index)}</b></div>
                                        <div className="text-14">Instrument: <b>{getPercussiveInstrument(item.value)}</b> </div>
                                        <div className="text-14">On Velocity: <b>{onVelocity} ({onDynamic})</b></div>
                                        <div className="text-14">Off Velocity: <b>{offVelocity} ({offDynamic})</b></div>
                                        <div className="text-14">Duration: <b>{duration}</b></div>
                                    </div>
                                </Grid>
                            )
                        }
                        else {
                            return (
                                <Grid item>
                                    <div key={index} style={{padding: "10px"}}>
                                        <div className="text-16" style={{color: item.color}}><b>{voiceName(index)}</b></div>
                                        <div className="text-14">Frequency: <b>{frequency(item.value)} Hz</b></div>
                                        <div className="text-14">Pitch: <b>{valueToNote(item.value)['pitch']}</b></div>
                                        <div className="text-14">Octave: <b>{valueToNote(item.value)['octave']}</b></div>
                                        <div className="text-14">On Velocity: <b>{onVelocity} ({onDynamic})</b></div>
                                        <div className="text-14">Off Velocity: <b>{offVelocity} ({offDynamic})</b></div>
                                        <div className="text-14">Duration: <b>{duration}</b></div>
                                    </div>
                                </Grid>
                            )
                        }
                    })}
                    </Grid>
                </Grid>
            </Grid>
        )


        let currentKey = getCurrentMapItem(currentTick,props.keyMap)
        let currentTime = getCurrentMapItem(currentTick,props.timeMap)
        let currentTempo = getCurrentMapItem(currentTick,props.tempoMap)

        let renderSignatures = () => {
            return [
                (currentKey === null) ?
                    null :
                    (
                        <Grid item>
                            <aside style={{color: signatureColors.key}}>Key
                                Signature: <b>{currentKey['keySignature']}</b></aside>
                        </Grid>
                    ),
                (currentTime === null) ?
                    null :
                    (
                        <Grid item>
                            <aside style={{color: signatureColors.time}}>Time
                                Signature: <b>{currentTime['timeSignature']['numBeats']}|{currentTime['timeSignature']['beatValue']}</b></aside>
                        </Grid>
                    ),
                (currentTempo === null) ?
                    null :
                    (
                        <Grid item>
                            <aside
                                style={{color: signatureColors.tempo}}>Tempo: <b>{currentTempo['tempoIndication']}</b> Crotchet
                                BPM
                            </aside>
                        </Grid>
                    )
            ]

        }

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
                            <p className="label">Crotchet Beat: <b>{props.payload[0].payload['beat']}</b></p>
                        </Grid>

                        {renderSignatures()}

                        {props.payload.map((item, index) => {
                            const note = valueToNote(item.value)
                            const comp = item.payload['composite'][index]
                            let isPercussive = comp['isPercussive']
                            if(isPercussive) {
                                let k =[(
                                    <Grid item>
                                        <aside style={{color: `${item.stroke}`}}>{getPercussiveInstrument(item.value)}</aside>
                                    </Grid>
                                )]
                                if(index===0){
                                    k.unshift(
                                        <Grid item>
                                            <aside className="label">Timbres:</aside>
                                        </Grid>
                                    )
                                }
                                return k
                            }
                            else{
                                let k =[(
                                    <Grid item>
                                        <aside style={{color: `${item.stroke}`}}>{note['pitch']}-{note['octave']}</aside>
                                    </Grid>
                                )]
                                if(index===0){
                                    k.unshift(
                                        <Grid item>
                                            <aside className="label">Pitches:</aside>
                                        </Grid>
                                    )
                                }
                                return k
                            }
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

    // check if track data is appropriate
    if(props.trackData.length === 0)
        return(<div/>);
    else{
        let chordDictionary = {}
        let intervalDictionary = {}
        let ticksPerBeat = props.ticksPerBeat
        let isPercussive = (props.instrument.toUpperCase() === "DRUMSET")

        // preset colour palette
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
        //pre-processing lines
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

        lineData.sort((a,b)=>{return a['tick']-b['tick']})

        const items = []
        for(let x = 0; x < maxVoices; x++){
            items.push(x)
        }



        function * pairwise (iterable) {
            const iterator = iterable[Symbol.iterator]()
            let current = iterator.next()
            let next = iterator.next()
            while (!next.done) {
                yield [current.value, next.value]
                current = next
                next = iterator.next()
            }
        }

        //pre-processing ritardando / ritenueto
        let paceArray = []
        let blacklistedTempoTicks = []
        let isRit = false
        let isAcc = false
        let isTempoChange = false
        let isKeyChange = false
        let isTimeChange = false


        if(typeof props.tempoIndicationMap!=="undefined" && typeof props.timeSignatureMap!=="undefined"){

            isTempoChange = (props.tempoIndicationMap.length > 1)
            isKeyChange = (props.keySignatureMap.length > 1)
            isTimeChange = (props.timeSignatureMap.length > 1)


            //go through all pairs of items in the map
            props.tempoIndicationMap.sort((a,b)=>{return a['tick'] - b['tick']})
            for(let pair of pairwise(props.tempoIndicationMap)){
                //get the time signature at the point of start
                let startTick = pair[0]['tick']
                let ts = getCurrentMapItem(startTick, props.timeSignatureMap)
                //get the length of bar in ticks
                let length = (4*ts['timeSignature']['numBeats']*ticksPerBeat)/ts['timeSignature']['beatValue']
                //check to see if the next change is within a bar
                let endTick = pair[1]['tick']
                if(endTick < startTick+length){
                    let accelerating = (pair[0]['tempoIndication'] <= pair[1]['tempoIndication'])
                    if(accelerating) isAcc = true
                    if(!accelerating) isRit = true
                    paceArray.push({start:startTick, end: endTick, accelerating: accelerating})
                    //blacklist from representation
                    for(let tick of [startTick,endTick])
                        if(!blacklistedTempoTicks.includes(tick)) blacklistedTempoTicks.push(tick)
                }
            }

            let startIndex = 0
            while(true){
                let startItem = paceArray[startIndex]
                if(typeof startItem === 'undefined') break //stop if there are no more items

                //combine as many items as possible
                let currIndex = startIndex
                let curr = startItem
                let next = paceArray[currIndex+1]
                while(next && next['start']===curr['end'] && next['accelerating']===curr['accelerating']){
                    currIndex++
                    curr = paceArray[currIndex]
                    next = paceArray[currIndex+1]
                }

                let threshold = ticksPerBeat
                let rangeTicks = curr['end']-startItem['start']
                if(rangeTicks > threshold){
                    //combine all the items between the indices
                    let merged = {start:startItem['start'], end: curr['end'], accelerating: startItem['accelerating']}
                    let deleted = (currIndex - startIndex) + 1
                    paceArray.splice(startIndex,deleted,merged)
                    startIndex++
                }
                else{
                    let deleted = (currIndex - startIndex) + 1
                    paceArray.splice(startIndex,deleted)
                }
            }


        }


        return (
            <div>

                <Grid container direction="row" justifyContent={"center"} spacing={2}>
                    {
                        isRit?
                        <Grid item justifyContent={"center"}>
                            <Grid container alignItems={"stretch"}>
                                <Grid item><Icon style={{fontSize:"12px",color: signatureColors.rit}}>fiber_manual_record</Icon></Grid>
                                <Grid item> Ritardando</Grid>
                            </Grid>
                        </Grid>:
                        null
                    }
                    {
                        isAcc?
                        <Grid item justifyContent={"center"}>
                            <Grid container alignItems={"center"}>
                                <Grid item><Icon style={{fontSize:"12px",color: signatureColors.acc}}>fiber_manual_record</Icon></Grid>
                                <Grid item> Accelerando</Grid>
                            </Grid>
                        </Grid>:
                        null
                    }
                    {
                        isKeyChange?
                            <Grid item justifyContent={"center"}>
                                <Grid container alignItems={"center"}>
                                    <Grid item><Icon style={{fontSize:"12px",color: signatureColors.key}}>fiber_manual_record</Icon></Grid>
                                    <Grid item> Key Signature Change </Grid>
                                </Grid>
                            </Grid>:
                            null
                    }
                    {
                        isTimeChange?
                            <Grid item justifyContent={"center"}>
                                <Grid container alignItems={"center"}>
                                    <Grid item><Icon style={{fontSize:"12px",color: signatureColors.time}}>fiber_manual_record</Icon></Grid>
                                    <Grid item> Time Signature Change </Grid>
                                </Grid>
                            </Grid>:
                            null
                    }
                    {
                        isTempoChange?
                            <Grid item justifyContent={"center"}>
                                <Grid container alignItems={"center"}>
                                    <Grid item><Icon style={{fontSize:"12px",color: signatureColors.tempo}}>fiber_manual_record</Icon></Grid>
                                    <Grid item> Tempo Indication Change </Grid>
                                </Grid>
                            </Grid>:
                            null
                    }

                </Grid>

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
                            <XAxis domain={null} scale="linear" interval="preserveStart" type="number" dataKey="beat" label={{ value: 'Beat number', position: 'bottom' }}/>
                            <YAxis domain={["dataMin-10","dataMax+10"]}  label={{ value: 'Pitch (~log Hz)', angle: -90, position: 'left' }}/>
                            <Tooltip content={
                                <CustomTooltip
                                    payload
                                    keyMap={(typeof props.keySignatureMap==="undefined"?[]:props.keySignatureMap)}
                                    timeMap={(typeof props.timeSignatureMap==="undefined"?[]:props.timeSignatureMap)}
                                    tempoMap={(typeof props.tempoIndicationMap==="undefined"?[]:props.tempoIndicationMap)}
                                    chordDictionary={chordDictionary}
                                    intervalDictionary={intervalDictionary}
                                    props/> }
                            />
                            <Legend verticalAlign="top" wrapperStyle={{ lineHeight: '40px' }} />
                            {
                                (typeof props.keySignatureMap==="undefined"?[]:props.keySignatureMap).map((item)=>{
                                    return <ReferenceLine x={1+(item['tick'] / ticksPerBeat)} stroke={signatureColors.key} strokeDasharray="3 3" strokeWidth={2}/>
                                })
                            }
                            {
                                (typeof props.timeSignatureMap==="undefined"?[]:props.timeSignatureMap).map((item)=>{
                                    return <ReferenceLine x={1+(item['tick'] / ticksPerBeat)} stroke={signatureColors.time} strokeDasharray="3 3" strokeWidth={2}/>
                                })
                            }
                            {
                                (typeof props.tempoIndicationMap==="undefined"?[]:props.tempoIndicationMap).map((item)=>{
                                    if(blacklistedTempoTicks.includes(item['tick'])) return null;
                                    return <ReferenceLine x={1+(item['tick'] / ticksPerBeat)} stroke={signatureColors.tempo} strokeDasharray="3 3" strokeWidth={2}/>
                                })
                            }
                            {
                                paceArray.map((item)=> {
                                    let quality = (item['accelerating']) ? "Accel." : "Rit."
                                    let col = (item['accelerating']) ? signatureColors.acc : signatureColors.rit
                                    return (
                                        <ReferenceArea x1={1 + (item['start'] / ticksPerBeat)}
                                                       x2={1 + (item['end'] / ticksPerBeat)} stroke={col}
                                                       fill={col}
                                        />
                                    )
                                })
                            }
                            <Brush dataKey="beat" height={30} stroke="#387dd6"/>

                            {items.map((value,index)=>{
                                return <Line key={voiceName(value)} dataKey={voiceName(value)} stroke={color[index%13]} type="monotone" strokeWidth={2}/>
                            })}
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </div>
        )
    }
}

export default TrackViewer;
