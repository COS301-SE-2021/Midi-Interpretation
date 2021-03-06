import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import TrackMenu from "../../matx/components/TrackMenu";
import MidiSenseService from "../services/MidiSenseService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Grid, Slider, Snackbar} from "@material-ui/core";
import Cookies from "universal-cookie";
import GenreTable from "../../matx/components/GenreTable";
import {withStyles} from "@material-ui/core/styles";
import KeySignature from "../../styles/images/keyMap"
import TimeSignature from "../../styles/images/timeMap"
import Typography from "@material-ui/core/Typography";
import * as PropTypes from "prop-types";
import {Alert} from '@material-ui/lab';

Alert.propTypes = {
    severity: PropTypes.string,
    onClose: PropTypes.any,
    children: PropTypes.node
};

/**
 * This class defines the interpretation of a midi file that has been supplied by the server
 * It displays:
 *      - Song Title
 *      - Piece Meta Data
 *          - Key
 *          - Time Signature
 *          - Tempo Indication
 *          - Genres
 *      - Tracks
 *      - Location In Track
 *      - Bar Information
 *          - Bar number
 *          - Chords
 *          - Bar events
 *          - Notes
 *              - Pitch
 *              - Velocity
 *              - Octave Value
 *
 * Navigation:
 *      -> Upload
 *
 */

class Display extends Component {

  /**
   * The main constructor for the Display view
   * The state values are defined here as well as the methods for the view
   *
   * @constructor
   * @param props
   */

  constructor(props) {
      super(props)

      // Initialize the cookie system
      this.cookies = new Cookies()

      this.state = {
        expired: false,
        error: false,
        openError:false,
        errorCount: 0,
        songTitle: "Song Title",
        keySignatureMap: [{"tick": 0, "keySignature": "Cmaj"}],
        tempoIndicationMap: [{"tick": 0, "tempoIndication": 120}],
        timeSignatureMap: [{"tick": 0, "timeSignature": {"numBeats": 4, "beatValue": 4}}],
        currentTrack: 0,
        trackListing: [],
        trackData:[],
        ticksPerBeat:1,
        numberOfGenres: 5,
        instrument: "Unknown",
        fileDesignator: this.cookies.get('fileDesignator'),
        genreData:[],
        midisenseService: new MidiSenseService(),
        selectedIndex:0,
        lineData:[],
        items:[],
        color : [
          "#37A2DA",
          "#32C5E9",
          "#67E0E3",
          "#9FE6B8",
          "#FFDB5C",
          "#ff9f7f",
          "#fb7293",
          "#E062AE",
          "#E690D1",
          "#e7bcf3",
          "#9d96f5",
          "#8378EA",
          "#96BFFF"
        ],
          keyMap: new KeySignature(),
          timeMap: new TimeSignature(),
      }

      this.setState({keySignatureMap : [this.state.keySignature]})
      this.setState({timeSignatureMap : [this.state.timeSignature]})
      this.setState({tempoIndicationMap : [this.state.tempoIndication]})

      // Check if a track has been processed previously, otherwise navigate back to upload

      if(this.cookies.get('fileDesignator') === undefined){
          //this.props.history.push("/Upload")
          //window.location.reload(false)
          this.handleError()
      }

      // Begin gathering data from the server

      this.getTrackMetadata(this.state.currentTrack)

  }

    /**
     * Update the display data when componentDidMount
     */

  componentDidMount() {
      this.refreshScoreDetails()
  }

  //====================================
  // DISPLAY STATE VALUE SETTERS
  //====================================

    /**
     * setSongTitle
     * @param st - new song title
     */

  setSongTitle = (st) => {
      this.setState({
        songTitle: st
      })
  }

    /**
     * setCurrentTrack
     * @param ct - new current track
     */

  setCurrentTrack = (ct) => {
      this.setState({
        currentTrack: ct
      })
      this.getTrackMetadata(ct)
  }

    /**
     * setTrackListing
     * @param tl - new track listing
     */

  setTrackListing = (tl) => {
      this.setState({
        trackListing: tl
      })
  }

    /**
     * setTrackData
     * @param td
     */

  setTrackData = (td) => {

      this.setState({
          trackData: td
      })
  }

    /**
     * setTicksPerBeat
     * @param t
     */

  setTicksPerBeat = (t) => {
      this.setState({
          ticksPerBeat: t
      })
  }

    /**
     * setInstrument
     * @param i
     */

  setInstrument = (i) => {
      this.setState({
          instrument: i
      })
  }

    /**
    /**
     * setSelected
     * @param s
     */

  setSelected = (s) => {
      this.setState({
          selectedIndex:s
      })
  }

    /**
     * getDigitsFromNumber
     * @param t
     * @returns {number[]|*[]}
     */

