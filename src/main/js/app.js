import 'jquery';
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.css';

import React from 'react';
import ReactDOM from 'react-dom';
import { Redirect } from 'react-router';
import Form from "react-jsonschema-form";

const schema = {
  "definitions": {
    "players": {
      "type": "array",
      "items": {
        "type": "string",
        "enum": [
          "Albert",
          "Alvaro",
          "Arnau",
          "Arturo",
          "Cristian",
          "Enric",
          "Erik",
          "Fran",
          "Guillem",
          "Jordi",
          "Jose",
          "Marc",
          "Pau",
          "Ramon",
          "Ricard",
          "Sergi",
          "Toni"
        ]
      },
      "uniqueItems": true
    },
    "teams": {
      "type": "string",
      "enum": ["blaus", "grocs"]
    },
    "metrics": {
      "type": "string",
      "enum": ["gols", "assistències"]
    }
  },
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
          "team": {
            "$ref": "#/definitions/teams"
          },
          "score": {
            "type": "integer"
          },
          "players": {
            "$ref": "#/definitions/players"
          },
          "metrics": {
            "type": "array",
            "title": "Metrics",
            "items": {
              "type": "object",
              "properties": {
                "metric": {
                  "$ref": "#/definitions/metrics"
                },
                "value": {
                  "type": "integer"
                },
                "players": {
                  "$ref": "#/definitions/players"
                }
              }
            }
          }
        }
      }
    }
  }
};

const uiSchema = {
  "score": {
    "ui:widget": "updown"
  }
};

const log = (type) => console.log.bind(console, type);

class App extends React.Component {

  state = {
    toMainPage: false,
  };
  
  submit = ({formData}, e) =>  {
    fetch('/games/simple/add', {
      method: 'POST',
      body: JSON.stringify(formData),
      headers:{
        'Content-Type': 'application/json'
      }
    }).then(res => res.json())
    .catch(error => console.error('Error:', error))
    .then(response => this.setState(() => ({
          toMainPage: true
        }))
    );
  };
  
  render() {
    if (this.state.toMainPage === true) {
        //FIXME: Use <Redirect> and a <Router> instead. Learn how to work 
        //with single page applications
        window.location.href = '/';
    }
    
    return (
    <div className="App">
      <div className="container-fluid">
        <div className="col-xs-12 col-sm-12 col-md-12">
          <Form schema={schema}
                uiSchema={uiSchema}
                onChange={log("changed")}
                onSubmit={this.submit}
                onError={log("errors")}/>
        </div>
      </div>
    </div>);
  }
}

ReactDOM.render(<App />, document.getElementById('react'));

