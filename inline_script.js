var deffered_functions = [];
var script_loaded = false;

checkCache();

function checkCache() {
  var need_update = [];

  for (var key in cached_ts) {
    if (localStorage[key] != undefined) {
      setCached(key);
      if (localStorage['ts_'+key] != undefined) {
        if (localStorage['ts_'+key] >= cached_ts[key]) {
          //setCached(key);
          //if (key == 'script')  OnScriptLoadedRun();
          continue;
          }
        }
      }

    // -- cache miss -- //
    console.log('need_update (' + key + ')');
    need_update.push(key);
    }

  if (need_update.length > 0) {
    console.log(need_update);
    var send_data = {'names' : need_update};
    ajaxPost('/a/res/', JSON.stringify(send_data), cachedLoaded, cachedLoadFailed);
    }
  }




  // ---- put cached resources to page ---- //

function setCached(name) {
  console.log('setCached(' + name + ')');

  var fst = document.getElementById(name);
  if (fst) {
    //console.log('found. removing setted resource...');
    fst.parentNode.removeChild(fst);
    //console.log('setting new.');
    }


  if (name == 'sprite_actions') {
    var st = document.createElement('STYLE');
    st.id = name;
    st.innerHTML = 'hr, div.actions_bar >a >div:first-child {background-image: url(' + localStorage[name] + ');}';
    document.head.appendChild(st);
    }

  else if (name == 'sprite_modules') {
    var st = document.createElement('STYLE');
    st.id = name;
    st.innerHTML = 'div.modules_bar >a >div:first-child {background-image: url(' + localStorage[name] + ');}';
    document.head.appendChild(st);
    }

  else if (name == 'sprite_modules_2') {
    var st = document.createElement('STYLE');
    st.id = name;
    st.innerHTML = '@media (min-resolution: 1.2dppx) {div.modules_bar >a >div:first-child {background-image: url(' + localStorage[name] + '); background-size: 512px;}}';
    document.head.appendChild(st);
    }

  else if (name == 'script') {
    var st = document.createElement('SCRIPT');
    st.id = name;
    st.innerHTML = localStorage[name];
    document.head.appendChild(st);
    //OnScriptLoadedRun();
    }

  else if (name == 'style') {
    var st = document.createElement('STYLE');
    st.id = name;
    st.innerHTML = localStorage[name];
    document.head.appendChild(st);
    }

  }




function cachedLoaded(r) {
  var aw = {};

  try { aw = JSON.parse(r); }
  catch(e) { console.error(r);  return; }

  console.log(aw);


  if (aw.res != undefined) {
    console.log(aw.res);

    for (var key in aw.res) {
      console.log(key);
      //console.log(aw.res[key]);

      localStorage['ts_'+key] = cached_ts[key];
      localStorage[key] = aw.res[key];

        // ---- re-set resource to page ---- //
      setCached(key);
      //if (key == 'script')  OnScriptLoadedRun();
      }

    }

  }




function cachedLoadFailed(ar) {
  var aw = {};

  try { aw = JSON.parse(ar.responseText); }
  catch(e) { console.log(ar.responseText);  return; }


  if (aw.error != undefined)  console.log('error: ' + aw.error);

  if (aw.exception != undefined) {
    var ex = aw.exception;
    console.error(ex.class + ': ' + ex.desc);

    for (var i = 0; i < ex.stack_trace.length; i++) {
      var st = ex.stack_trace[i];
      console.log(st.class + ' ' + st.method + ' (' + st.file + ' : ' + st.line + ')');
      }

    }
  }




function ajaxPost(url, data, cb_success, cb_failed) {
  var ar = new XMLHttpRequest();

  ar.onreadystatechange = function(e) {

    if (this.readyState == 4) {
      if (this.status == 200) {
        cb_success(this.responseText);
        }
      else {
        if (cb_failed)  cb_failed(this);
        }
      }
    };

  ar.open('POST', url, true);

  ar.setRequestHeader("Content-Type", "application/json");
  //ar.setRequestHeader("Content-Type", "application/json; charset=utf-8");
  ar.send(data);
  }


  // ---- wait, until cache script loaded ---- //

//while (localStorage['script'] == undefined) {}



/*
function OnScriptLoaded(deffered_function) {
  //console.log("script_loaded: " + script_loaded);

  if (script_loaded) {
    //console.log("OnScriptLoaded(). starting immediately.");
    deffered_function();
    }
  else {
    //console.log("OnScriptLoaded(). deffering.");
    deffered_functions.push(deffered_function);
    }
  }


function OnScriptLoadedRun() {
  //console.log("OnScriptLoadedRun()");
  script_loaded = true;
  //console.log("script_loaded: " + script_loaded);
  for (var i in deffered_functions) {
    deffered_functions[i]();
    }
  }
*/
