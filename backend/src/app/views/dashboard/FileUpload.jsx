import React, {Component, Fragment, useMemo} from "react";
import {Grid, Card, Button, Fab, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";
import Dropzone, {useDropzone} from 'react-dropzone'

import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";

class FileUpload extends Component {
  state = {};

    constructor(props) {
        super(props);
        this.onDrop = (files) => {
            this.setState({files})
        };
        this.state = {
            files: []
        };
    }

  render() {
      const classes = makeStyles;
      let { theme } = this.props;

      const files = this.state.files.map(file => (
          <li key={file.name}>
              {file.name} - {file.size} bytes
          </li>
      ));



      return (
          <div className="m-sm-30">
              <div className="mb-sm-30">
                  <Breadcrumb
                      routeSegments={[
                          { name: "File Upload" }
                      ]}
                  />
              </div>
              <SimpleCard title="Upload File">
                  <Dropzone onDrop={this.onDrop}>
                      {({getRootProps, getInputProps}) => (
                          <section className="container">
                              <div {...getRootProps({className: 'dropzone'})}>
                                  <input {...getInputProps()} />
                                  <p>Drag your midi file here, or click to browse for a file</p>
                              </div>
                              <aside>
                                  <ul>{files}</ul>
                              </aside>
                          </section>
                      )}
                  </Dropzone>
              </SimpleCard>
              <div className="py-3" />
          </div>
      );
  };
  //   return (
  //     <Fragment>
  //       <div className="pb-24 pt-7 px-5 bg-primary">
  //         <div className="card-title capitalize text-white mb-1 text-white-secondary">
  //             New Midi Project
  //         </div>
  //       </div>
  //         <SimpleCard>
  //             <div className="card-title capitalize text-white mb-1 text-primary">
  //               Upload Midi File
  //           </div>
  //             <br/>
  //
  //         </SimpleCard>
  //     </Fragment>
  //   );
  // }



}

export default withStyles({}, { withTheme: true })(FileUpload);
