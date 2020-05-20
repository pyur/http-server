  // ---------------- table actions ---------------- //
/*
function TableActions(table_id, cb_functions) {
  //console.log("TableActions(). table_id: " + table_id);
  //console.log(cb_functions);
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
*/

function TableActions(e, table, cb_functions) {
  //console.log("TableActions(). table_id: " + table_id);
  //console.log(cb_functions);
  //console.log(table);
  //console.log(e);


//  var table_actions_clicked = function(e) {
  //e.stopPropagation();
  //e.preventDefault();

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

//    }


//  table.addEventListener("click", table_actions_clicked, false);
  }




  // ---------------- form send ---------------- //
/*
//function FormSend(form_id, cb_ok, cb_failed) {
function FormSend_v1(form_id) {
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
*/


function FormSendOk(response_text) {
  console.log("FormSendOk");
  console.log(response_text);

  try { aw = JSON.parse(response_text); }
  catch(e) { console.error(response_text);  return; }

  if (aw.location != undefined) {
    //console.log("redirecting to " + aw.location);
    window.location = aw.location;
    }

  }



function FormSendFailed(response_obj) {
  console.log("FormSendFailed");
  console.log(response_obj);
  }


/*
function FormSend(form_id, cb_ok, cb_failed) {
  console.log("FormSend()");
  var form = document.getElementById(form_id);
  var submit_button;

  var enable_submit_button = function() { submit_button.disabled = false; }

  form.onsubmit = function(e) {
    console.log("FormSend. onsubmit()");
    submit_button.disabled = true;
    var timeout_handle = setTimeout(enable_submit_button, 3000);
    //clearTimeout(timeout_handle);  // on failed response

    var url = form.action;
    var send_data = {};

    for (var i = 0; i < form.elements.length; i++) {
      //console.log(form.elements[i].type);
      if (form.elements[i].type == "submit")  continue;
      send_data[form.elements[i].name] = form.elements[i].value;  // ?need to screen value
      }

    if (wrap != undefined)  send_data = {wrap : send_data};

    //ajaxPost(url, JSON.stringify(send_data), cb_ok, cb_failed);
    ajaxPost(url, JSON.stringify(send_data), FormSendOk, FormSendFailed);

    return false;
    }



  for (var i = 0; i < form.elements.length; i++) {
    if (form.elements[i].type == "submit") {
      submit_button = form.elements[i];
      }
    }

//  if (submit_button != undefined) {
//    submit_button.addEventListener("click", submit_clicked, false);
//    }

  }
*/


function FormSend(form, wrap) {
  //console.log("FormSend()");
  var submit_button;

  for (var i = 0; i < form.elements.length; i++) {
    if (form.elements[i].type == "submit") {
      submit_button = form.elements[i];
      }
    }

  var enable_submit_button = function() { submit_button.disabled = false; }


  submit_button.disabled = true;
  var timeout_handle = setTimeout(enable_submit_button, 3000);
  //clearTimeout(timeout_handle);  // on failed response

  var url = form.action;
  var send_data = {};

  for (var i = 0; i < form.elements.length; i++) {
    //console.log(form.elements[i].type);
    if (form.elements[i].type == "submit")  continue;
    send_data[form.elements[i].name] = form.elements[i].value;  // ?need to screen value
    }

  if (wrap != undefined) {
    wrapped = {};
    wrapped[wrap] = send_data;
    send_data = wrapped;
    }

  //ajaxPost(url, JSON.stringify(send_data), cb_ok, cb_failed);
  ajaxPost(url, JSON.stringify(send_data), FormSendOk, FormSendFailed);

  return false;
  }

