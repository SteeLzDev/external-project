var AspaDupla = '"';
var AspaSimples = "'";

function Remote(Url,NomeJanela,Caracteristicas) {
  var remote = null;
  remote = window.open(Url,NomeJanela,Caracteristicas);
}

function parse_num(n) {
  return numeral().unformat(n);
  /*
  var i;
  var s;

  s = "";
  for (i = 0; i < n.length ; i++) {
    if (n.charAt(i) != getGroupingSeparator()) {
      s += n.charAt(i);
    }
  }
  n = "";
  for (i = 0; i < s.length ; i++) {
    if (s.charAt(i) == getDecimalSeparator()) {
      n += getDecimalSeparator();
    } else {
      n += s.charAt(i);
    }
  }
  return n; */
}

// Verifica se o valor passado por parametro e um numero
function isNumber(x) {
  var anum=/(^\d+$)|(^\d+\.\d+$)/;
  if (anum.test(x)) {
    testresult=true;
  } else {
    testresult=false;
  }
  return (testresult);
}

// Carrega �ndices para o pr�ximo controle e controle anterior
function InicializarIndices() {
  if (document.CargaInicial==null) {
    document.CargaInicial=false; // Seta para s� fazer uma vez por documento
    var ctrlAnterior=null;
    var IndAnt=0;
    for (var i=0; i<document.forms[0].elements.length;i++) {
      var e=document.forms[0].elements[i];
      if (e.type!="hidden" && e.type!="image" && e.type!="select") {
        if (ctrlAnterior != null) {
          ctrlAnterior.IndicePosterior=i;
        }
        ctrlAnterior=e;
        e.Indice=i;
        e.IndiceAnterior=IndAnt;
      }
    }
  }
}

// Colocar o foco em determinado campo
function SetarFoco(ind) {
  InicializarIndices();
  if (isNaN(ind) && document.forms[0].elements[ind].type!="hidden") {
    document.forms[0].elements[ind].focus();
  } else {
    for (;ind<document.forms[0].elements.length;ind++) {
      if (document.forms[0].elements[ind].type!="hidden") {
        break;
      }
    }
    if (ind<=document.forms[0].elements.length) {
      document.forms[0].elements[ind].focus();
    }
  }
}

// Verificar qual navegador
function QualNavegador() {
  var s = navigator.appName;
  if (s == "Microsoft Internet Explorer") {
    return "IE";
  } else if (navigator.userAgent.indexOf("Firefox") != -1) {
    return "NE";
  } else if (s == "Netscape") {
    return "NE";
  } else {
    return "";
  }
}

// Setar o evento com mascara e op��o de n�o setar o style default do eConsig
// para que se possa configurar styles customizados externamente.
function SetarEventoMascaraStyle(ctrl, Mascara, AutoSkip, Command, defaultStyle) {
  if (defaultStyle) {
    fin(ctrl);
  }

  // Filtra navegadores conhecidos
  var s = QualNavegador();
  if (s.length==0) {
    return;
  }

  var md = new MobileDetect(window.navigator.userAgent);
  var isChromeInAndroid = md.is('AndroidOS') && md.is('Chrome');

  if (ctrl.onkeypress==null) {
    if (AutoSkip==null) {  AutoSkip=true;  }
    ctrl.oldValue="";
    ctrl.Mask=Mascara;
    if (Mascara.charAt(0) == '#') {
      ctrl.Tam = Mascara.substring(2, Mascara.length);
    } else {
      ctrl.Tam = Mascara.length;
    }
    ctrl.AutoSkip=AutoSkip;
    ctrl.Saltar=false;
    if ((Command != null) && (Command != '')) {
      ctrl.Command = Command;
    }
    InicializarIndices();
    ctrl.onkeypress=ValidarTecla;
    ctrl.onkeyup=SaltarCampo;
    if (isChromeInAndroid) {
      ctrl.onkeyup=ValidarTeclaChromeMobile;
      ctrl.onkeydown=keyDownHandler;
    }
  }
}

function keyDownHandler(event) {
  event.target._oldValue = event.target.value;
}
function difference(value1, value2) {
  var output = [];
  for (i = 0; i < value2.length; i++) {
    if (value1[i] !== value2[i]) {
      output.push(value2[i]);
    }
  }
  return output.join("");
}

function SetarEventoMascara(ctrl, Mascara, AutoSkip, Command) {
  SetarEventoMascaraStyle(ctrl, Mascara, AutoSkip, Command, true);
}

// Setar o evento
function SetarEvento(ctrl, Tam, Tipo, AutoSkip, Command) {
  // Calcula a mascara
  if (Tipo == '') { Tipo = '*'; }

  var mask = '#' + Tipo + Tam;
  SetarEventoMascara(ctrl, mask, AutoSkip, Command);
}

