import React, {Component, Fragment, useMemo, useState} from "react";
import {Grid, Card, Button, Fab, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import Dropzone, {useDropzone} from 'react-dropzone'
import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";
import 'react-responsive-combo-box/dist/index.css'
import MidiSenseService from "../services/MidiSenseService";

class Upload extends Component {


  constructor(props){
      super(props);
      this.state = {
          files: [],
          isFileSet: false,
          uploadButtonText: "Please upload a file to continue.",
          fileDesignator: "",
          pieceMetadata: null,
          trackInfo: null,
          trackMetadata: null,
          trackOverview: null,
          trackIndex: 0
      }

      this.backendService = new MidiSenseService()

      this.onDrop = (files) => {
          this.setState({
              files: files,
              isFileSet: true,
              uploadButtonText: "UploadFile"
          })
      };

      this.uploadFile = () => {
          const uploadFile = this.state.files[0]
          console.log("Call to upload file : "+JSON.stringify(uploadFile))
          this.backendService.interpreterUploadFile(
              uploadFile,

              (res)=>{
                  const designator = res['fileDesignator']
                  localStorage.setItem("fileDesignator", designator)
                  console.log("Upload successful : assigned designator "+designator)
                  alert("Successfully uploaded, beginning interpretation")
                  this.beginInterpretation()
              },

              (error)=>{
                  console.error("File upload failed : "+error)
              }
          )
      }

      this.beginInterpretation = () => {
          const designator = localStorage.getItem("fileDesignator")
          this.backendService = new MidiSenseService()
          console.log("Call to interpret current file")
          this.backendService.interpreterProcessFile(
              designator,

              (res)=>{
                  const success = res['success']
                  const message = res['message']
                  console.log("Interpretation request "+(success===true?"accepted":"declined")+": "+message)

              },

              (error)=>{
                  console.error("Interpretation request failed : "+JSON.stringify(error))
              }
          )
      }
  }



  componentDidMount() {

  }

  shouldComponentUpdate() {
    return true;
  }

  render() {
      const classes = makeStyles;
      let { theme } = this.props;

      const files = this.state.files.map(file => (
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
              <SimpleCard>
                  <div className={"w-400"}>
                      <img src={process.env.PUBLIC_URL + '/assets/images/logo-full.png'} alt={"MidiSense Logo"}/>
                  </div>
                  <br/>
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
                                  className={`h-200 w-full border-radius-8 elevation-z6 bg-light-gray flex justify-center items-center cursor-pointer`}
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
                                                      <ul>{JSON.stringify(this.state.files) }</ul>
                                                  </aside>
                                              </section>
                                          )}
                                      </Dropzone>
                                  </div>
                              </div>
                          </Grid>

                  <br/>
                  <Button
                      variant="outlined"
                      disabled={!this.state.isFileSet}
                      color="primary"
                      className={classes.button}
                      onClick={()=>{/*INSERT UPLOAD FILE HERE*/}}>
                      Process Your File
                  </Button>
              </SimpleCard>
          </div>


      );
  };



}

export default withStyles({}, { withTheme: true })(Upload);
