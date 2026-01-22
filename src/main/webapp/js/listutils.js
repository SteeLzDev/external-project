/* Original:  Bob Rockers (brockers@subdimension.com)
   This script and many more are available free online at
   The JavaScript Source!! http://javascript.internet.com */

function add(fbox,tbox) {
  if(fbox.value != "") {
    var no = new Option();
    no.value = fbox.value;
    no.text = fbox.value;
    tbox.options[tbox.options.length] = no;
    fbox.value = "";
  }
}

function add(box,name,value) {
  if(name != "" && value != "") {
    var no = new Option();
    no.value = value;
    no.text = name;
    box.options[box.options.length] = no;
  }
}

function exists(box,name,value) {
  for(var i=0; i<box.options.length; i++) {
    if(box.options[i].value == value && box.options[i].text  == name) {
      return true;
    }
  }
  return false;
}

function remove(box) {
  for(var i=0; i<box.options.length; i++) {
    if(box.options[i].selected && box.options[i] != "") {
      box.options[i].value = "";
      box.options[i].text = "";
    }
  }
  BumpUp(box);
}

function remove(box,name,value) {
  for(var i=0; i<box.options.length; i++) {
    if(box.options[i].value == value &&
       box.options[i].text  == name) {
      box.options[i].value = "";
      box.options[i].text = "";
    }
  }
  BumpUp(box);
}

function BumpUp(abox) {
  for(var i = 0; i < abox.options.length; i++) {
    if(abox.options[i].value == "")  {
      for(var j = i; j < abox.options.length - 1; j++)  {
        abox.options[j].value = abox.options[j + 1].value;
        abox.options[j].text = abox.options[j + 1].text;
      }
      var ln = i;
      break;
    }
  }
  if(ln < abox.options.length)  {
    abox.options.length -= 1;
    BumpUp(abox);
  }
}


/* Original:  Roelof Bos (roelof667@hotmail.com)
   Web Site:  http://www.refuse.nl
   This script and many more are available free online at
   The JavaScript Source!! http://javascript.internet.com */

function move(list,index,to) {
  var total = list.options.length-1;
  if (index == -1) return false;
  if (to == +1 && index == total) return false;
  if (to == -1 && index == 0) return false;
  var items = new Array;
  var values = new Array;

  for (i = total; i >= 0; i--) {
    items[i] = list.options[i].text;
    values[i] = list.options[i].value;
  }
  for (i = total; i >= 0; i--) {
    if (index == i) {
      list.options[i + to] = new Option(items[i],values[i], 0, 1);
      list.options[i] = new Option(items[i + to], values[i + to]);
      i--;
    } else {
      list.options[i] = new Option(items[i], values[i]);
    }
  }

  list.focus();
}