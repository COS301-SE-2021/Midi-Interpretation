import React, {Component, Fragment, useMemo} from "react";
import {Grid, Card, Button, Fab, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import Dropzone, {useDropzone} from 'react-dropzone'
import Avatar from '@material-ui/core/Avatar';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Link from '@material-ui/core/Link';
import Box from '@material-ui/core/Box';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';

import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";
import {Text} from "victory";

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
          fileDesignator: "ca09ae60-d3df-43b6-95a6-233b5f81ea2c"
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
              }, (error)=>{
                  console.log(error)
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
                  this.display.trackMetadata = res
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
              <SimpleCard title="Upload File">
                  <Button onClick={this.processFile}> Process Your File</Button>
              </SimpleCard>

              <SimpleCard title="Get Piece Metadata">
                  <div>{JSON.stringify(this.display.pieceMetadata)}</div>
                  <Button onClick={this.getPieceMetadata}> Get MetaData</Button>
              </SimpleCard>

              <SimpleCard title="Get Track Info">
                  <div>{JSON.stringify(this.display.trackInfo)}</div>
                  <Button onClick={this.getTrackInfo}> Get Track Info </Button>
              </SimpleCard>

              <SimpleCard title="Get Track Metadata">
                  <div>{JSON.stringify(this.display.trackMetadata)}</div>
                  <Button onClick={this.getTrackMetadata}> Get Track Metadata </Button>
              </SimpleCard>

              <SimpleCard title="Get Track Overview">
                  <div>{JSON.stringify(this.display.trackOverview)}</div>
                  <Button onClick={this.getTrackOverview}> Get Track Overview </Button>
              </SimpleCard>

          </div>


      );
  };



}

export default withStyles({}, { withTheme: true })(FileUpload);
