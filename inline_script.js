
checkCache();

function checkCache() {
  var need_update = [];

  for (var key in cached_ts) {
    if (localStorage[key] != undefined) {
      if (localStorage['ts_'+key] != undefined) {
        if (localStorage['ts_'+key] >= cached_ts[key]) {
          setCached(key);
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




function setCached(name) {
  console.log('setCached(' + name + ')');

  var fst = document.getElementById(name);
  if (fst) {
    console.log('found. removing setted action sprite...');
    fst.parentNode.removeChild(fst);
    console.log('setting new.');
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
    }

  }


/*
if (localStorage.spriteActions) {
  console.log('get action sprite from cache.');
  setSpriteActions();
  }
else {
  console.log('action sprite miss in cache.');
  }



if (localStorage.spriteModules) {
  console.log('load module sprite from cache.');
  setSpriteModules();
  }
else {
  console.log('module sprite miss in cache.');
  }
*/



  // ---- set sprites ---- //

function setSpriteActions(value) {
  console.log("setSpriteActions()");

  //search for already setted sprite, and remove it if necessary
  var fst = document.getElementById('sprite_actions');
  if (fst) {
    console.log('found. removing setted action sprite...');
    fst.parentNode.removeChild(fst);
    console.log('setting new.');
    }
  var st = document.createElement('STYLE');
  st.id = 'sprite_actions';
  //st.innerHTML = 'a.s {background-image: url(' + localStorage.spriteActions + ');}';
  st.innerHTML = 'hr, div.actions_bar >a >div:first-child {background-image: url(' + value + ');}';
  document.head.appendChild(st);
  }



function setSpriteModules(value) {
  console.log("setSpriteModules()");
  console.log(value);
  //search for already setted sprite, and remove it if necessary
  var fst = document.getElementById('sprite_modules');
  if (fst) {
    console.log('found. removing setted module sprite...');
    fst.parentNode.removeChild(fst);
    console.log('setting new.');
    }
  var st = document.createElement('STYLE');
  st.id = 'sprite_modules';
  st.innerHTML = 'div.modules_bar >a >div:first-child {background-image: url(' + value + ');}';
    //-webkit-min-device-pixel-ratio: 1.2
//  st.innerHTML += '@media (min-resolution: 1.2dppx) {div.modules_bar >a >div:first-child {background-image: url(' + value + '); background-size: 512px;}}';

  document.head.appendChild(st);
  }



function setSpriteModules2(value) {
  console.log("setSpriteModules2()");
  //search for already setted sprite, and remove it if necessary
  var fst = document.getElementById('sprite_modules_2');
  if (fst) {
    console.log('found. removing setted module sprite...');
    fst.parentNode.removeChild(fst);
    console.log('setting new.');
    }
  var st = document.createElement('STYLE');
  st.id = 'sprite_modules_2';
//  st.innerHTML = 'div.modules_bar >a >div:first-child {background-image: url(' + value + ');}';
    //-webkit-min-device-pixel-ratio: 1.2
  st.innerHTML += '@media (min-resolution: 1.2dppx) {div.modules_bar >a >div:first-child {background-image: url(' + value + '); background-size: 512px;}}';

  document.head.appendChild(st);
  }




  // ---- sprites loaders ---- //
/*
function spriteActionsLoaded(r) {
  var aw = {};

  try { aw = JSON.parse(r); }
  catch(e) { return; }

  console.log('new action sprite loaded and put to cache.');

  localStorage.spriteActions = aw.data;
  localStorage.tsSpriteActions = aw.ts;
  console.log('new action sprite ts: ' + aw.ts);

  setSpriteActions();
  }



function spriteModulesLoaded(r) {
  var aw = {};
  //console.log(r);

  try { aw = JSON.parse(r); }
  catch(e) { return; }

  console.log('new module sprite loaded and put to cache.');

  localStorage.spriteModules = aw.data;
  localStorage.spriteModules2 = aw.data2;

  localStorage.tsSpriteModules = aw.ts;
  console.log('new module sprite ts: ' + aw.ts);

  setSpriteModules();
  }
*/



function cachedLoaded(r) {
  var aw = {};

  try { aw = JSON.parse(r); }
  catch(e) { console.error(r);  return; }

  //console.log('new action sprite loaded and put to cache.');
  console.log(aw);



//  localStorage.spriteActions = aw.data;
//  localStorage.tsSpriteActions = aw.ts;
//  console.log('new action sprite ts: ' + aw.ts);

//  setSpriteActions();


  if (aw.res != undefined) {
    console.log(aw.res);
//    var cached = aw.cached;

//    for (var i = 0; i < res.length; i++) {
    for (var key in aw.res) {
//      var rs = res[i];
      //console.log(st.class + ' ' + st.method + ' (' + st.file + ' : ' + st.line + ')');
      console.log(key);
      //console.log(aw.res[key]);
//      console.log(rs.name);
//      console.log(rs.data);
//      console.log(rs.ts);

      localStorage['ts_'+key] = cached_ts[key];
      localStorage[key] = aw.res[key];
//      localStorage['ts_'+rs.name] = rs.ts;
//      localStorage[rs.name] = rs.data;

//      cached_ts[rs.name] = rs.ts;
      // ---- re-set_dom(res) ---- //
      setCached(key);
      }

    // if (key == 'sprite_actions')  setSpriteActions();  todo: refresh()
//    checkCache();
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




  // ---- validate and fetch ---- //
/*
if (localStorage.tsSpriteModules == undefined || localStorage.tsSpriteModules < tsSpriteModules) {
  console.log('cached module sprite is outdated. fetching new one from server.');
  //ajaxMin('/a/res/sm/', spriteModulesLoaded, spriteLoadFailed);

  var send_data = {};
  ajaxPost('/a/res/sm/', JSON.stringify(send_data), spriteModulesLoaded, spriteLoadFailed);
  }
else {
  console.log('cached module sprite is actual.');
  console.log('localStorage.tsSpriteModules ('+localStorage.tsSpriteModules+')' + ' > ' + 'tsSpriteModules ('+tsSpriteModules+')');
  }



if (localStorage.tsSpriteActions == undefined || localStorage.tsSpriteActions < tsSpriteActions) {
  console.log('cached action sprite is outdated.');
//  ajaxMin('/a/res/sa/', spriteActionsLoaded);

  var send_data = {};
  ajaxPost('/a/res/sa/', JSON.stringify(send_data), spriteModulesLoaded, spriteLoadFailed);
  }
else {
  console.log('cached action sprite is actual.');
  console.log('localStorage.tsSpriteActions ('+localStorage.tsSpriteActions+')' + ' > ' + 'tsSpriteActions ('+tsSpriteActions+')');
  }
*/

/*
validateAndFetch('sprite_modules', tsSpriteModules);
validateAndFetch('sprite_actions', tsSpriteActions);


function validateAndFetch(res_name, ts) {

  if (localStorage['ts_'+res_name] == undefined)  localStorage['ts_'+res_name] = 0;

  if (localStorage['ts_'+res_name] < ts) {
    console.log('cached \"' + res_name + '\" is outdated. fetching new one from server.');
    var send_data = {'name' : res_name};
//x    ajaxPost('/a/res/', JSON.stringify(send_data), spriteModulesLoaded, cachedLoadFailed);
    ajaxPost('/a/res/', JSON.stringify(send_data), cachedLoaded, cachedLoadFailed);
    }
  else {
    console.log('cached \"' + res_name + '\" is actual.');
//    console.log('localStorage.tsSpriteModules ('+localStorage.tsSpriteModules+')' + ' > ' + 'tsSpriteModules ('+tsSpriteModules+')');
    console.log('localStorage (' + localStorage['ts_'+res_name] + ')' + ' >= ' + 'ts (' + ts + ')');
    }

  }
*/









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

  ar.setRequestHeader("Content-Type", "application/json");
  //ar.setRequestHeader("Content-Type", "application/json; charset=utf-8");
  ar.send(data);
  }
