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

  }

  componentDidMount() {
      this.backendService = new MidiSenseService()
      alert('Starting interpretation')
      this.backendService.interpreterProcessFile(
          "0bc8dcc5-79a0-4b5a-9be5-b9978b9febe1",
          (res)=>{alert(res)},
          (error)=>{alert(error)}
      )
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
              <SimpleCard>
                  <div className={"w-400"}>
                      <img src={process.env.PUBLIC_URL + '/assets/images/logo-full.png'}></img>
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
