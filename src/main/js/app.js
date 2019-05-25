import 'jquery';
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.css';

import React from 'react';
import ReactDOM from 'react-dom';
import Form from "react-jsonschema-form";

const schema = {
      "properties": {
        "parties": {
          "items": {
            "properties": {
              "members": {
                "items": {
                  "properties": {
                    "name": {
                      "type": "string"
                    }
                  },
                  "type": "object"
                },
                "type": "array"
              },
              "metrics": {
                "items": {
                  "properties": {
                    "name": {
                      "type": "string"
                    },
                    "value": {
                      "type": "integer"
                    }
                  },
                  "type": "object"
                },
                "type": "array"
              },
              "score": {
                "type": "integer"
              },
              "tags": {
                "items": {
                  "properties": {
                    "name": {
                      "type": "string"
                    },
                    "value": {
                      "type": "string"
                    }
                  },
                  "type": "object"
                },
                "type": "array"
              },
              "team": {
                "properties": {
                  "name": {
                    "type": "string"
                  }
                },
                "type": "object"
              }
            },
            "type": "object"
          },
          "type": "array"
        },
        "timestamp": {
          "type": "number"
        },
        "tournament": {
          "type": "string"
        }
      },
      "type": "object"
    }
;

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