function SaltarCampo(ctrl) {
  if (ctrl == null || ctrl.oldValue == undefined) {  ctrl = this;  }
  if (ctrl.oldValue == undefined || ctrl.oldValue.length > ctrl.value.length) {
    return ;
  }

  if (ctrl.AutoSkip && ctrl.Saltar) {
    if (ctrl.Saltar) {
      ctrl.Saltar = false;
      if (ctrl.Command != null) {
        eval(ctrl.Command);
      }

      var nextFocus = ctrl.nf;
      if (nextFocus == null) {
        nextFocus = ctrl.getAttribute("nf");
      }

      if (nextFocus == null) {
        while (ctrl.IndicePosterior != null && document.forms[0].elements[ctrl.IndicePosterior].disabled == true) {
          ctrl = document.forms[0].elements[ctrl.IndicePosterior];
        }
        if (ctrl.IndicePosterior != null) {
          SetarFoco(ctrl.IndicePosterior);
        } else {
          ctrl.blur();
          ctrl.focus();
        }
      } else {
        elt = getElt(nextFocus);
        if (elt != null) {
          while (elt.IndicePosterior != null && elt.disabled == true) {
            elt = document.forms[0].elements[elt.IndicePosterior];
          }
          if (elt != null) {
            elt.focus();
          }
        }
      }
    }
  }
}

function ValidarTeclaChromeMobile(event) {
  var ok = ValidarTecla(event);
  event.target._newValue = event.target.value;
  var diff = difference(event.target._oldValue, event.target._newValue);
  if (!ok) {
    // Se ao validar a tecla, ela n�o � aceita, remove o valor adicionado ao campo original
    event.target.value = event.target.value.substring(0, event.target.value.length - diff.length);
  }
  SaltarCampo(event.target);
}

