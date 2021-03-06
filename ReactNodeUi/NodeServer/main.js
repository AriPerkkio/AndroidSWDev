var express = require('express')
var app = express()
var Dao = require('./app/dao');
var dao = new Dao({useMockupData: true});

// GET
const PINGAZURE     = '/pingAzure';
const PINGOKEANOS   = '/pingOkeanos';
const PINGDIGIOCEAN = '/pingDigiOcean';
const COUNTBT       = '/countBt';
const COUNTWIFI     = '/countWifi';
const GETALLBT      = '/getAllBt';
const GETALLWIFI    = '/getAllWifi';
var pathsGet = [ PINGAZURE, PINGOKEANOS, PINGDIGIOCEAN, COUNTBT, COUNTWIFI, GETALLBT, GETALLWIFI ];

// POST
const SYNCBT   = '/syncBt';
const SYNCWIFI = '/syncWifi';
const UPLOAD   = '/upload';
var pathsPost = [ SYNCBT, SYNCWIFI, UPLOAD ];

var argv = process.argv.slice(2);
var _debug = false;
var port  = 8081;

if(argv) {
  for(var i = 0; i < argv.length; i++) {
    var arg = argv[i];
    switch(arg.replace(/-/g, '')) {
      case 'help':
      case 'usage':
        console.log('Valid args: ', '\n--debug', '\n--port <port_number>', '\n');
        process.exit();
      case 'debug':
        _debug = true;
        break;
      case 'port':
        i++;
        port = argv[i];
        break;
      default:
        console.log('Skipped unknown arg : ' +arg);
    }
  }
}

app.get(pathsGet, getHandler);
app.post(pathsPost, postHandler);

app.listen(port, () => debug("Server running on "+port));

function debug() {
  if(_debug) {
    console.log(" ");
    Object.values(arguments).forEach( (arg) => console.log(arg));
  }
}

function getHandler(req, res) {
  debug("getHandler", "GET "+req.url);

  res.setHeader('Access-Control-Allow-Origin', '*');

  switch(req.url) {
    case PINGAZURE :
    case PINGOKEANOS :
    case PINGDIGIOCEAN :
      res.send('GET, ping cmd: '+ req.url);
      break;
    case COUNTBT :
      res.send("Bluetooth: " + dao.countBt());
      break;
    case COUNTWIFI :
      res.send("Wifi: " + dao.countWifi());
      break;
    case GETALLBT :
      res.json(dao.getAllBt());
      break;
    case GETALLWIFI :
      res.json(dao.getAllWifi());
      break;
    default:
      res.send('GET ERROR, unknown cmd: '+ req.url);
  }
}

function postHandler(req, res) {
  debug("postHandler", "POST "+req.url);
  var responseText;

  switch(req.url) {
    case SYNCBT :
      responseText = 'POST, syncBt cmd: '+ req.url;
      break;
    case SYNCWIFI :
      responseText = 'POST, syncWifi cmd: '+ req.url;
      break;
    case UPLOAD :
      responseText = 'POST, upload cmd: '+ req.url;
      break;
    default:
      responseText = 'POST ERROR, unknown cmd: '+ req.url;
  }
  res.send(responseText+'\n');
}