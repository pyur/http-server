
function ajaxMin(url, cbSuccess, cbFailed) {
  var ar = new XMLHttpRequest();

  ar.onreadystatechange = function() {
    if (this.readyState == 4) {
      if (this.status == 200) {
        cbSuccess(this.responseText);
        }
      else {
        if (cbFailed)  cbFailed(this);
        }
      }
    };

  ar.open('GET', url, true);

  ar.send();
  }



// ----

function setSpriteActions() {
  //search for already setted sprite, and remove it if necessary
  var fst = document.getElementById('spriteActions');
  if (fst) {
    console.log('found. removing setted sprite...');
    fst.parentNode.removeChild(fst);
    console.log('setting new.');
    }
  var st = document.createElement('STYLE');
  st.id = 'spriteActions';
  //st.innerHTML = 'a.s {background-image: url(' + localStorage.spriteActions + ');}';
  st.innerHTML = 'hr {background-image: url(' + localStorage.spriteActions + ');}';
  document.head.appendChild(st);
  }



if (localStorage.spriteActions) {
  console.log('get sprite from cache.');
  setSpriteActions();
  }
else {
  console.log('sprite miss in cache.');
  }



function spriteActionsLoaded(r) {
  var aw = {};

  try { aw = JSON.parse(r); }
  catch(e) { return; }

  console.log('new sprite loaded and put to cache.');

  localStorage.spriteActions = aw.data;
  localStorage.tsSpriteActions = aw.ts;
  console.log('new sprite ts: ' + aw.ts);

  setSpriteActions();
  }


if (localStorage.tsSpriteActions == undefined || localStorage.tsSpriteActions < tsSpriteActions) {
  console.log('cached sprite is outdated.');
  ajaxMin('/a/res/sa/', spriteActionsLoaded);
  }
else {
  console.log('cached sprite is actual.');
  console.log('localStorage.tsSpriteActions ('+localStorage.tsSpriteActions+')' + ' > ' + 'tsSpriteActions ('+tsSpriteActions+')');
  }




// ----

function setSpriteModules() {
  //search for already setted sprite, and remove it if necessary
  var fst = document.getElementById('spriteModules');
  if (fst) {
    console.log('found. removing setted sprite...');
    fst.parentNode.removeChild(fst);
    console.log('setting new.');
    }
  var st = document.createElement('STYLE');
  st.id = 'spriteModules';
  st.innerHTML = 'div.modules_bar >a >div:first-child {background-image: url(' + localStorage.spriteModules + ');}';
    //-webkit-min-device-pixel-ratio: 1.2
  st.innerHTML += '@media (min-resolution: 1.2dppx) {div.modules_bar >a >div:first-child {background-image: url(' + localStorage.spriteModules2 + '); background-size: 512px;}}';

  document.head.appendChild(st);
  }



if (localStorage.spriteModules) {
  console.log('load sprite from cache.');
  setSpriteModules();
  }
else {
  console.log('sprite miss in cache.');
  }



function spriteModulesLoaded(r) {
  var aw = {};

  try { aw = JSON.parse(r); }
  catch(e) { return; }

  console.log('new sprite loaded and put to cache.');

  localStorage.spriteModules = aw.data;
  localStorage.spriteModules2 = aw.data2;

  localStorage.tsSpriteModules = aw.ts;
  console.log('new sprite ts: ' + aw.ts);

  setSpriteModules();
  }


if (localStorage.tsSpriteModules == undefined || localStorage.tsSpriteModules < tsSpriteModules) {
  console.log('cached sprite is outdated. fetching new one from server.');
  ajaxMin('/a/res/sm/', spriteModulesLoaded);
  }
else {
  console.log('cached sprite is actual.');
  console.log('localStorage.tsSpriteModules ('+localStorage.tsSpriteModules+')' + ' > ' + 'tsSpriteModules ('+tsSpriteModules+')');
  }




  // -------------------------------- static library -------------------------------- //

function ajaxPost(url, data, cb_success, cb_failed) {
  var ar = new XMLHttpRequest();

  ar.onreadystatechange = function(e) {
    //console.log(this.readyState);
    //console.log(e);

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

  ar.setRequestHeader("Content-Type", "application/json");  // ? not working?
  //ar.setRequestHeader("Content-Type", "application/json; charset=utf-8");
  ar.send(data);
  }



  // ---------------- table actions ---------------- //

function TableActions(table_id, cb_functions) {
  var table = document.getElementById(table_id);

  var table_actions_clicked = function(e) {
    e.stopPropagation();
    e.preventDefault();

    var t = e.target;

    while (t.tagName != "HR") {
      if (t.parentNode === undefined || t.parentNode === null)  return;
      t = t.parentNode;
      }


      // ---- left mouse button ---- //
    if (e.button == 0) {
      if (t.parentNode === undefined || t.parentNode === null)  return;
      var td = t.parentNode;
      //console.log(td.tagName);
      if (td.tagName != "TD")  return;
      if (td.parentNode === undefined || td.parentNode === null)  return;
      var tr = td.parentNode;
      //console.log(tr.tagName);
      if (tr.tagName != "TR")  return;

      var row_id = tr.getAttribute("data-id");
      //console.log(row_id);

      if (row_id == undefined)  return;

      //alert("row_id: " + row_id);
      //console.log(cb_functions);


      //console.log(td.childNodes);
      for (var i = 0; i < td.childNodes.length; i++) {
        //if (td.childNodes[i] == t)  console.log(i);
        if (td.childNodes[i] == t)  cb_functions[i](row_id);
        }



      }

    }


  table.addEventListener("click", table_actions_clicked, false);
  }




  // ---------------- form send ---------------- //

//function FormSend(form_id, cb_ok, cb_failed) {
function FormSend(form_id) {
  var form = document.getElementById(form_id);
  var submit_button;

  var submit_clicked = function(e) {
    //to do: lock button for 10 seconds
    //console.log("submit clicked");

    var url = form.action;
    var send_data = {};

    for (var i = 0; i < form.elements.length; i++) {
      if (form.elements[i].name == "submit")  continue;
      send_data[form.elements[i].name] = form.elements[i].value;  // ?need to screen value
      }

    //ajaxPost(url, JSON.stringify(send_data), cb_ok, cb_failed);
    ajaxPost(url, JSON.stringify(send_data), FormSendOk, FormSendFailed);
    }



  //for (var i = 0; i < form.childNodes.length; i++) {
  for (var i = 0; i < form.elements.length; i++) {
    if (form.elements[i].name == "submit") {
      submit_button = form.elements[i];
      }
    }

  if (submit_button != undefined) {
    submit_button.addEventListener("click", submit_clicked, false);
    }

  }



function FormSendOk(response_text) {
  console.log("FormSendOk");
  console.log(response_text);
  }



function FormSendFailed(response_obj) {
  console.log("FormSendFailed");
  console.log(response_obj);
  }