// Fazer o salto de campo
function ValidarTecla(evnt) {
  var tk;
  var c;
  var original;
  // Recebe a tela pressionada
  tk = (window.event ? window.event.keyCode : evnt.which);
  
  var field = evnt.target;
  var md = new MobileDetect(window.navigator.userAgent);
  var isChromeInAndroid = md.is('AndroidOS') && md.is('Chrome');
  if (isChromeInAndroid) {
    if (field._oldValue != undefined && field._oldValue.length > field.value.length) {
      // Se o tamanho do texto diminuiu, ent�o foi pressionado o backspace
      return true;
    }
    // Se � Chrome no Android como � evento de key, o caractere inserido j� est� no campo 
    tk = field.value.charAt(field.selectionStart - 1).charCodeAt();
    if (isNaN(tk)) {
      return true;
    }
  }
  
  c = String.fromCharCode(tk);
  original = c;
  c = c.toUpperCase();

  /* -- Este trecho faz com que o <Enter> tenha a fun��o de <Tab>.
   *    Para que o foco do campo atual mude para campos do Tipo "image" deve-se:
   * 1) Definir o atributo nf do campo atual para o nome do campo "image"
   *    desejado; e
   * 2) Definir os atributos NAME e/ou ID do campo "image".
   */
  if (tk == 13 && field.type != 'textarea') {
    field.oldValue = field.value;
    field.Saltar = true;
    SaltarCampo(this);
    return false;
  }

  // No FireFox os comandos acionados com Ctrl ou Alt
  // disparam eventos no onKeyPress
  if (!window.event) {
    if (evnt.ctrlKey || evnt.altKey) {
      return true;
    }
  }

  // S� aceita teclas alfanum�ricas. N�o aceita teclas de controle
  if (tk < 32) {
    return true;
  }
  if (tk > 255) {
    return false;
  }
  if (field.Mask.charAt(0) == '#') {
    m = field.Mask.charAt(1);
  } else if (isChromeInAndroid) {
    // Se � Chrome no Android como � evento de key, o caractere inserido j� est� no campo 
    m = field.Mask.charAt(field.value.length - 1);
  } else {
    m = field.Mask.charAt(field.value.length);
  }

  if (m == "-" || m == "." || m == "," || m == "/" || m == ":" || m == " ") {
    if (isChromeInAndroid) {
      // Se � Chrome no Android, insere o caractere da m�scara antes do �ltimo d�gito, visto 
      // que � tratado em um evento de keyUp ou seja o caractere j� est� no campo 
   	  field.value = field.value.substring(0, field.value.length - 1) + m + field.value.substring(field.value.length - 1);
    } else {
      field.value += m;
    }
  }

  if (field.Mask.charAt(0) == '#') {
    m = field.Mask.charAt(1);
  } else if (isChromeInAndroid) {
    // Se � Chrome no Android como � evento de key, o caractere inserido j� est� no campo 
    m = field.Mask.charAt((field.value.length - 1) % field.Tam);
  } else {
    m = field.Mask.charAt(field.value.length % field.Tam);
  }

  switch (m) {
    case "D": // D�gito
      if (c<"0" || c>"9") {
        return false;
      }
      break;
    case "N": // Number
      if ((c<"0" || c>"9") && (c!=getDecimalSeparator() && c!=getGroupingSeparator())) {
        return false;
      }
      if ((c==getDecimalSeparator()) && ((field.value.indexOf(getDecimalSeparator())>-1) || (field.value.length==0))) {
        return false;
      }
      if ((c==getGroupingSeparator()) && (field.value.length==0)) {
        return false;
      }
      break;
    case "F": // Float
      if ((c<"0" || c>"9") && (c!=getDecimalSeparator()) && (c!="-")) {
        return false;
      }
      if ((c==getDecimalSeparator()) && ((field.value.indexOf(getDecimalSeparator())>-1) || (field.value.length==0))) {
        return false;
      }
      if ((c=="-") && (field.value.length!=0)) {
        return false;
      }
      break;
    case "I": // IP
      // Apenas s�o permitidos algarismos, pontos ou asteriscos.
      if ((c < "0" || c > "9") && (c != ".") && (c!="*")) {
        return false;
      }

      // S� pode inicar por algarismo.
      if (field.value.length == 0 && (c < "0" || c > "9")) {
        return false;
      }

      // Caso seja um algarismo
      if (c >= "0" && c <= "9") {
          // N�o pode haver mais de 3 algarismos seguidos.
          if (field.value.length - field.value.lastIndexOf(".") > 3) {
            return false;
          }

          // N�o pode vir depois de asterisco.
          if (field.value.charAt(field.value.length - 1) == "*") {
            return false;
          }
      }
      
      // Conta quantos pontos j� existem.
      var qtdePontos = 0;
      for (i = 0; i < field.value.length; i++) {
        if (field.value.charAt(i) == ".") {
          qtdePontos += 1;
        }
      }
        
      // Caso seja um ponto
      if (c == ".") {
        // N�o pode haver dois pontos seguidos.
        if (field.value.charAt(field.value.length - 1) == ".") {
          return false;
        }
        
        // N�o pode haver mais de 3 pontos.
        if (qtdePontos == 3) {
          return false;
        }
      }

      // Caso seja asterisco.
      if (c == "*") {
        // S� pode existir depois de ponto.
        if (field.value.charAt(field.value.length - 1) != ".") {
          return false;
        }

        // J� devem existir no m�nimo 2 pontos.
        if (qtdePontos < 1) {
          return false;
        }
      }
      
      break;
    case "M": // MENSAGEM - FAQ
      if (c == "'" || c == "\"" || c == "<" || c == ">") {
         return false;
      }
      break;
    case "C": // Caracteres
      if (c<"A" || c>"Z") {
        return false;
      }
      break;
    case "U": // Caracteres Ma�usculo apenas
        if (original<"A" || original>"Z") {
          return false;
        }
        break;
    case "A": // Alfa Num�rico
      if ((c<"A" || c>"Z") && (c<"0" || c>"9")) {
        return false;
      }
      break;
    case "E": // Campo c�digo identificador
        if ((c<"A" || c>"Z") && (c<"0" || c>"9")&& (c!="_")) {
          return false;
        }
        break;
    case "L": // Login
      if ((c<"A" || c>"Z") && (c<"0" || c>"9") && 
          (c!="/") && (c!="_") && (c!="-") && (c!=".")) {
        return false;
      }
      break;
    case "T": // Telefone
      if ((c<"0" || c>"9") && (c!="-") && (c!="(") && (c!=")") && (c!=",") && (c!=" ")) {
        return false;
      }
      if ((c==" ") && (field.value.length==0)) {
        return false;
      }
      break;
    case "P": // Lista de Prazos
      if ((c<"0" || c>"9") && (c!=",")) {
        return false;
      }
      break;
    default:
      break;
  }

 /* Se AutoSkip for True, e a qtd de caracteres for igual a Tam, entao o foco muda para o proximo campo.
    Se AutoSkip for True, e a qtd de caracteres for igual a Tam, e nao houver um proximo campo entao
     a funcao retorna false, nao incluindo o caractere no campo.
    Se AutoSkip for False e a qtd de caracteres for igual a Tam, entao a funcao retorna false, nao
     incluindo o caractere no campo.
 */
  if (field.AutoSkip) {
    field.oldValue=field.value;
    if (isChromeInAndroid) {
      field.Saltar=(field.value.length>=field.Tam);
    } else {
      field.Saltar=(field.value.length>=field.Tam-1);
    }
  } else {
    if (field.value.length>=field.Tam) {
      return false;
    }
  }
  return true;
}

