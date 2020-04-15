
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
  st.innerHTML = 'a.s {background-image: url(' + localStorage.spriteActions + ');}';
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
  ajaxMin('/res/sa/', spriteActionsLoaded);
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
  ajaxMin('/res/sm/', spriteModulesLoaded);
  }
else {
  console.log('cached sprite is actual.');
  console.log('localStorage.tsSpriteModules ('+localStorage.tsSpriteModules+')' + ' > ' + 'tsSpriteModules ('+tsSpriteModules+')');
  }

