/* xbdhtml.js (Cross-Browser Nav4/Gecko/IE DHTML API)
   14 May 98, Eric Krock, Copyright Netscape Communications
   Permission is granted to reuse, redistribute, and modify
   without charge.
*/

function stringToNumber(s) {
  return parseInt(('0' + s), 10);
}

function Is () {
  var agt=navigator.userAgent.toLowerCase();

  this.major = stringToNumber(navigator.appVersion);
  this.minor = parseFloat(navigator.appVersion);

  this.nav  = ((agt.indexOf('mozilla')!=-1) && ((agt.indexOf('spoofer')==-1)
      && (agt.indexOf('compatible') == -1)));
  this.nav2 = (this.nav && (this.major == 2));
  this.nav3 = (this.nav && (this.major == 3));
  this.nav4 = (this.nav && (this.major == 4));

  this.nav5  = (this.nav && (this.major == 5));
  this.nav6  = (this.nav && (this.major == 5));
  this.gecko = (this.nav && (this.major >= 5));

  this.ie   = (agt.indexOf("msie") != -1);
  this.ie3  = (this.ie && (this.major == 2));
  this.ie4  = (this.ie && (this.major == 3));
  this.ie5  = (this.ie && (this.major == 4));

  this.opera = (agt.indexOf("opera") != -1);

  this.nav4up = this.nav && (this.major >= 4);
  this.ie4up  = this.ie  && (this.major >= 4);
}

var is = new Is();

function dw(str, minVersion, maxVersion) {
  if ( ((dw.arguments.length < 3) || (is.major <= maxVersion))
       && ((dw.arguments.length < 2) || (is.major >= minVersion)) ) {
    document.write(str);
  }
}

function dwb (str, aBoolean) {
  if ((dwb.arguments.length < 2) || aBoolean) document.write(str);
}

function sv(str, minVersion, maxVersion)
{   if ( ((sv.arguments.length < 3) || (is.major <= maxVersion))
         && ((sv.arguments.length < 2) || (is.major >= minVersion)))
    return str;
    else return "";
}

function sb (str, aBoolean)
{   if  ((sb.arguments.length < 2) || aBoolean)
    return str;
    else return "";
}

function getElt () {
  if (is.nav4) {
    var currentLayer = document.layers[getElt.arguments[0]];
    for (var i=1; i<getElt.arguments.length && currentLayer; i++) {
      currentLayer = currentLayer.document.layers[getElt.arguments[i]];
    }
    return currentLayer;
  } else if(document.getElementById && document.getElementsByName) {
    var name = getElt.arguments[getElt.arguments.length-1];
    if(document.getElementById(name)) {
      return document.getElementById(name);
    } else if (document.getElementsByName(name)) {
      return document.getElementsByName(name)[0];
    }
  } else if (is.ie4up) {
    var elt = eval('document.all.' + getElt.arguments[getElt.arguments.length-1]);
    return(elt);
  }
}

function showElt(elt) {
  setEltVisibility(elt, 'visible');
}

function hideElt(elt) {
  setEltVisibility(elt, 'hidden');
}

function showEltByName(eltName) {
  showElt(getElt(eltName));
}

function hideEltByName(eltName) {
  hideElt(getElt(eltName));
}

function setEltVisibility (elt, value) {
  if (navigator.appName == "Netscape") elt.style.display = (value=='hidden'?'none':'block');
  else if (is.nav4) elt.visibility = value;
  else if (elt.style) elt.style.visibility = value;
}

function getEltVisibility (elt) {
  if (is.nav4) {
    var value = elt.visibility;
    if (value == "show") return "visible";
    else if (value == "hide") return "hidden";
    else return value;
  } else if (elt.style) {
    return elt.style.visibility;
  }
}
