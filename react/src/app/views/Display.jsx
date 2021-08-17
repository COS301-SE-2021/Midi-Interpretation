import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import SelectedMenu from "../../matx/components/SelectedMenu";
import MidiSenseService from "../services/MidiSenseService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Grid} from "@material-ui/core";
import Cookies from "universal-cookie";
import GenreTable from "../../matx/components/GenreTable";
import {withStyles} from "@material-ui/core/styles";
import KeySignature from "../../styles/images/keyMap"
import TimeSignature from "../../styles/images/timeMap"

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

    setTicksPerBeat = (t) => {
        this.setState({
            ticksPerBeat: t
        })
    }

    setInstrument = (i) => {
        this.setState({
            instrument: i
        })
    }

  setSelected = (s) => {
      this.setState({
          selectedIndex:s
      })
  }

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
              this.setTicksPerBeat(trackString['ticks_per_beat'])
              this.setInstrument(trackString['instrument'])

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
                                  <div>
                                      <Grid container
                                            justify="space-evenly"
                                            spacing={3}
                                            direction="row"
                                            justifyContent="space-evenly"
                                            alignItems="center"
                                      >
                                          <br/>
                                          <Grid container item lg={12} style={{textAlign:'center'}}>
                                              <Grid item lg={12}>
                                                  <h1>{this.state.songTitle}</h1>
                                                  <aside>Data found at the start of the piece</aside>
                                              </Grid>
                                          </Grid>

                                          <Grid container item lg={12} style={{textAlign:'center'}}>

                                              <Grid item m={4} lg={4}>
                                                  <h5>Key Signature</h5>
                                                  <h6>{this.state.keySignature}</h6>
                                                  <br/>
                                                  <div>
                                                      <img src={this.state.keyMap.getLinkForKey(this.state.keySignature)} style={{ height: '100px'}}/>
                                                  </div>

                                              </Grid>
                                              <Grid item m={4} lg={4}>

                                                  <h5>Time Signature</h5>
                                                  <h6>{this.state.timeSignature['numBeats'] + "/" + this.state.timeSignature['beatValue']}</h6>
                                                  <br/>
                                                  <div>
                                                      <img src={this.state.timeMap.getLinkForTime(this.state.timeSignature['numBeats'])} style={{ height: '40px'}}/>
                                                      <br/>
                                                      <img src={this.state.timeMap.getLinkForTime(this.state.timeSignature['beatValue'])} style={{ height: '40px'}}/>
                                                  </div>

                                              </Grid>
                                              <Grid item m={4} lg={4}>
                                                  <h5>Tempo Indication</h5>
                                                  <h6>{this.state.tempoIndication}</h6>
                                                  <br/>
                                                  {
                                                      this.getDigitsFromNumber(this.state.tempoIndication).map((item)=>{
                                                          return <span><img src={this.state.timeMap.getLinkForTime(item)}/></span>
                                                      })
                                                  }
                                              </Grid>
                                          </Grid>

                                      </Grid>
                                  </div>
                              </SimpleCard>
                          </Grid>
                          <Grid item xs={12} sm={12} m={12} lg={6}>
                              <SimpleCard title="Genre Analysis" subtitle="Here's what we think your file sounds like.">
                                  <div style={{ height: '300px', width: '100%'}}>
                                      <GenreTable genreData={this.state.genreData.slice(0,this.state.numberOfGenres)}/>
                                  </div>
                              </SimpleCard>
                          </Grid>
                      </Grid>

                  </div>

                  <br/>

                  <SimpleCard title="Timeline" subtitle="Here you'll find the sequence of events for a chosen channel.">
                      <SelectedMenu setTrack={this.setCurrentTrack} inputOptions={this.state.trackListing}/>
                      <TrackViewer trackData={{"trackData":this.state.trackData, "ticksPerBeat":this.state.ticksPerBeat, "instrument": this.state.instrument}} callSelect={this.setSelected}/>
                      <br/>
                      <Grid container
                            justify="space-evenly"
                            spacing={1}
                            direction="row"
                            justifyContent="space-evenly"
                            alignItems="center"
                      >
                          <div id="dataDisplay"></div>
                      </Grid>

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
