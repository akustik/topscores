import 'jquery';
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.css';

import React from 'react';
import ReactDOM from 'react-dom';
import Form from "react-jsonschema-form";

const schema = {
  "title": "Game creator",
  "type": "object",
  "properties": {
    "tournament": {
      "type": "string",
      "title": "Tournament",
      "enum": ["2018-2019"]
    },
    "teams": {
      "type": "array",
      "title": "Teams",
      "items": {
        "type": "object",
        "properties": {
          "name": {
            "type": "string",
            "title": "Name",
            "enum": ["blaus", "grocs"]
          },
          "players": {
            "type": "array",
            "title": "Players",
            "items": {
              "type": "object",
              "properties": {
                "name": {
                  "type": "string",
                  "title": "Name",
                  "enum": ["Guillem", "Ramon"]
                }
              }
            }
          }
        }
      }
    }
  }
};

const onSubmit = ({formData}, e) => console.log("Data submitted: ",  formData);
const log = (type) => console.log.bind(console, type);

class App extends React.Component {
  
  render() {
    return (
    <div className="App">
      <div className="container-fluid">
        <div className="col-sm-5">
          <Form schema={schema}
                onChange={log("changed")}
                onSubmit={log("submitted")}
                onError={log("errors")}/>
        </div>
      </div>
    </div>);
  }

  componentDidMount() {
    fetch('/games/list')
    .then(response => response.json())
    .then(log("obtained"));
  }
}

export default App;

ReactDOM.render(<App />, document.getElementById('react'));