function ValidaMascara(ctrl) {
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
            fouterr(ctrl);
            return false;
          }
          break;
        case "N":
          if ((c<"0" || c>"9") && (c!=getGroupingSeparator() && c!=getDecimalSeparator())) {
            fouterr(ctrl);
            return false;
          }
          if (ctrl.type=='select-one') {
            if ((c==getDecimalSeparator()) && ((ctrl.options[ctrl.selectedIndex].value.indexOf(getDecimalSeparator())>-1) || (i==0))) {
              fouterr(ctrl);
              return false;
            }
          } else {
            if ((c==getDecimalSeparator()) && ((ctrl.value.indexOf(getDecimalSeparator())>-1) || (i==0))) {
              fouterr(ctrl);
              return false;
            }
          }
          if ((c==getGroupingSeparator()) && (i==0)) {
            fouterr(ctrl);
            return false;
          }
          break;
        case "F":
          if ((c<"0" || c>"9") && (c!=getDecimalSeparator()) && (c!="-")) {
            fouterr(ctrl);
            return false;
          }
          if ((c==getDecimalSeparator()) && (i==0)) {
            fouterr(ctrl);
            return false;
          }
          if ((c=="-") && (i!=0)) {
            fouterr(ctrl);
            return false;
          }
          break;
        case "M": // MENSAGEM - FAQ
          if (c == "'" || c == "\"" || c == "<" || c == ">") {
            fouterr(ctrl);
            return false;
          }
          break;
        case "C":
          if (c<"A" || c>"Z") {
            fouterr(ctrl);
            return false;
          }
          break;
        case "A":
          if ((c<"A" || c>"Z" ) && ( c<"0" || c>"9")) {
            fouterr(ctrl);
            return false;
          }
          break;
        case "E":
          if ((c<"A" || c>"Z" ) && ( c<"0" || c>"9") && (c!="_")) {
            fouterr(ctrl);
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
            fouterr(ctrl);
            return false;
          }
          if (((c==" ") || (c=="-") || (c==")")) && (i==0)) {
            fouterr(ctrl);
            return false;
          }
          break;
        case "-":
          if (c != "-") {
            fouterr(ctrl);
            return false;
          }
          break;
        case ".":
          if (c != ".") {
            fouterr(ctrl);
            return false;
          }
          break;
        case ",":
          if (c != ",") {
            fouterr(ctrl);
            return false;
          }
          break;
        case "/":
          if (c != "/") {
            fouterr(ctrl);
            return false;
          }
          break;
        case " ":
          if (c != " ") {
            fouterr(ctrl);
            return false;
          }
          break;
        default:
          break;
      }
    }

    if ((ctrl.Mask.charAt(0) != '#') && (controle != ctrl.Tam) &&
        (controle > 0)) {
      fouterr(ctrl);
      return false;
    }
  }
  return true;
}

//
// Funcao para atualizar um Combo de acordo com um determinado valor (Filtro).
// --> Conteudo: array de Opcoes para preenchimento do Combo.
//               - Opcoes: array no formato [codigo, texto, valor_para_filtro,
//                                           valor_a_ser_selecionado].
//
function FiltraCombo(Combo, Conteudo, Filtro, Selecionado) {
  var i;
  if (Selecionado == null) {  Selecionado = 'NULL'  };
  var s = 1;
  var sdp = 0;
  for (i = Combo.length - 1; i >= 0 ; i--) {
    Combo.options[i] = null;
  }

  Combo.options[Combo.length] = new Option (mensagem('rotulo.campo.selecione'), 'NULL');

  for(i = 0; i < Conteudo.length; i++) {
    if (Conteudo[i][2] == Filtro) {
      Combo.options[Combo.length] = new Option (Conteudo[i][1], Conteudo[i][0]);
      if (String(Conteudo[i][0]).toUpperCase() == String(Selecionado).toUpperCase()) {
        sdp = s;
      }
      s++;
    }
  }
  if (Combo.options[sdp] != null) {
    Combo.options[sdp].selected = true;
  }
}