  getDigitsFromNumber = (t) => {
      if (typeof t !== 'number')
          return [0]

      t = ""+t
      let arrayDigits = []
      for(let char of t){
          arrayDigits.push(char)
      }
      return arrayDigits
  }



  //====================================
  // DISPLAY METHODS
  //====================================

  /**
   * getScoreMetadata will get and set the key signature, tempt indication, time signature and track listing
   */

  getScoreMetadata = () => {
      /**
       * displayGetPieceMetadata
       */
     this.state.midisenseService.displayGetPieceMetadata(this.state.fileDesignator,
        (res) => {
            console.log(res)
          this.setState({keySignatureMap: res['keySignatureMap']})
          this.setState({tempoIndicationMap: res['tempoIndicationMap']})
          this.setState({timeSignatureMap: res['timeSignatureMap']})
          this.setState({error:false})
        },
         (res) =>{
            this.handleError(res)
         })
      /**
       * displayGetTrackInfo
       */
     this.state.midisenseService.displayGetTrackInfo(this.state.fileDesignator,
        (res) => {
            console.log(res)
            for (const track of res) {
              let currentListing = this.state.trackListing
              currentListing.push((track['index'] + 1) + ". " + track['trackName'])
              this.setTrackListing(currentListing)
            }
            this.setState({error:false})
        },
         (res) =>{
            this.handleError(res)
         })

      /**
       * intelligenceAnalyseGenre
       */
      this.state.midisenseService.intelligenceAnalyseGenre(this.state.fileDesignator,
         (res) => {
             console.log(res)
             const success = res['success']
             if(success === undefined || !success)
                 this.handleError(res)
             else {

                 const genreData = res['genreArray']

                 for (let i = 0; i < genreData.length; i++) {
                     genreData[i].Certainty = parseFloat(genreData[i].Certainty).toFixed(3);
                 }

                 this.setState({genreData: genreData})
                 this.setState({error:false})
             }
          },
          (res)=>{
              this.handleError(res)
          })
  }

    /**
     * getTrackMetadata
     *
     * Get the note data associated with track n
     *
     * @param n
     */

  getTrackMetadata = (n) =>{

      this.state.midisenseService.displayGetTrackMetadata(this.state.fileDesignator, n,
          (res) => {
            console.log(res)
              const success = res['success']
              if(success === undefined || !success) {
                  this.setState({
                      expired: true
                  })
              }
              else {
                  let trackString = res['trackString']
                  trackString = JSON.parse(trackString)
                  this.setTrackData(trackString['track'])
                  this.setTicksPerBeat(trackString['ticks_per_beat'])
                  this.setInstrument(trackString['instrument'])
                  this.setState({expired: false})
              }
          },
          (res) =>{
              this.setState({
                  expired: true
              })
          })
  }


  /**
   * refreshScoreDetails calls setSongTitle and getScoreMetadata
   */

  refreshScoreDetails = () => {
      // Ensures that a file has been processed (this is a secondary level of protection)
      if(this.cookies.get('fileDesignator') !==undefined) {
          this.setSongTitle(this.cookies.get('title'))
          this.getScoreMetadata()
          this.getTrackMetadata(this.state.currentTrack)
      }
  }

    /**
     * handleError
     * When an error occurs we will retry until a threshold is reached and then indicate the error.
     */

