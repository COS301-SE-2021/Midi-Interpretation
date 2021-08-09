import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import SelectedMenu from "../../matx/components/SelectedMenu";
import MidiSenseService from "../services/MidiSenseService";
import {withStyles} from "@material-ui/styles";
import localStorage from "../services/localStorageService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Container, Grid} from "@material-ui/core";
import GenrePie from "../../matx/components/GenrePie";
import Cookies from "universal-cookie";


//THESE ARE HARD CODED
localStorage.setItem("songTitle","This is a song")
localStorage.setItem("fileDesignator", "0bc8dcc5-79a0-4b5a-9be5-b9978b9febe1")
//==============================================

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
 * Components:
 *      -> MidiSenseService
 *      -> LocalStorageService
 *      -> Breadcrumb
 *      -> SimpleCard
 *      -> DiscreteSlider
 *      -> SelectedMenu
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
      super(props);
      this.cookies = new Cookies();
      this.state = {
        rowsPerPage: 5,
        page: 0,
        songTitle: "Song Title",
        keySignature: "Unknown",
        tempoIndication: "Unknown",
        timeSignature: {},
        currentTrack: {trackNumber: "No Track Selected", trackInstrument: "No Instrument"},
        trackListing: [],
        midisenseService: new MidiSenseService()
      }
  }

  componentDidMount() {
      this.refreshScoreDetails()
  }

    //====================================
  // DISPLAY STATE VALUE SETTERS
  //====================================

    /**
     * setRowsPerPage
     * @param rpp - new rows per page
     */

  setRowsPerPage = (rpp) => {
      this.setState({
        rowsPerPage: rpp
      })
  }

    /**
     * setPage
     * @param p - new current page
     */

  setPage = (p) => {
      this.setState({
        page: p
      })
  }

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
     * setKeySignature
     * @param ks - new key signature
     */

  setKeySignature = (ks) => {
      this.setState({
        keySignature: ks
      })
  }

    /**
     * setTempoIndication
     * @param ti - new tempo indication
     */

  setTempoIndication = (ti) => {
      this.setState({
        tempoIndication: ti
      })
  }

    /**
     * setTimeSignature
     * @param ts - new time signature
     */

  setTimeSignature = (ts) => {
      this.setState({
        timeSignature: ts
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

  //====================================
  // DISPLAY METHODS
  //====================================

  /**
   * handleChangePage sends the event and the new page to setPage when the current page is changed
   * @param event
   * @param newPage
   */

  handleChangePage = (event, newPage) => {
    this.setPage(newPage);
  };

  /**
   * handleChangeRowsPerPage sends the event to setRowsPerPage to change how many rows are displayed in the table
   * @param event
   */

  handleChangeRowsPerPage = event => {
    this.setRowsPerPage(+event.target.value);
  };

  /**
   * getScoreMetadata will get and set the key signature, tempt indication, time signature and track listing
   */

  getScoreMetadata = () => {
     const fileDesignator = this.cookies.get('fileDesignator')

     this.state.midisenseService.displayGetPieceMetadata(fileDesignator,
        (res) => {
          const keySignature = res['keySignature']
          const tempoIndication = res['tempoIndication']
          const timeSignature = res['timeSignature']
          this.setKeySignature(keySignature)
          this.setTempoIndication(tempoIndication)
          this.setTimeSignature(timeSignature)
        },
        (error) => {

        })

     this.state.midisenseService.displayGetTrackInfo(fileDesignator,
        (res) => {
            for (const track of res) {
              let currentListing = this.state.trackListing
              currentListing.push((track['index'] + 1) + ". " + track['trackName'])
              this.setTrackListing(currentListing)
            }
        },
        (error) => {

        }
     )
  }

  /**
   * refreshScoreDetails calls setSongTitle and getScoreMetadata
   */

  refreshScoreDetails = () => {
      if(this.cookies.get('fileDesignator') !==undefined) {
          this.setSongTitle(this.cookies.get('title'))
          this.getScoreMetadata()
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
                  <SimpleCard title="Analysis">
                      <Grid container justify="space-evenly">
                          <Grid item>
                              <h1>{this.state.songTitle}</h1>
                              <br/>
                              <h4>
                                  Piece Meta Data:
                              </h4>
                              <p>
                                  <li>Key: {this.state.keySignature} </li>
                                  <li>Time Signature: {this.state.timeSignature['numBeats'] + "/" + this.state.timeSignature['beatValue']}</li>
                                  <li>Tempo Indication: {this.state.tempoIndication}</li>
                              </p>
                          </Grid>
                          <Grid item>
                              <h4>
                                  Genre:
                              </h4>
                              <div style={{ height: '200px', width: '200px'}}>
                                <GenrePie/>
                              </div>
                          </Grid>
                      </Grid>
                  </SimpleCard>
                  <br/>
                  <SimpleCard>
                      <h4>Track</h4>
                      <SelectedMenu inputOptions={this.state.trackListing}/>


                  </SimpleCard>
                  <br/>
                  <SimpleCard title="Display">

                          <div style={{ height: '400px', width: '100%'}}>
                              <TrackViewer/>
                          </div>

                  </SimpleCard>
              </div>
          </div>
      );
  }
}

/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Display);

