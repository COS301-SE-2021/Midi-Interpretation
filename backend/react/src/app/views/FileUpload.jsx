import React, {Component, Fragment, useMemo} from "react";
import {Grid, Card, Button, Fab, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import Dropzone, {useDropzone} from 'react-dropzone'

import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";
import 'react-responsive-combo-box/dist/index.css'


class FileUpload extends Component {

  constructor(props){
      super(props);
      this.onDrop = (files) => {
          this.fileUpload.uploadButtonText = "UploadFile"
          this.fileUpload.isSet = true
          this.fileUpload.files = files
      };
      this.fileUpload = {
          files: [],
          isSet: false,
          uploadButtonText: "Please upload a file to continue.",
          fileDesignator: "a1d7a79c-c22a-4ba3-ba7a-5a269dd8da98"
      };
      this.display = {
          pieceMetadata: null,
          trackInfo: null,
          trackMetadata: null,
          trackOverview: null,
          trackIndex: 0
      }


      this.uploadFile = () => {
          //get the file
          const file = this.fileUpload.files[0];
          console.log(file);
          const formData = new FormData();

          // Update the formData object
          formData.append(
              "myFile",
              file,
              file.name
          );

          const request = new Request("http://localhost:8080/interpreter/uploadFile", {
              method: 'POST',
              body: formData,
              headers: new Headers({
                  'accept': '*/*',
                  'Content-Type': 'multipart/form-data'
              })
          });
          fetch(request)
              .then(res=>res.json())
              .then((res)=>{
                  console.log(res)
              }, (error)=>{
                  console.log(error)
              })
      }

      this.processFile = () => {
          alert('Beginning processing file')
          const request = new Request("http://localhost:8080/interpreter/processFile", {
              method: 'POST',
              body: JSON.stringify({
                  "fileDesignator": this.fileUpload.fileDesignator
              }),
              headers: new Headers({
                  'accept': 'application/json',
                  'Content-Type': 'application/json'
              })
          });
          fetch(request)
              .then(res=>res.json())
              .then((res)=>{
                  console.log(res)
                  alert('Your file has successfully been interpreted')
              }, (error)=>{
                  console.log(error)
                  alert('Failed to interpret the given file.')
              })
      }

      this.getPieceMetadata = () => {
          const request = new Request("http://localhost:8080/display/getPieceMetadata", {
              method: 'POST',
              body: JSON.stringify({
                  "fileDesignator": this.fileUpload.fileDesignator
              }),
              headers: new Headers({
                  'accept': 'application/json',
                  'Content-Type': 'application/json'
              })
          });
          fetch(request)
              .then(res=>res.json())
              .then((res)=>{
                  this.display.pieceMetadata = res
              }, (error)=>{
                  console.log(error)
              })
      }

      this.getTrackInfo = () => {
          const request = new Request("http://localhost:8080/display/getTrackInfo", {
              method: 'POST',
              body: JSON.stringify({
                  "fileDesignator": this.fileUpload.fileDesignator
              }),
              headers: new Headers({
                  'accept': 'application/json',
                  'Content-Type': 'application/json'
              })
          });
          fetch(request)
              .then(res=>res.json())
              .then((res)=>{
                  this.display.trackInfo = res
              }, (error)=>{
                  console.log(error)
              })
      }

      this.getTrackMetadata = () => {
          const request = new Request("http://localhost:8080/display/getTrackMetadata", {
              method: 'POST',
              body: JSON.stringify({
                  "fileDesignator": this.fileUpload.fileDesignator,
                  "trackIndex": this.display.trackIndex
              }),
              headers: new Headers({
                  'accept': 'application/json',
                  'Content-Type': 'application/json'
              })
          });
          fetch(request)
              .then(res=>res.json())
              .then((res)=>{
                  this.display.trackMetadata = res['trackString']
              }, (error)=>{
                  console.log(error)
              })
      }

      this.getTrackOverview = () => {
          const request = new Request("http://localhost:8080/display/getTrackOverview", {
              method: 'POST',
              body: JSON.stringify({
                  "fileDesignator": this.fileUpload.fileDesignator,
                  "trackIndex": this.display.trackIndex
              }),
              headers: new Headers({
                  'accept': 'application/json',
                  'Content-Type': 'application/json'
              })
          });
          fetch(request)
              .then(res=>res.json())
              .then((res)=>{
                  this.display.trackOverview = res
              }, (error)=>{
                  console.log(error)
              })
      }

  }

  componentDidMount() {
        this.getPieceMetadata()
        this.getTrackInfo()
        this.getTrackMetadata()
        this.getTrackOverview()
  }

    shouldComponentUpdate() {
        return true;
    }


  render() {
      const classes = makeStyles;
      let { theme } = this.props;

      const files = this.fileUpload.files.map(file => (
          <li key={file.name}>
              {file.name} - {file.size} bytes
          </li>
      ));

      const options = [{
          key: 'Jenny Hess',
          text: 'Jenny Hess',
          value: 'Jenny Hess',
      }]

      return (



          <div className="m-sm-30" >
              <div className="mb-sm-30">
                  <Breadcrumb
                      routeSegments={[
                          { name: "File Upload" }
                      ]}
                  />
              </div>
              <SimpleCard title="Upload File">
                  <Dropzone onDrop={this.onDrop}>
                      {({getRootProps, getInputProps}) => (
                          <section className="container">
                              <div {...getRootProps({className: 'dropzone'})}>
                                  <input {...getInputProps()} />
                                  <p>Drag your midi file here, or click to browse for a file</p>
                              </div>
                              <aside>
                                  <ul>{files}</ul>
                              </aside>
                          </section>
                      )}
                  </Dropzone>
                  <Button disabled={!this.fileUpload.isSet} color="secondary" onClick={this.uploadFile}>
                      {this.fileUpload.uploadButtonText}
                  </Button>
              </SimpleCard>
              <SimpleCard title="Process File">
                  <Button onClick={this.processFile}> Process Your File</Button>
              </SimpleCard>

              <SimpleCard title="Piece Metadata">
                  <div id="music_container">
                      <div>
                          <table border={1}>
                              <thead>
                              <tr>
                                  <th>Element</th>
                                  <th>Value</th>
                              </tr>
                              </thead>
                              <tbody>
                              <tr>
                                  <td>Key Signature</td>
                                  <td >{(this.display.pieceMetadata!=null)?this.display.pieceMetadata['keySignature']:"loading..."}</td>
                              </tr>
                              <tr>
                                  <td rowSpan={2}>Time Signature</td>
                                  <td >{(this.display.pieceMetadata!=null)?this.display.pieceMetadata['timeSignature']['numBeats']:"loading..."}</td>
                              </tr>
                              <tr>
                                  <td >{(this.display.pieceMetadata!=null)?this.display.pieceMetadata['timeSignature']['beatValue']:"loading..."}</td>
                              </tr>
                              <tr>
                                  <td rowSpan={2}>Tempo Indication</td>
                                  <td >{(this.display.pieceMetadata!=null)?this.display.pieceMetadata['tempoIndication']:"loading..."}</td>
                              </tr>
                              </tbody>
                          </table>
                      </div>
                  </div>
              </SimpleCard>

              <SimpleCard title={"Track Parts"}>
                  <div>
                      {(this.display.trackInfo==null)?"Loading...":this.display.trackInfo.map((item) => <div color={"grey"}>{item['trackName']}</div>)}
                  </div>
              </SimpleCard>

              <SimpleCard title="Get Track Overview">
                  <p>Track {this.display.trackIndex+1} : {(this.display.trackInfo==null)?"Loading...":this.display.trackInfo[this.display.trackIndex]['trackName']}</p>
                  <div>{(this.display.trackInfo==null)?"Loading...":JSON.stringify(this.display.trackOverview)}</div>
              </SimpleCard>

              <SimpleCard title="Get Track Metadata">
                  <p>Track {this.display.trackIndex+1} : {(this.display.trackInfo==null)?"Loading...":this.display.trackInfo[this.display.trackIndex]['trackName']}</p>
                  <div>{(this.display.trackInfo==null)?"Loading...":JSON.stringify(this.display.trackMetadata)}</div>
              </SimpleCard>



          </div>


      );
  };



}

export default withStyles({}, { withTheme: true })(FileUpload);
