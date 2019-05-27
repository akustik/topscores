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
          "team": {
            "type": "string",
            "enum": ["blaus", "grocs"]
          },
          "score": {
            "type": "integer"
          },
          "players": {
            "type": "array",
            "items": {
              "type": "string",
              "enum": ["Guillem", "Ramon"]
            }
          }
        }
      }
    }
  }
};

const onSubmit = ({formData}, e) =>  {
  fetch('/web/create/simple', {
    method: 'POST',
    body: JSON.stringify(formData),
    headers:{
      'Content-Type': 'application/json'
    }
  }).then(res => res.json())
  .catch(error => console.error('Error:', error))
  .then(response => console.log('Success:', response));
};
const log = (type) => console.log.bind(console, type);

class App extends React.Component {
  
  render() {
    return (
    <div className="App">
      <div className="container-fluid">
        <div className="col-sm-5">
          <Form schema={schema}
                onChange={log("changed")}
                onSubmit={ onSubmit }
                onError={log("errors")}/>
        </div>
      </div>
    </div>);
  }
}

ReactDOM.render(<App />, document.getElementById('react'));

