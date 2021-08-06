import React, {Component} from "react";
import {Grid, Button, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import Dropzone from 'react-dropzone'
import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";
import 'react-responsive-combo-box/dist/index.css'
import MidiSenseService from "../services/MidiSenseService";
import localStorage from "../services/localStorageService";

/**
 * This class defines the first view that a user will be presented with
 * The view will explain how to use our service and act as an about page for MIDISense
 * There is a dropzone where the user can upload the midi file they intend to analyse
 * Finally, the user will need to press a button to initiate the processing of their file
 *
 * Navigation:
 *      -> Display
 *
 * Components:
 *      -> MidiSenseService
 *      -> LocalStorageService
 *      -> Breadcrumb
 *      -> SimpleCard
 */

class Upload extends Component {

    /**
     * The main constructor for the Upload view
     * The state values are defined here as well as the methods for the view
     *
     * @constructor
     * @param props
     */

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

        /**
         * onDrop handles files being added to the dropzone
         *
         * @param files - File to be interpreted
         */

      this.onDrop = (files) => {
          this.setState({
              files: files,
              isFileSet: true,
              uploadButtonText: "UploadFile"
          })
      };

        /**
         * uploadFile is called when the user presses the process file button
         * This will send the file to the server, store the file designator and then request interpretation
         * The view will then be changed to a loading screen until the server responds and then present the display view
         *
         * Prerequisite: a file has been uploaded
         *
         * @property alert
         * @property JSON
         */

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

        /**
         * beginInterpretation handles sending a request to the server to begin the interpretation of the uploaded
         * midi file
         *
         * Prerequisite: the server has received the uploaded file and returned a designator
         */

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

    /**
     * componentDidMount is invoked immediately after a component is mounted (inserted into the tree)
     */

  componentDidMount() {

  }

    /**
     * shouldComponentUpdate lets React know if a component’s output is not affected by the current change in state
     * or props. In our case, true.
     *
     * @param nextProps
     * @param nextState
     * @param nextBool
     * @returns {boolean}
     */

  shouldComponentUpdate(nextProps, nextState, nextBool) {
    return true;
  }

    /**
     * This method returns the elements that we want displayed
     *
     * REQUIRED
     *
     * @returns {JSX.Element}
     */

  render() {
      const classes = makeStyles;
      return (
          <div className="m-sm-30" >
              <div className="mb-sm-30">
                  <Breadcrumb
                      routeSegments={[
                          { name: "File Upload" }
                      ]}
                  />
              </div>

              <Grid container justify="space-evenly" spacing={3} alignItems="centre">

              <SimpleCard title="Upload File">

                      <Grid item>
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
                      onClick={()=>{
                          if(this.state.isFileSet){
                              this.uploadFile()
                              this.beginInterpretation()
                          }
                      }}>
                      Process Your File
                  </Button>
              </SimpleCard>
                  <Grid item>
                      <div className={"max-w-500 min-w-300"}>
                          <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Technical_character.svg'} alt={"Person singing"}/>
                      </div>
                  </Grid>
              </Grid>
          </div>
      );
  };
}

/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Upload);