function AtualizaFiltraComboExt(Combo, MatrizConteudo, Filtro, Filtro1, Selecionado, IncluiTudo, incluiSelecione, valorSelecione, textoSelecione) {
  var i;
  var s = 0;
  var sdp = 0;
  var Encontrou = false;

  for (i = Combo.length - 1; i >= 0 ; i--) {
    Combo.options[i] = null;
  }

  if (String(Filtro).toUpperCase() == "NULL") Filtro = "";
  if (String(Filtro1).toUpperCase() == "NULL") Filtro1 = "";

  IncluiTudo = IncluiTudo || (Filtro == "") && (Filtro1 == "");

  if(incluiSelecione) {
    Combo.options[Combo.length] = new Option (textoSelecione, valorSelecione);
    s++;
    if (valorSelecione == Selecionado.toUpperCase()) {
      sdp = s - 1;
    }
  }

  for(i = 0; i < MatrizConteudo.length; i++) {
    var ndp = "";
    var ndp1 = "";
    Filtro = Filtro.toUpperCase();
    Filtro1 = Filtro1.toUpperCase();
    if (Filtro != "") {
      var ndp = MatrizConteudo[i][2];
    }
    if (Filtro1 != "") {
      ndp1 = MatrizConteudo[i][3];
    }

    if (IncluiTudo || ((ndp == Filtro) && (ndp1 == Filtro1)) || (MatrizConteudo[i][0] == "") || (MatrizConteudo[i][0] == "0")) {
      if ((MatrizConteudo[i][0] != "0") && (MatrizConteudo[i][0] != "")) {
        Encontrou = true;
      }
      Combo.options[Combo.length] = new Option (MatrizConteudo[i][1], MatrizConteudo[i][0]);
      s++;
      if (MatrizConteudo[i][0] == Selecionado.toUpperCase()) {
        sdp = s - 1;
      }
    }
  }

  if (Combo.options[sdp] != null) {
    Combo.options[sdp].selected=true;
  }
  return Encontrou;
}

function trim(Str) {
  if (Str != '') {
    while (Str.charAt(0) == ' ') {
      Str = Str.substr(1, Str.length);
    }
    while (Str.charAt(Str.length - 1) == ' ') {
      Str = Str.substr(0, Str.length - 1);
    }
  }
  return Str;
}

// Arredonda n�meros no formato ingl�s
function ARRED(NUM, DEC) {
  var number=numeral(NUM).value();
  if (isNaN(parseInt(number))) {
    return number;
  } else {
    if (isNaN(parseFloat(number))) {
      return number;
    } else {
      if((DEC == 0) || (typeof(DEC) != 'number')) {
        return Math.round(number);
      } else {
        return Math.round(number * Math.pow(10, DEC)) / Math.pow(10, DEC);
      }
    }
  }
}

// Formata sem separador de milhar (n�mero formato ingl�s)
function FormataContabil(Numero, CasasDecimais, FmtNeg) {
  return FormataContabilEx(Numero, CasasDecimais, FmtNeg).split(getGroupingSeparator()).join('');
}

//Formata com separador de milhar (n�mero formato ingl�s)
function FormataContabilEx(Numero, CasasDecimais, FmtNeg) {
  var SeparadorMilhar = getGroupingSeparator();
  var SeparadorDecimal = getDecimalSeparator();
  var NumeroNegativo = (Numero < 0);
  if (NumeroNegativo) {
    Numero = Math.abs(Numero);
  }
  Numero = ARRED(Numero, CasasDecimais);
  var a = String(Numero).split('.');
  var i;

  if (!a[1]) {  a[1] = '';  }
  for (i=0; i< CasasDecimais; i++) {  a[1] += '0';  }
  a[1] = a[1].substring(0, CasasDecimais);
  if (String(a[0]).length > 3) {
    sa = String(a[0]);
    na = '';
    while (sa.length > 3) {
      na = String(sa.substr(sa.length - 3, 3)) + na;
      sa = sa.substr(0, sa.length - 3);
      if (sa.length > 0) {  na = SeparadorMilhar + String(na);  }
    }
    if (sa.length > 0) {  na = sa + String(na);  }
    a[0] = na;
  }
  if (CasasDecimais > 0) {
    resultado = a.join(SeparadorDecimal);
  } else {
    resultado = a.join('');
  }
  if (NumeroNegativo) {
    if (FmtNeg == '(') {
      resultado = '(' + resultado + ')';
    } else {
      resultado = '-' + resultado;
    }
  }
  return resultado;
}

function fin(ctrl) {
  // if ((ctrl.type == 'radio') || (ctrl.type != 'check') && (ctrl.type != 'checkbox')) {
  //   ctrl.style.backgroundColor='#EBEBEB'; ctrl.style.color='black';
  // } else {
  //   ctrl.style.backgroundColor='#DDDDDD'; ctrl.style.color='black';
  // }
}

function fout(ctrl) {
  if ((ctrl.type != 'radio') && (ctrl.type != 'check') && (ctrl.type != 'checkbox')) {
    ctrl.style.backgroundColor='white'; ctrl.style.color='black';
  } else {
    ctrl.style.backgroundColor=''; ctrl.style.color='black';
  }
  
  if (ctrl.type == 'text') {
    // Remove os espa�os em branco, caso o usu�rio tenha colado 
    // um conte�do no campo com espa�os
    ctrl.value = trim(ctrl.value);
  }
}

function fouterr(ctrl) {
  ctrl.style.backgroundColor='white'; ctrl.style.color='red';
}

function ConfirmaUrl(Msg, URL) {
  if (confirm(Msg)) {
    if (typeof(postData) == "function") {
      postData(URL);
    } else {
      window.location.href = URL;
    }
  } else {
    return;
  }
}

