import React, {Component, useRef} from "react";
import {Grid, Button, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";
import 'react-responsive-combo-box/dist/index.css'
import MidiSenseService from "../services/MidiSenseService";
import Cookies from 'universal-cookie';
import {Redirect} from "react-router-dom";
import ResponsiveDialog from "./ResponsiveDialog";

import 'react-dropzone-uploader/dist/styles.css'
import Dropzone from 'react-dropzone-uploader'



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
      this.cookies = new Cookies();
      this.state = {
          files: [],
          isFileSet: false,
          uploadButtonText: "Please upload a file to continue.",
          fileDesignator: "",
          pieceMetadata: null,
          trackInfo: null,
          trackMetadata: null,
          trackOverview: null,
          trackIndex: 0,
          cookies: this.cookies.get('allowCookies'),
          path: ""
      }
      this.backendService = new MidiSenseService()


        /**
         * onDrop handles files being added to the dropzone
         *
         * @param files - File to be interpreted
         */

      this.handleChangeStatus = ({ meta, file, xhr }, status) => {
          if (status === 'done' && this.cookies.get('allowCookies') !== undefined){
              let response = JSON.parse(xhr.response);
              let designator = response.fileDesignator
              this.cookies.set('fileDesignator', designator, { path: '/' });
              this.cookies.set('title', file.name, { path: '/' });
              console.log(response)
          }

          console.log(status, meta, file)

      }
      this.getUploadParams = ({ meta }) => { return { url: 'http://localhost:8080/interpreter/uploadFile' } }

      this.onSubmit = () => {
          this.setState({
              isFileSet: this.cookies.get('allowCookies') !== undefined,
              uploadButtonText: "UploadFile",
          })
      }

        /**
         * beginInterpretation handles sending a request to the server to begin the interpretation of the uploaded
         * midi file
         *
         * Prerequisite: the server has received the uploaded file and returned a designator
         */

      this.beginInterpretation = () => {
          const designator = this.cookies.get('fileDesignator')
          this.backendService = new MidiSenseService()
          console.log("Call to interpret current file")
          this.backendService.interpreterProcessFile(
              designator,

              (res)=>{
                  const success = res['success']
                  const message = res['message']
                  console.log("Interpretation request "+(success===true?"accepted":"declined")+": "+message)
                  this.props.history.push("/Display");
              },

              (error)=>{
                  console.error("Interpretation request failed : "+JSON.stringify(error))
                  this.props.history.push("/Upload");
              }
          )
      }

      this.ProcessFile = () => {
          if(this.cookies.get('allowCookies') !== undefined) {
              if (this.state.isFileSet) {
                  this.props.history.push("/Loading");
                  this.beginInterpretation()
                  this.render()
              }
          }
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

              <Grid container justify="space-evenly" spacing={3} alignItems="center">

              <SimpleCard title="Upload File">
                      <Grid item>
                          <div
                              className={`h-200 w-full border-radius-8 elevation-z6 bg-light-gray flex justify-center items-center cursor-pointer`}
                          >
                          <div>
                              <div id="toast">

                              </div>

                              <Dropzone
                                  disabled={this.cookies.get('allowCookies') === undefined}
                                  getUploadParams={this.getUploadParams}
                                  onChangeStatus={this.handleChangeStatus}
                                  accept=".mid"
                                  maxFiles={1}
                                  multiple={false}
                                  canCancel={false}
                                  onSubmit={this.onSubmit}
                                  inputContent={
                                      <div key="key" className={"m-10"}>
                                          <Icon className={"center"} fontSize="large">backup</Icon>
                                          <p>Drag your midi file here, or click to browse for a file</p>
                                          <aside>
                                              {this.state.path}
                                          </aside>
                                      </div>
                                  }
                                  styles={{dropzone: { width: 400, height: 200 },}}
                              />
                          </div>
                      </div>
                      </Grid>

                  <br/>
                  <Button
                      variant="outlined"
                      disabled={!this.state.isFileSet}
                      color="primary"
                      className={classes.button}
                      onClick={()=>{ this.ProcessFile() }
                      }>
                      {
                          this.cookies.get('allowCookies') === undefined ?
                              <div>Cookies Required</div> :
                              <div>Process File</div>
                      }
                  </Button>
              </SimpleCard>
                  <Grid item>
                      <div className={"max-w-500 min-w-300"}>
                          <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Technical_character.svg'} alt={"Person singing"}/>
                      </div>
                  </Grid>
              </Grid>
              {
                  this.cookies.get('allowCookies') === undefined ?
                  <ResponsiveDialog/> :
                  <div/>
              }
          </div>
      );
  };
}

/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Upload);
