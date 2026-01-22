function fouterrv4(ctrl) {	
  ctrl.mascaraValida = false;
  if (ctrl.type=='text') {
     ctrl.style.backgroundColor='white'; ctrl.style.color='red';
  }    
}

function SetarEventoMascaraV4(ctrl, Mascara, AutoSkip, Command) {
	var defaultStyle = ctrl.mascaraValida == undefined || ctrl.mascaraValida;
	SetarEventoMascaraStyle(ctrl, Mascara, AutoSkip, Command, defaultStyle);
	if (ctrl.mascaraValida != undefined) {
		ctrl.mascaraValida = true;
	}
}

function ValidaMascaraV4(ctrl) {
  if (ctrl.Mask != null) {
    if (ctrl.type=='select-one') {
      if (ctrl.options[ctrl.selectedIndex].value.length >= ctrl.Tam) {
        ctrl.options[ctrl.selectedIndex].value = ctrl.options[ctrl.selectedIndex].value.substring(0, ctrl.Tam);
      }
    } else {
      if (ctrl.value.length >= ctrl.Tam) {
        ctrl.value = ctrl.value.substring(0, ctrl.Tam);
      }
    }

    if (ctrl.type=='select-one') {
      var controle = ctrl.options[ctrl.selectedIndex].value.length;
    } else {
      var controle = ctrl.value.length;
    }

    for (i=0;i<controle;i++) {
      if (ctrl.type=='select-one') {
        c = ctrl.options[ctrl.selectedIndex].value.charAt(i).toUpperCase();
      } else {
        c = ctrl.value.charAt(i).toUpperCase();
      }
      if (ctrl.Mask.charAt(0) == '#') {
        m = ctrl.Mask.charAt(1);
      } else {
        m = ctrl.Mask.charAt(i);
      }

      switch (m) {
        case "D":
          if (c<"0" || c>"9") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "N":
          if ((c<"0" || c>"9") && (c!=getGroupingSeparator() && c!=getDecimalSeparator())) {
            fouterrv4(ctrl);
            return false;
          }
          if (ctrl.type=='select-one') {
            if ((c==getDecimalSeparator()) && ((ctrl.options[ctrl.selectedIndex].value.indexOf(getDecimalSeparator())>-1) || (i==0))) {
              fouterrv4(ctrl);
              return false;
            }
          } else {
            if ((c==getDecimalSeparator()) && ((ctrl.value.indexOf(getDecimalSeparator())>-1) || (i==0))) {
              fouterrv4(ctrl);
              return false;
            }
          }
          if ((c==getGroupingSeparator()) && (i==0)) {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "F":
          if ((c<"0" || c>"9") && (c!=getDecimalSeparator()) && (c!="-")) {
            fouterrv4(ctrl);
            return false;
          }
          if ((c==getDecimalSeparator()) && (i==0)) {
            fouterrv4(ctrl);
            return false;
          }
          if ((c=="-") && (i!=0)) {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "M": // MENSAGEM - FAQ
          if (c == "'" || c == "\"" || c == "<" || c == ">") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "C":
          if (c<"A" || c>"Z") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "A":
          if ((c<"A" || c>"Z" ) && ( c<"0" || c>"9")) {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "E":
	      if ((c<"A" || c>"Z" ) && ( c<"0" || c>"9") && (c!="_")) {
	        fouterrv4(ctrl);
	        return false;
          }
          break;
        case "L": // Login
	      if ((c<"A" || c>"Z") && (c<"0" || c>"9") && 
	          (c!="/") && (c!="_") && (c!="-") && (c!=".")) {
            return false;
          }
          break;
        case "T":
          if ((c<"0" || c>"9") && (c!="-") && (c!="(") && (c!=")") && (c!=",") && (c!=" ")) {
            fouterrv4(ctrl);
            return false;
          }
          if (((c==" ") || (c=="-") || (c==")")) && (i==0)) {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "-":
          if (c != "-") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case ".":
          if (c != ".") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case ",":
          if (c != ",") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case "/":
          if (c != "/") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        case " ":
          if (c != " ") {
            fouterrv4(ctrl);
            return false;
          }
          break;
        default:
          break;
      }       
    }

    if ((ctrl.Mask.charAt(0) != '#') && (controle != ctrl.Tam) &&
        (controle > 0)) {
      fouterrv4(ctrl);
      return false;
    }
  }
  return true;
}