function selRow(tr) {
  tr.oldClassName=tr.className;
  tr.className="Ls";
}

function unselRow(tr) {
  tr.className=tr.oldClassName;
}

function checkAll(form, chkNome) {
  for (i=0; i < form.elements.length; i++) {
    var e = form.elements[i];
    if (((e.type == 'check') || (e.type == 'checkbox')) && (e.name == chkNome)) {
      e.checked = true;
    }
  }
}

function uncheckAll(form, chkNome, chkValor) {
  for (i=0; i < form.elements.length; i++) {
    var e = form.elements[i];
    if (((e.type == 'check') || (e.type == 'checkbox')) && 
         (e.name == chkNome || chkNome == null) &&
         (e.value == chkValor || chkValor == null)) {
      e.checked = false;
    }
  }
}

/**
  Desmarca os demais checkboxs com mesmo nome, mas com
  valor diferente do chkValor. Faz com que o check 
  tenha o mesmo comportamento de um radio button.
  Ex: onClick="uncheckOthers(f0, 'infSaldoDevedor', this.value)"
*/
function uncheckOthers(form, chkNome, chkValor) {
  for (i=0; i < form.elements.length; i++) {
    var e = form.elements[i];
    if (((e.type == 'check') || (e.type == 'checkbox')) && (e.name == chkNome)) {
      if (e.value != chkValor) {
        e.checked = false;
      }
    }
  }
}

/*
  Faz o boqueio de diversas 'entidades'.
  @param status   - Status atual da  'entidade' ( 0 - bloqueado , 1 - desbloqueado )
                    Se a 'entidade' for usuario ( 1 - ativo , 2 - bloqueado, 3 - cancelado )
  @param codigo   - Codigo da 'entidade'
  @param tipo     - Tipo   da 'entidade' ( ORG, CSA, SVC, CSE, COR, EST )
  @param alink    - Link para a p�gina que ir� bloquear / desbloquear a entidade
  @param desc     - Descri��o da 'entidade' que sera bloqueada / desbloqueada
  @param msg      - Mensagem de confirma��o adicional
*/
function BloquearEntidade(status, codigo, tipo, alink, desc, msg) {
  var url = alink + (alink.indexOf('?') == -1 ? "?" : "&") + "status=" + status + "&codigo=" + codigo;
  if (status == "1") {
    return ConfirmaUrl((msg != null && msg != '' ? msg + "\n" : "") + mensagem('mensagem.confirmacao.bloqueio.entidade').replace('{0}', desc), url);
  } else {
    return ConfirmaUrl((msg != null && msg != '' ? msg + "\n" : "") + mensagem('mensagem.confirmacao.desbloqueio.entidade').replace('{0}', desc), url);
  }
}

/*
  Exclui diversas 'entidades'.
  @param codigo - Codigo da 'entidade'
  @param tipo   - Tipo   da 'entidade' ( ORG, CSA, SVC, CSE, COR, EST )
  @param alink  - Link para a p�gina que realiza a exclus�o
  @param desc   - Descri��o da 'entidade' que sera excluida
*/
function ExcluirEntidade(codigo, tipo, alink, desc) {
  var url = alink + (alink.indexOf('?') == -1 ? "?" : "&")  + "codigo=" + codigo + "&excluir=sim";
  return ConfirmaUrl(mensagem('mensagem.confirmacao.exclusao.entidade').replace('{0}', desc), url);
}

function VerificaPeriodoExt(Dia, Mes, Ano, DiaFim, MesFim, AnoFim, NumDiasPermitido,
                            Hora, Minuto, Segundo, HoraFim, MinutoFim, SegundoFim) {
  if ((Dia != "") && (Mes != "") && (Ano != "") &&
      (DiaFim != "") && (MesFim != "") && (AnoFim != ""))   {
    var datai = new Date(Mes + "/" + Dia + "/" + Ano);
    var dataf = new Date(MesFim + "/" + DiaFim + "/" + AnoFim);
    var Prazo;

    dataf.setHours(datai.getHours());
    dataf.setMinutes(datai.getMinutes());
    dataf.setSeconds(datai.getSeconds());

    if ((Hora && Hora != "") && (Minuto && Minuto != "") && (Segundo && Segundo != "")) {
      datai.setHours(Hora);
      datai.setMinutes(Minuto);
      datai.setSeconds(Segundo);
    }

    if ((HoraFim && HoraFim != "") && (MinutoFim && MinutoFim != "") && (SegundoFim && SegundoFim != "")) {
      dataf.setHours(HoraFim);
      dataf.setMinutes(MinutoFim);
      dataf.setSeconds(SegundoFim);
    }

    if (dataf < datai) {
      alert(mensagem('mensagem.erro.data.final.menor.inicial'));
      return false;
    } else {
      Prazo = dataf - datai;
      Prazo = Math.round(Prazo/86400000);
      if ((NumDiasPermitido && NumDiasPermitido != "") && (Prazo > NumDiasPermitido)) {
        alert(mensagem('mensagem.erro.dias.periodo.invalido').replace('{0}', NumDiasPermitido));
        return false;
      } else {
        return true;
      }
    }
  } else {
    return true;
  }
}

