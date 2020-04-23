
var socket;

function ws_open() {
  console.log("Соединение установлено.");
  //msgAppendBubble("Соединение установлено.");
  //snd_ok.play();


  //var packet = {
  //  "act" : "ech",  // enter chat
  //  "cht" : chat_id
  //  }
  //
  //socket.send(JSON.stringify(packet));

    // fetch user_names
//  var packet = {
//    "act" : "fun"
//    }
//
//  socket.send(JSON.stringify(packet));

    // request last messages
    // fetch chat history
//  var packet = {
//    "act" : "fch",
//    "cht" : chat_id
//    }
//
//  socket.send(JSON.stringify(packet));
  }




function ws_close(event) {

  if (event.wasClean) {
    console.log("Соединение закрыто чисто.");
    //msgAppendBubble("Соединение закрыто чисто.");
    //msgAppendBubble(event.code);
    //msgAppendBubble(event.reason);
    }

  else {
    console.log("Обрыв соединения.");
    //msgAppendBubble("Обрыв соединения.");
    }

  //alert('Код: ' + event.code + ' причина: ' + event.reason);
  console.log(event);
  }




function ws_message(event) {

  var message = null;

  try {
    message = JSON.parse(event.data);
    console.info(message);
    }
  catch(e) {
    //console.log();
    //console.info();
    //console.warn();
    //console.trace();
    console.error("не парсится json от сервера.");
    console.error(e.message);
    console.log(event.data);

    //console.table();
    }


  if (message.act == "reg") {
    user = message.user;
//x    console.log("user: " + user);
    }


  if (message.act == "fch") {
    var user_fch = message.user;
    var x = message.x;
    var y = message.y;
    var val = message.val;
    fieldChange(x, y, val);
    }


  }


function ws_error(error) {
  //alert("Ошибка " + error.message);
  console.log("Ошибка");
  console.log(error);
  }


//function closeButton() {
//  console.log("Закрываем соединение...");
//  //socket.close();  // не отправляет даже код закрытия
//  socket.close(4020, "closed by button");
//  }



// https://developer.mozilla.org/ru/docs/Web/API/Element
// https://developer.mozilla.org/ru/docs/Web/API/HTMLElement
// https://developer.mozilla.org/ru/docs/Web/API/HTMLTableElement
// https://developer.mozilla.org/ru/docs/Web/API/HTMLTableRowElement
// https://developer.mozilla.org/ru/docs/Web/API/HTMLTableCellElement

var table_own;
var table_enemy;


var table_own_clicked;



  // ---------------- on load ---------------- //

window.onload = function () {

  socket = new WebSocket("wss://c.vtof.ru:443/battleship/");

  socket.onopen = ws_open;
  socket.onclose = ws_close;
  socket.onmessage = ws_message;
  socket.onerror = ws_error;


  // ---------------- //

  var container = document.createElement('DIV');
  document.body.appendChild(container);

  var container_own = document.createElement('DIV');
  container_own.style.display = "inline-block";
  container_own.style.width = "400px";
  container.appendChild(container_own);

  var container_enemy = document.createElement('DIV');
  container_enemy.style.display = "inline-block";
  container.appendChild(container_enemy);


  var col_letters = ['А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'К'];

  table_own = document.createElement('TABLE');

  for (var y = -1; y < 10; y++) {
    var row = table_own.insertRow();

    for (var x = -1; x < 10; x++) {
      var cell = row.insertCell();
      //cell.style.cssText = '...';
      cell.style.width = "30px";
      cell.style.height = "30px";
      cell.style.textAlign = "center";
      cell.style.padding = 0;

      if (y < 0 || x < 0) {
        cell.style.border = "none";
        }

      if (y < 0 && x >= 0) {
        cell.innerText = col_letters[x];
        }

      else if (x < 0 && y >= 0) {
        cell.innerText = y + 1;
        }

      if (y >= 0 && x >= 0) {
        cell.id = "x" + x + "y" + y;
        }

      }

    }

  container_own.appendChild(table_own);

  table_own.addEventListener("click", table_own_clicked, false);



  table_enemy = document.createElement('TABLE');

  for (var y = -1; y < 10; y++) {
    var row = table_enemy.insertRow();

    for (var x = -1; x < 10; x++) {
      var cell = row.insertCell();
      //cell.style.cssText = '...';
      cell.style.width = "30px";
      cell.style.height = "30px";
      cell.style.textAlign = "center";
      cell.style.padding = 0;

      if (y < 0 || x < 0) {
        cell.style.border = "none";
        }

      if (y < 0 && x >= 0) {
        cell.innerText = col_letters[x];
        }

      else if (x < 0 && y >= 0) {
        cell.innerText = y + 1;
        }

      if (y >= 0 && x >= 0) {
        cell.id = "x" + x + "y" + y;
        }

      }

    }

  container_enemy.appendChild(table_enemy);


  table_enemy.addEventListener("click", table_enemy_clicked, false);



  }




var table_own_clicked = function(e) {
  e.stopPropagation();
  e.preventDefault();

  var t = e.target;

  while (t.tagName != 'TD') {
    if (t.parentNode === undefined)  return;
    t = t.parentNode;
    }


    // ---- left mouse button ---- //
  if (e.button == 0) {
    if (t.id == "")  return;
    //alert(t.id);
    x = getX(t.id);
    y = getY(t.id);

    var packet = {
      "act" : "tch",
      "user" : user,
      "x" : x,
      "y" : y
      }

    socket.send(JSON.stringify(packet));
    }

  }




var table_enemy_clicked = function(e) {
  e.stopPropagation();
  e.preventDefault();

  var t = e.target;

  while (t.tagName != 'TD') {
    if (t.parentNode === undefined)  return;
    t = t.parentNode;
    }


    // ---- left mouse button ---- //
  if (e.button == 0) {
    if (t.id == "")  return;
    alert("enemy " + t.id);
    }

  }



function getX(str) {

  var s = 0;
  var xx = '';
  for (var i = 0; i < str.length; i++) {
    var ch = str[i];

    if (s == 0) {
      if (ch == 'x')  s = 1;
      }

    else if (s == 1) {
      if (ch >= '0' && ch <= '9')  xx += ch;
      else  return parseInt(xx);
      }

    }

  return  parseInt(xx);
  }

function getY(str) {

  var s = 0;
  var yy = '';
  for (var i = 0; i < str.length; i++) {
    var ch = str[i];

    if (s == 0) {
      if (ch == 'y')  s = 1;
      }

    else if (s == 1) {
      if (ch >= '0' && ch <= '9')  yy += ch;
      else  return parseInt(yy);
      }

    }

  return  parseInt(yy);
  }



function fieldChange(x, y, val) {
  var tr = table_own.rows[y+1].cells[x+1].innerHTML = val;
  }