  handleError = () => {
      this.setState({
          errorCount: this.state.errorCount+1,
          error: true
      })

      if(this.state.errorCount >= 5){
          this.setState({
              openError: true
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
      this.setSelected = this.setSelected.bind(this)

      return (
          <div className="w-full overflow-auto">
              <div className="m-sm-30">
                  <div className="mb-sm-30">
                      <Breadcrumb
                          routeSegments={[
                              {name: "Display"}
                          ]}
                      />
                  </div>
                  <div>
                      <Grid container
                            justify="space-evenly"
                            spacing={1}
                            direction="row"
                            justifyContent="space-evenly"
                            alignItems="stretch"
                      >
                          <Grid item xs={12} sm={12} m={12} lg={6} >
                              <SimpleCard title="Metadata" subtitle="Technical and performance-related information.">
                                  {(this.state.error) ? <div/> :
                                      <div>
                                          <Grid container
                                                justify="space-evenly"
                                                spacing={3}
                                                direction="row"
                                                justifyContent="space-evenly"
                                                alignItems="center"
                                          >
                                              <br/>
                                              <Grid container item lg={12} style={{textAlign: 'center'}}>
                                                  <Grid item lg={12}>
                                                      <h1>{this.state.songTitle}</h1>
                                                      <aside>Data found at the start of the file</aside>
                                                  </Grid>
                                              </Grid>

                                              <Grid container item lg={12} style={{textAlign: 'center'}}>

                                                  <Grid item m={4} lg={4}>
                                                      <h5>Key Signature</h5>
                                                      <h6>{(this.state.keySignatureMap)?this.state.keySignatureMap[0]['keySignature']:null}</h6>
                                                      <br/>
                                                      <div>
                                                          {(this.state.keySignatureMap)?<img alt={"key signature"}
                                                               src={this.state.keyMap.getLinkForKey(this.state.keySignatureMap[0]['keySignature'])}
                                                               style={{height: '100px'}}/>:<div/>}
                                                      </div>

                                                  </Grid>
                                                  <Grid item m={4} lg={4}>

                                                      <h5>Time Signature</h5>
                                                      <h6>{(this.state.timeSignatureMap)?this.state.timeSignatureMap[0]['timeSignature']['numBeats'] + "/" + this.state.timeSignatureMap[0]['timeSignature']['beatValue']:null}</h6>
                                                      <br/>
                                                          {(this.state.timeSignatureMap)?
                                                              <div>
                                                                  <img alt={"time signature"}
                                                                       src={this.state.timeMap.getLinkForTime(this.state.timeSignatureMap[0]['timeSignature']['numBeats'])}
                                                                       style={{height: '40px'}}/>
                                                                  <br/>
                                                                  <img alt={"time signature"}
                                                                  src={this.state.timeMap.getLinkForTime(this.state.timeSignatureMap[0]['timeSignature']['beatValue'])}
                                                                  style={{height: '40px'}}/>
                                                              </div>:<div/>
                                                          }
                                                  </Grid>
                                                  <Grid item m={4} lg={4}>
                                                      <h5>Tempo Indication</h5>
                                                      <h6>{(this.state.tempoIndicationMap)?this.state.tempoIndicationMap[0]['tempoIndication']:null} Crotchet
                                                          BPM</h6>
                                                      <br/>
                                                      {(this.state.tempoIndicationMap)?
                                                          this.getDigitsFromNumber(this.state.tempoIndicationMap[0]['tempoIndication']).map((item) => {
                                                              return <span><img alt={"Tempo"}
                                                                                src={this.state.timeMap.getLinkForTime(item)}/></span>
                                                          }):null
                                                      }
                                                  </Grid>
                                              </Grid>

                                          </Grid>
                                      </div>
                                  }
                              </SimpleCard>
                          </Grid>
                          <Grid item xs={12} sm={12} m={12} lg={6}>
                              <SimpleCard title="Genre Analysis" subtitle="Here's what we think your file sounds like. Slide to adjust the number of suggestions.">
                                  {(this.state.error) ? <div/> :
                                      <div>
                                          <div style={{height: '300px', width: '100%'}}>
                                              <GenreTable
                                                  genreData={this.state.genreData.slice(0, this.state.numberOfGenres)}/>
                                          </div>
                                          <Typography id="discrete-slider" gutterBottom>
                                          Suggestions
                                          </Typography>
                                          <Slider
                                          defaultValue={5}
                                          aria-labelledby="discrete-slider"
                                          valueLabelDisplay="auto"
                                          step={1}
                                          marks
                                          min={5}
                                          max={10}
                                          onChange={(e,val)=>{this.setState({numberOfGenres: val})}}
                                          />
                                      </div>
                                  }
                              </SimpleCard>
                          </Grid>
                      </Grid>

                  </div>

                  <br/>

                  <SimpleCard title="Timeline" subtitle="Here you'll find the sequence of events for a chosen channel.">
                      {(this.state.error) ? <div/> :
                          <div>
                              <TrackMenu setTrack={this.setCurrentTrack} inputOptions={this.state.trackListing}/>
                              <TrackViewer
                              trackData={{"trackData":this.state.trackData, "ticksPerBeat":this.state.ticksPerBeat, "instrument": this.state.instrument, "keySignatureMap":this.state.keySignatureMap, "timeSignatureMap":this.state.timeSignatureMap,"tempoIndicationMap":this.state.tempoIndicationMap}}
                              callSelect={this.setSelected}
                              />
                              <br/>
                              <Grid
                              container
                              spacing={1}
                              direction="row"
                              justifyContent="flex-start"
                              alignItems="flex-start"
                              >
                              <div style={{marginLeft:"100px"}} id="dataDisplay"/>
                              </Grid>
                          </div>
                      }
                  </SimpleCard>

                  <Snackbar open={this.state.expired} onClose={()=>{this.setState({expired:false})}}>
                      <Alert className="text-white" variant="filled" onClose={this.CloseError} severity="warning">
                          Your file has expired. Try uploading a new file!
                      </Alert>
                  </Snackbar>

                  <Snackbar open={this.state.openError || !this.state.keySignatureMap} onClose={()=>{this.setState({openError:false})}}>
                      <Alert className="text-white" variant="filled" onClose={()=>{this.setState({openError:false})}} severity="error">
                          The display has failed to load, your file may have been corrupted or has expired. Try uploading a new file!
                      </Alert>
                  </Snackbar>
              </div>
          </div>
      );
  }
}

/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Display);
