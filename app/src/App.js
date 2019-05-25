import React from 'react';
import './App.css';

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

const log = (type) => console.log.bind(console, type);

function App() {
  return (
    <div className="App">
      <div className="container-fluid">
        <div className="col-sm-5">
          <Form schema={schema}
                onChange={log("changed")}
                onSubmit={log("submitted")}
                onError={log("errors")} />
        </div>
      </div>
    </div>
  );
}

export default App;
