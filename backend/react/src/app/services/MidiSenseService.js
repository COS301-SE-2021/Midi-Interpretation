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
        this.targetURL = "http://localhost:8080"
        this.targetHeaders = {
            'accept': 'application/json',
            'Content-Type': 'application/json'
        }
        this.uploadHeaders = {
            'accept': '*/*',
            'Content-Type': 'multipart/form-data'
        }
        this.targetMethod = "POST"
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
        formData.append(
            "myFile",
            file,
            file.name
        );

        this.genericRequest(
            this.targetURL+endpoint,
            formData,
            this.uploadHeaders,
            this.targetMethod,
            onSuccess,
            onFailure
        )
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
        onFailure = (error)=>{},
        onSuccess = (res)=>{})
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