function SelecionaComboExt(Combo, Selecionado) {
  if (Selecionado != '') {
    for (var i = Combo.length - 1; i >= 0 ; i--) {
      if (String(Combo.options[i].text).toUpperCase().indexOf(String(Selecionado).toUpperCase()) != -1) {
        Combo.options[i].selected = true;
        break;
      }
    }
  }
}

function SelecionaComboMsg(Combo, Selecionado, Msg) {
  var achou = false;
  for (var i = Combo.length - 1; i >= 0 ; i--) {
    if (String(Combo.options[i].value).toUpperCase() == String(Selecionado).toUpperCase()) {
      Combo.options[i].selected = true;
      achou = true;
      break;
    }
  }
  if (!achou) {
    Combo.options[0].selected = true;
    if (Msg != '') alert(Msg);
  }
}

function SelecionaComboBanco(Combo, Selecionado, ArrayBancos) {
  var ii, achou = false, c, Msg = mensagem('mensagem.erro.banco.invalido');

  c = String(Selecionado).substring(0,1);
  if (c>="0" && c<="9") {
    SelecionaComboMsg(Combo, parseFloat(Selecionado), Msg);
    return;
  }

  for ( var j = ArrayBancos.length - 1; j >= 0 ; j--) {
    ii = String(ArrayBancos[j][1]).toUpperCase().search( String(Selecionado).toUpperCase());
    if (ii > -1 ) {
      achou = true;
      break;
    }
  }
  if (achou) {
    achou = false;
    for (var i = Combo.length - 1; i >= 0 ; i--) {
      if (Combo.options[i].value == ArrayBancos[j][0]) {
        Combo.options[i].selected = true;
        achou = true;
        break;
      }
    }
  }
  if (!achou) {
    Combo.options[0].selected = true;
    alert(Msg);
  }
}

// retorna o valor da opc�o do check que esta marcada
//   exemplo:
//     getCheckedRadio("frmPesquisaAssunto", "assuntoHierarquiaUnidade")
function getCheckedRadio(formname, checkname) {
  var radio = eval("document.forms."+formname+"."+checkname);
  if (radio == undefined) {
    return null;
  }
  if (radio.length == undefined && (radio.type == "hidden" || radio.checked)) {
    return radio.value;
  }
  for (c=0;c<radio.length;c++) {
    if (radio[c].checked) {
      return radio[c].value;
    }
  }
  return null;
}

// habilita/desabilita um radio button
//   exemplo:
//     disableRadioButton("frmPesquisaAssunto", "assuntoHierarquiaUnidade", true)
function disableRadioButton(formname, checkname, disable) {
  var radio = eval("document.forms."+formname+"."+checkname);
  if (radio != undefined) {
    if (radio.length == undefined) {
      radio.disabled = disable;
    } else {
      for (c=0; c<radio.length; c++) {
        radio[c].disabled = disable;
      }
    }
  }
}

// marca um radio de acordo com o valor
//   exemplo:
//     setCheckedRadio("frmPesquisaAssunto", "assuntoHierarquiaUnidade", "0")
function setCheckedRadio(formname, checkname, defaultValue) {
  var radio = eval("document.forms."+formname+"."+checkname);
  if (radio != undefined) {
    if (radio.length == undefined) {
      if (radio.value == defaultValue) {
        radio.checked = true;
      }
    } else {
      for (c=0; c<radio.length; c++) {
        if (radio[c].value == defaultValue) {
          radio[c].checked = true;
        }
      }
    }
  }
}

// Formata o texto para poder ser usado em compara��o:
// - colocando o texto em mai�sculas;
// - removendo todos os caracteres que n�o sejam alfa-num�ricos;
// - e removendo os zeros no in�cio.
function formataParaComparacao(texto) {
  result = texto;
  if (result != null && result != '') {
    result = result.toUpperCase();
    result = result.replace(/\W/g, "").replace(/_/, "");
    while (result.indexOf("0") == 0) {
      result = result.substring(1, result.length);
    }
  }
  return result;
}

// Focaliza o primeiro campo editavel de um formulario
function focusFirstField() {
  ok = false;
  if (document.forms[0] != null) {
    // alert(document.forms[0].elements.length);
    for (var i = 0; (i < document.forms[0].elements.length) && !ok; i++) {
      var e = document.forms[0].elements[i];
      // alert(e.name + " - " + e.type);
      if (e.type != 'button' && e.type != 'hidden' && 
          e.type != 'image'  && e.type != 'reset'  && 
          e.type != 'submit') {
        if (!e.disabled) {
          e.focus();
          ok = true;
        }
      }
    }
  }
}

