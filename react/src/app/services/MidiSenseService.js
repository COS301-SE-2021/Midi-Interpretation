/**
 * Class that handles RESTful requests made to the MidiSense backend. Implements a generic framework for requests
 * and automates specific requests for service functions.
 */
class MidiSenseService {
    // private targetHeaders;
    // private targetURL: string;
    // private uploadHeaders: { accept: string; "Content-Type": string };
    // private targetMethod: string;

    constructor() {
        this.targetURL = "http://127.0.0.1:7070"
        this.targetHeaders = {
            'accept': 'application/json',
            'Content-Type': 'application/json'
        }
        this.uploadHeaders = {
            'accept': '*/*',
            'Content-Type': 'multipart/form-data'
        }
        this.targetMethod = "POST"
        this.simpleOctave = ["Perfect Unison","Semitone","Tone","Minor 3rd","Major 3rd","Perfect 4th","Tritone","Perfect 5th","Minor 6th","Major 6th","Minor 7th","Major 7th"]
        this.compoundOctave = ["Perfect Octave","Minor 2nd","Major 2nd","Minor 3rd","Major 3rd","Perfect 4th","Tritone","Perfect 5th","Minor 6th","Major 6th","Minor 7th","Major 7th"]
    }

    //====================================
    // DISPLAY SYSTEM REQUESTS
    //====================================

    displayGetPieceMetadata(fileDesignator, onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/display/getPieceMetadata"
        this.genericRequest(
            this.targetURL+endpoint,
            {"fileDesignator": fileDesignator},
            this.targetHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    displayGetTrackInfo(fileDesignator, onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/display/getTrackInfo"
        this.genericRequest(
            this.targetURL+endpoint,
            {"fileDesignator": fileDesignator},
            this.targetHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    displayGetTrackMetadata(fileDesignator, trackIndex, onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/display/getTrackMetadata"
        this.genericRequest(
            this.targetURL+endpoint,
            {"fileDesignator": fileDesignator, "trackIndex": trackIndex },
            this.targetHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    displayGetTrackOverview(fileDesignator, trackIndex, onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/display/getTrackOverview"
        this.genericRequest(
            this.targetURL+endpoint,
            {"fileDesignator": fileDesignator, "trackIndex": trackIndex },
            this.targetHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    //====================================
    // INTELLIGENCE SYSTEM REQUESTS
    //====================================

    intelligenceAnalyseGenre(fileDesignator, onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/intelligence/analyseGenre"
        this.genericRequest(
            this.targetURL+endpoint,
            {"fileDesignator": fileDesignator},
            this.targetHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    intelligenceAnalyseChord(pitchArray, onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/intelligence/analyseChord"
        this.genericRequest(
            this.targetURL+endpoint,
            {"pitchArray": pitchArray},
            this.targetHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    //====================================
    // INTERPRETER SYSTEM REQUESTS
    //====================================

    interpreterProcessFile(fileDesignator, onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/interpreter/processFile"

        this.genericRequest(
            this.targetURL+endpoint,
            {"fileDesignator": fileDesignator},
            this.targetHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    interpreterUploadFile(file,onSuccess = (res)=>{}, onFailure= (res)=>{}){
        const endpoint = "/interpreter/uploadFile"
        const formData = new FormData();

        // Update the formData object
        formData.append("file",file);

        this.genericRequest(
            this.targetURL+endpoint,
            file,
            this.uploadHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
    }

    intelligenceAnalyseInterval(pitchArray){
        let range = Math.abs(pitchArray[0]-pitchArray[1])
        let isCompound = (range >= 12)
        if (!isCompound)
            return this.simpleOctave[range]
        else{
            range = range % 12
            return (range===0?"":"Compound ")+this.compoundOctave[range]
        }



    }

    //====================================
    // AUXILIARY METHODS
    //====================================

    /** Abstraction of a RESTful request to some host, given a set of parameters encoding the properties of the request
     *
     * @param url the address of the host. Defaults to callback address
     * @param body the body of the request, as a valid JSON Object: <pre class="code"> {"name": "Frank"} </pre> Defaults to empty
     * @param headers the headers of the request, as a valid JSON Object: <pre class="code"> {"accept": "application/json"} </pre> Defaults to empty
     * @param method the request method to be used: <pre class="code"> GET, POST, SET, PUT, DELETE </pre> Defaults to POST
     * @param onFailure callback to the function executed after an unsuccessful response code is received,
     * takes one parameter, the error body: <pre class="code"> (error)=>{onFailure(error)} </pre> Defaults to empty
     * @param onSuccess callback to the function executed after a successful response code is received,
     * takes one parameter, the response body: <pre class="code"> (res)=>{onSuccess(res)} </pre> Defaults to empty
     */
    genericRequest(
        url="http://localhost",
        body={},
        headers={},
        method="POST",
        onSuccess = (res)=>{},
        onFailure = (error)=>{})
    {

        //create a request parametrisation
        const requestParameters = new Request(url, {
            method: method,
            body: JSON.stringify(body),
            headers: new Headers(headers)
        });

        //make the request, accept JSON as response and handle response
        fetch(requestParameters).then(res=>res.json()).then(

            //Upon a successful status code being received
            (res)=>{
                onSuccess(res)
            },

            //Upon an unsuccessful status code being received
            (error)=>{
                onFailure(error)
            })
    }
}

export default MidiSenseService