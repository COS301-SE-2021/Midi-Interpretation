import React, {Component, Fragment, useMemo, useState} from "react";
import {Grid, Card, Button, Fab, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import Dropzone, {useDropzone} from 'react-dropzone'

import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";
import 'react-responsive-combo-box/dist/index.css'


class Upload extends Component {

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

      return (
          <div className="m-sm-30" >
              <div className="mb-sm-30">
                  <Breadcrumb
                      routeSegments={[
                          { name: "File Upload" }
                      ]}
                  />
              </div>
              <SimpleCard title="Welcome to MIDISense">
                  <p>MIDISense is an interactive system that helps composers and enthusiasts the ability to leverage
                      the power of Midi as well as a powerful AI in order to gain insight into your music.
                  </p>
                  <br/>
                  <div>
                      <h6>
                        To use MIDISense
                      </h6>
                      <li>Upload a midi file below</li>
                      <li>Push the process button</li>
                      <li>Review the details of your piece</li>
                  </div>
                  <br/>
              </SimpleCard>

              <br/>

              <SimpleCard title="Upload File">

                          <Grid>
                              <div
                                  className={`h-132 w-full border-radius-8 elevation-z6 bg-light-gray flex justify-center items-center cursor-pointer`}
                              >
                                  <div >
                                      <Dropzone onDrop={this.onDrop}>
                                          {({getRootProps, getInputProps}) => (
                                              <section className="container">
                                                  <div {...getRootProps({className: 'dropzone'})}>
                                                      <input {...getInputProps()} />
                                                      <div className={"mx-10"}>
                                                          <Icon className={"center"} fontSize="large">backup</Icon>
                                                          <p>Drag your midi file here, or click to browse for a file</p>
                                                      </div>
                                                  </div>
                                                  <aside>
                                                      <ul>{files}</ul>
                                                  </aside>
                                              </section>
                                          )}
                                      </Dropzone>
                                  </div>
                              </div>
                          </Grid>

                  <br/>
                  <Button disabled={!this.fileUpload.isSet} color="secondary" onClick={this.uploadFile}>
                      {this.fileUpload.uploadButtonText}
                  </Button>
                  <Button
                      variant="outlined"
                      color="primary"
                      className={classes.button}
                      onClick={this.processFile}>
                      Process Your File
                  </Button>
              </SimpleCard>
          </div>


      );
  };



}

export default withStyles({}, { withTheme: true })(Upload);
