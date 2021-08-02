import React from "react";
import {
  IconButton,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Icon,
  TablePagination
} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "../../matx";
import SimpleExpansionPanel from "../../matx/components/SimpleExpansionPanel";
import DiscreteSlider from "../../matx/components/DiscreteSlider";
import SelectedMenu from "../../matx/components/SelectedMenu";
import MidiSenseService from "../services/MidiSenseService";

const subscribarList = [
  {
    name: "1",
    date: "18 january, 2019",
    amount: 1000,
    status: "close",
    company: "A D Em"
  },
  {
    name: "2",
    date: "10 january, 2019",
    amount: 9000,
    status: "open",
    company: "C A"
  },
  {
    name: "3",
    date: "10 january, 2019",
    amount: 9000,
    status: "open",
    company: "Em D"
  },
  {
    name: "4",
    date: "8 january, 2019",
    amount: 5000,
    status: "close",
    company: "F#"
  },
  {
    name: "5",
    date: "1 january, 2019",
    amount: 89000,
    status: "open",
    company: "G E"
  },
  {
    name: "6",
    date: "1 january, 2019",
    amount: 89000,
    status: "open",
    company: "E F"
  },
  {
    name: "7",
    date: "1 january, 2019",
    amount: 89000,
    status: "open",
    company: "A D Em"
  },
  {
    name: "8",
    date: "1 january, 2019",
    amount: 89000,
    status: "open",
    company: "F#"
  },
  {
    name: "9",
    date: "1 january, 2019",
    amount: 89000,
    status: "open",
    company: "G E"
  }
];

//THESE ARE HARD CODED
localStorage.setItem("songTitle","This is a song")
localStorage.setItem("fileDesignator", "0bc8dcc5-79a0-4b5a-9be5-b9978b9febe1")
//==============================================

const Display = () => {
  const [rowsPerPage, setRowsPerPage] = React.useState(5)
  const [page, setPage] = React.useState(0)
  const [songTitle, setSongTitle] = React.useState("Song Title")
  const [keySignature, setKeySignature] = React.useState("Unknown")
  const [tempoIndication, setTempoIndication] = React.useState("Unknown")
  const [timeSignature, setTimeSignature] = React.useState({})
  const [currentTrack, setCurrentTrack] = React.useState({trackNumber: "No Track Selected",trackInstrument:"No Instrument"})
  const [trackListing, setTrackListing] = React.useState([])
  const midisenseService = new MidiSenseService()

  const getScoreMetadata = () => {
    const fileDesignator = localStorage.getItem("fileDesignator")
    midisenseService.displayGetPieceMetadata(fileDesignator,
        (res)=>{
            const keySignature = res['keySignature']
            const tempoIndication = res['tempoIndication']
            const timeSignature = res['timeSignature']
            setKeySignature(keySignature)
            setTempoIndication(tempoIndication)
            setTimeSignature(timeSignature)
        },
        (error)=>{

        })
    midisenseService.displayGetTrackInfo(fileDesignator,
        (res)=>{
          for (const track of res) {
              let currentListing = trackListing
              currentListing.push((track['index']+1)+". "+track['trackName'])
              setTrackListing(currentListing)
          }
        },
        (error)=>{

        })

  }

  const refreshScoreDetails = ()=>{
    setSongTitle(localStorage.getItem("songTitle"))
    getScoreMetadata()

  }

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = event => {
    setRowsPerPage(+event.target.value);
  };

  return (
    <div className="w-full overflow-auto">
      <div className="m-sm-30" >
        <div className="mb-sm-30">
          <Breadcrumb
              routeSegments={[
                { name: "Display" }
              ]}
          />
        </div>
        <div>
          <button onClick={refreshScoreDetails}></button>
        </div>
        <SimpleCard title="Analysis">
          <h1>{songTitle}</h1>
          <br/>
          <h4>
            Piece Meta Data:
          </h4>
          <p>
            <li>Key: {keySignature} </li>
            <li>Time Signature: {timeSignature['numBeats']+"/"+timeSignature['beatValue']}</li>
            <li>Tempo Indication: {tempoIndication}</li>
          </p>
        </SimpleCard>
        <br/>
        <SimpleCard>
          <h4>Track</h4>
          <SelectedMenu inputOptions={trackListing}></SelectedMenu>
          <DiscreteSlider></DiscreteSlider>
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
              {subscribarList
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((subscriber, index) => (
                  <TableRow key={index}>
                    <TableCell className="px-0 capitalize" align="left">
                      {subscriber.name}
                    </TableCell>
                    <TableCell className="px-0 capitalize" align="left">
                      {subscriber.company}
                    </TableCell>
                    <TableCell className="px-0">
                      <SimpleExpansionPanel></SimpleExpansionPanel>
                    </TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>

          <TablePagination
            className="px-4"
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={subscribarList.length}
            rowsPerPage={rowsPerPage}
            page={page}
            backIconButtonProps={{
              "aria-label": "Previous Page"
            }}
            nextIconButtonProps={{
              "aria-label": "Next Page"
            }}
            onChangePage={handleChangePage}
            onChangeRowsPerPage={handleChangeRowsPerPage}
          />
        </SimpleCard>
      </div>
    </div>
  );
};

export default Display;
