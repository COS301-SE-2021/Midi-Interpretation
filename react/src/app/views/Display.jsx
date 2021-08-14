import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import SelectedMenu from "../../matx/components/SelectedMenu";
import MidiSenseService from "../services/MidiSenseService";
import {withStyles} from "@material-ui/styles";
import TrackViewer from "../../matx/components/TrackViewer";
import {Grid} from "@material-ui/core";
import Cookies from "universal-cookie";
import GenreTable from "../../matx/components/GenreTable";

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
        currentTrack: 0,
        trackListing: [],
        trackData:[],
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
      }

      if(this.cookies.get('fileDesignator') === undefined){
          this.props.history.push("/Upload")
      }

      this.getTrackMetadata(this.state.currentTrack)

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
     *
     * @property JSON
     * @param td
     */

  setTrackData = (td) => {

      this.setState({
          trackData: td
      })
  }

  setSelected = (s) => {
      this.setState({
          selectedIndex:s
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

     this.state.midisenseService.displayGetPieceMetadata(this.state.fileDesignator,
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

     this.state.midisenseService.displayGetTrackInfo(this.state.fileDesignator,
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

      this.state.midisenseService.intelligenceAnalyseGenre(this.state.fileDesignator,
          (res) => {
              const genreData = res['genreArray']

              for (let i = 0 ; i < genreData.length ; i++){
                  genreData[i].Certainty = parseFloat(genreData[i].Certainty).toFixed(3);
              }

              this.setState({genreData: genreData})
          },
          (error) => {

          }
      )
  }

  getTrackMetadata = (n) =>{

      this.state.midisenseService.displayGetTrackMetadata(this.state.fileDesignator, n,
          (res) => {
              let trackString = res['trackString']
              trackString = JSON.parse(trackString)
              this.setTrackData(trackString['track'])
          },
          (error) => {

          })
  }


  /**
   * refreshScoreDetails calls setSongTitle and getScoreMetadata
   */

  refreshScoreDetails = () => {
      if(this.cookies.get('fileDesignator') !==undefined) {
          this.setSongTitle(this.cookies.get('title'))
          this.getScoreMetadata()
          this.getTrackMetadata(this.state.currentTrack)
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
                      </Grid>
                  </SimpleCard>
                  <br/>

                  <SimpleCard>
                      <h4>Genre</h4>
                      <div style={{ height: '200px', width: '100%'}}>
                          <GenreTable genreData={this.state.genreData}/>
                      </div>
                  </SimpleCard>
                  <br/>

                  <SimpleCard>
                      <h4>Track</h4>
                      <SelectedMenu setTrack={this.setCurrentTrack} inputOptions={this.state.trackListing}/>

                  </SimpleCard>
                  <br/>
                    <TrackViewer trackData={this.state.trackData} callSelect={this.setSelected}/>
                  <br/>
                      {
                          this.state.trackData[this.state.selectedIndex] !== undefined ?
                              <SimpleCard title={"Tick: " + this.state.trackData[this.state.selectedIndex].tick}>
                                  <Grid container
                                        direction="row"
                                        justifyContent="flex-start"
                                        alignItems="flex-start"
                                        spacing={3}>
                                      {this.state.trackData[this.state.selectedIndex].notes.map((value, index) => {
                                          return (
                                              <Grid key={index} item>
                                                  <div key={index} className="text-16" style={{color:this.state.color[index%13]}}><b>{"Voice "+index}</b></div>
                                                  <div key={index+"1"} className="text-14"> {"Value: " + value.value}</div>
                                                  <div key={index+"2"} className="text-14"> {"On Velocity: " + value.on_velocity}</div>
                                                  <div key={index+"3"} className="text-14"> {"Off Velocity: " + value.off_velocity}</div>
                                                  <div key={index+"4"} className="text-14"> {"Duration: " + value.duration}</div>
                                              </Grid>
                                          )
                                      })}
                                  </Grid>
                              </SimpleCard>
                              : <div/>
                      }
              </div>
          </div>
      );
  }
}

/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Display);