function hideEmptyFieldSet() {
  var fieldtags = ['input', 'textarea', 'select'];
  var fieldsets = document.getElementsByTagName('fieldset');
  for (var j = 0; j < fieldsets.length; j++) {
    var fieldset = fieldsets[j];
    var qtd = 0;

    for (var i = 0; i < fieldtags.length; i++) {
      var fields = fieldset.getElementsByTagName(fieldtags[i]);
      for (var k = 0; k < fields.length; k++) {
        if (fields[k].type != 'hidden') {
          qtd++;
        }
      }
    }
    if (qtd == 0) {
      fieldset.style.display = 'none';
    }
  }
}

function limparCombo(combo) {
  for (i=0; i<combo.length; i++) {
    combo.options[i].selected = false;
  }
}

function enableAll() {
  if (document.forms[0] != null) {
    for (var i = 0; (i < document.forms[0].elements.length); i++) {
      var e = document.forms[0].elements[i];
      if (e.type != 'button' && e.type != 'hidden' && 
          e.type != 'image'  && e.type != 'reset'  && 
          e.type != 'submit') {
        if (e.disabled) {
          e.disabled = false;
        }
      }
    }
  }
}

function addOption(combo, posicao, valor, codigo) {
  var options = combo.options;
  for (i=options.length;i>posicao; i--) {
    opt = new Option('FAKE', 'FAKE');
    oldOpt = options[i-1];
    options[i-1] = opt;
    options[i] = oldOpt;
  }
  options[0] = new Option(valor, codigo);
}

function loadSelectOptions(select, options, selectedValue, skipDefaultValue) {
  clearSelectOptions(select);

  if (skipDefaultValue == undefined || skipDefaultValue == null || skipDefaultValue == false) {
    var el = document.createElement("option");
    el.textContent = mensagem('rotulo.campo.selecione');
    el.value = '';
    el.selected = true;
    select.appendChild(el);
  }

  for (var i = 0; i < options.length; i++) {
    el = document.createElement("option");
    el.textContent = options[i];
    el.value = options[i];
    if (options[i] == selectedValue) {
      el.selected = true;
    }
    select.appendChild(el);
  }
}

function clearSelectOptions(select) {
  while (select.options.length > 0) {
    select.remove(0);
  }
}

function getFieldValue(field) {
   switch(field.type) {
      case "text" :
      case "textarea" :
      case "password" :
      case "number" :
      case "hidden" :
         return field.value;

      case "select-one" :
         var i = field.selectedIndex;
         if (i == -1) {
           return "";
         } else {
           return (field.options[i].value == "") ? field.options[i].text : field.options[i].value;
         }

      case "select-multiple" :
         var allChecked = new Array();
         for (i = 0; i < field.options.length; i++) {
           if (field.options[i].selected) {
             allChecked[allChecked.length] = (field.options[i].value == "") ? field.options[i].text : field.options[i].value;
           }
         }
         return allChecked;

      case "radio" :
      case "checkbox" :
         if (field.checked) {
           return field.value;
         } else {
           return "";
         }

      default :
         if (field[0].type == "radio") {
            for (i = 0; i < field.length; i++) {
              if (field[i].checked) {
                return field[i].value;
              }
            }
            return "";
         } else if(field[0].type == "checkbox") {
            var allChecked = new Array();
            for (i = 0; i < field.length; i++) {
              if (field[i].checked) {
                allChecked[allChecked.length] = field[i].value;
              }
            }
            return allChecked;
         }
         break;
   }

   return "";
}

function lpad(input, width, pad) {
  pad = pad || '0';
  input = input + '';
  return input.length >= width ? input : new Array(width - input.length + 1).join(pad) + input;
}

function rpad(input, width, pad) {
  pad = pad || '0';
  input = input + '';
  return input.length >= width ? input : input + new Array(width - input.length + 1).join(pad);
}

function addHint(obj, op) {
    var msgPadrao = mensagem('ajuda.campo.captcha');
    if (op=='blur') {
        // preenche o valor padr�o se o campo estiver vazio
        if (obj.value == '') {
            obj.value = msgPadrao;
            obj.className = 'EditCaptchaHint';
        }
        obj.blur();
    } else if (op == 'focus') {
        // delete o valor padr�o 
        if (obj.value == msgPadrao) {
            obj.value = '';
            obj.className='EditLogin';
        }
        obj.focus();
    }
    return;
}
function bi(campoPreenchido) {
	if (campoPreenchido == 1){
	$('#biModal').modal('show');
	} else {
	$('#biEmBrancoModal').modal('show');
	}
	
}
