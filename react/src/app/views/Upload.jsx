import React, {Component} from "react";
import {Grid, Button, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";
import 'react-responsive-combo-box/dist/index.css'
import MidiSenseService from "../services/MidiSenseService";
import Cookies from 'universal-cookie';
import ResponsiveDialog from "../../matx/components/ResponsiveDialog";
import 'react-dropzone-uploader/dist/styles.css'
import Dropzone from 'react-dropzone-uploader'
import Load from "../../matx/components/LoadingOverlay";


/**
 * This class defines the first view that a user will be presented with
 * The view will explain how to use our service and act as an about page for MIDISense
 * There is a dropzone where the user can upload the midi file they intend to analyse
 * Finally, the user will need to press a button to initiate the processing of their file
 *
 * Navigation:
 *      -> Display
 *
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

      // Initialize the cookie system
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
          modalVisible: false,
          cookies: this.cookies.get('allowCookies'),
          path: "",
          first: true
      }
      this.backendService = new MidiSenseService()

        /**
         * onDrop handles files being added to the dropzone
         *
         * @param files - File to be interpreted
         */

      this.handleChangeStatus = ({ meta, file, xhr }, status) => {
          if (status === 'done' && this.cookies.get('allowCookies') !== undefined){
              let tomorrow = new Date()
              tomorrow.setDate(tomorrow.getDate()+1);

              let response = JSON.parse(xhr.response);
              let designator = response.fileDesignator

              if(this.state.fileDesignator !== "")
                  this.setState({first:false})

              this.cookies.set('fileDesignator', designator, { path: '/',
                  expires: tomorrow // Will expire after 24hr from setting (value is in Date object)
              });
              this.cookies.set('title', file.name, { path: '/' ,
                  expires: tomorrow // Will expire after 24hr from setting (value is in Date object)
              });
          }
      }

      // TODO: url must be defined dynamically and not hard coded
      this.getUploadParams = () => { return { url: 'http://localhost:8080/interpreter/uploadFile' } }

        /**
         * Handle the form submission
         */

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
          this.backendService.interpreterProcessFile(
              designator,

              /**
               * onSuccess
               * handles navigation to Display
               * @param res
               */

              (res)=>{
                  this.props.history.push("/Display")

                  if(this.state.first){
                      window.location.reload(false)
                  }
              },

              /**
               * onFailure
               * reloads page on failure (room for extended error handling)
               * @param error
               */

              (error)=>{
                  this.props.history.push("/Upload");
              }
          )
      }

        /**
         * ProcessFile
         * Calls sub-methods to begin processing
         * @constructor
         */
      this.ProcessFile = () => {
          //ensure cookies are allowed
          if(this.cookies.get('allowCookies') !== undefined) {
              //ensure there were no errors in processing the file
              if (this.state.isFileSet) {
                  this.setModalVisible(true)
                  this.beginInterpretation()
                  this.render()
              }
          }
      }

        /**
         * setModalVisible
         * Popup to indicate that the system is processing the file
         * @param v
         */
      this.setModalVisible = (v) => {
          this.setState({
              modalVisible:v
          })
        }
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
              <Load  modalVisible={this.state.modalVisible} setModalVisible={this.setModalVisible}/>
              <div className="mb-sm-30">
                  <Breadcrumb
                      routeSegments={[
                          { name: "File Upload" }
                      ]}
                  />
              </div>

              <div style={{height:"50px"}}/>
              <Grid container direction="row" justifyContent="space-evenly" spacing={5} alignItems="flex-start">
              <SimpleCard title="Upload File">
                      <Grid item>
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
                                      <Grid style={{padding:"20px"}} container key="key" className={"m-10 text-primary"}
                                            justifyContent="center"
                                            alignItems="center"
                                            direction="column"
                                            spacing={2}
                                      >
                                          <Grid item>
                                            <Icon className={"center"} fontSize="large">backup</Icon>
                                          </Grid>
                                          <Grid item>
                                            <p>Drag your midi file here, or click to browse for a file</p>
                                          </Grid>
                                      </Grid>
                                  }
                                  styles={{
                                      dropzone: {
                                          borderRadius: "",
                                          overflow: "hidden",
                                          width: 400,
                                          height: 200,
                                          backgroundColor:"#FFF",
                                          border: "1px dashed #D2D2D2FF"
                                      },
                                      submitButton:{
                                          backgroundColor:"#FFf",
                                          border:"1px solid #387dd6",
                                          color:"#387dd6"
                                      }

                                  }}
                              />
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
                  <br/>
                  <Grid item>
                      <div className={"max-w-500 min-w-300"}>
                          <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Technical_character.svg'} alt={"Person singing"}/>
                      </div>
                  </Grid>
              </Grid>

              <div style={{height:"50px"}}/>
              <div><asside className="text-muted">To use MIDISense File Analysis:</asside></div>
              <div><asside className="text-muted">First, <b>Upload</b> a midi file bellow, <b>Wait</b> for it to upload
                  then press the <b>Submit</b> button. Finally, process the <b>Process</b> button</asside></div>
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
