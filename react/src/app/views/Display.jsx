import React, {Component} from "react";
import {Table, TableHead, TableBody, TableRow, TableCell, TablePagination} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "../../matx";
import SimpleExpansionPanel from "../../matx/components/SimpleExpansionPanel";
import DiscreteSlider from "../../matx/components/DiscreteSlider";
import SelectedMenu from "../../matx/components/SelectedMenu";
import MidiSenseService from "../services/MidiSenseService";
import {withStyles} from "@material-ui/styles";
import localStorage from "../services/localStorageService";


const BarList = [
  {
    number: "1",
    chords: "A D Em"
  },
  {
    number: "2",
    chords: "C A"
  },
  {
    number: "3",
    chords: "Em D"
  },
  {
    number: "4",
    chords: "F#"
  },
  {
    number: "5",
    chords: "G E"
  },
  {
    number: "6",
    chords: "E F"
  },
  {
    number: "7",
    chords: "A D Em"
  },
  {
    number: "8",
    chords: "F#"
  },
  {
    number: "9",
    chords: "G E"
  }
];

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
      this.state = {
        // fileDesignator: "",
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
     const fileDesignator = localStorage.getItem("fileDesignator")

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
    this.setSongTitle(localStorage.getItem("songTitle"))
    this.getScoreMetadata()

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
                  <div>
                      <button onClick={this.refreshScoreDetails}/>
                  </div>
                  <SimpleCard title="Analysis">
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
                  </SimpleCard>
                  <br/>
                  <SimpleCard>
                      <h4>Track</h4>
                      <SelectedMenu inputOptions={this.state.trackListing}/>
                      <DiscreteSlider/>
                  </SimpleCard>
                  <br/>
                  <SimpleCard title="Display">
                      <Table className="whitespace-pre">
                          <TableHead>
                              <TableRow>
                                  <TableCell className="px-0">Bar</TableCell>
                                  <TableCell className="px-0">Chords</TableCell>
                                  <TableCell className="px-0">Notes</TableCell>
                              </TableRow>
                          </TableHead>
                          <TableBody>
                              {BarList
                                  .slice(this.state.page * this.state.rowsPerPage, this.state.page * this.state.rowsPerPage + this.state.rowsPerPage)
                                  .map((bar, index) => (
                                      <TableRow key={index}>
                                          <TableCell className="px-0 capitalize" align="left">
                                              {bar.number}
                                          </TableCell>
                                          <TableCell className="px-0 capitalize" align="left">
                                              {bar.chords}
                                          </TableCell>
                                          <TableCell className="px-0">
                                            <SimpleExpansionPanel/>
                                          </TableCell>
                                      </TableRow>
                                  ))}
                          </TableBody>
                      </Table>
                      <TablePagination
                          className="px-4"
                          rowsPerPageOptions={[5, 10, 25]}
                          component="div"
                          count={BarList.length}
                          rowsPerPage={this.state.rowsPerPage}
                          page={this.state.page}
                          backIconButtonProps={{
                            "aria-label": "Previous Page"
                          }}
                          nextIconButtonProps={{
                            "aria-label": "Next Page"
                          }}
                          onChangePage={this.handleChangePage}
                          onChangeRowsPerPage={this.handleChangeRowsPerPage}
                      />
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

